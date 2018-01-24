/*
 * Copyright 2018 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.todo;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;
import io.realm.todo.model.Item;
import io.realm.todo.ui.ItemsRecyclerAdapter;

import static io.realm.todo.Constants.REALM_URL;

public class ItemsActivity extends AppCompatActivity {

    private Realm mRealm;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        // get the selected category
        category = getIntent().getStringExtra("category");

        setSupportActionBar(findViewById(R.id.toolbar));

        findViewById(R.id.fab).setOnClickListener(view -> {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null);
            EditText taskText = dialogView.findViewById(R.id.task);

            Spinner spinner = dialogView.findViewById(R.id.categories);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.category_array, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            new AlertDialog.Builder(ItemsActivity.this)
                    .setTitle("Add a new task")
                    .setMessage("What do you want to do next?")
                    .setView(dialogView)
                    .setPositiveButton("Add", (dialog, which) -> {
                        mRealm.executeTransactionAsync(realm -> {
                            Item item = new Item();
                            item.setBody(taskText.getText().toString());
                            item.setCategory(spinner.getSelectedItem().toString());
                            realm.insert(item);
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        RealmResults<Item> items = setUpRealm();

        final ItemsRecyclerAdapter itemsRecyclerAdapter = new ItemsRecyclerAdapter(items);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemsRecyclerAdapter);


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                String id = itemsRecyclerAdapter.getItem(position).getItemId();
                mRealm.executeTransactionAsync(realm -> realm.where(Item.class).equalTo("itemId", id).findFirst().deleteFromRealm());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private RealmResults<Item> setUpRealm() {
        // Use Partial Sync to only sync on the device the Items
        // belonging to the selected category
        SyncConfiguration configuration = new SyncConfiguration.Builder(
                SyncUser.currentUser(),
                REALM_URL + "/items")
                .partialRealm()
                .build();
        mRealm = Realm.getInstance(configuration);

        return mRealm
                .where(Item.class)
                .equalTo("category", category)
                .sort("timestamp", Sort.DESCENDING)
                .findAllAsync("subscription");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.unsubscribeAsync("subscription", new Realm.UnsubscribeCallback() {
            @Override
            public void onSuccess(String subscriptionName) {
                mRealm.close();
            }

            @Override
            public void onError(String subscriptionName, Throwable error) {
                mRealm.close();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SyncUser syncUser = SyncUser.currentUser();
            if (syncUser != null) {
                syncUser.logout();
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

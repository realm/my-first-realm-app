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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.todo.model.Item;
import io.realm.todo.ui.ItemsRecyclerAdapter;

public class ItemsActivity extends AppCompatActivity {

    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.fab).setOnClickListener(view -> {
            final EditText taskEditText = new EditText(ItemsActivity.this);

            new AlertDialog.Builder(ItemsActivity.this)
                    .setTitle("Add a new task")
                    .setMessage("What do you want to do next?")
                    .setView(taskEditText)
                    .setPositiveButton("Add", (dialog, which) -> {
                        mRealm.beginTransaction();
                        Item item = new Item();
                        item.setBody(taskEditText.getText().toString());
                        mRealm.insert(item);
                        mRealm.commitTransaction();
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        // Setup the recycler view with the result of the Realm query
        mRealm = Realm.getDefaultInstance();
        RealmResults<Item> items = mRealm
                .where(Item.class)
                .sort("timestamp", Sort.DESCENDING)
                .findAllAsync();
        recyclerView.setAdapter(new ItemsRecyclerAdapter(items));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // TODO handle logout
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

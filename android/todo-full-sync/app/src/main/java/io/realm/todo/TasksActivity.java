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

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;
import io.realm.todo.model.Item;
import io.realm.todo.model.Project;
import io.realm.todo.ui.TasksRecyclerAdapter;

public class TasksActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_PROJECT_URL = "TasksActivity.projectUrl";

    private Realm realm;
    private TextView statusView;
    private TasksRecyclerAdapter tasksRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        setSupportActionBar(findViewById(R.id.toolbar));
        statusView = findViewById(R.id.status);

        findViewById(R.id.fab).setOnClickListener(view -> {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null);
            EditText taskText = dialogView.findViewById(R.id.task);
            new AlertDialog.Builder(TasksActivity.this)
                    .setTitle("Add a new task")
                    .setMessage("What do you want to do next?")
                    .setView(dialogView)
                    .setPositiveButton("Add", (dialog, which) -> realm.executeTransactionAsync(realm -> {
                        Item item = new Item();
                        item.setBody(taskText.getText().toString());
                        realm.where(Project.class).findFirst().getTasks().add(item);
                    }))
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        setTitle("Loading");

        String projectUrl = getIntent().getStringExtra(INTENT_EXTRA_PROJECT_URL);
        SyncConfiguration config = SyncUser.current()
                .createConfiguration(projectUrl)
                .fullSynchronization()
                .initialData(realm -> {
                    Project project = new Project();
                    String userId = SyncUser.current().getIdentity();
                    project.setOwner(userId);
                    project.setName("My ToDo List");
                    project.setTimestamp(new Date());
                    realm.insert(project);
                })
                .waitForInitialRemoteData(30, TimeUnit.SECONDS)
                .build();

        Realm.getInstanceAsync(config, new Realm.Callback() {
            @Override
            public void onSuccess(Realm realm) {
                TasksActivity.this.realm = realm;
                Project project = realm.where(Project.class).findFirst();
                if (project != null) {
                    setTitle(project.getName());
                    RealmResults<Item> tasks = project.getTasks().where().sort("timestamp", Sort.ASCENDING).findAllAsync();
                    tasks.addChangeListener(list -> {
                        if (list.isEmpty()) {
                            setStatus("Press + to add a new task");
                        } else {
                            setStatus("");
                        }
                    });
                    tasksRecyclerAdapter = new TasksRecyclerAdapter(tasks);
                    recyclerView.setAdapter(tasksRecyclerAdapter);
                } else {
                    setStatus("Could not load project");
                }
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                String id = tasksRecyclerAdapter.getItem(position).getItemId();
                realm.executeTransactionAsync(realm -> {
                    Item item = realm.where(Item.class).equalTo("itemId", id).findFirst();
                    if (item != null) {
                        item.deleteFromRealm();
                    }
                });
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setStatus(String str) {
        statusView.setText(str);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SyncUser syncUser = SyncUser.current();
            if (syncUser != null) {
                syncUser.logOut();
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

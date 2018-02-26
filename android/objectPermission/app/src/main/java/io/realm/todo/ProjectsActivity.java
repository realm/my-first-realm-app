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
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.SyncUser;
import io.realm.sync.permissions.Permission;
import io.realm.sync.permissions.Role;
import io.realm.todo.model.Project;
import io.realm.todo.ui.ProjectsRecyclerAdapter;

public class ProjectsActivity extends AppCompatActivity {
    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        setSupportActionBar(findViewById(R.id.toolbar));

        String roleId = getIntent().getStringExtra("role_id");

        findViewById(R.id.fab).setOnClickListener(view -> {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null);
            EditText taskText = dialogView.findViewById(R.id.task);
            new AlertDialog.Builder(ProjectsActivity.this)
                    .setTitle("Add a new project")
                    .setView(dialogView)
                    .setPositiveButton("Add", (dialog, which) -> realm.executeTransactionAsync(realm -> {

                        String name = taskText.getText().toString();
                        Project project = realm.createObject(Project.class, UUID.randomUUID().toString());
                        project.setName(name);
                        project.setTimestamp(new Date());

                        // Create a restrictive permission to limit read/write access to the current user only
                        if (roleId != null) {
                            Role role = realm.where(Role.class).equalTo("name", roleId).findFirst();
                            Permission permission = new Permission(role);
                            permission.setCanRead(true);
                            permission.setCanQuery(true);
                            permission.setCanCreate(true);
                            permission.setCanUpdate(true);
                            permission.setCanUpdate(true);
                            permission.setCanDelete(true);
                            permission.setCanSetPermissions(true);
                            permission.setCanModifySchema(true);

                            project.getPermissions().add(permission);
                        }
                    }))
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        // perform a partial query to obtain
        // only projects belonging to our  SyncUser.
        // the permission system will filter out and return only our projects, no
        // need to filter by the project owner like we did in Tutorial#2 (PartialSync)
        realm = Realm.getDefaultInstance();
        RealmResults<Project> projects = realm
                .where(Project.class)
                .sort("timestamp", Sort.DESCENDING)
                .findAllAsync();

        final ProjectsRecyclerAdapter itemsRecyclerAdapter = new ProjectsRecyclerAdapter(this, projects);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemsRecyclerAdapter);
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
            SyncUser syncUser = SyncUser.currentUser();
            if (syncUser != null) {
                syncUser.logout();
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

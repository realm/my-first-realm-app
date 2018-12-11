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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import io.realm.ObjectServerError;
import io.realm.PermissionManager;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncUser;
import io.realm.log.RealmLog;
import io.realm.permissions.Permission;
import io.realm.todo.model.Project;
import io.realm.todo.ui.ProjectsRecyclerAdapter;

public class ProjectsActivity extends AppCompatActivity {

    private Realm realm;

    private RecyclerView recyclerView;
    private TextView statusView;
    private PermissionManager pm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        setSupportActionBar(findViewById(R.id.toolbar));
        recyclerView = findViewById(R.id.recycler_view);
        statusView = findViewById(R.id.status);

        findViewById(R.id.fab).setOnClickListener(view -> {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null);
            ((EditText) dialogView.findViewById(R.id.task)).setHint(R.string.project_description);
            EditText taskText = dialogView.findViewById(R.id.task);
            new AlertDialog.Builder(ProjectsActivity.this)
                    .setTitle("Add a new project")
                    .setView(dialogView)
                    .setPositiveButton("Add", (dialog, which) -> {
                        String projectName = taskText.getText().toString();
                        if (!isValidProjectName(projectName)) {
                            Toast.makeText(ProjectsActivity.this, "Invalid project name. It must only contain " +
                                    "the characters a-z and 0-9", Toast.LENGTH_SHORT).show();
                        } else {
                            String path = "/~/project-" + projectName;
                            String url = Constants.REALM_URL + path;
                            SyncConfiguration config = SyncUser.current().createConfiguration(url)
                                    .fullSynchronization()
                                    .initialData(realm -> {
                                        Project project = new Project();
                                        String userId = SyncUser.current().getIdentity();
                                        String name = taskText.getText().toString();
                                        project.setOwner(userId);
                                        project.setName(name);
                                        project.setTimestamp(new Date());
                                        realm.insert(project);
                                    })
                                    .build();
                            RealmLog.info("Connecting to " +  config.getServerUrl().toString());
                            Realm.getInstance(config).close();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        // Using the current SyncUser identity, we create a subscription for projects created by that user.
        setStatus("Loading...");
        pm = SyncUser.current().getPermissionManager();
        pm.getPermissions(new PermissionManager.PermissionsCallback() {

            @Override
            public void onSuccess(RealmResults<Permission> permissions) {
                RealmResults<Permission> filteredPermissions = permissions.where().contains("path", "/project-").findAllAsync();
                final ProjectsRecyclerAdapter itemsRecyclerAdapter = new ProjectsRecyclerAdapter(ProjectsActivity.this, filteredPermissions);
                recyclerView.setLayoutManager(new LinearLayoutManager(ProjectsActivity.this));
                recyclerView.setAdapter(itemsRecyclerAdapter);
                if (permissions.isEmpty()) {
                    setStatus("Press + to add a new project");
                } else {
                    setStatus("");
                }
            }

            @Override
            public void onError(ObjectServerError error) {
                showMessage(error.getErrorMessage());
            }
        });
    }

    private boolean isValidProjectName(String projectName) {
        return projectName.matches("[a-z0-9]*");
    }

    private void setStatus(String str) {
        statusView.setText(str);
    }

    private void showMessage(String msg) {
        Toast.makeText(ProjectsActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pm.close();
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

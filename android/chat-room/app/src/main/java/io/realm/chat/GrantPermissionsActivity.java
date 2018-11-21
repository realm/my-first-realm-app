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

package io.realm.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncUser;
import io.realm.chat.model.PrivateChatRoom;
import io.realm.chat.ui.EditPermissionsRecyclerAdapter;
import io.realm.chat.ui.GrantPermissionsRecyclerAdapter;
import io.realm.chat.ui.GrantedPermission;
import io.realm.chat.util.PermissionHelper;
import io.realm.sync.permissions.Permission;
import io.realm.sync.permissions.PermissionUser;
import io.realm.sync.permissions.Role;

/**
 * Allow the connected user to grant (read/write) permissions to the other users, for the newly created {@link PrivateChatRoom}
 * or edit the permissions for an existing {@link PrivateChatRoom}.
 */
public class GrantPermissionsActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grant_permissions_layout);

        setSupportActionBar(findViewById(R.id.toolbar));

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        String chatRoom = getIntent().getStringExtra("room_name");
        boolean editMode = getIntent().getBooleanExtra("edit_mode", false);

        final String identity = SyncUser.current().getIdentity();
        setTitle(chatRoom + " " + identity);

        realm = Realm.getDefaultInstance();

        final AtomicReference<EditPermissionsRecyclerAdapter> permissionsRecyclerAdapter = new AtomicReference<>();
        final AtomicReference<GrantPermissionsRecyclerAdapter> usersRecyclerAdapter = new AtomicReference<>();

        if (editMode) {
            PrivateChatRoom room = realm.where(PrivateChatRoom.class).equalTo("name", chatRoom).findFirst();
            // current user role
            Role privateRole = realm.where(PermissionUser.class).equalTo("id", identity).findFirst().getPrivateRole();

            RealmResults<Permission> aclFiltered = room.getACL().where().notEqualTo("role.name", privateRole.getName()).findAll();

            permissionsRecyclerAdapter.set(new EditPermissionsRecyclerAdapter(aclFiltered));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(permissionsRecyclerAdapter.get());

        } else {
            // these are not users existing in ROS, but users already connected using partial sync (exclude current user)
            RealmResults<PermissionUser> users = realm.where(PermissionUser.class)
                    .notEqualTo("id", identity)
                    .findAllAsync();

            usersRecyclerAdapter.set(new GrantPermissionsRecyclerAdapter(users));
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(usersRecyclerAdapter.get());
        }

        findViewById(R.id.save).setOnClickListener(view -> {
            // begin an async transaction to update permissions
            realm.executeTransactionAsync(bgRealm -> {
                List<GrantedPermission> grantedPermissions =
                        editMode ? permissionsRecyclerAdapter.get().getGrantedPermission() : usersRecyclerAdapter.get().getGrantedPermission();

                if (editMode) {
                    PermissionHelper.updateGrantedPermissions(bgRealm, grantedPermissions, chatRoom);
                } else {
                    PermissionHelper.grantPermissions(bgRealm, grantedPermissions, chatRoom, identity);
                }
            }, this::finish);
        });
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

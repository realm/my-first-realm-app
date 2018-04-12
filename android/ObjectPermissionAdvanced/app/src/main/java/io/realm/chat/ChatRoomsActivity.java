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

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncUser;
import io.realm.chat.model.PrivateChatRoom;
import io.realm.chat.model.PublicChatRoom;
import io.realm.chat.ui.PrivateChatRoomsRecyclerAdapter;
import io.realm.chat.ui.PublicChatRoomsRecyclerAdapter;

/**
 * Displays the list of {@link PublicChatRoom} and {@link PrivateChatRoom} available to the current user, with
 * the possibility to add new ones.
 */
public class ChatRoomsActivity extends AppCompatActivity {
    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_rooms_activity);

        setSupportActionBar(findViewById(R.id.toolbar));

        setTitle("Welcome " + SyncUser.current().getIdentity());

        findViewById(R.id.btn_add_public_room).setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null);
            EditText taskText = dialogView.findViewById(R.id.task);
            new AlertDialog.Builder(ChatRoomsActivity.this)
                    .setTitle("Add a public Chat Room")
                    .setView(dialogView)
                    .setPositiveButton("Add", (dialog, which) -> {
                        final String name = taskText.getText().toString();
                        realm.executeTransactionAsync(realm -> realm.createObject(PublicChatRoom.class, name));
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        findViewById(R.id.btn_add_private_room).setOnClickListener(v -> {
            View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null);
            EditText taskText = dialogView.findViewById(R.id.task);
            new AlertDialog.Builder(ChatRoomsActivity.this)
                    .setTitle("Add a private Chat Room")
                    .setView(dialogView)
                    .setPositiveButton("Add", (dialog, which) -> {
                        String name = taskText.getText().toString();
                        Intent intent = new Intent(ChatRoomsActivity.this, GrantPermissionsActivity.class);
                        intent.putExtra("room_name", name);
                        ChatRoomsActivity.this.startActivity(intent);
                    })
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        });

        realm = Realm.getDefaultInstance();
        RealmResults<PublicChatRoom> publicChatRooms = realm
                .where(PublicChatRoom.class)
                .findAllAsync();

        RealmResults<PrivateChatRoom> privateChatRooms = realm
                .where(PrivateChatRoom.class)
                .findAllAsync();

        PublicChatRoomsRecyclerAdapter publicChatRoomsRecyclerAdapter = new PublicChatRoomsRecyclerAdapter(this, publicChatRooms);
        RecyclerView publicRecyclerView = findViewById(R.id.recycler_view_public);
        publicRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        publicRecyclerView.setAdapter(publicChatRoomsRecyclerAdapter);

        PrivateChatRoomsRecyclerAdapter privateChatRoomsRecyclerAdapter = new PrivateChatRoomsRecyclerAdapter(this, privateChatRooms);
        RecyclerView privateRecyclerView = findViewById(R.id.recycler_view_private);
        privateRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        privateRecyclerView.setAdapter(privateChatRoomsRecyclerAdapter);
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

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import io.realm.Realm;
import io.realm.SyncUser;
import io.realm.todo.model.ChatRoom;
import io.realm.todo.model.Message;
import io.realm.todo.model.PrivateChatRoom;
import io.realm.todo.model.PublicChatRoom;
import io.realm.todo.ui.MessagesRecyclerAdapter;

/**
 * Displays discussion ({@link Message}) for the selected room, (this could be {@link PublicChatRoom} or {@link PrivateChatRoom}.
 */
public class ChatRoomActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messages_layout);

        setSupportActionBar(findViewById(R.id.toolbar));

        EditText editTextMessage = findViewById(R.id.message);

        String chatRoom = getIntent().getStringExtra("room_name");
        boolean isPrivate = getIntent().getBooleanExtra("is_private_room", false);

        findViewById(R.id.send).setOnClickListener(view -> realm.executeTransactionAsync(realm -> {
            Message message = new Message();
            message.setBody(editTextMessage.getText().toString());
            message.setAuthor(SyncUser.current().getIdentity());
            if (isPrivate) {
                PrivateChatRoom privateRoom = realm.where(PrivateChatRoom.class).equalTo("name", chatRoom).findFirst();
                if (privateRoom != null) {// need to check if this room still visible to this user (maybe permission to read were revoked in the meantime)
                    privateRoom.getMessages().add(message);
                }
            } else {
                realm.where(PublicChatRoom.class).equalTo("name", chatRoom).findFirst().getMessages().add(message);
            }
        }, () -> editTextMessage.setText("")));

        realm = Realm.getDefaultInstance();
        ChatRoom room;
        if (isPrivate) {
            room = realm.where(PrivateChatRoom.class).equalTo("name", chatRoom).findFirst();
        } else {
            room = realm.where(PublicChatRoom.class).equalTo("name", chatRoom).findFirst();
        }

        String identity = SyncUser.current().getIdentity();
        setTitle(room.getName() + " " + identity);

        final MessagesRecyclerAdapter messagesRecyclerAdapter = new MessagesRecyclerAdapter(room.getMessages(), identity);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messagesRecyclerAdapter);
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

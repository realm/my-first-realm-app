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


package io.realm.todo.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmObject;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.sync.permissions.ObjectPrivileges;
import io.realm.todo.ChatRoomActivity;
import io.realm.todo.GrantPermissionsActivity;
import io.realm.todo.R;
import io.realm.todo.model.ChatRoom;
import io.realm.todo.model.PrivateChatRoom;

/**
 * Adapter to display the list of private chat rooms.
 */
public class PrivateChatRoomsRecyclerAdapter extends RealmRecyclerViewAdapter<PrivateChatRoom, PrivateChatRoomsRecyclerAdapter.MyViewHolder> {
    private final Context context;

    public PrivateChatRoomsRecyclerAdapter(Context context, OrderedRealmCollection<PrivateChatRoom> data) {
        super(data, true);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.private_chat_room_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ChatRoom chatRoom = getItem(position);
        if (chatRoom != null) {
            holder.textView.setText(chatRoom.getName());
            ObjectPrivileges privileges = RealmObject.getRealm(chatRoom).getPrivileges(chatRoom);
            if (privileges.canSetPermissions()) {
                holder.btnEdit.setVisibility(View.VISIBLE);
                holder.btnEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(PrivateChatRoomsRecyclerAdapter.this.context, GrantPermissionsActivity.class);
                    intent.putExtra("room_name", holder.textView.getText().toString());
                    intent.putExtra("edit_mode", true);
                    PrivateChatRoomsRecyclerAdapter.this.context.startActivity(intent);
                });
            } else {
                holder.btnEdit.setVisibility(View.GONE);
            }

            holder.textView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra("room_name", chatRoom.getName());
                intent.putExtra("is_private_room", true);
                context.startActivity(intent);
            });
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView btnEdit;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.room_name);
            btnEdit = itemView.findViewById(R.id.btn_edit);
        }
    }
}

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
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.todo.ChatRoomActivity;
import io.realm.todo.model.ChatRoom;
import io.realm.todo.model.PublicChatRoom;

/**
 * Adapter to display the list of public chat rooms.
 */
public class PublicChatRoomsRecyclerAdapter extends RealmRecyclerViewAdapter<PublicChatRoom, PublicChatRoomsRecyclerAdapter.MyViewHolder> {
    private final Context context;

    public PublicChatRoomsRecyclerAdapter(Context context, OrderedRealmCollection<PublicChatRoom> data) {
        super(data, true);
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ChatRoom chatRoom = getItem(position);
        if (chatRoom != null) {
            holder.textView.setText(chatRoom.getName());
            holder.textView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChatRoomActivity.class);
                intent.putExtra("room_name", chatRoom.getName());
                intent.putExtra("is_private_room", false);
                context.startActivity(intent);
            });
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}

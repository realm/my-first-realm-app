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

package io.realm.chat.ui;

import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.chat.R;
import io.realm.chat.model.Message;

/**
 * Adapter to display the list of {@link Message}.
 */
public class MessagesRecyclerAdapter extends RealmRecyclerViewAdapter<Message, MessagesRecyclerAdapter.MyViewHolder> {
    private final String userIdentity; // used to differentiate visually messages from other users
    @ColorInt
    private int primaryColor;
    @ColorInt
    private int secondaryColor;

    public MessagesRecyclerAdapter(OrderedRealmCollection<Message> data, String userIdentity) {
        super(data, true);
        this.userIdentity = userIdentity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item_layout, parent, false);
        primaryColor = parent.getContext().getResources().getColor(android.R.color.darker_gray);
        secondaryColor = parent.getContext().getResources().getColor(android.R.color.transparent);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = getItem(position);
        if (message != null) {
            holder.setItem(message);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView body;
        TextView author;

        MyViewHolder(View itemView) {
            super(itemView);
            body = itemView.findViewById(R.id.body);
            author = itemView.findViewById(R.id.author);
        }

        void setItem(Message item){
            this.body.setText(item.getBody());
            this.author.setText(item.getAuthor());
            if (userIdentity.equals(item.getAuthor())) {
                ((View)body.getParent()).setBackgroundColor(primaryColor);
            } else {
                ((View)body.getParent()).setBackgroundColor(secondaryColor);
            }
        }
    }
}

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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.todo.R;
import io.realm.todo.model.Item;

public class TasksRecyclerAdapter extends RealmRecyclerViewAdapter<Item, TasksRecyclerAdapter.MyViewHolder> {

    public TasksRecyclerAdapter(OrderedRealmCollection<Item> data) {
        super(data, true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Item item = getItem(position);
        holder.setItem(item);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        CheckBox checkBox;
        Item item;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.body);
            checkBox = itemView.findViewById(R.id.checkbox);
            checkBox.setOnClickListener(this);
        }

        void setItem(Item item){
            this.item = item;
            this.textView.setText(item.getBody());
            this.checkBox.setChecked(item.getIsDone());
        }

        @Override
        public void onClick(View v) {
            String itemId = item.getItemId();
            boolean isDone = this.item.getIsDone();
            this.item.getRealm().executeTransactionAsync(realm -> {
                Item item = realm.where(Item.class).equalTo("itemId", itemId).findFirst();
                if (item != null) {
                    item.setIsDone(!isDone);
                }
            });
        }
    }
}

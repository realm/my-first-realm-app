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
import io.realm.permissions.Permission;
import io.realm.todo.Constants;
import io.realm.todo.TasksActivity;

public class ProjectsRecyclerAdapter extends RealmRecyclerViewAdapter<Permission, ProjectsRecyclerAdapter.MyViewHolder> {
    private final Context context;

    public ProjectsRecyclerAdapter(Context context, OrderedRealmCollection<Permission> data) {
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
    public void onBindViewHolder(MyViewHolder holder, int position)  {
        final Permission project = getItem(position);
        if (project != null) {
            String title =  extractProjectName(project.getPath());
            holder.textView.setText(title);
            holder.textView.setOnClickListener(v -> {
                Intent intent = new Intent(context, TasksActivity.class);
                intent.putExtra(TasksActivity.INTENT_EXTRA_PROJECT_URL, Constants.REALM_URL + project.getPath());
                context.startActivity(intent);
            });
        }
    }

    private String extractProjectName(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1].replace("project-", "");
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}

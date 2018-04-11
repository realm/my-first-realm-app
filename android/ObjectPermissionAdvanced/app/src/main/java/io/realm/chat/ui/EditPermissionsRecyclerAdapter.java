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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.chat.R;
import io.realm.sync.permissions.Permission;

/**
 * Adapter to display the list of existing granted permission(s) per user id.
 */
public class EditPermissionsRecyclerAdapter extends RealmRecyclerViewAdapter<Permission, EditPermissionsRecyclerAdapter.MyViewHolder> {
    private Set<MyViewHolder> userPermissions = new HashSet<>(getItemCount());

    public EditPermissionsRecyclerAdapter(OrderedRealmCollection<Permission> data) {
        super(data, true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.permission_item_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Permission permission = getItem(position);
        if (permission != null) {
            holder.textView.setText(permission.getRole().getMembers().first().getId());
            holder.canReadCheckBox.setChecked(permission.canRead());
            holder.canWriteCheckBox.setChecked(permission.canUpdate());
            userPermissions.add(holder);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        CheckBox canReadCheckBox;
        CheckBox canWriteCheckBox;

        MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.body);
            canReadCheckBox = itemView.findViewById(R.id.can_read_checkbox);
            canWriteCheckBox = itemView.findViewById(R.id.can_write_checkbox);
        }
    }

    public List<GrantedPermission> getGrantedPermission () {
        List<GrantedPermission> permissions = new ArrayList<>(userPermissions.size());
        GrantedPermission grantedPermission;
        for (MyViewHolder viewHolder : userPermissions) {
            grantedPermission = new GrantedPermission();
            grantedPermission.userId = viewHolder.textView.getText().toString();
            grantedPermission.canRead = viewHolder.canReadCheckBox.isChecked();
            grantedPermission.canWrite = viewHolder.canWriteCheckBox.isChecked();
            permissions.add(grantedPermission);
        }
        return permissions;
    }
}

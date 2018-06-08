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

package io.realm.chat.model;

import io.realm.RealmList;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;
import io.realm.sync.permissions.Permission;

@RealmClass
public class PrivateChatRoom implements ChatRoom {
    @PrimaryKey
    @Required
    private String name;
    private RealmList<Message> messages;
    private RealmList<Permission> permissions;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RealmList<Message> getMessages() {
        return messages;
    }

    /**
     * @return The list of {@link Permission} restricting access to this private chat room.
     */
    public RealmList<Permission> getACL() {
        return permissions;
    }

    public void setName(String name) {
        this.name = name;
    }
}

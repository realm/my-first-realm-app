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

    public RealmList<Permission> getACL() {
        return permissions;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package io.realm.chat.model;

import io.realm.RealmList;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
import io.realm.annotations.Required;

@RealmClass
public class PublicChatRoom implements ChatRoom {
    @Required
    @PrimaryKey
    private String name;
    private RealmList<Message> messages;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public RealmList<Message> getMessages() {
        return messages;
    }

    public void setName(String name) {
        this.name = name;
    }
}

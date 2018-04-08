package io.realm.todo.model;

import io.realm.RealmList;
import io.realm.RealmModel;

public interface ChatRoom extends RealmModel{
    String getName();
    RealmList<Message> getMessages();
}

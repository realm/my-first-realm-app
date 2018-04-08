package io.realm.chat.model;

import io.realm.RealmList;
import io.realm.RealmModel;

public interface ChatRoom extends RealmModel{
    String getName();
    RealmList<Message> getMessages();
}

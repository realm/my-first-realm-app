package io.realm.chat.model;


import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Message extends RealmObject {
    @Required
    private String body;
    @Required
    private String author;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}

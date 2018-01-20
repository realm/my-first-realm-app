package io.realm.todo;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;


public class Item extends RealmObject {
    @PrimaryKey
    private String itemId;
    @Required
    private String body;
    @Required
    private Boolean isDone;
    @Required
    private Date timestamp;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(Boolean done) {
        isDone = done;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Item() {
        this.itemId = UUID.randomUUID().toString();
        this.body = "";
        this.isDone = false;
        this.timestamp = new Date();
    }
}

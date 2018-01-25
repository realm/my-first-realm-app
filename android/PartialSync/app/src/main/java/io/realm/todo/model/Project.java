package io.realm.todo.model;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Project extends RealmObject {
    @PrimaryKey
    @Required
    private String id;

    @Required
    private String owner;

    @Required
    private String name;

    @Required
    private Date timestamp;

    private RealmList<Item> items;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public RealmList<Item> getTasks() {
        return items;
    }

    public void setTasks(RealmList<Item> items) {
        this.items = items;
    }
}

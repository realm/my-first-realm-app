package io.realm.todo;

import android.app.Application;

import io.realm.Realm;

public class ToDoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}

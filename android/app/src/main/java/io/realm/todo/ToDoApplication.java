package io.realm.todo;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by maximilianalexander on 1/17/18.
 */

public class ToDoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}

package io.realm.todo;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.sync.permissions.ClassPermissions;
import io.realm.sync.permissions.Permission;
import io.realm.sync.permissions.RealmPermissions;

public class PermissionHelper {

    /**
     * Configure the permissions on the Realm and each model class.
     * This will only succeed the first time that this code is executed. Subsequent attempts
     * will silently fail due to `canSetPermissions` having already been removed.
     *
     * @param postInitialization block to run after the Role has been added or found, usually a
     *                           navigation to the next screen.
     */
    public static void initializePermissions(Runnable postInitialization) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmPermissions> realmPermissions = realm.where(RealmPermissions.class).findAllAsync();
        Permission realmPermission = realmPermissions.first().getPermissions().first();
        if (realmPermission.canModifySchema()) {// schema is not yet locked

            // Temporary workaround: wait until the permission system is synchronized before applying changes.
            realmPermissions.addChangeListener((permissions, changeSet) -> {
                switch (changeSet.getState()) {
                    case UPDATE: {
                        realmPermissions.removeAllChangeListeners();
                        // setup and lock the schema
                        realm.executeTransactionAsync(bgRealm -> {
                            // Lower "everyone" Role on Item & Project to restrict permission modifications
                            Permission itemPermission = bgRealm.where(ClassPermissions.class).equalTo("name", "Item").findFirst().getPermissions().first();
                            Permission projectPermission = bgRealm.where(ClassPermissions.class).equalTo("name", "Project").findFirst().getPermissions().first();
                            itemPermission.setCanQuery(false);
                            itemPermission.setCanSetPermissions(false);
                            projectPermission.setCanSetPermissions(false);

                            // Lock the permission and schema
                            RealmPermissions permission = bgRealm.where(RealmPermissions.class).equalTo("id", 0).findFirst();
                            Permission everyonePermission = permission.getPermissions().first();
                            everyonePermission.setCanModifySchema(false);
                            everyonePermission.setCanSetPermissions(false);
                        }, () -> {
                            realm.close();
                            postInitialization.run();
                        });
                    }
                }
            });
        } else {
            realm.close();
            postInitialization.run();
        }
    }
}

package io.realm.todo;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.SyncUser;
import io.realm.sync.permissions.ClassPermissions;
import io.realm.sync.permissions.Permission;
import io.realm.sync.permissions.RealmPermissions;
import io.realm.sync.permissions.Role;

public class PermissionHelper {

    /**
     * Called after a given user is logged in.
     * This will ensure the permissions system is setup before starting using the app.
     * @param user current logged in SyncUser.
     * @param postInitialization block to run after the Role has been added or found, usually a
     *                           navigation to the next screen.
     */
    public static void initializePermissions(SyncUser user, Runnable postInitialization) {
        ensureRoleExists(user, postInitialization);// How to close the Realm

        // FIXME workaround until this is fixed in Sync
        //       wait for the permission system to be synchronized before applying changes
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmPermissions> allAsync = realm.where(RealmPermissions.class).findAllAsync();
        allAsync.addChangeListener((realmPermissions, changeSet) -> {
            switch (changeSet.getState()) {
                case UPDATE: {
                    allAsync.removeAllChangeListeners();
                    initializeRealmPermissions(realm);
                }
            }
        });
    }

    /**
     * Configure the permissions on the Realm and each model class.
     * This will only succeed the first time that this code is executed. Subsequent attempts
     * will silently fail due to `canSetPermissions` having already been removed.
     * @param realm partially synchronized Realm.
     */
    private static void initializeRealmPermissions(Realm realm) {
        // setup and lock the schema
        realm.executeTransactionAsync(bgRealm -> {
            // Lower "everyone" Role on Item & Project to restrict permission modifications
            Permission itemPermission = bgRealm.where(ClassPermissions.class).equalTo("name", "Item").findFirst().getPermissions().first();
            Permission projectPermission = bgRealm.where(ClassPermissions.class).equalTo("name", "Project").findFirst().getPermissions().first();
            itemPermission.setCanSetPermissions(false);
            projectPermission.setCanSetPermissions(false);

            // Lock the permission and schema
            RealmPermissions permission = bgRealm.where(RealmPermissions.class).equalTo("id", 0).findFirst();
            Permission everyonePermission = permission.getPermissions().first();
            everyonePermission.setCanModifySchema(false);
            everyonePermission.setCanSetPermissions(false);
        }, realm::close);
    }

    /**
     * Ensure there's a role named for the user.
     * @param user current logged in SyncUser.
     * @param postInitialization block to run after the Role has been added or found, usually a
     *                           navigation to the next screen.
     */
    private static void ensureRoleExists(SyncUser user, Runnable postInitialization) {
        Realm realm = Realm.getDefaultInstance();
        // Create a role for this user if it doesn't exist yet
        String roleId = "role_" + user.getIdentity();
        Role role = realm.where(Role.class).equalTo("name", roleId).findFirst();
        if (role == null) {
            realm.executeTransactionAsync(bgRealm -> {
                // Create a Role specific to this user
                Role userRole = bgRealm.createObject(Role.class, roleId);
                // add current user to it
                userRole.addMember(user.getIdentity());
            }, () -> {
                realm.close();
                postInitialization.run();
            });

        } else {
            realm.close();
            postInitialization.run();
        }
    }
}

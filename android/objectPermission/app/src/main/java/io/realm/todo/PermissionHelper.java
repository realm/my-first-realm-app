package io.realm.todo;

import android.os.Handler;
import android.os.Looper;

import io.realm.Realm;
import io.realm.SyncUser;
import io.realm.sync.permissions.ClassPermissions;
import io.realm.sync.permissions.Permission;
import io.realm.sync.permissions.RealmPermissions;
import io.realm.sync.permissions.Role;

public class PermissionHelper {

    // create user specific Role, used to enforce ACL
    // if it doesn't already exists
    // should be called from UI thread
    public static void createUserSpecificRoleIfNotExist(SyncUser user, String roleId, Runnable postStep) {
        Realm realm = Realm.getDefaultInstance();
        Role role = realm.where(Role.class).equalTo("name", roleId).findFirst();
        if (role == null) {
            realm.executeTransactionAsync(bgRealm -> {
                // Create a Role specific to this user
                Role userRole = bgRealm.createObject(Role.class, roleId);
                // add current user to it
                userRole.addMember(user.getIdentity());
            }, () -> {
                realm.close();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(postStep);
            });
        } else {
            realm.close();
            postStep.run();
        }
    }

    // Upload schema, and define permission for this Realm
    // should be called from UI thread
    public static void createRealmAndLockSchema(SyncUser adminUser, Runnable postStep) {
        Realm realm = Realm.getDefaultInstance();

        // if the Role admin is not present this means that the schema needs to be initialised & locked
        Role adminRole = realm.where(Role.class).equalTo("name", "admin").findFirst();
        if (adminRole == null) {
            // setup and lock the schema
            realm.executeTransactionAsync(bgRealm -> {
                Role admin = bgRealm.createObject(Role.class, "admin");
                admin.addMember(adminUser.getIdentity());
                Role everyone = bgRealm.where(Role.class).equalTo("name", "everyone").findFirst();

                // only admin can administer the Realm now, (not the everyone user)
                RealmPermissions permission = bgRealm.where(RealmPermissions.class).equalTo("id", 0).findFirst();

                // Lower the everyone permission
                Permission everyonePermission = new Permission(everyone);
                everyonePermission.setCanRead(true);
                everyonePermission.setCanUpdate(true);
                everyonePermission.setCanModifySchema(false);
                everyonePermission.setCanSetPermissions(true);// FIXME CHECK IF THIS IS NEEDED?
                everyonePermission.setCanCreate(false);//N/A
                everyonePermission.setCanDelete(false);//N/A
                everyonePermission.setCanQuery(false);//N/A

                // Admin can do everything
                Permission adminPermission = new Permission(admin);
                adminPermission.setCanCreate(true);
                adminPermission.setCanDelete(true);
                adminPermission.setCanModifySchema(true);
                adminPermission.setCanQuery(true);
                adminPermission.setCanRead(true);
                adminPermission.setCanSetPermissions(true);
                adminPermission.setCanUpdate(true);

                permission.getPermissions().deleteAllFromRealm();
                permission.getPermissions().add(everyonePermission);
                permission.getPermissions().add(adminPermission);

                // Lower "everyone" Role on Item & Project to restrict schema/permission modifications
                ClassPermissions itemPermission = bgRealm.where(ClassPermissions.class).equalTo("name", "Item").findFirst();
                ClassPermissions projectPermission = bgRealm.where(ClassPermissions.class).equalTo("name", "Project").findFirst();

                Permission userModelPrivileges = new Permission(everyone);
                userModelPrivileges.setCanRead(true);
                userModelPrivileges.setCanQuery(true);
                userModelPrivileges.setCanUpdate(true);
                userModelPrivileges.setCanCreate(true);
                userModelPrivileges.setCanDelete(true);
                userModelPrivileges.setCanSetPermissions(false);
                userModelPrivileges.setCanModifySchema(false);

                itemPermission.getPermissions().deleteAllFromRealm();
                projectPermission.getPermissions().deleteAllFromRealm();
                itemPermission.getPermissions().add(userModelPrivileges);
                projectPermission.getPermissions().add(userModelPrivileges);
            }, () -> {
                realm.close();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(postStep);
            });
        } else {
            realm.close();
            postStep.run();
        }
    }
}

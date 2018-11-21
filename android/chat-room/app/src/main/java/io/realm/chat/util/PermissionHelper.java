package io.realm.chat.util;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.chat.model.PrivateChatRoom;
import io.realm.chat.ui.GrantedPermission;
import io.realm.sync.permissions.ClassPermissions;
import io.realm.sync.permissions.Permission;
import io.realm.sync.permissions.PermissionUser;
import io.realm.sync.permissions.RealmPermissions;
import io.realm.sync.permissions.Role;

/**
 * A helper class defining permission related actions.
 */
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
        RealmPermissions realmPermission = realm.where(RealmPermissions.class).findFirst();
        if (realmPermission == null || realmPermission.getPermissions().first().canModifySchema()) {
            // Permission schema is not yet locked
            // Temporary workaround: register an async query to wait until the permission system is synchronized before applying changes.
            RealmResults<RealmPermissions> realmPermissions = realm.where(RealmPermissions.class).findAllAsync();
            realmPermissions.addChangeListener((permissions, changeSet) -> {
                if (changeSet.isCompleteResult()) {
                    realmPermissions.removeAllChangeListeners();
                    // setup and lock the schema
                    realm.executeTransactionAsync(bgRealm -> {
                        // Remove update permissions from the __Role table to prevent a malicious user
                        // from adding themselves to another user's private role.
                        Permission rolePermission = bgRealm.where(ClassPermissions.class).equalTo("name", "__Role").findFirst().getPermissions().first();
                        rolePermission.setCanUpdate(false);
                        rolePermission.setCanCreate(false);

                        // Lower "everyone" Role on Message & PrivateChatRoom and PublicChatRoom to restrict permission modifications
                        Permission messagePermission = bgRealm.where(ClassPermissions.class).equalTo("name", "Message").findFirst().getPermissions().first();
                        Permission publicChatPermission = bgRealm.where(ClassPermissions.class).equalTo("name", "PrivateChatRoom").findFirst().getPermissions().first();
                        Permission privateChatPermission = bgRealm.where(ClassPermissions.class).equalTo("name", "PublicChatRoom").findFirst().getPermissions().first();
                        messagePermission.setCanQuery(false); // Message are not queryable since they're accessed via RealmList (from PublicChatRoom or PrivateChatRoom)
                        messagePermission.setCanSetPermissions(false);
                        publicChatPermission.setCanSetPermissions(false);
                        privateChatPermission.setCanSetPermissions(false);

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
            });
        } else {
            realm.close();
            postInitialization.run();
        }
    }

    /**
     * Defines a set of permissions for the selected {@link PermissionUser}.
     *
     * This will grant read and/or modify privileges to the private role associated with the selected {@link PermissionUser}.
     * @param realm synced Realm.
     * @param grantedPermissions list of user-id to grant read/write privileges to.
     * @param chatRoom the {@link PrivateChatRoom} to add permission(s) to.
     */
    public static void updateGrantedPermissions(Realm realm, List<GrantedPermission> grantedPermissions, String chatRoom) {
        PrivateChatRoom privateChatRoom = realm.where(PrivateChatRoom.class).equalTo("name", chatRoom).findFirst();

        // update permission which are already part of the ACL
        // create new ones if not
        for (GrantedPermission grantedPermission : grantedPermissions) {
            boolean addToAcl = false;
            // create a permission using the built-in role of the user
            Role userRole = realm.where(PermissionUser.class).equalTo("id", grantedPermission.userId).findFirst().getPrivateRole();
            Permission userPermission = privateChatRoom.getACL().where().equalTo("role.name", userRole.getName()).findFirst();
            if (userPermission == null) {
                // create a permission for this user
                userPermission = new Permission(userRole);
                addToAcl = true;
            }

            // update/set permission
            userPermission.setCanRead(grantedPermission.canRead);
            userPermission.setCanQuery(grantedPermission.canRead);
            userPermission.setCanCreate(grantedPermission.canWrite);
            userPermission.setCanUpdate(grantedPermission.canWrite);
            userPermission.setCanDelete(grantedPermission.canWrite);
            userPermission.setCanSetPermissions(false);
            userPermission.setCanModifySchema(false);

            if (addToAcl) {
                privateChatRoom.getACL().add(userPermission);// should only add if created
            }
        }
    }

    /**
     * Update a set of permissions, previously granted via {@link #updateGrantedPermissions(Realm, List, String)}.
     *
     * This will grant or revoke a read/modify privileges from the permission granted to the user-id's {@link Role}.
     * @param realm synced Realm.
     * @param grantedPermissions list of user-id to grant read/write privileges to.
     * @param chatRoom the {@link PrivateChatRoom} that will update permission(s) to.
     * @param identity user identity of the current user.
     */
    public static void grantPermissions(Realm realm, List<GrantedPermission> grantedPermissions, String chatRoom, String identity) {
        PrivateChatRoom privateChatRoom = realm.createObject(PrivateChatRoom.class, chatRoom);

        for (GrantedPermission grantedPermission : grantedPermissions) {
            // create a permission using the built-in role of the user
            Role userRole = realm.where(PermissionUser.class).equalTo("id", grantedPermission.userId).findFirst().getPrivateRole();

            Permission userPermission = new Permission.Builder(userRole).noPrivileges().build();
            userPermission.setCanRead(grantedPermission.canRead);
            userPermission.setCanQuery(grantedPermission.canRead);
            userPermission.setCanCreate(grantedPermission.canWrite);
            userPermission.setCanUpdate(grantedPermission.canWrite);

            privateChatRoom.getACL().add(userPermission);
        }

        Role privateRole = realm.where(PermissionUser.class).equalTo("id", identity).findFirst().getPrivateRole();
        Permission adminPermission = new Permission.Builder(privateRole).allPrivileges().build();
        privateChatRoom.getACL().add(adminPermission);
    }
}

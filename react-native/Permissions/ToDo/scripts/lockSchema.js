const Realm = require("realm");

const MY_INSTANCE_ADDRESS = "YOUR-INSTANCE-ID.cloud.realm.i"; // <- update this

const SERVER_URL = `https://${MY_INSTANCE_ADDRESS}`;

const username = 'ADMIN_USER'; // <- update this
const password = 'ADMIN_PASS'; // <- update this

const Project = {
  name: "Project",
  primaryKey: "projectId",
  properties: {
    projectId: "string",
    owner: "string",
    name: "string",
    timestamp: "date",
    items: "Item[]",
    permissions: "__Permission[]"
  }
};

const Item = {
  name: "Item",
  primaryKey: "itemId",
  properties: {
    itemId: "string",
    body: "string",
    isDone: "bool",
    timestamp: "date"
  }
};

// Login an admin user
function lockSchema() {
  Realm.Sync.User.login(SERVER_URL, username, password)
    .then(user => {
      // Create a configuration to open the default Realm
      const config = user.createConfiguration({
        schema: [Project, Item]
      })
      // Open Realm
      Realm.open(config)
        .then(realm => {
          realm.write(() => {
            // Lower __Role permissions
            const rolePermission = realm.objects("__Class").filtered("name == '__Role'")[0].permissions[0];
            rolePermission.canUpdate = false;
            rolePermission.canCreate = false;

            // Lower Project permissions
            const projectPermissions = realm.objects("__Class").filtered("name == 'Project'")[0].permissions[0];
            projectPermissions.canSetPermissions = false;

            // Lower Item permissions
            const itemPermissions = realm.objects("__Class").filtered("name == 'Item'")[0].permissions[0];
            itemPermissions.canSetPermissions = false;
            itemPermissions.canQuery = false;

            // console.log(realm.objects("__Class"))
            console.log(Realm.Permissions.Realm.schema)

            // Lock the permissions and schema
            const everyonePermission = realm.objects("__Realm")[0].permissions[0]
            everyonePermission.canModifySchema = false;
            everyonePermission.canSetPermissions = false;
          })
        })
    })
    .catch(error => {
      console.log(error)
    })
}


lockSchema();

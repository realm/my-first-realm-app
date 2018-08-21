const assert = require("assert");
const program = require("commander");
const fs = require("fs-extra");
const { resolve } = require("path");
const Realm = require("realm");
const uuid = require("uuid/v4");

program
  .option("-u, --url <url>", "Realm Object Server URL, including https://")
  .option("-n, --nickname <nickname>", "The nickname to use when authenticating")
  .option("-s, --schema <simple|advanced>", "Which schema should be used? 'simple' (only the Task model) or 'advanced' (both the Task and Project model)")
  .parse(process.argv);

async function run(url, nickname, schemaName) {
  assert(url, "You must specify a server URL");
  // Remove any trailing slashes from the URL
  url = url.replace(/\/+$/, "");
  // The URL should start with either http or https
  assert(url.indexOf("http") === 0, "Expected the URL to start with 'http'");
  // Expect a nickname
  assert(nickname, "You must specify a nickname");
  // Expect a schema
  assert(schemaName, "You must specify a schema");
  // Check that the schema exists
  const schemaPath = resolve(__dirname, `${schemaName}-schema.json`);
  assert(fs.existsSync(schemaPath), `Cannot find schema: ${schemaPath}`);

  // Read the schema from disk
  const schema = require(schemaPath);
  // Open up a Realm
  // const syncUrl = url.replace(/^http/, "realm") + "/";
  const user = await Realm.Sync.User.registerWithProvider(url, {
    provider: "nickname",
    providerToken: nickname,
  });

  /*
  // TODO: Use this way of creating a configuration, once the ROS GraphQL Service can open query-based realms.
  // @see https://github.com/realm/realm-graphql-service/issues/70
  const config = user.createConfiguration();
  */

  // Create a configuration
  const config = {
    schema,
    sync: {
      user,
      url: url.replace(/^http/, "realm") + "/~/todo",
      fullSynchronization: true,
    }
  };
  // Open the Realm
  const realm = new Realm(config);

  function createItem(body) {
    realm.create("Item", {
      itemId: uuid(),
      isDone: false,
      timestamp: new Date(),
      body,
    });
  }

  // Add some test data
  if (schemaName === "simple") {
    realm.write(() => {
      createItem("Buy milk");
      createItem("Buy cookies");
      createItem("Invite Santa");
    });
  }
  // Wait for the upload of the schema
  await waitForUpload(realm);
  // Close the realm
  realm.close();
}

function cleanUp() {
  const rosPath = resolve(__dirname, "../realm-object-server");
  if (fs.existsSync(rosPath)) {
    console.log(`Deleting ${rosPath}`);
    fs.removeSync(rosPath);
  }
}

function waitForUpload(realm) {
  let session = realm.syncSession;
  return new Promise(resolve => {
    let callback = (transferred, total) => {
      // When everything has been uploaded, resolve the promise
      if (transferred === total) {
        session.removeProgressNotification(callback);
        resolve(realm);
      }
    };
    // Add a listener for progress
    session.addProgressNotification('upload', 'forCurrentlyOutstandingWork', callback);
  });
}

// Clean up after any previous failing runs
cleanUp();

run(program.url, program.nickname, program.schema)
.then(() => {
  cleanUp();
  process.exit(0);
}, err => {
  console.error(err.message);
  process.exit(1);
});

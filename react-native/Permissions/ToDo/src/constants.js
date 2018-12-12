// **** Realm Cloud Users:
// **** Replace MY_INSTANCE_ADDRESS with the hostname of your cloud instance
// **** e.g., "mycoolapp.us1.cloud.realm.io"
// ****
// ****
// **** ROS On-Premises Users
// **** Replace the AUTH_URL and REALM_URL strings with the fully qualified versions of
// **** address of your ROS server, e.g.: "http://127.0.0.1:9080" and "realm://127.0.0.1:9080"

const MY_INSTANCE_ADDRESS = "YOUR-INSTANCE-ID.cloud.realm.io"; // <- update this

export const SERVER_URL = `https://${MY_INSTANCE_ADDRESS}`;

// **** Realm Cloud Users:
// **** Replace MY_INSTANCE_ADDRESS with the hostname of your cloud instance
// **** e.g., "mycoolapp.us1.cloud.realm.io"
// ****
// ****
// **** ROS On-Premises Users
// **** Replace the AUTH_URL and REALM_URL strings with the fully qualified versions of
// **** address of your ROS server, e.g.: "http://127.0.0.1:9080" and "realm://127.0.0.1:9080"
    

export const MY_INSTANCE_ADDRESS = 'iostestapp.us1.cloud.realm.io'; // <- update this

export const AUTH_URL  = `https://${MY_INSTANCE_ADDRESS}`;
export const REALM_URL = `realms://${MY_INSTANCE_ADDRESS}/todo-react-native2`;
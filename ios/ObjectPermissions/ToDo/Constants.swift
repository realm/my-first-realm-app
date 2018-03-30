import Foundation

struct Constants {
    // **** Realm Cloud Users:
    // **** Replace MY_INSTANCE_ADDRESS with the hostname of your cloud instance
    // **** e.g., "mycoolapp.us1.cloud.realm.io"
    // ****
    // ****
    // **** ROS On-Premises Users
    // **** Replace the AUTH_URL string with the fully qualified address of your ROS server, e.g.:
    // **** "http://127.0.0.1:9080" and "realm://127.0.0.1:9080"
    
    static let MY_INSTANCE_ADDRESS = "MY_INSTANCE_ADDRESS" // <- update this

    static let AUTH_URL  = URL(string: "https://\(MY_INSTANCE_ADDRESS)")!
}

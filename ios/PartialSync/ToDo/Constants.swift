
import Foundation
struct Constants {
    
    
    // **** Realm Cloud Users:
    // **** Replace the "MY_INSTANCE_ADDRESS" here with the hostname of your
    // **** Realm Cloud instance or the URL of your on-premises Realm Object Server
    // ****
    // **** ROS On-Premises Users
    // **** Replace `https://MY_INSTANCE_ADDRESS ` with the address of your
    // **** your ROS server, e.g.: "http://127.0.0.1:9080"

    // ****
    // **** Replace the "MY_INSTANCE_ADDRESS" here with the hostname of your
    // **** Realm Cloud instance or the URL of your on-premises Realm Object Server
    // ****

    //static let AUTH_URL = URL(string: "https://MY_INSTANCE_ADDRESS")!
    static let AUTH_URL = URL(string: "https://todo.us1.cloud.realm.io")!

    static var REALM_URL: URL {
        let realmsUrlString = Constants.AUTH_URL.absoluteString.replacingOccurrences(of: "https://", with: "realms://")
        return URL(string: "\(realmsUrlString)/ToDo-PS")!
    }
    
}

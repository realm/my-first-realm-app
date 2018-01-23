
import Foundation
struct Constants {
    // ****
    // **** Replace the "MY_INSTANCE_ADDRESS" here with the hostname of your
    // **** Realm Cloud instance or the URL of your on-premises Realm Object Server
    // ****

    static let AUTH_URL = URL(string: "https://MY_INSTANCE_ADDRESS")!
    
    static var REALM_URL: URL {
        let realmsUrlString = Constants.AUTH_URL.absoluteString.replacingOccurrences(of: "https://", with: "realms://")
        return URL(string: realmsUrlString)!
    }
    
}

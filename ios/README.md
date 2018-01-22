# Realm Cloud ToDo List - iOS

This is to complement the ToDo List tutorial located here:
https://docs.realm.io/cloud/ios-todo-app

# Requirements:

1. XCode 9.2 
2. Cocoapods 1.2.x or higher

# Setup

1. Run `pod install --repo-update`
2. Open `ToDo.xcworkspace` and a `Constants.swift` file.
3. Edit `Constants.swift` to look like this:

```swift
import Foundation
struct Constants {
    
    static let AUTH_URL = URL(string: "https://MY_INSTANCE_ADDRESS")!
    
    static var REALM_URL: URL {
        let realmsUrlString = Constants.AUTH_URL.absoluteString.replacingOccurrences(of: "https://", with: "realms://")
        return URL(string: realmsUrlString)!
    }
    
}
```

4. Replace the `MY_INSTANCE_ADDRESS` with your Cloud Instance Address. 
5. Run the project!

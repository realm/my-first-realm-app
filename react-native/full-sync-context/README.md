# React Native ToDo app

This app uses Realm JS to implement a simple todo list.

## Installing dependencies

First, install all NPM dependencies

    npm install

Secondly, install all CocoaPod dependencies

    cd ios
    pod install

## Start the app

Start the app in an iOS simulator

    npm run ios

Or an Android emulator

    npm run android

## How this app was created

### Initializing a new React Native app

    npx react-native init --directory ./full-sync ToDo

### Installed Realm JS

Following https://realm.io/docs/javascript/latest/#getting-started

    npm install realm

### Installed React Navigation

Following https://reactnavigation.org/docs/en/getting-started.html

    npm install @react-navigation/native
    npm install react-native-reanimated react-native-gesture-handler react-native-screens react-native-safe-area-context @react-native-community/masked-view

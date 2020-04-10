import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createStackNavigator} from '@react-navigation/stack';
import Realm from 'realm';

import {LoginScreen} from './LoginScreen';
import {ToDoRealmProvider} from './ToDoRealmProvider';
import {ItemsScreen} from './ItemsScreen';
import {HeaderButton} from './HeaderButton';

const Stack = createStackNavigator();

const ItemsScreenWrapper = props => (
  <ToDoRealmProvider>
    {({realm}) => <ItemsScreen {...props} realm={realm} />}
  </ToDoRealmProvider>
);

export const App = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator
        initialRouteName={Realm.Sync.User.current ? 'Items' : 'Login'}>
        <Stack.Screen name="Login" component={LoginScreen} />
        <Stack.Screen
          name="Items"
          component={ItemsScreenWrapper}
          options={({navigation}) => ({
            headerTitle: 'Things ToDo!',
            headerRight: () => (
              <HeaderButton
                title="Logout"
                onPress={() => {
                  if (Realm.Sync.User.current) {
                    Realm.Sync.User.current.logout();
                  }
                  navigation.replace('Login');
                }}
              />
            ),
          })}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

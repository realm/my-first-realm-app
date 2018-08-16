/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
import { Scene, Router } from 'react-native-router-flux';
import LoginForm from './src/components/loginForm';
import TasksList from './src/components/tasks';

export default class App extends Component {
  render() {
    return (
        <Router>
          <Scene hideNavBar key="root">
            <Scene key="auth">
              <Scene key="login" component={LoginForm} title="Please Login"/>
            </Scene>

            <Scene key="main">
              <Scene 
                key="tasks" 
                component={TasksList} 
                title="Tasks" />
            </Scene> 
          </Scene>
        </Router>
    );
  }
}

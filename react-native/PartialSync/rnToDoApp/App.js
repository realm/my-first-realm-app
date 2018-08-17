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
import ProjectList from './src/components/projectList';

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
                component={ProjectList} 
                title="Tasks" />
            </Scene> 
          </Scene>
        </Router>
    );
  }
}

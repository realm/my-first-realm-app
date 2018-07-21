/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View} from 'react-native';
import { Actions, Scene, Router } from 'react-native-router-flux';
import LoginForm from './components/loginForm';
import ProjectsList from './components/projects';

export default class App extends Component {

  render() {
    return (
      <Router>
        <Scene key="root">
          <Scene key="auth">
            <Scene key="login" component={LoginForm} title="Please Login"  titleStyle={{flex :1}}/>
          </Scene>

          <Scene key="main">
            <Scene 
              key="projects" 
              component={ProjectsList} 
              title="Projects" />
          </Scene> 
        </Scene>
      </Router>
    );
  }
}

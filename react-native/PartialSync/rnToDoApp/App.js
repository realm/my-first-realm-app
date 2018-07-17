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
  state = {
    username: '',
  }

  receiveUsername(input) {
    this.setState({ username: input})
  }

  render() {
    return (
      <Router>
        <Scene>
          <Scene key="auth">
            <Scene key="login" component={LoginForm} title="Please Login" />
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

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
  welcome: {
    fontSize: 20,
    textAlign: 'center',
    margin: 10,
  },
  instructions: {
    textAlign: 'center',
    color: '#333333',
    marginBottom: 5,
  },
});

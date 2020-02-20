import React, {Component, createRef} from 'react';
import {Button, StyleSheet, Text, View} from 'react-native';
import Realm from 'realm';

import {SERVER_URL} from '../constants';
import {TextInput} from './TextInput';

export class LoginScreen extends Component {
  state = {
    username: '',
    password: '',
    error: null,
  };

  passwordRef = createRef();

  render() {
    const {username, password, error} = this.state;
    return (
      <View style={styles.container}>
        <Text style={styles.message}>
          Please enter a username and password.
        </Text>
        <TextInput
          style={styles.input}
          placeholder="Username"
          autoCapitalize="none"
          value={username}
          onChangeText={this.setUsername}
          onSubmitEditing={this.focusPassword}
        />
        <TextInput
          style={styles.input}
          ref={this.passwordRef}
          placeholder="Password"
          onChangeText={this.setPassword}
          value={password}
          onSubmitEditing={this.onSignIn}
          secureTextEntry
        />
        <Button title="Sign In" onPress={this.onSignIn} />
        <Button title="Sign Up" onPress={this.onSignUp} />
        {error ? <Text style={styles.error}>{error.message}</Text> : null}
      </View>
    );
  }

  setUsername = username => {
    this.setState({username});
  };

  setPassword = password => {
    this.setState({password});
  };

  focusPassword = () => {
    if (this.passwordRef.current) {
      this.passwordRef.current.focus();
    }
  };

  authenticate = createUser => {
    // Obtain a credentials instance
    const credentials = Realm.Sync.Credentials.usernamePassword(
      this.state.username,
      this.state.password,
      createUser,
    );
    // Perform the login
    Realm.Sync.User.login(SERVER_URL, credentials).then(
      () => {
        this.props.navigation.replace('Items');
      },
      error => {
        this.setState({error});
      },
    );
  };

  onSignIn = () => {
    this.authenticate(false);
  };

  onSignUp = () => {
    this.authenticate(true);
  };
}

const styles = StyleSheet.create({
  container: {
    flexDirection: 'column',
    marginTop: 32,
    paddingHorizontal: 24,
  },
  message: {
    marginBottom: 5,
  },
  input: {
    marginVertical: 5,
  },
  error: {
    color: 'red',
  },
});

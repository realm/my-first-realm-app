import React from "react";
import { View, StyleSheet } from "react-native";
import { Actions } from "react-native-router-flux";
import Realm from "realm";

import { SERVER_URL } from "../constants";
import { Project, Item } from "../schemas";

import { Button } from "./Button";
import { ModalView } from "./ModalView";

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center"
  },
  buttons: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center"
  }
});

export class LoginForm extends React.Component {
  state = {};

  componentDidMount() {
    // Check if we're already authenticated
    if (Realm.Sync.User.current) {
      this.onAuthenticated(Realm.Sync.User.current);
    } else {
      this.setState({ isModalVisible: true });
    }
  }

  render() {
    // Show the modal if the user is not authenticated
    const isAuthenticated = !!Realm.Sync.User.current;

    return (
      <View style={styles.container}>
        <ModalView
          placeholder="Please Enter a Username"
          confirmLabel="Login"
          isModalVisible={!isAuthenticated}
          handleSubmit={this.handleSubmit}
          error={this.state.error}
        />
        {isAuthenticated && (
          <View style={styles.buttons}>
            <Button onPress={this.onLogout}>Logout</Button>
            <Button onPress={this.onOpenProjects}>Go to projects</Button>
          </View>
        )}
      </View>
    );
  }

  onAuthenticated(user) {
    // Create a configuration to open the default Realm
    const config = user.createConfiguration({
      schema: [Project, Item]
    });
    // Open the Realm
    const realm = new Realm(config);
    // Navigate to the main scene
    Actions.authenticated({ user, realm });
  }

  onLogout = () => {
    if (Realm.Sync.User.current) {
      Realm.Sync.User.current.logout();
      this.forceUpdate();
    }
  };

  onOpenProjects = () => {
    if (Realm.Sync.User.current) {
      this.onAuthenticated(Realm.Sync.User.current);
    }
  };

  handleSubmit = async nickname => {
    try {
      // Reset any previous errors that might have happened
      this.setState({ error: undefined });
      // Attempt to authenticate towards the server
      const creds = Realm.Sync.Credentials.nickname(nickname);
      const user = await Realm.Sync.User.login(SERVER_URL, creds);
      // Hide the modal
      this.setState({ isModalVisible: false });
      this.onAuthenticated(user);
    } catch (error) {
      this.setState({ isModalVisible: true, error });
    }
  };
}

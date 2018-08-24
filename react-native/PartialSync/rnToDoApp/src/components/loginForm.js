import React, { Component } from "react";
import { View, StyleSheet } from "react-native";
import { Actions } from "react-native-router-flux";
import Realm from "realm";

import { SERVER_URL } from "../constants";
import { Project, Item } from "../schemas";

import { Button } from "./Button";
import { ModalView } from "./ModalView";

export const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center"
  }
});

export class LoginForm extends Component {
  state = {
    isModalVisible: false
  };

  render() {
    const { isModalVisible } = this.state;

    return (
      <View style={styles.container}>
        <Button onPress={this.toggleModal}>Login</Button>
        <ModalView
          placeholder="Please Enter a Username"
          isModalVisible={isModalVisible}
          toggleModal={this.toggleModal}
          handleSubmit={this.handleSubmit}
          error={this.state.error}
        />
      </View>
    );
  }

  toggleModal = () => {
    this.setState({
      isModalVisible: !this.state.isModalVisible,
      // Reset the error when toggling the modal
      error: undefined
    });
  };

  handleSubmit = async nickname => {
    try {
      // Reset any previous errors that might have happened
      this.setState({ error: undefined });
      // Attempt to authenticate towards the server
      const user = await Realm.Sync.User.registerWithProvider(SERVER_URL, {
        provider: "nickname",
        providerToken: nickname
      });
      // Hide the modal
      this.setState({ isModalVisible: false });
      // Create a configuration to open the default Realm
      const config = user.createConfiguration({
        schema: [Project, Item]
      });
      // Open the Realm
      const realm = await Realm.open(config);
      // Navigate to the main scene
      Actions.authenticated({ user, realm });
    } catch (error) {
      this.setState({ isModalVisible: true, error });
    }
  };
}

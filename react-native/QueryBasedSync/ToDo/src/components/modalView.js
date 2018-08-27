import PropTypes from "prop-types";
import React, { Component } from "react";
import { View, Text, TextInput } from "react-native";
import Modal from "react-native-modal";
import { StyleSheet } from "react-native";

const white = "white";

const styles = StyleSheet.create({
  content: {
    flexDirection: "column",
    backgroundColor: white,
    padding: 16,
    justifyContent: "center",
    alignItems: "center",
    borderRadius: 4
  },
  input: {
    width: "100%",
    textAlign: "center"
  },
  buttons: {
    flexDirection: "row",
    marginTop: 10
  }
});

import { Button } from "./Button";

export class ModalView extends Component {
  static propTypes = {
    confirmLabel: PropTypes.string,
    error: PropTypes.object,
    handleSubmit: PropTypes.func,
    isModalVisible: PropTypes.bool,
    placeholder: PropTypes.string,
    toggleModal: PropTypes.func
  };

  state = {
    text: ""
  };

  componentDidUpdate(prevProps) {
    // Reset the text state when the modal becomes visible
    if (!prevProps.isModalVisible && this.props.isModalVisible) {
      this.setState({ text: "" });
    }
  }

  render() {
    const {
      confirmLabel,
      error,
      isModalVisible,
      placeholder,
      toggleModal
    } = this.props;

    return (
      <Modal isVisible={isModalVisible}>
        <View style={styles.content}>
          {error && <Text>{error.message}</Text>}
          <TextInput
            style={styles.input}
            autoFocus={true}
            placeholder={placeholder}
            onChangeText={this.onChangeText}
            value={this.state.text}
            onSubmitEditing={this.onConfirm}
          />
          <View style={styles.buttons}>
            <Button onPress={this.onConfirm}>
              {confirmLabel || "Confirm"}
            </Button>
            <Button onPress={toggleModal}>Cancel</Button>
          </View>
        </View>
      </Modal>
    );
  }

  onChangeText = text => {
    this.setState({ text });
  };

  onConfirm = () => {
    this.props.handleSubmit(this.state.text);
  };
}

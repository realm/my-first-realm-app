import PropTypes from "prop-types";
import React from "react";
import { StyleSheet } from "react-native";
import * as ReactNativeElements from "react-native-elements";

const styles = StyleSheet.create({
  container: {
    marginLeft: 6,
    marginRight: 6,
    marginTop: 4,
    marginBottom: 4
  }
});

export const Button = ({ onPress, children, style }) => (
  <ReactNativeElements.Button
    backgroundColor="lightblue"
    buttonStyle={style}
    containerViewStyle={styles.container}
    color="black"
    borderRadius={4}
    onPress={onPress}
    title={children}
  />
);

Button.propTypes = {
  onPress: PropTypes.func,
  children: PropTypes.string,
  style: PropTypes.object
};

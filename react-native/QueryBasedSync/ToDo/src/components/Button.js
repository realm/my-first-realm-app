import PropTypes from "prop-types";
import React from "react";
import * as ReactNativeElements from "react-native-elements";

export const Button = ({ onPress, children }) => (
  <ReactNativeElements.Button
    backgroundColor="lightblue"
    color="black"
    borderRadius={4}
    onPress={onPress}
    title={children}
  />
);

Button.propTypes = {
  onPress: PropTypes.func,
  children: PropTypes.string
};

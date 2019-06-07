import PropTypes from "prop-types";
import React from "react";
import { Animated, PanResponder } from "react-native";

export class SwipeDeleteable extends React.Component {
  static propTypes = {
    onDeletion: PropTypes.func.isRequired,
    onPress: PropTypes.func
  };

  _panResponder = PanResponder.create({
    onStartShouldSetPanResponder: () => true,
    onPanResponderMove: (e, gestureState) => {
      this._offset.setValue(gestureState.dx);
    },
    onPanResponderRelease: (e, gestureState) => {
      if (gestureState.dx === 0 && this.props.onPress) {
        // We consider it a press if the user didn't pan
        this.props.onPress();
      } else {
        // Delete the item if the gesture is released 30% to either side
        const isDeletion = Math.abs(gestureState.dx) > this._width / 3;
        // If the item is deleted, complete the swipe in the correct direction
        const toValue = isDeletion
          ? gestureState.dx > 0
            ? this._width
            : -this._width
          : 0;
        // Apply a sprint to the offset
        Animated.spring(this._offset, {
          toValue,
          speed: 48
        }).start();
        // Delete the item right away
        if (isDeletion) {
          this.props.onDeletion();
        }
      }
    }
  });

  _offset = new Animated.Value(0);

  render() {
    const { children } = this.props;
    return (
      <Animated.View
        style={{
          transform: [
            {
              translateX: this._offset
            }
          ]
        }}
        onLayout={this.onLayout}
        {...this._panResponder.panHandlers}
      >
        {children}
      </Animated.View>
    );
  }

  onLayout = event => {
    this._width = event.nativeEvent.layout.width;
  };
}

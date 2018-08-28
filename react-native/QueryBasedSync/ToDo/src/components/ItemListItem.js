import PropTypes from "prop-types";
import React, { Component } from "react";
import { Animated, PanResponder } from "react-native";
import { ListItem } from "react-native-elements";

const checkedIcon = {
  name: "check-box",
  color: "#555"
};

const uncheckedIcon = {
  name: "check-box-outline-blank",
  color: "#555"
};

export class ItemListItem extends Component {
  static propTypes = {
    onToggleDone: PropTypes.func.isRequired,
    onDeleted: PropTypes.func.isRequired,
    item: PropTypes.object.isRequired
  };

  _panResponder = PanResponder.create({
    onStartShouldSetPanResponder: () => true,
    onPanResponderMove: (e, gestureState) => {
      this._offset.setValue(gestureState.dx);
    },
    onPanResponderRelease: (e, gestureState) => {
      // Delete the item if the gesture is released 30% to either side
      const isDeleted = Math.abs(gestureState.dx) > this._width / 3;
      // If the item is deleted, complete the swipe in the correct direction
      const toValue = isDeleted
        ? gestureState.dx > 0
          ? this._width
          : -this._width
        : 0;
      // Apply a sprint to the offset
      Animated.spring(this._offset, {
        toValue,
        speed: 48
      }).start(() => {
        // Delete the item once the animation settles
        if (isDeleted) {
          this.props.onDeleted(this.props.item);
        }
      });
    }
  });

  _offset = new Animated.Value(0);

  render() {
    const { item } = this.props;
    return (
      <Animated.View
        style={{
          transform: [
            {
              translateX: this._offset
            }
          ]
        }}
        {...this._panResponder.panHandlers}
      >
        <ListItem
          title={item.body}
          rightIcon={item.isDone ? checkedIcon : uncheckedIcon}
          onPressRightIcon={this.onPressRightIcon}
          onLayout={this.onListItemLayout}
        />
      </Animated.View>
    );
  }

  onPressRightIcon = () => {
    const { item } = this.props;
    this.props.onToggleDone(item);
  };

  onListItemLayout = event => {
    this._width = event.nativeEvent.layout.width;
  };
}

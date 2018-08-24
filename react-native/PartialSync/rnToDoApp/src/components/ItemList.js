import PropTypes from "prop-types";
import React, { Component } from "react";
import { View, FlatList, Text, StyleSheet } from "react-native";
import { Actions } from "react-native-router-flux";
import { List, ListItem } from "react-native-elements";
import { v4 as uuid } from "uuid";

export const styles = StyleSheet.create({
  placeholder: {
    textAlign: "center",
    padding: 10
  }
});

const itemKeyExtractor = item => item.itemId;

import { ModalView } from "./ModalView";

export class ItemList extends Component {
  static propTypes = {
    user: PropTypes.object,
    realm: PropTypes.object,
    project: PropTypes.object
  };

  state = {
    dataVersion: 0,
    isModalVisible: false
  };

  componentDidMount() {
    const { project } = this.props;

    // Register an action to create an item
    Actions.refresh({
      title: `Items of "${project.name}"`,
      rightTitle: " Create",
      onRight: () => {
        this.toggleModal();
      }
    });

    // Get a result containing all items
    const items = project.items;

    // When the list of items change, React won't know about it because the Result object itself did not change.
    items.addListener(() => {
      // Bump a data version counter that we'll pass to components that should update when the items change.
      this.setState({ dataVersion: this.state.dataVersion + 1 });
    });

    // No need to create a subscription here:
    // We assume another subscription is created for all the users projects already.

    // Update the state with the items
    this.setState({ items });
  }

  componentWillUnmount() {
    const { items } = this.state;
    // Remove all listeners from the subscription
    if (this.subscription) {
      this.subscription.removeAllListeners();
    }
    // Remove all listeners from the items
    if (items) {
      items.removeAllListeners();
    }
  }

  render() {
    const { dataVersion, isModalVisible, items } = this.state;
    return (
      <View>
        {!items || items.length === 0 ? (
          <Text style={styles.placeholder}>Create your first item</Text>
        ) : (
          <List>
            <FlatList
              data={items}
              extraData={dataVersion}
              renderItem={this.renderItem}
              keyExtractor={itemKeyExtractor}
            />
          </List>
        )}
        <ModalView
          placeholder="Please enter a description"
          isModalVisible={isModalVisible}
          toggleModal={this.toggleModal}
          handleSubmit={this.onItemCreation}
        />
      </View>
    );
  }

  renderItem = ({ item }) => (
    <ListItem key={item.itemId} title={item.body} hideChevron={true} />
  );

  onSubscriptionChange = () => {
    // Realm.Sync.SubscriptionState.Complete
    // Realm.Sync.SubscriptionState.Error
  };

  toggleModal = () => {
    this.setState({ isModalVisible: !this.state.isModalVisible });
  };

  onItemCreation = body => {
    const { realm, project } = this.props;
    // Open a write transaction
    realm.write(() => {
      // Create a project
      const item = realm.create("Item", {
        itemId: uuid(),
        body,
        isDone: false,
        timestamp: new Date()
      });
      // Add the item to the project
      project.items.push(item);
    });
    // Reset the state
    this.setState({ isModalVisible: false });
  };
}

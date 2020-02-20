import React, {Component} from 'react';
import {Button, StyleSheet, FlatList, View} from 'react-native';
import Realm from 'realm';
import {v4 as uuid} from 'uuid';

import {schema} from './schema.js';
import {ItemView} from './ItemView';
import {AddItemModal} from './AddItemModal.js';

export class ItemsScreen extends Component {
  state = {changes: 0, items: null, addItemModalVisible: false};
  componentDidMount() {
    const user = Realm.Sync.User.current;
    // Create the configuration
    const config = user.createConfiguration({
      schema,
      sync: {
        url: '~/ToDo',
        fullSynchronization: true,
      },
    });
    // Return a Realm instance
    this.realm = new Realm(config);
    const items = this.realm.objects('Item').sorted('timestamp', true);
    // Add a listener, updating the components when the
    items.addListener(() => {
      this.setState({changes: this.state.changes + 1});
    });
    this.setState({items});

    this.props.navigation.setOptions({
      headerLeft: () => <Button onPress={this.showAddItemModal} title="＋" />,
    });
  }

  componentWillUnmount() {
    this.realm.close();
  }

  render() {
    const {changes, items} = this.state;
    return (
      <>
        <FlatList
          style={styles.container}
          renderItem={({item}) => (
            <ItemView
              item={item}
              onBodyChange={this.onItemBodyChange.bind(null, item)}
              onDoneToggle={this.onItemDoneToggle.bind(null, item)}
            />
          )}
          data={items}
          keyExtractor={item => item.itemId}
          extraData={changes}
        />
        <AddItemModal
          visible={this.state.addItemModalVisible}
          onCancel={this.hideAddItemModal}
          onSave={this.onAddItem}
        />
      </>
    );
  }

  onItemBodyChange = (item, body) => {
    this.realm.write(() => {
      item.body = body;
    });
    this.setState({
      // We need to manually trigger en update to the UI (due to https://github.com/realm/realm-js/issues/2655)
      changes: this.state.changes + 1,
    });
  };

  onItemDoneToggle = item => {
    this.realm.write(() => {
      item.isDone = !item.isDone;
    });
    this.setState({
      // We need to manually trigger en update to the UI (due to https://github.com/realm/realm-js/issues/2655)
      changes: this.state.changes + 1,
    });
  };

  showAddItemModal = () => {
    this.setState({addItemModalVisible: true});
  };

  hideAddItemModal = () => {
    this.setState({addItemModalVisible: false});
  };

  onAddItem = body => {
    this.realm.write(() => {
      this.realm.create('Item', {
        itemId: uuid().toUpperCase(),
        body,
        isDone: false,
        timestamp: new Date(),
      });
    });
    this.setState({
      addItemModalVisible: false,
      // We need to manually trigger en update to the UI (due to https://github.com/realm/realm-js/issues/2655)
      changes: this.state.changes + 1,
    });
  };
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
});

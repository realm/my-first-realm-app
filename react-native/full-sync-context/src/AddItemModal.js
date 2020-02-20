import React, {useState} from 'react';
import {Button, View, StyleSheet, Text} from 'react-native';
import Modal from 'react-native-modal';

import {TextInput} from './TextInput';

export const AddItemModal = ({visible, onCancel, onSave}) => {
  const [body, setBody] = useState('');
  function onSubmit() {
    onSave(body);
  }
  return (
    <Modal isVisible={visible} onBackButtonPress={onCancel}>
      <View style={styles.container}>
        <Text style={styles.title}>Add Item</Text>
        <TextInput
          style={styles.input}
          placeholder="New Item Text"
          value={body}
          onChangeText={setBody}
          onSubmitEditing={onSubmit}
        />
        <View style={styles.controls}>
          <Button title="Cancel" onPress={onCancel} />
          <Button title="Save" onPress={onSubmit} />
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: '#eee',
    borderRadius: 10,
  },
  title: {
    fontSize: 16,
    padding: 20,
    textAlign: 'center',
  },
  input: {
    marginHorizontal: 20,
  },
  controls: {
    flexDirection: 'row',
    justifyContent: 'center',
    padding: 10,
  },
});

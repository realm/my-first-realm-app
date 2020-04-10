import React from 'react';
import {Button, StyleSheet, View, TextInput} from 'react-native';

export const ItemView = ({item, onBodyChange, onDoneToggle}) => {
  return (
    <View style={styles.item}>
      <TextInput
        style={styles.input}
        value={item.body}
        onChangeText={onBodyChange}
      />
      <View style={styles.accessory}>
        <Button
          style={styles.button}
          title={item.isDone ? '✓' : ' '}
          onPress={onDoneToggle}
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  item: {
    flex: 1,
    flexDirection: 'row',
    borderBottomWidth: 1,
    borderBottomColor: '#ddd',
    marginLeft: 20,
    alignItems: 'center',
  },
  input: {
    paddingVertical: 12,
    fontSize: 16,
    flexGrow: 1,
  },
  accessory: {
    width: 50,
  },
});

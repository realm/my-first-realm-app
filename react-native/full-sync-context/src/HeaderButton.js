import React from 'react';
import {StyleSheet, Platform, Text} from 'react-native';

export const HeaderButton = ({title, onPress}) => (
  <Text onPress={onPress} style={styles.text}>
    {title}
  </Text>
);

const styles = StyleSheet.create({
  text: {
    marginHorizontal: 15,
    fontSize: 16,
    color: Platform.select({
      ios: '#007AFF',
    }),
  },
});

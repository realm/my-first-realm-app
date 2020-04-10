import React from 'react';
import {TextInput as RNTextInput} from 'react-native';

export const TextInput = props => (
  <RNTextInput
    {...props}
    style={{
      backgroundColor: 'white',
      borderWidth: 1,
      borderColor: '#ccc',
      padding: 10,
      borderRadius: 5,
      ...props.style,
    }}
  />
);

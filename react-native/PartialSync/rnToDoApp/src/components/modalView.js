import React, { Component } from 'react';
import { View, Text, TextInput, TouchableOpacity } from 'react-native';
import Modal from 'react-native-modal';

import { styles } from '../styles'


class ModalView extends Component {
    state = {
        text: '',
    }

    render() {
        const { isModalVisible, placeholder, toggleModal } = this.props;

        return(
            <Modal isVisible={isModalVisible}>
                <View style={styles.modalContent}>
                    <TextInput
                        placeholder={placeholder}
                        onChangeText={(text) => {
                            this.setState({ text })
                        }}
                        value={this.state.text}
                    />
                    <View style={styles.buttonGroup}>
                        <TouchableOpacity onPress={this.props.handleSubmit}>
                            <View style={styles.button}>
                                <Text>
                                    Confirm
                                </Text>
                            </View>
                        </TouchableOpacity>
                        <TouchableOpacity onPress={toggleModal}>
                            <View style={styles.button}>
                                <Text>Cancel</Text>
                            </View>
                        </TouchableOpacity>
                    </View>
                </View>
            </Modal>
        );
    }
}

export default ModalView;
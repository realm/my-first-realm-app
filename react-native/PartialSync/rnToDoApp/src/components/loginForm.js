import React, { Component } from 'react';
import { Text, TouchableOpacity, View, TextInput } from 'react-native';
import { Actions } from 'react-native-router-flux';
import Modal from 'react-native-modal';
import { styles } from '../styles';

import ModalView from './modalView';

class loginForm extends Component {
    state = {
        isModalVisible: false,
        username: '',
    };
    
    toggleModal = () => {
        this.setState({ isModalVisible: !this.state.isModalVisible });
    };

    handleSubmit = () => {
        this.toggleModal();
        Actions.main({ username: this.state.username});
    }

    render() {
        const { isModalVisible } = this.state

        return(
            <View style={styles.container}>
                <TouchableOpacity onPress={this.toggleModal}>
                    <View style={styles.button}>
                        <Text>Login</Text>
                    </View>
                </TouchableOpacity>
                <ModalView 
                    placeholder='Please Enter a Username'
                    isModalVisible={isModalVisible}
                    toggleModal={this.toggleModal}
                />
            </View>
        );
    }
}



export default loginForm;

{/* <Modal isVisible={this.state.isModalVisible}>
                    <View style={styles.modalContent}>
                        <TextInput
                            placeholder="Please Enter a Username"
                            onChangeText={(text) => this.setState({ username: text })}
                            value={this.state.username}
                        />
                        <View style={styles.buttonGroup}>
                            <TouchableOpacity onPress={this.handleSubmit}>
                                <View style={styles.button}>
                                    <Text>Confirm</Text>
                                </View>
                            </TouchableOpacity>
                            <TouchableOpacity onPress={this.toggleModal}>
                                <View style={styles.button}>
                                    <Text>Cancel</Text>
                                </View>
                            </TouchableOpacity>
                        </View>
                    </View>
                </Modal> */}
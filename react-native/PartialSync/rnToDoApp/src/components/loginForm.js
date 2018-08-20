import React, { Component } from 'react';
import { Text, TouchableOpacity, View, TextInput } from 'react-native';
import { Actions } from 'react-native-router-flux';
import { styles } from '../styles';

import ModalView from './modalView';

class loginForm extends Component {
    state = {
        isModalVisible: false,
    };
    
    toggleModal = () => {
        this.setState({ isModalVisible: !this.state.isModalVisible });
    };

    handleSubmit = (username) => {
        this.toggleModal();
        Actions.main({ username });
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
                    handleSubmit={this.handleSubmit.bind(this)}
                />
            </View>
        );
    }
}



export default loginForm;
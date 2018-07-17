import React, { Component } from 'react';
import { Text, TouchableOpacity, View, TextInput } from 'react-native';
import { Actions } from 'react-native-router-flux';
import Modal from "react-native-modal";
import { styles } from '../styles';

class loginForm extends Component {
    constructor(props) {
        super(props)
        this.state = {
            isModalVisible: false,
            username: '',
        };
    }
    
    toggleModal = () => {
        this.setState({ isModalVisible: !this.state.isModalVisible });
    };

    handleSubmit = () => {
        this.toggleModal();
        Actions.main({ username: this.state.username});
    }

    render() {
        return(
            <View style={styles.container}>
                <TouchableOpacity onPress={this.toggleModal}>
                    <View style={styles.button}>
                        <Text>Login</Text>
                    </View>
                </TouchableOpacity>
                <Modal isVisible={this.state.isModalVisible}>
                    <View style={styles.modalContent}>
                        <TextInput
                            placeholder="Please Enter a Username"
                            onChangeText={(text) => this.setState({ username: text })}
                            value={this.state.username}
                        />
                        <TouchableOpacity onPress={this.handleSubmit}>
                            <View style={styles.button}>
                                <Text>Confirm</Text>
                            </View>
                        </TouchableOpacity>
                    </View>
                </Modal>
            </View>
        );
    }
}



export default loginForm;
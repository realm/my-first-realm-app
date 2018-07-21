import React, { Component } from 'react';
import { View, Text, TextInput, TouchableOpacity } from 'react-native';
import Realm from 'realm';
import Modal from "react-native-modal";
import { AUTH_URL, REALM_URL } from '../constants';
import { constants } from '../constants';
import { styles } from '../styles'
import { Actions } from '../node_modules/react-native-router-flux';
import { projectSchema } from '../schemas';
 
class Projects extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isModalVisible: false,
            projectName: '',
            projects: null,
        }
    }

    componentWillMount() {
        Actions.refresh({
            rightTitle: "Create",
            onRight: () => { 
                this.toggleModal();
             }
        }); 

        Realm.Sync.User.login(AUTH_URL, this.props.username, 'password')
        .then((user) => {
            Realm.open({
                schema: [projectSchema],
                sync: {
                    user: user,
                    url: REALM_URL,
                }
            })
            .then((realm) => {
                let results = realm.objects('project');
                this.setState({ projects: results });
            })
        })
    }

    toggleModal = () => {
        this.setState({ isModalVisible: !this.state.isModalVisible });
    };

    handleSubmit() {
        Realm.Sync.User.login(AUTH_URL, this.props.username, 'password')
        .then((user) => {
            Realm.open({
                schema: [projectSchema],
                sync: {
                    user: user,
                    url: REALM_URL,
                }
            })
            .then((realm) => {
                let date = new Date().now().getTime()
                realm.write(() => {
                    realm.create('project', {
                        projectID: Math.random().toString(36).substr(2, 9),
                        owner: this.props.username,
                        name: this.state.projectName,
                        createdAt: date,
                    })
                })
            })
        })
        .catch(error => {
            console.log(error)
        })
        this.setState({ projectName: '' });
    }

    render() {
        console.log(this.state.projects)
        return(
            <View>
                <Text>
                    Projects Page
                    {this.props.username}
                </Text>
                <Modal isVisible={this.state.isModalVisible}>
                    <View style={styles.modalContent}>
                        <TextInput
                            placeholder="Please Enter a Project Name"
                            onChangeText={(text) => this.setState({ projectName: text })}
                            value={this.state.projectName}
                        />
                        <View style={styles.buttonGroup}>
                            <TouchableOpacity onPress={this.handleSubmit.bind(this)}>
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
                </Modal>
            </View>
        );
    }
}

export default Projects;
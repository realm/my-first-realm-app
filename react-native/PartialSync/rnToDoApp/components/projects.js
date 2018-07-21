import React, { Component } from 'react';
import { View, Text, TextInput, TouchableOpacity } from 'react-native';
import Realm from 'realm';
import Modal from "react-native-modal";
import { AUTH_URL, REALM_URL } from '../constants';
import { constants } from '../constants';
import { styles } from '../styles'
import { Actions } from '../node_modules/react-native-router-flux';
import { projectSchema } from '../schemas';
import { List, ListItem } from 'react-native-elements';
 
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
        console.log('hit submit');
        Realm.Sync.User.login(AUTH_URL, this.props.username, 'password')
        .then((user) => {
            console.log('hit open')
            Realm.open({
                schema: [projectSchema],
                sync: {
                    user: user,
                    url: REALM_URL,
                }
            })
            .then((realm) => {
                console.log('hit write')
                let date = Date.now()
                console.log(date)
                realm.write(() => {
                    realm.create('project', {
                        projectID: Math.random().toString(36).substr(2, 9),
                        owner: user.identity,
                        name: this.state.projectName,
                        createdAt: date,
                    })
                })
            })
        })
        // .catch(error => {
        //     console.log(error)
        // })
    }

    render() {
        return(
            <View>
                <Text>
                    Projects Page
                </Text>
                <Modal isVisible={this.state.isModalVisible}>
                    <View style={styles.modalContent}>
                        <TextInput
                            placeholder="Please Enter a Project Name"
                            onChangeText={(text) => {
                                this.setState({ projectName: text });
                                console.log(this)
                            }}
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
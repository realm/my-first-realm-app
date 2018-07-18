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
        }
    }

    componentWillMount() {
        Actions.refresh({
            rightTitle: "Create",
            onRight: () => { 
                this.toggleModal();
             }
        }); 

        Realm.Sync.User.registerWithProvider(AUTH_URL, { provider: 'nickname', name: this.props.username })
            .then((user) => {
                Realm.open({
                    schema: [projectSchema],
                    sync: {
                        user: user,
                        url: REALM_URL,
                        // partial: true,
                    }
                }).then((realm) => {
                    // realm.write(() => {
                    //     realm.create('project', {
                    //             projectID: '1',
                    //             owner: this.props.username,
                    //             name: 'task1',
                    //             createdAt: 'date',
                    //         }
                    //     )
                    // })
                    let results = realm.objects('project');
                    this.setState({ projects: results })
                })
            })
        .catch((error) => {
            // Realm.Sync.User.register(AUTH_URL, this.props.username, 'password')
            console.log(error)
        })
    }

    toggleModal = () => {
        this.setState({ isModalVisible: !this.state.isModalVisible });
    };

    handleSubmit() {
        // Realm.Sync.User.login(AUTH_URL, this.props.username, 'password')
        //     .then((user) => {
        //         Realm.open({
        //             schema: [projectSchema],
        //             sync: {
        //                 user: user,
        //                 url: REALM_URL,
        //                 partial: true,
        //             }
        //         })
        //     })
    }

    render() {
        return(
            <View>
                <Text>
                    Projects Page
                    {this.props.username}
                    {this.state.projects}
                </Text>
                <Modal isVisible={this.state.isModalVisible}>
                    <View style={styles.modalContent}>
                        <TextInput
                            placeholder="Please Enter a Project Name"
                            onChangeText={(text) => this.setState({ projectName: text })}
                            value={this.state.projectName}
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

export default Projects;
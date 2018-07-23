import React, { Component } from 'react';
import { View, Text, TextInput, TouchableOpacity, ListView } from 'react-native';
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
            dataSource: null,
            username: this.props.username,
        }
    }

    componentWillMount() {
        Actions.refresh({
            rightTitle: "Create",
            onRight: () => { 
                this.toggleModal();
             }
        });

        this.fetchProjects();
    }

    fetchProjects() {
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
                console.log('hit query')
                let results = realm.objects('project');
                this.createDataSource(results);
            })
        })
        .catch(error => {
            console.log(error)
        })
    }

    toggleModal = () => {
        this.setState({ isModalVisible: !this.state.isModalVisible });
    };

    createDataSource(projects) {
        const data = new ListView.DataSource({
          rowHasChanged: (r1, r2) => r1 !== r2
        });
    
        this.setState({ dataSource: data.cloneWithRows(projects) });
    }

    handleSubmit() {
        Realm.Sync.User.login(AUTH_URL, this.state.username, 'password')
        .then((user) => {
            Realm.open({
                schema: [projectSchema],
                sync: {
                    user: user,
                    url: REALM_URL,
                }
            })
            .then((realm) => {
                let date = Date.now()
                realm.write(() => {
                    realm.create('project', {
                        projectID: Math.random().toString(36).substr(2, 9),
                        owner: user.identity,
                        name: this.state.projectName,
                        createdAt: date,
                    })
                })
            })
            .then(() => {
                this.setState({ projectName: '' });
                this.toggleModal();

                this.fetchProjects();
            })
        })
        .catch(error => {
            console.log(error)
        })
    }

    deleteItem = () => {
        console.log('hit delete')
    }

    renderRow(data) {
        return(
            <TouchableOpacity onPress={this.deleteItem}>
                <ListItem
                    key={data.projectID}
                    title={data.name}
                    hideChevron
                />
            </TouchableOpacity>
        );
    }

    renderList() {
        if (this.state.dataSource) {
            return(
                <List>
                    <ListView 
                        // style={{flex: 1}}
                        // enableEmptySections
                        renderRow={this.renderRow.bind(this)}
                        dataSource={this.state.dataSource}
                    />
                </List>
            );
        }
        return(
            <Text>
                Loading
            </Text>
        );
    }

    render() {
        return(
            <View>
                {this.renderList()}
                <Modal isVisible={this.state.isModalVisible}>
                    <View style={styles.modalContent}>
                        <TextInput
                            placeholder="Please Enter a Project Name"
                            onChangeText={(text) => {
                                this.setState({ projectName: text });
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
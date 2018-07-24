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
            user: null,
        }
    }

    componentWillMount() {
        Actions.refresh({
            rightTitle: "Create",
            onRight: () => { 
                this.toggleModal();
             }
        });

        Realm.Sync.User.registerWithProvider(AUTH_URL, { provider: 'nickname', providerToken: this.props.username, userInfo: { is_admin: true }})
        .then((user) => {
            this.setState({ user: user });
        })
        .then(() => {
            this.fetchProjects(this.state.user);
        })
    }

    fetchProjects(user) {
        Realm.open({
            schema: [projectSchema],
            sync: {
                user: user,
                url: REALM_URL,
            }
        })
        .then((realm) => {
            let results = realm.objects('project');
            this.createDataSource(results);
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
        const { user } = this.state;
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
                console.log('hit write')
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

            this.fetchProjects(user);
        })
        .catch(error => {
            console.log(error)
        })
    }

    deleteItem = (id) => {
        console.log('hit delete')
        console.log(id)
        Realm.open({
            schema: [projectSchema],
            sync: {
                user: this.state.user,
                url: REALM_URL,
            }
        })
        .then(realm => {
            realm.write(() => {
              const projectToDelete = realm.objects('project').filtered(`projectID = "${id}"`);
              realm.delete(projectToDelete);
            })
        })
        .then(() => {
            this.fetchProjects(this.state.user);
        })
    }

    renderRow(data) {
        return(
            <TouchableOpacity onPress={() => { this.deleteItem(data.projectID) }}>
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
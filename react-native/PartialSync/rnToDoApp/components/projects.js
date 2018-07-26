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
            realm: null,
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
            Realm.open({
                schema: [projectSchema],
                sync: {
                    user: user,
                    url: REALM_URL,
                    partial: true,
                }
            })
            .then((realm) => {
                this.setState({ realm, user })
                this.fetchProjects(realm);
            })
        })
    }

    // componentWillReceiveProps(nextProps) {
    //     this.fetchProjects(nextProps.user);
    // }

    fetchProjects(realm) {
        // Realm.Sync.User.login(AUTH_URL, this.props.username, 'password')
        let results = realm.objects('project').filtered(`owner = "${this.state.user.identity}"`)
        let subscription = results.subscribe();
        results.addListener(() => {
            switch (subscription.state) {
            case Realm.Sync.SubscriptionState.Creating:
                break;
            case Realm.Sync.SubscriptionState.Complete:
                console.log('hit sub complete')
                let partialResults = realm.objects('project');
                this.createDataSource(partialResults);
                break;
            case Realm.Sync.SubscriptionState.Error:
                console.log('An error occurred: ', results.error);
                break;
            default:
                console.log(state)
                break;
            }
        })
        // .catch(() => {
        //     Realm.Sync.User.register(AUTH_URL, this.props.username, 'password')
        //     .then(() => {
        //         this.fetchProjects();
        //     })
        //     .catch(error => {
        //         console.log(error)
        //     })
        // })
    }

    // fetchProjects(user) {
    //     Realm.open({
    //         schema: [projectSchema],
    //         sync: {
    //             user: user,
    //             url: REALM_URL,
    //         }
    //     })
    //     .then((realm) => {
    //         let results = realm.objects('project');
    //         this.createDataSource(results);
    //     })
    //     .catch(error => {
    //         console.log(error)
    //     })
    // }

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
        const { user, realm } = this.state;
        realm.write(() => {
            realm.create('project', {
                projectID: Math.random().toString(36).substr(2, 9),
                owner: user.identity,
                name: this.state.projectName,
            })
        })
        this.setState({ projectName: '' });
        this.toggleModal();
    }

    deleteItem = (id) => {
        const { realm } = this.state;
        realm.write(() => {
            let projectToDelete = realm.objects('project').filtered(`projectID = "${id}"`);
            realm.delete(projectToDelete);
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
                        enableEmptySections
                        renderRow={this.renderRow.bind(this)}
                        dataSource={this.state.dataSource}
                    />
                </List>
            );
        }
        return(
            <View>
                <Text style={styles.text}>
                    Create a Task!
                </Text>
            </View>
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
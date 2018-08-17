import React, { Component } from 'react';
import { View, Text, TextInput, TouchableOpacity, ListView } from 'react-native';
import Realm from 'realm';
import Modal from 'react-native-modal';
import { AUTH_URL, REALM_URL } from '../constants';
import { styles } from '../styles'
import { Actions } from 'react-native-router-flux';
import { taskSchema } from '../schemas';
import { List, ListItem } from 'react-native-elements';

class ProjectList extends Component {
    constructor(props) {
        super(props);
        this.state = {
            isModalVisible: false,
            taskName: '',
            dataSource: null,
            user: null,
            realm: null,
        }
    }

    componentWillMount() {
        Actions.refresh({
            rightTitle: 'Create',
            onRight: () => { 
                this.toggleModal();
             }
        });

        Realm.Sync.User.registerWithProvider(AUTH_URL, { provider: 'nickname', providerToken: this.props.username, userInfo: { is_admin: true }})
        .then((user) => {
            Realm.open({
                schema: [taskSchema],
                sync: {
                    user: user,
                    url: REALM_URL,
                    partial: true,
                }
            })
            .then((realm) => {
                this.setState({ realm, user })
                this.fetchTasks(realm);
            })
        })
    }

    fetchTasks(realm) {
        let results = realm.objects('task').filtered(`owner = "${this.state.user.identity}"`)
        let subscription = results.subscribe();
        results.addListener(() => {
            switch (subscription.state) {
            case Realm.Sync.SubscriptionState.Creating:
                break;
            case Realm.Sync.SubscriptionState.Complete:
                console.log('hit sub complete')
                let partialResults = realm.objects('task');
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
    }
    toggleModal = () => {
        this.setState({ isModalVisible: !this.state.isModalVisible });
    };

    createDataSource(tasks) {
        const data = new ListView.DataSource({
          rowHasChanged: (r1, r2) => r1 !== r2
        });
    
        this.setState({ dataSource: data.cloneWithRows(tasks) });
    }

    handleSubmit() {
        const { user, realm } = this.state;
        realm.write(() => {
            realm.create('task', {
                taskID: Math.random().toString(36).substr(2, 9),
                owner: user.identity,
                name: this.state.taskName,
            })
        })
        this.setState({ taskName: '' });
        this.toggleModal();
    }

    deleteItem = (id) => {
        const { realm } = this.state;
        realm.write(() => {
            let taskToDelete = realm.objects('task').filtered(`taskID = "${id}"`);
            realm.delete(taskToDelete);
        })
    }

    renderRow(data) {
        return(
            <TouchableOpacity onPress={() => { this.deleteItem(data.taskID) }}>
                <ListItem
                    key={data.taskID}
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
                        enableEmptySections
                        renderRow={this.renderRow.bind(this)}
                        dataSource={this.state.dataSource}
                    />
                </List>
            );
        }
        return(
            <View>
                <Text>
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
                            placeholder="Please Enter a task Name"
                            onChangeText={(text) => {
                                this.setState({ taskName: text });
                            }}
                            value={this.state.taskName}
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


export default ProjectList;
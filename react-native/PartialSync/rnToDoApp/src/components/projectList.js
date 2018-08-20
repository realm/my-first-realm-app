import React, { Component } from 'react';
import { View, ListView } from 'react-native';
import Realm from 'realm';
import ModalView from './modalView';
import { AUTH_URL, REALM_URL } from '../constants';
import { Actions } from 'react-native-router-flux';
import { taskSchema } from '../schemas';

import PageView from './pageView';

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
            console.log('being login')
            // Application will not open realm in debugger
            Realm.open({
                schema: [taskSchema],
                sync: {
                    user: user,
                    url: REALM_URL,
                    partial: true,
                }
            })
            .then((realm) => {
                console.log('finish config')
                this.setState({ realm, user })
                this.fetchTasks(realm);
            })
        })
        .catch(e => {
            console.log(e)
        })
    }

    fetchTasks(realm) {
        let results = realm.objects('task').filtered(`owner = "${this.state.user.identity}"`)
        let subscription = results.subscribe();
        results.addListener(() => {
            switch (subscription.state) {
            case Realm.Sync.SubscriptionState.Complete:
                console.log('hit sub complete')
                console.log(subscription.state)
                let partialResults = realm.objects('task');
                this.createDataSource(partialResults);
                break;
            case Realm.Sync.SubscriptionState.Error:
                // console.log('An error occurred: ', results.error);
                break;
            default:
                console.log(subscription.state)
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

    handleSubmit(projectName) {
        const { user, realm } = this.state;
        realm.write(() => {
            realm.create('task', {
                taskID: Math.random().toString(36).substr(2, 9),
                owner: user.identity,
                name: projectName,
            })
        })
        this.setState({ taskName: '' });
        this.toggleModal();
    }

    render() {
        const { isModalVisible, dataSource } = this.state;

        return(
            <View>
                <PageView
                    dataSource={dataSource}
                    placeholder='Create a project!'
                />
                <ModalView 
                    placeholder='Please Enter a Project Name'
                    isModalVisible={isModalVisible}
                    toggleModal={this.toggleModal}
                    handleSubmit={this.handleSubmit.bind(this)}
                />
            </View>
        );
    }
}


export default ProjectList;
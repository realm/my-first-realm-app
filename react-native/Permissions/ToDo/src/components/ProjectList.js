import PropTypes from "prop-types";
import React, { Component } from "react";
import Realm from "realm";
import { View, FlatList, Text, StyleSheet } from "react-native";
import { Actions } from "react-native-router-flux";
import { List, ListItem } from "react-native-elements";
import { v4 as uuid } from "uuid";

const projectKeyExtractor = project => project.projectId;

const styles = StyleSheet.create({
  placeholder: {
    textAlign: "center",
    padding: 10
  }
});

import { ModalView } from "./ModalView";
import { SwipeDeleteable } from "./SwipeDeleteable";

export class ProjectList extends Component {
  static propTypes = {
    user: PropTypes.object,
    realm: PropTypes.object
  };

  state = {
    dataVersion: 0,
    isModalVisible: false
  };

  componentDidMount() {
    const { realm } = this.props;

    // Register an action to create a project
    Actions.refresh({
      rightTitle: " Create",
      onRight: () => {
        this.toggleModal();
      }
    });

    // Get a result containing all projects
    const projects = realm
      .objects("Project")
      .filtered("owner == $0", this.props.user.identity)
      .sorted("timestamp", true);

    // When the list of projects change, React won't know about it because the Result object itself did not change.
    projects.addListener(() => {
      // Bump a data version counter that we'll pass to components that should update when the projects change.
      this.setState({ dataVersion: this.state.dataVersion + 1 });
    });

    // Create a subscription and add a listener
    // Remember to remove the listener when component unmounts
    this.subscription = projects.subscribe();
    this.subscription.addListener(this.onSubscriptionChange);

    // Update the state with the projects
    this.setState({ projects });
  }

  componentWillUnmount() {
    const { projects } = this.state;
    if (this.subscription) {
      // Remove all listeners from the subscription
      this.subscription.removeAllListeners();
    }
    if (projects) {
      projects.removeAllListeners();
    }
  }

  render() {
    const { dataVersion, isModalVisible, projects } = this.state;
    return (
      <View>
        {!projects || projects.length === 0 ? (
          <Text style={styles.placeholder}>Create your first project</Text>
        ) : (
          <List>
            <FlatList
              data={projects}
              extraData={dataVersion}
              renderItem={this.renderProject}
              keyExtractor={projectKeyExtractor}
            />
          </List>
        )}
        <ModalView
          placeholder="Please Enter a Project Name"
          confirmLabel="Create Project"
          isModalVisible={isModalVisible}
          toggleModal={this.toggleModal}
          handleSubmit={this.onProjectCreation}
        />
      </View>
    );
  }

  renderProject = ({ item }) => (
    <SwipeDeleteable
      key={item.projectId}
      onPress={() => {
        this.onProjectPress(item);
      }}
      onDeletion={() => {
        this.onProjectDeletion(item);
      }}
    >
      <ListItem
        title={item.name}
        badge={{
          value: item.items.length
        }}
        hideChevron={true}
      />
    </SwipeDeleteable>
  );

  onSubscriptionChange = () => {
    // Realm.Sync.SubscriptionState.Complete
    // Realm.Sync.SubscriptionState.Error
  };

  toggleModal = () => {
    this.setState({ isModalVisible: !this.state.isModalVisible });
  };

  onProjectCreation = projectName => {
    const { user, realm } = this.props;
    // Open a write transaction
    realm.write(() => {
      // Create a project
      const project = realm.create("Project", {
        projectId: uuid(),
        owner: user.identity,
        name: projectName,
        timestamp: new Date()
      });

      const systemUser = realm.objects(Realm.Permissions.User.schema.name).filtered(`id = '${user.identity}'`)[0];

      // Create permission
      const permission = realm.create(Realm.Permissions.Permission, {
        role: systemUser.role,
        canRead: true,
        canUpdate: true,
        canDelete: true,
      })

      // Add permission to project
      project.permissions.push(permission);

    });
    // Reset the state
    this.setState({ isModalVisible: false });
  };

  onProjectPress = project => {
    const { user, realm } = this.props;
    Actions.items({ project, realm, user, title: project.name });
  };

  onProjectDeletion = project => {
    const { realm } = this.props;
    // Open a write transaction
    realm.write(() => {
      // Delete the project
      realm.delete(project);
    });
  };
}

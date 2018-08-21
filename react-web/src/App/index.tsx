import {
  ApolloClient,
  concat,
  HttpLink,
  InMemoryCache,
  split,
} from "apollo-client-preset";
import { WebSocketLink } from "apollo-link-ws";
import { getMainDefinition } from "apollo-utilities";
import * as React from "react";
import { Credentials, GraphQLConfig, User } from "realm-graphql-client";

import { SERVER_URL } from "../constants";

import { AuthenticatedScene } from "./AuthenticatedScene";
import { LoginScene } from "./LoginScene";

const REALM_PATH = "/~/todo";

interface IIdleState {
  status: "idle";
}

interface IAuthenticatedState {
  status: "authenticated";
  client: ApolloClient<any>;
}

type IAppContainerState = IIdleState | IAuthenticatedState;

class AppContainer extends React.Component<{}, IAppContainerState> {
  public state: IAppContainerState = { status: "idle" };

  public render() {
    return this.state.status === "idle" ? (
      <LoginScene
        onAuthentication={this.onAuthentication}
      />
    ) : (
      <AuthenticatedScene client={this.state.client} />
    );
  }

  private onAuthentication = async (nickname: string) => {
    const user = await this.authenticate(nickname);
    const client = await this.createClient(user, REALM_PATH);
    this.setState({ status: "authenticated", client });
  }

  private authenticate(nickname: string) {
    if (this.state.status === "idle") {
      // Create a credentials object from the nickname
      const credentials = Credentials.nickname(nickname);
      // Authenticate the user
      return User.authenticate(credentials, SERVER_URL);
    } else {
      throw new Error("Cannot create a client before being authenticated");
    }
  }

  private async createClient(user: User, path: string) {
    // Create a configuration from the user
    const config = await GraphQLConfig.create(user, path);
    // Construct an HTTP link that knows how to authenticate against ROS
    const httpLink = concat(
      config.authLink,
      new HttpLink({ uri: config.httpEndpoint }),
    );
    // Construct a link based on WebSocket that can be used for real-time subscriptions
    const webSocketLink = new WebSocketLink({
      options: {
        connectionParams: config.connectionParams,
      },
      uri: config.webSocketEndpoint,
    });
    // Combine the links in a way that splits based on the operation
    const link = split(
      ({ query }) => {
        const definition = getMainDefinition(query);
        return (
          definition.kind === "OperationDefinition" &&
          definition.operation === "subscription"
        );
      },
      webSocketLink,
      httpLink,
    );
    // Create a client with the combined links
    return new ApolloClient({
      cache: new InMemoryCache(),
      link,
    });
  }
}

export { AppContainer as App };

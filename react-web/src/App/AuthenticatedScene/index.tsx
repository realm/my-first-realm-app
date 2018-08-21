import { ApolloClient } from "apollo-client-preset";
import * as React from "react";
import { ApolloProvider } from "react-apollo";

import { ItemsOverview } from "./ItemsOverview";

interface IAuthenticatedSceneProps {
  client: ApolloClient<any>;
}

export const AuthenticatedScene = ({client}: IAuthenticatedSceneProps) => (
  <ApolloProvider client={client}>
    <ItemsOverview />
  </ApolloProvider>
);

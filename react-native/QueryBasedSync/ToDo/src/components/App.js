/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * @format
 * @flow
 */

import React from "react";
import { Scene, Stack, Router } from "react-native-router-flux";

import { LoginForm } from "./LoginForm";
import { ProjectList } from "./ProjectList";
import { ItemList } from "./ItemList";

export const App = () => (
  <Router>
    <Scene hideNavBar={true}>
      <Scene key="login" component={LoginForm} title="Please Login" />
      <Stack key="authenticated">
        <Scene key="projects" component={ProjectList} title="Projects" />
        <Scene key="items" component={ItemList} title="Items" />
      </Stack>
    </Scene>
  </Router>
);

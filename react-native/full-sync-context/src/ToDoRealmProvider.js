import React, {useMemo} from 'react';
import {RealmProvider} from 'react-realm-context';

import {schema} from './schema.js';

export const ToDoRealmProvider = props => {
  // Create a memoized configuration
  const config = useMemo(() => {
    const user = Realm.Sync.User.current;
    // Create the configuration
    return user.createConfiguration({
      schema,
      sync: {
        url: '~/ToDo',
        fullSynchronization: true,
      },
    });
  }, []);
  // Use the configuration to wrap children with a RealmProvider
  return <RealmProvider {...config} children={props.children} />;
};

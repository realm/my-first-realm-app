# My First Realm App using Realm and React for Web

ToDo demo app using Realm Object Server, GraphQL and React for the Web.

## Current limitations

The Realm Object Server GraphQL service is still in its young and as of the time of writing it still has a couple of limitations - most significantly:

1. The service cannot open a query-based Realm, which is why this example app will be using full-sync instead of the new
   query-based synchronization mode, which the other apps in this repository is using.
2. The service does not expose a way to initialize the Realm with a schema or modify the schema after creation, which is
   why this repository contains a script that can create a Realm on the server:
   ```
   npm run create-realm -- --url https://[your-instance-id].cloud.realm.io/ -n nicky -s simple
   ```

# My First Realm App using Realm and React for Web

ToDo demo app using Realm Object Server, GraphQL and React for the Web.

## Current limitations

The Realm Object Server GraphQL service is still in its young and as of the time of writing it still has a couple of limitations - most significantly:

1. The service cannot open a query-based Realm, which is why this example app will be using full-sync instead of the new
   query-based synchronization mode, which the other apps in this repository is using.
2. The service does not expose a way to initialize the Realm with a schema or modify the schema after creation, which is
   why this repository contains a script that can create a Realm on the server.

## Scripts

### Build

```
npm run build
```

### Create Realm

To create a Realm with the correct schema run the "create-realm", ex:

```
npm run create-realm -- --url https://[your-instance-id].cloud.realm.io/ --nickname nicky --schema simple
```

This will authenticate towards the server specified at the `url` as "`nicky`" with the nickname authentication provider
(if you're connecting to a server running in the Realm Cloud, make sure to enabled this on the instance settings).

The script takes a `--schema` parameter which can be either `simple` or `advanced`:
- `simple`: Just the `Item` schema
- `advanced`: Both the `Item` and `Project` schemas

You can run with "advanced" schema after you've already created a Realm with the "simple" schema, without loosing data.

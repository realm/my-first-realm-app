// Copyright 2018 Realm Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

const Realm = require('realm');
const uuidv4 = require('uuid/v4');
const blessed = require('blessed');

// credentials and server settings
const USERNAME = "test"; // must be admin user
const PASSWORD = "testtest";
const SERVER = "kneth.us1.cloud.realm.io";

// the data model
const ItemSchema = {
    name: 'Item',
    primaryKey: 'itemId',
    properties: {
        'itemId': { type: 'string', optional: false },
        'body': { type: 'string', optional: false },
        'isDone': { type: 'bool', optional: false },
        'timestamp': { type: 'date', optional: false }
    }
};

const ProjectSchema = {
    name: 'Project',
    primaryKey: 'id',
    properties: {
        'id': { type: 'string', optional: false },
        'owner': { type: 'string', optional: false },
        'timestamp': { type: 'date', optional: false },
        'items': { type: 'list', objectType: 'Item' }
    }
};

// keep a reference to your Realm
var appstate = {
    user: undefined,
    realm: undefined,
    project: undefined,
}

// set up UI
 var screen = blessed.screen({
    smartCSR: true,
    title: 'Query-based sync demo'
});

// input form for new TODO item
var inputbox = blessed.box({
    parent: screen,
    keys: true,
    mouse: true,
    top: 5,
    right: 0,
    height: '25%',
    width: '25%',
    border: {
        type: 'line'
    },
});

var form = blessed.form({
    parent: inputbox,
    left: 1,
    top: 1,
})

// list of items
var itemlist = blessed.listtable({
    parent: screen,
    top: 5,
    width: '50%',
    height: '75%',
    border: {
        type: 'line'
    }
});

// status lines
var statusbox = blessed.box({
    parent: screen,
    width: '100%',
    height: 10,
    bottom: 0,
    border: {
        type: 'line'
    },
    scrollable: true,
    alwaysScroll: true
});

form.on('submit', (data) => {
    appstate.realm.write(() => {
        let item = appstate.realm.create(ItemSchema.name, { itemId: uuidv4(), body: data['body'], isDone: data['done'], timestamp: new Date()});
        appstate.project['items'].push(item);
        statusbox.pushLine(`Adding a new item: ${JSON.stringify(item)}`);
    });
    statusbox.scroll(1);
    screen.render();
})

var textfield = blessed.textbox({
    parent: form,
    mouse: true,
    keys: true,
    height: 1,
    width: 20,
    left: 1,
    top: 3,
    name: 'body',
    style: {
        bg: 'blue',
    }
});

textfield.on('focus', () => {
    textfield.readInput();
});

var isDone = blessed.checkbox({
    parent: form,
    top: 7,
    left: 1,
    mouse: true,
    keys: true,
    shrink: true,
    style: {
      bg: 'blue'
    },
    height: 1,
    name: 'done',
    content: 'done'
  });

var submit = blessed.button({
    parent: form,
    bottom: 0,
    height: 3,
    width: '25%',
    left: 1,
    mouse: true,
    keys: true,
    name: 'submit',
    content: 'submit',    
    style: {
        bg: 'blue',
        focus: {
            bg: 'red'
        }
    }
});

submit.on('press', () => {
    form.submit();
});

// press q to quit
screen.key('q', () => {
    screen.destroy();
    appstate.realm.close();
    process.exit();
});
screen.render();

// open the Realm and perform operations
Realm.Sync.User.login(`https://${SERVER}`, USERNAME, PASSWORD)
    .then(user => {
        statusbox.pushLine(`${USERNAME} is logged in (identity: ${user.identity})`);
        appstate.user = user;
        screen.render();
        Realm.open({
            sync: {
                url: `realms://${SERVER}/default`,
                user: user,
                fullSynchronization: false,
            },
            schema: [ItemSchema, ProjectSchema],
        })
            .then(realm => {
                statusbox.pushLine('Realm is open');
                appstate.realm = realm;
                screen.render();

                let projects = realm.objects(ProjectSchema.name).filtered(`owner = "${user.identity}" SORT (timestamp DESC)`);
                let subscription = projects.subscribe();
                subscription.addListener((sub, state) => {
                    switch (state) {
                    case Realm.Sync.SubscriptionState.Complete:
                        if (projects.isEmpty()) {                            
                            statusbox.pushLine('No projects found - creating one');
                            statusbox.scroll(1);
                            screen.render();
                            realm.write(() => {
                                let project = realm.create(ProjectSchema.name, { id: uuidv4(), owner: user.identity, timestamp: new Date(), items: [] });
                            });
                        } else {
                            statusbox.pushLine(`${projects.length} projects found`);
                            statusbox.scroll(1);
                            let rows = [];
                            rows.push(['Id', 'Body', 'done', 'timestamp']);
                            let project = projects[0];
                            project['items'].forEach(item => {
                                rows.push([item['itemId'], item['body'], item['isDone'].toString(), item['timestamp'].toString()]);
                            });
                            itemlist.setData(rows);
                            appstate.project = project;
                            screen.render();
                        };
                        break;
                    case Realm.Sync.SubscriptionState.Error:
                        itemlist.setData([]);
                        statusbox.pushLine(`Subscription state: ${state}`);
                        statusbox.scroll(1);
                        screen.render();
                        break;
                    }
                });
            })
    });

        

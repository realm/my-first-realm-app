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

// credentials and server settings
const USERNAME = "test"
const PASSWORD = "test"
const SERVER = "kneth.us1.cloud.realm.io"

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


// parsing command-line options
let getopt = require('node-getopt').create([
    [ 'c', 'create=ARG', 'create a new item' ],
    [ 'l', 'list',       'list all items' ],
    [ 'm', 'mark=ARG',   'mark a item done' ],
    [ 'h', 'help',       'display this text'],
]).bindHelp();
let opt = getopt.parse(process.argv.slice(2));

// open the Realm and perform operations
Realm.Sync.User.login(`https://${SERVER}`, USERNAME, PASSWORD)
    .then(user => {
        console.log(`${USERNAME} is logged in`);
        Realm.open({
            sync: {
                url: `realms://${SERVER}/~/todo`,
                user: user
            },
            schema: [ItemSchema],
        })
            .then(realm => {
                if (opt.options['create']) {
                    console.log('Create item');
                    realm.write(() => {
                        realm.create(ItemSchema.name, { itemId: uuidv4(), body: opt.options['create'], isDone: false, timestamp: new Date() });
                    });
                }
                if (opt.options['list']) {
                    let items = realm.objects(ItemSchema.name);
                    items.forEach((item) => {
                        console.log(item.itemId, item.body, item.isDone, item.timestamp);
                    });
                }
                if (opt.options['mark']) {
                    realm.write(() => {
                        let item = realm.objects(ItemSchema.name).filtered(`itemId = "${opt.options['mark']}"`);
                        item[0].isDone = true;
                        item[0].timestamp = new Date();
                    });
                }

                // wait for data to be uploaded to the server
                realm.syncSession.addProgressNotification('upload', 'forCurrentlyOutstandingWork', (transferred, transferable) => {
                    if (transferred === transferable) {
                        process.exit(0);
                    }
                });
            })
    });

        

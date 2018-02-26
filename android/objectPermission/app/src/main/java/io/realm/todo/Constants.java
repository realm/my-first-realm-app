/*
 * Copyright 2018 Realm Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.realm.todo;

final class Constants {
//    private static final String INSTANCE_ADDRESS = "YOUR_INSTANCE.cloud.realm.io";
    private static final String INSTANCE_ADDRESS = "192.168.1.65:9080";
    static final String AUTH_URL = "http://" + INSTANCE_ADDRESS + "/auth";
    static final String REALM_BASE_URL = "realm://" + INSTANCE_ADDRESS;
}

/*
 * Copyright 2020 Realm Inc.
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
package io.realm.todo.ui.tasks

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import io.realm.SyncUser
import io.realm.kotlin.where
import io.realm.todo.model.Project
import io.realm.todo.model.Task
import java.util.*
import java.util.concurrent.TimeUnit

class TasksViewModel(user: SyncUser, url: String) : ViewModel() {

    private lateinit var realm: Realm
    private lateinit var taskResults: RealmResults<Task>
    private val _tasks = MutableLiveData<RealmResults<Task>?>(null)
    private val _title = MutableLiveData<String>("Loading")
    private val _status = MutableLiveData<String>("")

    init {
        val config = SyncUser.current()
                .createConfiguration(url)
                .fullSynchronization()
                .initialData { realm: Realm ->
                    // Temporary fix for not detecting Client Resync correctly
                    if (realm.isEmpty) {
                        val project = Project()
                        project.owner = user.identity
                        project.name = "My ToDo List"
                        project.timestamp = Date()
                        realm.insert(project)
                    }
                }
                .waitForInitialRemoteData(30, TimeUnit.SECONDS)
                .build()

        Realm.getInstanceAsync(config, object : Realm.Callback() {
            override fun onSuccess(realm: Realm) {
                this@TasksViewModel.realm = realm
                val project = realm.where<Project>().findFirst()
                if (project != null) {
                    _title.postValue(project.name)
                    taskResults = project.tasks.where()
                            .sort(Task.FIELD_DONE, Sort.ASCENDING, Task.FIELD_TIMESTAMP, Sort.ASCENDING)
                            .findAllAsync()
                    taskResults.addChangeListener { results, _ ->
                        if (results.isEmpty()) {
                            _status.postValue("Press + to add a task.")
                        } else {
                            _status.postValue("")
                        }
                    }
                    _tasks.postValue(taskResults)
                } else {
                    throw IllegalStateException("Project has been deleted")
                }
            }

            override fun onError(error: Throwable) {
                super.onError(error)
                Log.e(TAG, error.toString())
                _status.postValue("Could not open the Realm. See Logcat.")
            }
        })
    }

    /**
     * Expose when the query is ready.
     * Updates to the query result is not propagated here.
     * Instead listen to changes on the RealmResults itself.
     */
    val tasks: LiveData<RealmResults<Task>?>
        get() = _tasks

    /**
     * Observe the title of the activity
     */
    val title: LiveData<String>
        get() = _title

    /**
     * Observe any status text that needs to be shown to the user
     */
    val status: LiveData<String>
        get() = _status

    /**
     * Create a new task
     */
    fun createTask(taskDescription: String) {
        realm.executeTransactionAsync { realm: Realm ->
            val item = Task()
            item.body = taskDescription
            val project = realm.where<Project>().findFirst()
            if (project != null) {
                project.tasks.add(item)
            } else {
                throw IllegalStateException("Project was deleted")
            }
        }
    }

    /**
     * Delete an existing task
     */
    fun deleteTask(id: String?) {
        if (id == null) return
        realm.executeTransactionAsync { realm: Realm ->
            val item = realm.where<Task>().equalTo(Task.FIELD_ID, id).findFirst()
            item?.deleteFromRealm()
        }
    }

    /**
     * Set the done flag for a given task
     */
    fun toggleTask(itemId: String, isDone: Boolean) {
        realm.executeTransactionAsync { realm: Realm ->
            realm.where<Task>().equalTo(Task.FIELD_ID, itemId).findFirst()?.apply {
                this.isDone = isDone
            }
        }
    }

    override fun onCleared() {
        realm.close()
    }

    companion object {
        private const val TAG = "TasksViewModel"
    }
}
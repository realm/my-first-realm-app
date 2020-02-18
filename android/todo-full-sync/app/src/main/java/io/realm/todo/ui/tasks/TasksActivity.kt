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

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmResults
import io.realm.SyncUser
import io.realm.todo.R
import io.realm.todo.model.Task
import io.realm.todo.ui.login.LoginActivity


class TasksActivity : AppCompatActivity() {

    private lateinit var vm: TasksViewModel
    private lateinit var statusView: TextView
    private lateinit var fabView: View
    private lateinit var progressView: View
    private var tasksRecyclerAdapter: TasksRecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Setup views and UI listeners
        progressView = findViewById(R.id.progressbar)
        progressView.visibility = View.INVISIBLE
        statusView = findViewById(R.id.status)
        fabView = findViewById<View>(R.id.fab)
        fabView.visibility = View.INVISIBLE
        fabView.setOnClickListener { view: View? ->
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_task, null)
            val taskText = dialogView.findViewById<EditText>(R.id.task)
            AlertDialog.Builder(this@TasksActivity)
                    .setTitle("Add a new task")
                    .setMessage("What do you want to do next?")
                    .setView(dialogView)
                    .setPositiveButton("Add") { _: DialogInterface?, _: Int ->
                        vm.createTask(taskText.text.toString())
                    }
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show()
        }
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.adapterPosition
                tasksRecyclerAdapter?.getItem(position)?.let {
                    vm.deleteTask(it.itemId)
                }
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        // Setup ViewModel and event streams
        val user: SyncUser? = SyncUser.current()
        if (user == null) {
            // The user was logged out. This can e.g. happen if the activity is restored
            // from the background.
            gotoLoginActivity()
            return
        }
        if (!intent.hasExtra(INTENT_EXTRA_PROJECT_URL)) {
            throw IllegalStateException("Missing project URL")
        }
        val url: String = intent.getStringExtra(INTENT_EXTRA_PROJECT_URL)!!
        vm = ViewModelProvider(this, TasksViewModelFactory(user, url)).get()
        vm.tasks.observe(this, Observer { data: RealmResults<Task>? ->
            val adapter = if (data != null) TasksRecyclerAdapter(vm, data) else null
            tasksRecyclerAdapter = adapter
            recyclerView.adapter = adapter
            fabView.visibility = if (adapter != null) View.VISIBLE else View.INVISIBLE
        })
        vm.status.observe(this, Observer { status: String ->
            statusView.text = status
        })
        vm.title.observe(this, Observer { screenTitle: String ->
            title = screenTitle
        })
        vm.syncProgress.observe(this, Observer {
            progressView.visibility = if (it) View.VISIBLE else View.INVISIBLE
         })
    }

    private fun gotoLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            val syncUser = SyncUser.current()
            if (syncUser != null) {
                syncUser.logOut()
                gotoLoginActivity()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val INTENT_EXTRA_PROJECT_URL = "TasksActivity.projectUrl"
    }
}
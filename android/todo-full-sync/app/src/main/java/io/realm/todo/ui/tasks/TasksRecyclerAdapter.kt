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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import io.realm.todo.R
import io.realm.todo.model.Task
import io.realm.todo.ui.tasks.TasksRecyclerAdapter.MyViewHolder

class TasksRecyclerAdapter(private val vm: TasksViewModel, data: OrderedRealmCollection<Task?>?) : RealmRecyclerViewAdapter<Task?, MyViewHolder>(data, true, false) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.clear()
        holder.setTask(getItem(position))
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var textView: TextView = itemView.findViewById(R.id.body)
        var checkBox: CheckBox = itemView.findViewById(R.id.checkbox)
        private var entry: Task? = null

        init {
            checkBox.setOnClickListener(this)
        }

        // Since ViewHolders might be re-used, we need to remove any potential old listeners.
        fun clear() {
            entry?.apply { removeAllChangeListeners() }
            entry = null
        }

        /**
         * We disabled `updateOnModifications` in `RealmRecyclerViewAdapter` because we are using
         * object notifications instead to update individual items.
         */
        fun setTask(task: Task?) {
            task?.let {
                textView.text = task.body
                checkBox.isChecked = task.isDone
                it.addChangeListener<Task> { item, changeSet ->
                    if (item.isValid) {
                        changeSet?.changedFields?.forEach { changedField: String ->
                            when(changedField) {
                                Task.FIELD_BODY -> textView.text = item.body
                                Task.FIELD_DONE -> checkBox.isChecked = item.isDone
                            }
                        }
                    }
                }
            }
            this.entry = task
        }

        override fun onClick(v: View) {
            entry?.let {
                vm.toggleTask(it.itemId, checkBox.isChecked)
            }
        }
    }
}
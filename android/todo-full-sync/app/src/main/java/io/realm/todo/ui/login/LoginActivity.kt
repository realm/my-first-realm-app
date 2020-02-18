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
package io.realm.todo.ui.login

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import io.realm.todo.Constants
import io.realm.todo.R
import io.realm.todo.ui.tasks.TasksActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var vm: LoginViewModel
    private lateinit var usernameView: EditText
    private lateinit var passwordView: EditText
    private lateinit var progressView: View
    private lateinit var loginFormView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Bind views and events
        usernameView = findViewById(R.id.username)
        passwordView = findViewById(R.id.password)
        loginFormView = findViewById(R.id.login_form)
        progressView = findViewById(R.id.login_progress)
        val signInButton = findViewById<Button>(R.id.sign_in_button)
        val signUpButton = findViewById<Button>(R.id.sign_up_button)
        signInButton.setOnClickListener { doLogin(false) }
        signUpButton.setOnClickListener { doLogin(true) }

        // Create and observe ViewModel
        vm = ViewModelProvider(this).get()
        vm.loginState.observe(this, Observer { state ->
            when (state) {
                is WaitingForUser -> {
                    usernameView.error = null
                    passwordView.error = null
                    usernameView.requestFocus()
                }
                is InvalidInput -> {
                    usernameView.error = state.usernameError
                    passwordView.error = state.passwordError
                }
                is LoginPending -> {
                    showProgress(true)
                }
                is LoginSuccess -> {
                    // Don't hide progress bar as it causes the screen to flicker.
                    gotoListActivity()
                }
                is LoginError -> {
                    showProgress(false)
                    usernameView.error = state.reason
                    Toast.makeText(this, state.error.errorCode.toString(), Toast.LENGTH_SHORT).show()
                }
                else -> throw UnsupportedOperationException("Unsupported state: $state")
            }
        })
    }

    private fun gotoListActivity() {
        val intent = Intent(this, TasksActivity::class.java)
        intent.putExtra(TasksActivity.INTENT_EXTRA_PROJECT_URL, Constants.REALM_URL + "/~/project")
        startActivity(intent)
    }

    private fun doLogin(createUser: Boolean) {
        val username = usernameView.text.toString()
        val password = passwordView.text.toString()
        vm.attemptLogin(username, password, createUser)
    }

    /**
     * Toggle between progress UI and login form.
     */
    private fun showProgress(show: Boolean) {
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)
        loginFormView.visibility = if (show) View.GONE else View.VISIBLE
        loginFormView
                .animate()
                .setDuration(shortAnimTime.toLong())
                .alpha(if (show) 0F else 1F)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        loginFormView.visibility = if (show) View.GONE else View.VISIBLE
                    }
                })
        progressView.visibility = if (show) View.VISIBLE else View.GONE
        progressView
                .animate()
                .setDuration(shortAnimTime.toLong())
                .alpha(if (show) 1F else 0F)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        progressView.visibility = if (show) View.VISIBLE else View.GONE
                    }
                })
    }
}
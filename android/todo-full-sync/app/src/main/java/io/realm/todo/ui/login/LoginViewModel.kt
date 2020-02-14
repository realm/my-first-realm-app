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

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.ObjectServerError
import io.realm.SyncCredentials
import io.realm.SyncUser
import io.realm.todo.Constants

/**
 * Enumerate the different states of a login
 * The [LoginActivity] determines how this is translated to view events.
 */
sealed class LoginState
object WaitingForUser : LoginState()
data class InvalidInput(val usernameError: String?, val passwordError: String?) : LoginState()
object LoginPending : LoginState()
data class LoginSuccess(val user: SyncUser) : LoginState()
data class LoginError(val error: String) : LoginState()

class LoginViewModel : ViewModel() {

    private val _state = MutableLiveData<LoginState>(
        when(SyncUser.current() != null) {
            true ->  LoginSuccess(SyncUser.current())
            false -> WaitingForUser
        }
    )

    /**
     * Observe the state of the login
     */
    val loginState: LiveData<LoginState>
        get() = _state

    fun attemptLogin(userName: String, password: String, createUser: Boolean) { // Reset errors.
        if (userName.isEmpty() || password.isEmpty()) {
            val userError: String? = if (userName.isEmpty()) "Missing username" else null
            val passwordError : String? = if (password.isEmpty()) "Missing password" else null
            _state.postValue(InvalidInput(userError, passwordError))
            return
        }

        _state.postValue(LoginPending)
        val credentials = SyncCredentials.usernamePassword(userName, password, createUser)

        SyncUser.logInAsync(credentials, Constants.AUTH_URL, object : SyncUser.Callback<SyncUser?> {
            override fun onSuccess(user: SyncUser?) {
                _state.postValue(LoginSuccess(user!!))
            }

            override fun onError(error: ObjectServerError) {
                Log.e(TAG, error.toString())
                _state.postValue(LoginError("Uh oh something went wrong! (check your logcat please)"))
            }
        })
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}
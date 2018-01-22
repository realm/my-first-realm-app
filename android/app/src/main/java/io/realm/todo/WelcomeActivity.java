package io.realm.todo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;

import io.realm.ObjectServerError;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import static io.realm.todo.Constants.AUTH_URL;

public class WelcomeActivity extends AppCompatActivity {

    private LoginTask mAuthTask = null;

    // UI references.
    private EditText mNicknameTextView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (SyncUser.currentUser() != null) {
            this.goToItemsActivity();
        }

        // Set up the login form.
        mNicknameTextView = findViewById(R.id.nickname);
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNicknameTextView.setError(null);
        // Store values at the time of the login attempt.
        String nickname = mNicknameTextView.getText().toString();
        showProgress(true);
        mAuthTask = new LoginTask(nickname);
        mAuthTask.execute((Void) null);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void goToItemsActivity(){
        Intent intent = new Intent(WelcomeActivity.this, ItemsActivity.class);
        startActivity(intent);
    }


    public class LoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mNickname;

        LoginTask(String nickname) {
            mNickname = nickname;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            SyncCredentials creds = SyncCredentials.nickname(mNickname, true);
            try {
                SyncUser user = SyncUser.login(creds, AUTH_URL);
            } catch (ObjectServerError e) {
                Log.d("Login", e.getErrorMessage());
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                goToItemsActivity();
            } else {
                mNicknameTextView.setError("Uh oh something went wrong!");
                mNicknameTextView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}


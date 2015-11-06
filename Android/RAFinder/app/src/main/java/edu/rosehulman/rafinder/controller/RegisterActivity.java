package edu.rosehulman.rafinder.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.model.Login;

public class RegisterActivity extends Activity {

    private ProgressDialog mRegisterProgressDialog;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmEmailView;
    private EditText mConfirmPasswordView;
    private EditText mRAEmailView;
    private EditText mConfirmRAEmailView;
    private View mLoginFormView;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_register);

        mRegisterProgressDialog = new ProgressDialog(this);
        mRegisterProgressDialog.setTitle("Loading");
        mRegisterProgressDialog.setMessage("Registering with Firebase...");
        mRegisterProgressDialog.setCancelable(false);

        // Set up the registration form.
        mEmailView = (EditText) findViewById(R.id.email);
        mConfirmEmailView = (EditText) findViewById(R.id.confirmEmail);
        mPasswordView = (EditText) findViewById(R.id.password);
        mConfirmPasswordView = (EditText) findViewById(R.id.confirmPassword);
        mRAEmailView = (EditText) findViewById(R.id.raEmail);
        mConfirmRAEmailView = (EditText) findViewById(R.id.confirmRaEmail);

        String email = getIntent().getStringExtra(ConfigKeys.KEY_USER_EMAIL);
        if (email != null) {
            mEmailView.setText(email);
        }

        Button registerButton = (Button) findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View ignored) {
                registerNewUser();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void registerNewUser() {
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();
        final String raEmail = mRAEmailView.getText().toString();

        if (!Login.isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
        }
        if (!Login.isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
        }
        if (!Login.isEmailValid(raEmail)) {
            mRAEmailView.setError(getString(R.string.error_invalid_ra_email));
        }
        if (!email.equals(mConfirmEmailView.getText().toString())) {
            mConfirmEmailView.setError(getString(R.string.error_email_mismatch));
        }
        if (!password.equals(mConfirmPasswordView.getText().toString())) {
            mConfirmPasswordView.setError(getString(R.string.error_password_mismatch));
        }
        if (!raEmail.equals(mConfirmRAEmailView.getText().toString())) {
            mConfirmRAEmailView.setError(getString(R.string.error_ra_email_mismatch));
        }

        showProgress(true);
        mRegisterProgressDialog.show();

        final Firebase ref = new Firebase(ConfigKeys.FIREBASE_ROOT_URL);
        ref.child("Employees/Resident Assistants").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.child("email").getValue().toString().equals(raEmail)) {
                        Log.d(ConfigKeys.LOG_TAG, "Found RA for email address <" + email + ">");
                        createUser(email, password, raEmail, ref);
                        return;
                    }
                }
                showError(getString(R.string.error_ra_email_does_not_exist));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // ignored
            }
        });

    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this).setTitle("Error").setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void createUser(final String email, String password, final String raEmail, final Firebase ref) {
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.

        ref.createUser(
                email,
                password,
                new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        // Left as-is for clarity
                        //noinspection Convert2Lambda
                        ref.child("Residents/" + result.get("uid"))
                                .setValue(new User(raEmail), new Firebase.CompletionListener() {
                                    @Override
                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {

                                        if (firebaseError != null) {
                                            showError(getString(R.string.error_generic));
                                            Log.e(ConfigKeys.LOG_TAG, firebaseError.getMessage());
                                        }

                                        showProgress(false);
                                        // CONSIDER: confirmation email
                                        // With only Firebase and Android, it's complicated.
                                        // There are no real users yet, so it doesn't matter much at this juncture.
                                        // I will continue on that during my Independent Study work.

                                        // login with new credentials
                                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        loginIntent.putExtra(ConfigKeys.KEY_USER_EMAIL, email);
                                        startActivity(loginIntent);
                                    }
                                });
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
                            showError(getString(R.string.error_email_in_use));
                        } else {
                            Log.e(ConfigKeys.LOG_TAG, firebaseError.getMessage());
                            showError(getString(R.string.error_generic));
                        }
                    }
                });
    }

    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginFormView.setVisibility(show ? View.GONE
                                                          : View.VISIBLE);
                    }
                });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE
                                                         : View.GONE);
                    }
                });
    }

    private void showError(String errorMessage) {
        showProgress(false);
        mRegisterProgressDialog.hide();
        showErrorDialog(errorMessage);
    }

    @SuppressWarnings("unused")
    private class User {
        private final String myRA;

        public User(String raEmail) {
            myRA = raEmail;
        }

        public String getMyRA() {
            return myRA;
        }
    }
}

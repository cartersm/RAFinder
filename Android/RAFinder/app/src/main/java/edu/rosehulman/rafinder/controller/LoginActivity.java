package edu.rosehulman.rafinder.controller;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.Environment;
import edu.rosehulman.rafinder.MainActivity;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.UserType;
import edu.rosehulman.rafinder.model.Login;
import edu.rosehulman.rafinder.model.person.ResidentAssistant;
import edu.rosehulman.rosefire.Rosefire;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    /**
     * A dialog that displays during the authentication process
     */
    private ProgressDialog mAuthProgressDialog;

    // UI references.
    private TextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private Login mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        Firebase firebase = new Firebase(ConfigKeys.FIREBASE_ROOT_URL);
        mLogin = new Login(firebase, this);

        setContentView(R.layout.activity_login);
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);

        // Set up the login form.
        mEmailView = (TextView) findViewById(R.id.email);
        switch (ConfigKeys.ENV) {
            case PROD:
                mEmailView.setHint(R.string.kerberos_username);
                break;
        }

        mPasswordView = (EditText) findViewById(R.id.password);
        switch (ConfigKeys.ENV) {
            case PROD:
                mPasswordView.setHint(R.string.kerberos_password);
                break;
        }
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View ignored) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        SharedPreferences prefs = getSharedPreferences(ConfigKeys.KEY_SHARED_PREFS, MODE_PRIVATE);
        UserType userType = UserType.valueOf(prefs.getString(ConfigKeys.KEY_USER_TYPE, UserType.NONE.toString()));
        String raEmail = prefs.getString(ConfigKeys.KEY_RA_EMAIL, "");
        String email = prefs.getString(ConfigKeys.KEY_USER_EMAIL, "");

        // Skip login screen if we're still authorized and have data.
        if (firebase.getAuth() != null
                && !userType.equals(UserType.NONE)
                && !raEmail.equals("")
                && !email.equals("")) {
            launchMainActivity(userType, raEmail, email);
        }

        if (ConfigKeys.ENV.equals(Environment.PROD)) {
            onRosefireLogin();
            mEmailView.setVisibility(View.GONE);
            mPasswordView.setVisibility(View.GONE);
            mEmailSignInButton.setVisibility(View.GONE);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && Login.isPasswordInvalid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (Login.isEmailInvalid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthProgressDialog.show();
            mLogin.loginWithPassword(email, password);
            mAuthProgressDialog.hide();
            showProgress(false);
        }
    }

    public void launchMainActivity(UserType userType, String raEmail, String email) {
        mAuthProgressDialog.hide();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ConfigKeys.KEY_USER_TYPE, userType.name());
        intent.putExtra(ConfigKeys.KEY_RA_EMAIL, raEmail);
        intent.putExtra(ConfigKeys.KEY_USER_EMAIL, email);

        // Store data for persistence
        SharedPreferences.Editor editor = getSharedPreferences(ConfigKeys.KEY_SHARED_PREFS, MODE_PRIVATE).edit();
        editor.putString(ConfigKeys.KEY_USER_TYPE, userType.name());
        editor.putString(ConfigKeys.KEY_RA_EMAIL, raEmail);
        editor.putString(ConfigKeys.KEY_USER_EMAIL, email);
        editor.apply();

        Log.d(ConfigKeys.LOG_TAG, "starting Main with userType <" + userType + "> and raEmail <" + raEmail + ">");
        startActivity(intent);
        finish();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
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

    /**
     * Displays an alert dialog that contains the specified error message
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this).setTitle("Error").setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public String getEmail() {
        return mEmailView.getText().toString();
    }

    public void showError(String errorMessage) {
        showErrorDialog(errorMessage);
        mAuthProgressDialog.hide();
    }

    // RoseFire
    public void onRosefireLogin() {
        Intent signInIntent = Rosefire.getSignInIntent(this, ConfigKeys.REGISTRY_TOKEN);
        startActivityForResult(signInIntent, ConfigKeys.RC_ROSEFIRE_LOGIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConfigKeys.RC_ROSEFIRE_LOGIN) {
            try {
                String token = Rosefire.getSignInResultFromIntent(data);
                mLogin.firebase.authWithCustomToken(token, mLogin.getAuthResultHandler());

            } catch (NullPointerException e) {
                // We backed out of the browser activity; exit
                finish();
                System.exit(0);
            }
        }
    }

    public void getRAAndLogin(final UserType userType, final String userEmail) {
        mLogin.firebase.child(ConfigKeys.EMPLOYEES + "/" + ConfigKeys.RESIDENT_ASSISTANTS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<ResidentAssistant> ras = new ArrayList<>((int) dataSnapshot.getChildrenCount());
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            ras.add(child.getValue(ResidentAssistant.class));
                        }
                        ResidentAssistant[] rasArray = new ResidentAssistant[(int) dataSnapshot.getChildrenCount()];
                        rasArray = ras.toArray(rasArray);
                        // sort alphabetically by name
                        Arrays.sort(rasArray, new Comparator<ResidentAssistant>() {
                            @Override
                            public int compare(ResidentAssistant lhs, ResidentAssistant rhs) {
                                return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
                            }
                        });

                        createRADialog(userType, rasArray, userEmail);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        // ignored
                    }
                });
    }

    private void createRADialog(final UserType userType, final ResidentAssistant[] ras, String userEmail) {
        DialogFragment dialogFragment = new RAChooserDialogFragment(this, ras, userType, userEmail);
        dialogFragment.show(getFragmentManager(), "raChooser");
    }

}
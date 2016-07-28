package edu.rosehulman.rafinder.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.BuildConfig;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.Environment;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.UserType;
import edu.rosehulman.rafinder.controller.LoginActivity;

/**
 * Models actions and data required for login.
 */
public class Login {
    public final Firebase firebase;
    private final LoginActivity loginActivity;

    private String raEmail = "";

    public Login(Firebase firebaseRef, LoginActivity loginActivity) {
        firebase = firebaseRef;
        this.loginActivity = loginActivity;
    }

    public void loginWithPassword(String email, String password) {
        firebase.authWithPassword(email, password, new AuthResultHandler());
    }



    public AuthResultHandler getAuthResultHandler() {
        return new AuthResultHandler();
    }

    private class AuthResultHandler implements Firebase.AuthResultHandler {
        public void onAuthenticated(AuthData authData) {
            setAuthenticatedUser(authData);
        }

        public void onAuthenticationError(FirebaseError firebaseError) {
            if (firebaseError.getCode() == FirebaseError.INVALID_EMAIL ||
                    firebaseError.getCode() == FirebaseError.USER_DOES_NOT_EXIST) {
                loginActivity.showError(loginActivity.getString(R.string.error_invalid_email));
            } else if (firebaseError.getCode() == FirebaseError.INVALID_PASSWORD) {
                loginActivity.showError(loginActivity.getString(R.string.error_incorrect_password));
            } else {
                Log.w(ConfigKeys.LOG_TAG, "Caught firebase error: " + firebaseError.getMessage());
            }
        }

        private void setAuthenticatedUser(AuthData authData) {
            if (authData != null) {
                firebase.child(ConfigKeys.EMPLOYEES)
                        .addListenerForSingleValueEvent(new EmployeeListener(authData.getUid() + "@rose-hulman.edu"));
            }
        }
    }

    private final class EmployeeListener implements ValueEventListener {
        private final String uid;

        private EmployeeListener(String uid) {
            this.uid = uid;
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (BuildConfig.DEBUG){
                Log.d(ConfigKeys.LOG_TAG, "in Employees callback for user <" + this.uid + ">");
                Log.d(ConfigKeys.LOG_TAG, "DataSnapshot url = " + dataSnapshot.getRef().getPath().toString());
            }
            if (dataSnapshot.getChildrenCount() != 4) {
                Log.wtf(ConfigKeys.LOG_TAG, "Employees table had wrong number of children");
            }
            UserType userType;
            for (DataSnapshot table : dataSnapshot.getChildren()) {
                for (DataSnapshot key : table.getChildren()) {
                    if (key.hasChild(ConfigKeys.EMPLOYEE_EMAIL) &&
                            key.child(ConfigKeys.EMPLOYEE_EMAIL).getValue(String.class).equals(this.uid)) {
                        raEmail = this.uid;
                        userType = UserType.valueOf(table.getKey().toUpperCase());
                        if (ConfigKeys.ENV == Environment.DEVELOPMENT) {
                            loginActivity.launchMainActivity(userType, raEmail, loginActivity.getEmail());
                        } else {
                            loginActivity.launchMainActivity(userType, raEmail, this.uid);
                        }
                    }
                }
            }

            if (raEmail.isEmpty()) {
                Log.d(ConfigKeys.LOG_TAG, "User <" + this.uid + "> not found in employees");
                userType = UserType.RESIDENT;
                SharedPreferences prefs =
                        loginActivity.getSharedPreferences(ConfigKeys.KEY_SHARED_PREFS, Context.MODE_PRIVATE);
                raEmail = prefs.getString(ConfigKeys.KEY_RA_EMAIL, "");
                Log.d(ConfigKeys.LOG_TAG, "Got RA Email '" + raEmail + "'");
                String userEmail = prefs.getString(ConfigKeys.KEY_USER_EMAIL, "");
                if (raEmail.isEmpty() || !userEmail.equals(this.uid)) {
                    Log.d(ConfigKeys.LOG_TAG, "Got user email '" + userEmail + "' and uid '" + uid + "'");
                    loginActivity.getRAAndLogin(userType, this.uid);
                } else if (!raEmail.isEmpty()) {
                    loginActivity.launchMainActivity(userType, raEmail, this.uid);
                }

            }

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            // ignored
        }
    }
}
package edu.rosehulman.rafinder.controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.UserType;
import edu.rosehulman.rafinder.model.person.ResidentAssistant;

class RAChooserDialogFragment extends DialogFragment {
    private LoginActivity mLoginActivity;
    private final ResidentAssistant[] mRAs;
    private final UserType mUserType;
    private String mUserEmail;

    public RAChooserDialogFragment(LoginActivity loginActivity, ResidentAssistant[] ras, UserType userType, String userEmail) {
        this.mLoginActivity = loginActivity;
        this.mRAs = ras;
        this.mUserType = userType;
        this.mUserEmail = userEmail;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_ra);
        final String[] raStrings = new String[mRAs.length];
        for (int i = 0; i < mRAs.length; i++) {
            ResidentAssistant ra = mRAs[i];
            raStrings[i] = ra.getName();
        }
        builder.setItems(raStrings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ResidentAssistant ra = mRAs[which];
                SharedPreferences prefs = mLoginActivity.getSharedPreferences(ConfigKeys.KEY_SHARED_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(ConfigKeys.KEY_RA_EMAIL, ra.getEmail());
                editor.putString(ConfigKeys.KEY_USER_EMAIL, mLoginActivity.getEmail());
                editor.apply();
                Log.d(ConfigKeys.LOG_TAG, "Set RA Email '" + prefs.getString(ConfigKeys.KEY_RA_EMAIL, "") + "'");
                mLoginActivity.launchMainActivity(mUserType, ra.getEmail(), mUserEmail);
            }
        });

        return builder.create();
    }
}

package edu.rosehulman.rafinder.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.BuildConfig;
import android.util.Log;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.UserType;
import edu.rosehulman.rafinder.model.person.ResidentAssistant;

public class RAChooserDialogFragment extends DialogFragment {
    private static final String KEY_RAS_ARRAY = "KEY_RAS_ARRAY";
    private static final String KEY_USER_TYPE = "KEY_USER_TYPE";
    private static final String KEY_USER_EMAIL = "KEY_USER_EMAIL";
    private ResidentAssistant[] mRAs;
    private UserType mUserType;
    private String mUserEmail;

    // empty constructor for lifecycle manager
    public RAChooserDialogFragment() {
    }

    public static RAChooserDialogFragment getInstance(ResidentAssistant[] ras, UserType userType, String userEmail) {
        RAChooserDialogFragment fragment = new RAChooserDialogFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(KEY_RAS_ARRAY, ras);
        args.putSerializable(KEY_USER_TYPE, userType);
        args.putString(KEY_USER_EMAIL, userEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRAs = (ResidentAssistant[]) getArguments().getParcelableArray(KEY_RAS_ARRAY);
        mUserType = (UserType) getArguments().getSerializable(KEY_USER_TYPE);
        mUserEmail = getArguments().getString(KEY_USER_EMAIL);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.select_ra);
        String[] raStrings = new String[mRAs.length];
        for (int i = 0; i < mRAs.length; i++) {
            ResidentAssistant ra = mRAs[i];
            raStrings[i] = ra.getName();
        }
        builder.setItems(raStrings, (DialogInterface dialog, int which) -> {
            ResidentAssistant ra = mRAs[which];
            Activity context = getActivity();
            SharedPreferences prefs = context.getSharedPreferences(ConfigKeys.KEY_SHARED_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(ConfigKeys.KEY_RA_EMAIL, ra.getEmail());
            editor.putString(ConfigKeys.KEY_USER_EMAIL, ((LoginActivity) context).getEmail());
            editor.apply();
            if (BuildConfig.DEBUG){
                Log.d(ConfigKeys.LOG_TAG, "Set RA Email '" + prefs.getString(ConfigKeys.KEY_RA_EMAIL, "") + "'");
            }
            ((LoginActivity) context).launchMainActivity(mUserType, ra.getEmail(), mUserEmail);
        });

        return builder.create();
    }
}

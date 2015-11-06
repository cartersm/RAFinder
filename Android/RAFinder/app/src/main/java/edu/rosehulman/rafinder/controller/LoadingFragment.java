package edu.rosehulman.rafinder.controller;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rosehulman.rafinder.R;

/**
 * A simple fragment to show a loading dialog.
 * Used as a placeholder to prevent NullPointerExceptions while loading data.
 */
public class LoadingFragment extends Fragment {

    private ProgressDialog mProgressDialog;

    public static LoadingFragment newInstance() {
        return new LoadingFragment();
    }

    public LoadingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_loading, container, false);

        mProgressDialog = new ProgressDialog(v.getContext());
        mProgressDialog.setTitle("Loading");
        mProgressDialog.setMessage("Loading Data...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mProgressDialog.dismiss();
    }

}

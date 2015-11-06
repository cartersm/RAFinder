package edu.rosehulman.rafinder.loader;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.model.Hall;

public class HallLoader extends Loader {
    private static final String RES_HALLS = "ResHalls";

    private Hall mHall;
    private final LoaderListener mListener;

    public HallLoader(String hallName, LoaderListener listener) {
        mListener = listener;
        loadData(new Firebase(ConfigKeys.FIREBASE_ROOT_URL + "/" + RES_HALLS + "/" + hallName));
    }

    protected void loadData(Firebase firebase) {
        Log.d(ConfigKeys.LOG_TAG, "Loading HallRoster data...");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mHall = new Hall(dataSnapshot);
                Log.d(ConfigKeys.LOG_TAG, "Finished loading Hall data.");
                mListener.onHallRosterLoadingComplete();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // ignored
            }
        });
    }

    public Hall getHall() {
        return mHall;
    }

}

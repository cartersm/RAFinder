package edu.rosehulman.rafinder.loader;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.model.Hall;

public class HallLoader extends Loader {
    private static final String RES_HALLS = "ResHalls";

    private List<Hall> mHalls;
    private final LoaderListener mListener;

    public HallLoader(LoaderListener listener) {
        mListener = listener;
        mHalls = new ArrayList<>();
        loadData(new Firebase(ConfigKeys.FIREBASE_ROOT_URL + "/" + RES_HALLS));
    }

    protected void loadData(Firebase firebase) {
        Log.d(ConfigKeys.LOG_TAG, "Loading HallRoster data...");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    mHalls.add(new Hall(child));
                }
                Log.d(ConfigKeys.LOG_TAG, "Finished loading Hall data.");
                mListener.onHallRosterLoadingComplete();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // ignored
            }
        });
    }

    public List<Hall> getHalls() {
        return mHalls;
    }

}

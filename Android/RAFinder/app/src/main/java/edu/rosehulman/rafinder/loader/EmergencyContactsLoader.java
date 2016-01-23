package edu.rosehulman.rafinder.loader;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.model.person.EmergencyContact;
import edu.rosehulman.rafinder.model.person.Employee;

@SuppressWarnings("unused")
public class EmergencyContactsLoader extends Loader {
    private static final String EMERGENCY_CONTACTS = "EmergencyContacts";
    private final LoaderListener mListener;
    private List<EmergencyContact> mContacts = new ArrayList<>();

    public EmergencyContactsLoader(LoaderListener listener) {
        mListener = listener;
        if (listener != null) {
//            Employee raOnDuty = listener.getDutyRoster().getOnDutyNow();
//            if (raOnDuty != null) {
//                mContacts.add(new EmergencyContact(raOnDuty, true));
//            }

            List<Employee> myRAs = listener.getMyRAs();
            for (Employee employee : myRAs) {
                mContacts.add(new EmergencyContact(employee, false));
            }
        }

        loadData(new Firebase(ConfigKeys.FIREBASE_ROOT_URL + "/" + EMERGENCY_CONTACTS));
    }

    protected void loadData(Firebase firebase) {
        Log.d(ConfigKeys.LOG_TAG, "Loading Emergency Contact data...");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String firebaseURL = ConfigKeys.FIREBASE_ROOT_URL + child.getRef().getPath().toString();
                    EmergencyContact contact = new EmergencyContact(firebaseURL);
                    mContacts.add(contact);
                }
                Log.d(ConfigKeys.LOG_TAG, "Finished loading Emergency Contact data.");
                mListener.onEmergencyContactsLoadingComplete();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // ignored
            }
        });
    }

    public List<EmergencyContact> getContactList() {
        return mContacts;
    }

    public void setContactList(List<EmergencyContact> contacts) {
        mContacts = contacts;
    }

}

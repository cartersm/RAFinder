package edu.rosehulman.rafinder.controller;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.adapter.EmergencyContactArrayAdapter;
import edu.rosehulman.rafinder.model.person.EmergencyContact;
import edu.rosehulman.rafinder.model.person.Employee;

/**
 * The Emergency Contacts list. Contains items of type {@link Employee}.
 */
public class EmergencyContactsFragment extends Fragment
        implements EmergencyContactArrayAdapter.EmergencyContactListener {
    private EmergencyContactsListener mListener;
    private List<EmergencyContact> emergencyContacts;

    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @return A new instance of fragment EmergencyContactsFragment.
     */
    public static EmergencyContactsFragment newInstance() {
        return new EmergencyContactsFragment();
    }

    public EmergencyContactsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mListener != null) {
            emergencyContacts = mListener.getEmergencyContacts();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);
        ListView listView = (ListView) view.findViewById(R.id.emergencyContactsListView);

        Collections.sort(emergencyContacts, new Comparator<EmergencyContact>() {
            @Override
            public int compare(EmergencyContact lhs, EmergencyContact rhs) {
                if (lhs.getPriority().ordinal() > rhs.getPriority().ordinal()) {
                    return -1;
                } else if (lhs.getPriority().ordinal() < rhs.getPriority().ordinal()) {
                    return 1;
                }
                return 0;
            }
        });
        EmergencyContactArrayAdapter mAdapter = new EmergencyContactArrayAdapter(
                getActivity(),
                R.layout.fragment_emergency_contacts,
                emergencyContacts,
                this);
        listView.setAdapter(mAdapter);
        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EmergencyContactsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement EmergencyContactsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void makePhoneCall(String phoneNumber) {
        if (mListener != null) {
            mListener.dialPhoneNumber(phoneNumber);
        }
    }

    @Override
    public void sendEmail(String emailAddress) {
        if (mListener != null) {
            mListener.sendEmail(emailAddress);
        }
    }

    public interface EmergencyContactsListener {
        public void dialPhoneNumber(String phoneNumber);
        public void sendEmail(String emailAddress);
        public List<EmergencyContact> getEmergencyContacts();
    }
}

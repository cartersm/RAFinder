package edu.rosehulman.rafinder.controller;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Collections;
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

    public EmergencyContactsFragment() {
    }

    public static EmergencyContactsFragment newInstance() {
        return new EmergencyContactsFragment();
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

        Collections.sort(emergencyContacts, (lhs, rhs) -> {
            if (lhs.getPriority().ordinal() > rhs.getPriority().ordinal()) {
                return -1;
            }
            if (lhs.getPriority().ordinal() < rhs.getPriority().ordinal()) {
                return 1;
            }
            return 0;
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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (EmergencyContactsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement EmergencyContactsListener");
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
        void dialPhoneNumber(String phoneNumber);

        void sendEmail(String emailAddress);

        List<EmergencyContact> getEmergencyContacts();
    }
}

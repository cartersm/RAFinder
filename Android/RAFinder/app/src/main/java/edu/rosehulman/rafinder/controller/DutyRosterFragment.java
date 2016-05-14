package edu.rosehulman.rafinder.controller;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.joda.time.LocalDate;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.adapter.DutyRosterListAdapter;
import edu.rosehulman.rafinder.model.DutyRoster;


/**
 * The RA view of the Duty Roster.
 */
public class DutyRosterFragment extends Fragment {
    private DutyRosterListener mListener;
    private DutyRoster mRoster;

    public DutyRosterFragment() {
    }

    public static DutyRosterFragment newInstance(LocalDate date) {
        DutyRosterFragment fragment = new DutyRosterFragment();
        Bundle args = new Bundle();
        args.putString(ConfigKeys.DATE, date.toString(ConfigKeys.dateFormat));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mListener != null) {
            mRoster = mListener.getDutyRoster();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_duty_roster, container, false);

        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.dutyRosterListView);
        DutyRosterListAdapter listAdapter = new DutyRosterListAdapter(getActivity(), mRoster);
        listView.setAdapter(listAdapter);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (DutyRosterListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DutyRosterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface DutyRosterListener {
        DutyRoster getDutyRoster();
    }
}

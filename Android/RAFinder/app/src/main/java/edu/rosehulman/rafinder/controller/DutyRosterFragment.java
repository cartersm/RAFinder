package edu.rosehulman.rafinder.controller;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.MainActivity;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.UserType;
import edu.rosehulman.rafinder.adapter.DutyRosterArrayAdapter;
import edu.rosehulman.rafinder.model.DutyRoster;
import edu.rosehulman.rafinder.model.DutyRosterItem;


/**
 * The RA view of the Duty Roster (which is editable for RAs, but not for SAs).
 */
public class DutyRosterFragment extends Fragment {
    private DutyRosterListener mListener;
    private DutyRoster roster;

    public static DutyRosterFragment newInstance(String hall, LocalDate date) {
        DutyRosterFragment fragment = new DutyRosterFragment();
        Bundle args = new Bundle();
        args.putString(ConfigKeys.HALL, hall);
        args.putString(ConfigKeys.DATE, date.toString(ConfigKeys.dateFormat));
        fragment.setArguments(args);
        return fragment;
    }

    public DutyRosterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mListener != null) {
            roster = mListener.getDutyRoster();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_duty_roster, container, false);
        List<DutyRosterItem> rosterItems = new ArrayList<>();
        rosterItems.addAll(roster.getRoster().values());
        TextView hallName = (TextView) view.findViewById(R.id.hallNameTextView);
        hallName.setText(mListener.getMyHall());
        DutyRosterArrayAdapter mAdapter = new DutyRosterArrayAdapter(getActivity(), R.layout.fragment_duty_roster_widget, rosterItems, mListener);
        MainActivity context = (MainActivity) view.getContext();
        ImageButton addButton = (ImageButton) view.findViewById(R.id.addDutyButton);
        if (context.getUserType().equals(UserType.RESIDENT_ASSISTANT)) {
            mAdapter.setEditable(true);
            addButton.setBackgroundResource(R.mipmap.ic_edit);
            mAdapter.setEditable(true);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.showAddDialog();
                }
            });
            addButton.setVisibility(View.VISIBLE);
        } else {
            addButton.setVisibility(View.GONE);
        }
        ListView listView = (ListView) view.findViewById(R.id.dutyRosterListView);
        listView.setAdapter(mAdapter);
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

    public interface DutyRosterListener
            extends DutyRosterArrayAdapter.DutyRosterAAListener,
            AddDutyRosterItemDialog.AddDutyRosterItemListener {
        public DutyRoster getDutyRoster();
        public String getMyHall();
        public void showAddDialog();
    }
}

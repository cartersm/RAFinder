package edu.rosehulman.rafinder.controller;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.List;

import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.adapter.HallRosterListAdapter;
import edu.rosehulman.rafinder.model.reshall.ResHall;

/**
 * The RA's view of a listing for a floor's residents
 */
public class HallRosterFragment extends Fragment {

    private HallRosterListener mListener;
    private ExpandableListAdapter mAdapter;

    public HallRosterFragment() {
    }

    public static HallRosterFragment newInstance() {
        HallRosterFragment fragment = new HallRosterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mListener != null) {
            mAdapter = new HallRosterListAdapter(getActivity(), mListener.getHalls());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hall_roster, container, false);
        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.hallList);
        listView.setAdapter(mAdapter);
        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                // Collapse previous parent if expanded.
                if ((previousGroup != -1) && (groupPosition != previousGroup)) {
                    listView.collapseGroup(previousGroup);
                }
                previousGroup = groupPosition;
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (HallRosterListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement HallRosterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface HallRosterListener {
        List<ResHall> getHalls();
    }

}

package edu.rosehulman.rafinder.controller;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.adapter.FloorRosterArrayAdapter;
import edu.rosehulman.rafinder.model.Floor;
import edu.rosehulman.rafinder.model.Hall;
import edu.rosehulman.rafinder.model.RoomEntry;


/**
 * The RA's view of a listing for a floor's residents
 */
public class HallRosterFragment extends Fragment
        implements HallHeaderFragment.HallHeaderFragmentListener {
    private Hall hall;

    private int floorIndex; //not necessarily the floor number
    private String floorName;
    private List<RoomEntry> rooms = new ArrayList<>();
    private HallRosterListener mListener;
    private ListAdapter mAdapter;

    public static HallRosterFragment newInstance(String hallName, String floorName) {
        HallRosterFragment fragment = new HallRosterFragment();
        Bundle args = new Bundle();
        args.putString(ConfigKeys.HALL, hallName);
        args.putString(ConfigKeys.FLOOR, floorName);
        fragment.setArguments(args);
        return fragment;
    }

    public HallRosterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            floorName = savedInstanceState.getString(ConfigKeys.FLOOR, null);
        } else if (getArguments() != null) {
            floorName = getArguments().getString(ConfigKeys.FLOOR, null);
        }
        if (mListener != null) {
            hall = mListener.getHall();
            Floor floor = hall.getFloor(floorName);
            if (floor != null) {
                rooms = floor.getRooms();
            }
        }

        mAdapter = new FloorRosterArrayAdapter(getActivity(), android.R.id.text1, rooms);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hall_roster, container, false);
        AbsListView listView = (AbsListView) view.findViewById(android.R.id.list);
        listView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (HallRosterListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement HallRosterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Hall getHall() {
        return hall;
    }

    @Override
    public int getCurrentFloorIndex() {
        return floorIndex;
    }

    @Override
    public void setCurrentFloorIndex(int index) {
        floorIndex = index;
    }

    public interface HallRosterListener {
        public Hall getHall();
    }

}

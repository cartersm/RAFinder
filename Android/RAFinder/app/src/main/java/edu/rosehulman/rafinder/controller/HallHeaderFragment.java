package edu.rosehulman.rafinder.controller;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.model.Hall;

/**
 * A Single listing for a Residence Hall, in the search list
 */
public class HallHeaderFragment extends Fragment {
    private HallHeaderFragmentListener mListener;

    public HallHeaderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hall_header, container, false);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);

        if (mListener == null) {
            return view;
        }

        Hall hall = mListener.getHall();
        int currentFloor = mListener.getCurrentFloorIndex();
        String[] floorNumbers = hall.getFloorNumbers();
        for (int i = 0; i < floorNumbers.length; i++) {
            final int floorNum = i;
            ToggleButton b = new ToggleButton(getActivity());
            b.setText(floorNumbers[i]);
            b.setPadding(5, 5, 5, 5);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.setCurrentFloorIndex(floorNum);
                }
            });
            b.setVisibility(View.VISIBLE);
            if (i == currentFloor) {
                b.setChecked(true);
            } else {
                b.setChecked(false);
            }
            layout.addView(b);
        }
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (HallHeaderFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement HallHeaderFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface HallHeaderFragmentListener {
        public Hall getHall();
        public int getCurrentFloorIndex();
        public void setCurrentFloorIndex(int i);
    }

}

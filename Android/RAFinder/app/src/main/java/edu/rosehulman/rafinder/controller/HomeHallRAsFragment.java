package edu.rosehulman.rafinder.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ToggleButton;

import java.util.List;

import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.adapter.EmployeeListArrayAdapter;
import edu.rosehulman.rafinder.model.person.Employee;

public class HomeHallRAsFragment extends HomeSubsectionFragment
        implements EmployeeListArrayAdapter.EmployeeListArrayAdapterListener {

    private HomeMyHallRAsListener mListener;

    public static HomeHallRAsFragment newInstance() {
        return new HomeHallRAsFragment();
    }

    public HomeHallRAsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_hall_ras, container, false);
        final ListView listView = (ListView) view.findViewById(R.id.myHallRAsFragment);

        List<Employee> hallRAs = mListener.getMyHallRAs();
        EmployeeListArrayAdapter mAdapter2 = new EmployeeListArrayAdapter(
                getActivity(),
                R.layout.fragment_home,
                hallRAs,
                this);
        listView.setAdapter(mAdapter2);
        HomeSubsectionFragment.setListViewHeightBasedOnChildren(listView);

        setToggleButtonListener(listView, (ToggleButton) view.findViewById(R.id.myHallExpander));
        return view;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (HomeMyHallRAsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement HomeMyHallRAsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void switchToProfile(Employee employee) {
        mListener.switchToProfile(employee);
    }

    public interface HomeMyHallRAsListener extends HomeSubsectionListener {
        List<Employee> getMyHallRAs();
    }

}

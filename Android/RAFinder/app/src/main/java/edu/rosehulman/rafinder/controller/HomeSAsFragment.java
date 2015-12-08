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

public class HomeSAsFragment extends HomeSubsectionFragment
        implements EmployeeListArrayAdapter.EmployeeListArrayAdapterListener {

    private HomeMySAListener mListener;

    public static HomeSAsFragment newInstance() {
        return new HomeSAsFragment();
    }

    public HomeSAsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_sas, container, false);
        final ListView listView = (ListView) view.findViewById(R.id.mySAsView);

        List<Employee> hallSAs = mListener.getMySAs();
        EmployeeListArrayAdapter mAdapter2 = new EmployeeListArrayAdapter(
                getActivity(),
                R.layout.fragment_home,
                hallSAs,
                this);
        listView.setAdapter(mAdapter2);
        HomeSubsectionFragment.setListViewHeightBasedOnChildren(listView);

        setToggleButtonListener(listView, (ToggleButton) view.findViewById(R.id.mySAExpander));
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (HomeMySAListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement HomeMySAListener");
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

    public interface HomeMySAListener extends HomeSubsectionListener {
        public List<Employee> getMySAs();
    }
}

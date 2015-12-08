package edu.rosehulman.rafinder.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.adapter.EmployeeListArrayAdapter;
import edu.rosehulman.rafinder.model.person.Employee;

public class HomeRAFragment extends HomeSubsectionFragment {
    private HomeMyRAListener mListener;

    public static HomeRAFragment newInstance() {
        return new HomeRAFragment();
    }

    public HomeRAFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_ra, container, false);
        final ListView listView = (ListView) view.findViewById(R.id.myRAView);

        List<Employee> myRAs = mListener.getMyRAs();
        if (myRAs.size() > 1) {
            ((TextView) view.findViewById(R.id.titleTextView)).setText(getString(R.string.my_ras));
        }
        EmployeeListArrayAdapter mAdapter2 = new EmployeeListArrayAdapter(
                getActivity(),
                R.layout.fragment_home,
                myRAs,
                this);
        listView.setAdapter(mAdapter2);
        HomeSubsectionFragment.setListViewHeightBasedOnChildren(listView);

        setToggleButtonListener(listView, (ToggleButton) view.findViewById(R.id.myRAExpander));
        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (HomeMyRAListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement HomeMyRAListener");
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

    public interface HomeMyRAListener extends HomeSubsectionListener {
        public List<Employee> getMyRAs();
    }

}

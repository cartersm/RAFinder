package edu.rosehulman.rafinder.controller;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import edu.rosehulman.rafinder.MainActivity;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.adapter.EmployeeListAdapter;
import edu.rosehulman.rafinder.model.EmployeeList;
import edu.rosehulman.rafinder.model.person.Employee;

/**
 * The Home Page.
 */
public class HomeFragment extends Fragment {
    private HomeListener mListener;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.employeesListView);
        EmployeeList employees = new EmployeeList((MainActivity) view.getContext());
        EmployeeListAdapter adapter = new EmployeeListAdapter(view.getContext(), employees);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (HomeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement HomeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface HomeListener {
        public void switchToProfile(Employee res);

    }

}

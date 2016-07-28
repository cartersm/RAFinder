package edu.rosehulman.rafinder.controller;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Spinner;

import java.util.List;

import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.UserType;
import edu.rosehulman.rafinder.adapter.EmployeeListAdapter;
import edu.rosehulman.rafinder.model.EmployeeList;
import edu.rosehulman.rafinder.model.person.Employee;

/**
 * The Home Page.
 */
public class HomeFragment extends Fragment {
    private HomeListener mListener;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Spinner hallSelectionSpinner = (Spinner) view.findViewById(R.id.hallSelectionSpinner);
        List<String> hallNames = mListener.getHallNames();
        ArrayAdapter<String> hallNameAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                hallNames);
        hallSelectionSpinner.setAdapter(hallNameAdapter);

        ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.employeesListView);
        EmployeeList employees = new EmployeeList(mListener, mListener.getHallName());
        EmployeeListAdapter employeeListAdapter = new EmployeeListAdapter(view.getContext(), employees);
        listView.setAdapter(employeeListAdapter);

        if (mListener.getUserType() != UserType.ADMINISTRATOR) {
            hallSelectionSpinner.setSelection(hallNames.indexOf(mListener.getHallName()));
        }

        hallSelectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String hall = hallSelectionSpinner.getItemAtPosition(i).toString();
                EmployeeList newEmployees = new EmployeeList(mListener, hall);
                employeeListAdapter.setData(newEmployees);
                employeeListAdapter.notifyDataSetChanged();
                listView.setAdapter(employeeListAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // ignored
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (HomeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement HomeListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface HomeListener {
        void switchToProfile(Employee employee);

        String getString(int id);

        List<String> getHallNames();

        UserType getUserType();

        Employee getUser();

        List<Employee> getMyRAs();

        String getHallName();

        List<Employee> getRAsForHall(String hallName);

        List<Employee> getSAsForHall(String hallName);
    }

}

package edu.rosehulman.rafinder.controller;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.adapter.SearchResultArrayAdapter;
import edu.rosehulman.rafinder.model.person.Employee;

/**
 * The search box and result list.
 */
public class SearchFragment extends Fragment {
    private SearchFragmentListener mListener;
    private List<Employee> mItems = new ArrayList<>();
    private ListAdapter mAdapter;

    public SearchFragment() {
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (SearchFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement SearchFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SearchResultArrayAdapter(getActivity(), R.layout.layout_search_item, mItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        EditText searchField = (EditText) view.findViewById(R.id.searchField);

        ListView listView = (ListView) view.findViewById(R.id.searchResultsFragment);
        listView.setAdapter(mAdapter);

        ImageButton searchButton = (ImageButton) view.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            mItems = mListener.getEmployeesForName(searchField.getText().toString());
            ((SearchResultArrayAdapter) mAdapter).refresh(mItems);
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface SearchFragmentListener {
        List<Employee> getEmployeesForName(String name);
    }
}

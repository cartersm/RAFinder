package edu.rosehulman.rafinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.model.person.Employee;

public class EmployeeListArrayAdapter extends ArrayAdapter<Employee> {
    private final Context mContext;
    private final List<Employee> mObjects;
    private final int mLayout;
    private final EmployeeListArrayAdapterListener mListener;

    public EmployeeListArrayAdapter(Context context,
                                    int textViewResourceId,
                                    List<Employee> objects,
                                    EmployeeListArrayAdapterListener callbacks) {
        super(context, R.layout.layout_ra_item, textViewResourceId, objects);
        mLayout = R.layout.layout_ra_item;
        mContext = context;
        mObjects = objects;
        mListener = callbacks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView != null ? convertView : inflater.inflate(mLayout, parent, false);
        TextView nameTextView = (TextView) view.findViewById(R.id.myRATextView);
        TextView statusTextView = (TextView) view.findViewById(R.id.status);
        ImageButton button = (ImageButton) view.findViewById(R.id.raMoreDetails);

        final Employee employee = mObjects.get(position);
        nameTextView.setText(employee.getName());
        statusTextView.setText(employee.getStatus());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.switchToProfile(employee);
            }
        });
        view.refreshDrawableState();
        return view;
    }

    public interface EmployeeListArrayAdapterListener {
        public void switchToProfile(Employee employee);
    }
}

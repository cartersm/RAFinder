package edu.rosehulman.rafinder.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import edu.rosehulman.rafinder.MainActivity;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.model.EmployeeList;
import edu.rosehulman.rafinder.model.person.Employee;

/**
 * An expandable list adapter for various employee types.
 */
public class EmployeeListAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private EmployeeList mEmployees;

    public EmployeeListAdapter(Context context, EmployeeList employees) {
        this.mContext = context;
        this.mEmployees = employees;
    }

    @Override
    public int getGroupCount() {
        return mEmployees.getEmployees().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mEmployees.getEmployees().get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mEmployees.getEmployees().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mEmployees.getEmployees().get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastItem, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView != null ?
                    convertView :
                    inflater.inflate(android.R.layout.simple_expandable_list_item_2, null);

        EmployeeList.EmployeeSubList employees = mEmployees.getEmployees().get(groupPosition);
        ((TextView) view.findViewById(android.R.id.text1)).setText(employees.getKey());

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView != null ? convertView : inflater.inflate(R.layout.layout_ra_item, null);

        final Employee employee = mEmployees.getEmployees().get(groupPosition).get(childPosition);

        ImageView imageView = (ImageView) view.findViewById(R.id.RAImageView);
        Bitmap profilePicture = employee.getProfilePictureAsBitmap();
        if (profilePicture != null) {
            imageView.setImageBitmap(profilePicture);
        } else {
            imageView.setImageBitmap(BitmapFactory.decodeResource(view.getResources(), R.drawable.ic_person));
        }
        ((TextView) view.findViewById(R.id.myRATextView)).setText(employee.getName());
        ((TextView) view.findViewById(R.id.status)).setText(mContext.getString(R.string.status_format, employee.getStatus()));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mContext).switchToProfile(employee);
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setData(EmployeeList newEmployees) {
        mEmployees = newEmployees;
    }
}

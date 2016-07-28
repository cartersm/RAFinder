package edu.rosehulman.rafinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.controller.HomeFragment;
import edu.rosehulman.rafinder.model.person.Employee;

/**
 * An Array Adapter for search results.
 */
public class SearchResultArrayAdapter extends ArrayAdapter<Employee> {
    private final Context mContext;
    private final List<Employee> mObjects;
    private final int mLayout;

    public SearchResultArrayAdapter(Context context, int resource, List<Employee> objects) {
        super(context, resource, objects);
        mLayout = resource;
        mContext = context;
        mObjects = objects;
    }

    public void refresh(List<Employee> newResults) {
        mObjects.clear();
        mObjects.addAll(newResults);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = (convertView != null) ? convertView : inflater.inflate(mLayout, parent, false);

        ImageView typeIcon = (ImageView) view.findViewById(R.id.typeIcon);
        TextView nameText = (TextView) view.findViewById(R.id.nameTextView);
        TextView roomText = (TextView) view.findViewById(R.id.roomTextView);
        TextView statusText = (TextView) view.findViewById(R.id.dutyStatusTextView);

        Employee employee = mObjects.get(position);
        nameText.setText(employee.getName());
        roomText.setText(mContext.getString(R.string.profile_room_format, employee.getHall(), employee.getRoom()));
        statusText.setText(employee.getStatus());
        typeIcon.setImageBitmap(employee.getProfilePictureAsBitmap());
        view.setOnClickListener(v -> ((HomeFragment.HomeListener) getContext()).switchToProfile(employee));

        view.refreshDrawableState();
        return view;
    }
}

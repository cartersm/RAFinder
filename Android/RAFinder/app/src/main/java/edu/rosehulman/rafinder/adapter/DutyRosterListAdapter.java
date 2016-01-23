package edu.rosehulman.rafinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.joda.time.LocalDate;

import edu.rosehulman.rafinder.MainActivity;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.model.DutyRoster;
import edu.rosehulman.rafinder.model.DutyRosterItem;

public class DutyRosterListAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private DutyRoster mRoster;

    public DutyRosterListAdapter(Context context, DutyRoster roster) {
        mContext = context;
        mRoster = roster;
    }

    @Override
    public int getGroupCount() {
        return mRoster.getRoster().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mRoster.getRosterAsList().get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mRoster.getRosterAsList().get(groupPosition);
    }

    @Override
    public DutyRosterItem getChild(int groupPosition, int childPosition) {
        return mRoster.getRosterAsList().get(groupPosition).get(childPosition);
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
                    inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);

        LocalDate date = mRoster.getRosterDatesAsList().get(groupPosition);
        String dateString = date.toString("EEE, MMM dd, yyyy");

        ((TextView) view.findViewById(android.R.id.text1)).setText(dateString);

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView != null ? convertView : inflater.inflate(R.layout.fragment_duty_roster_item, null);

        final DutyRosterItem item = mRoster.getRosterAsList().get(groupPosition).get(childPosition);

        ((TextView) view.findViewById(R.id.hallTextView)).setText(item.getHall());
        ((TextView) view.findViewById(R.id.nameTextView)).setText(item.getName());

        view.findViewById(R.id.callButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).dialPhoneNumber(item.getPhoneNumber());
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mContext).switchToProfile(item.getRa());
            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

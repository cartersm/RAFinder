package edu.rosehulman.rafinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;

import edu.rosehulman.rafinder.ExpandableHeightGridView;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.model.reshall.Floor;
import edu.rosehulman.rafinder.model.reshall.ResHall;
import edu.rosehulman.rafinder.model.reshall.Room;

/**
 * An Expandable List Adapter for the Hall Roster.
 */
public class HallRosterListAdapter extends BaseExpandableListAdapter {
    private final Context mContext;
    private final List<ResHall> mHalls;

    public HallRosterListAdapter(Context context, List<ResHall> halls) {
        mContext = context;
        mHalls = halls;
    }

    @Override
    public int getGroupCount() {
        return mHalls.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mHalls.get(groupPosition).getFloors().size();
    }

    @Override
    public ResHall getGroup(int groupPosition) {
        return mHalls.get(groupPosition);
    }

    @Override
    public Floor getChild(int groupPosition, int childPosition) {
        return mHalls.get(groupPosition).getFloors().get(childPosition);
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

        ((TextView) view.findViewById(android.R.id.text1)).setText(mHalls.get(groupPosition).getHall());

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView != null ?
                convertView :
                inflater.inflate(R.layout.layout_floor_roster, null);

        Floor floor = mHalls.get(groupPosition).getFloors().get(childPosition);

        String floorString = floor.getFloor() + " Floor";
        ((TextView) view.findViewById(R.id.floorTextView)).setText(floorString);

        List<Room> rooms = floor.getRooms();
        ExpandableHeightGridView roomList = (ExpandableHeightGridView) view.findViewById(R.id.roomList);
        roomList.setExpanded(true);
        roomList.setAdapter(new FloorRosterArrayAdapter(mContext, android.R.id.text1, rooms));

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}

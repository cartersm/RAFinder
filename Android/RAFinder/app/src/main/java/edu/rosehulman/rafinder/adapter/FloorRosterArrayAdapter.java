package edu.rosehulman.rafinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.model.reshall.Resident;
import edu.rosehulman.rafinder.model.reshall.Room;

/**
 * An Array Adapter for the Rooms on one floor.
 */
class FloorRosterArrayAdapter extends ArrayAdapter<Room> {
    private static final int MAX_ROOMMATES = 3;
    private final Context mContext;
    private final int mLayout;

    public FloorRosterArrayAdapter(Context context, int textViewResourceId, List<Room> objects) {
        super(context, R.layout.layout_room_entry, textViewResourceId, objects);
        mLayout = R.layout.layout_room_entry;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView != null ? convertView : inflater.inflate(mLayout, parent, false);
        TextView roomNumberTextView = (TextView) view.findViewById(R.id.roomNumberTextView);

        TextView[] roommates = {
                (TextView) view.findViewById(R.id.roommate1),
                (TextView) view.findViewById(R.id.roommate2),
                (TextView) view.findViewById(R.id.roommate3)
        };
        Room item = super.getItem(position);
        final List<Resident> residents = item.getResidents();
        int numResidents = residents.size();
        for (int i = 0; i < MAX_ROOMMATES; i++) {
            if (numResidents <= i) {
                roommates[i].setVisibility(View.INVISIBLE);
            } else {
                roommates[i].setText(residents.get(i).getName());
                if (residents.get(i).getType().equals("Resident Assistant")) {
                    roommates[i].setTextColor(mContext.getResources().getColor(R.color.red));
                    roommates[i].setText(roommates[i].getText() + " (RA)");
                } else if (residents.get(i).getType().equals("Sophomore Advisor")) {
                    roommates[i].setTextColor(mContext.getResources().getColor(R.color.blue));
                    roommates[i].setText(roommates[i].getText() + " (SA)");
                } else if (residents.get(i).getType().equals("Graduate Assistant")) {
                    roommates[i].setTextColor(mContext.getResources().getColor(R.color.green));
                    roommates[i].setText(roommates[i].getText() + " (GA)");
                }
            }
        }

        roomNumberTextView.setText(item.getNumber());
        view.refreshDrawableState();
        return view;
    }
}

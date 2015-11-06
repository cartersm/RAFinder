package edu.rosehulman.rafinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.model.RoomEntry;
import edu.rosehulman.rafinder.model.person.GraduateAssistant;
import edu.rosehulman.rafinder.model.person.Resident;
import edu.rosehulman.rafinder.model.person.ResidentAssistant;
import edu.rosehulman.rafinder.model.person.SophomoreAdvisor;

public class FloorRosterArrayAdapter extends ArrayAdapter<RoomEntry> {
    public static final int MAX_ROOMMATES = 5;
    private final Context mContext;
    private final int mLayout;

    public FloorRosterArrayAdapter(Context context, int textViewResourceId, List<RoomEntry> objects) {
        super(context, R.layout.layout_floor_roster_item, textViewResourceId, objects);
        mLayout = R.layout.layout_floor_roster_item;
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
                (TextView) view.findViewById(R.id.roommate3),
                (TextView) view.findViewById(R.id.roommate4),
                (TextView) view.findViewById(R.id.roommate5)
        };
        RoomEntry item = super.getItem(position);
        if (item instanceof RoomEntry.Lobby) {
            for (TextView textView : roommates) {
                textView.setVisibility(View.INVISIBLE);
            }
            roomNumberTextView.setTextSize(42);
        } else {
            final Resident[] residents = item.getResidents();
            int numResidents = residents.length;
            for (int i = 0; i < MAX_ROOMMATES; i++) {
                if (numResidents <= i) {
                    roommates[i].setVisibility(View.INVISIBLE);
                } else {
                    roommates[i].setText(residents[i].getName());
                    if (residents[i] instanceof ResidentAssistant) {
                        roommates[i].setTextColor(mContext.getResources().getColor(R.color.red));
                        roommates[i].setText(roommates[i].getText() + " (RA)");
                    } else if (residents[i] instanceof SophomoreAdvisor) {
                        roommates[i].setTextColor(mContext.getResources().getColor(R.color.blue));
                        roommates[i].setText(roommates[i].getText() + " (SA)");
                    } else if (residents[i] instanceof GraduateAssistant) {
                        roommates[i].setTextColor(mContext.getResources().getColor(R.color.green));
                        roommates[i].setText(roommates[i].getText() + " (GA)");
                    }
                }
            }
        }

        roomNumberTextView.setText(item.getRoomNumber());
        view.refreshDrawableState();
        return view;
    }
}

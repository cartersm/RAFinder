package edu.rosehulman.rafinder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.util.List;

import edu.rosehulman.rafinder.MainActivity;
import edu.rosehulman.rafinder.R;
import edu.rosehulman.rafinder.model.DutyRosterItem;
import edu.rosehulman.rafinder.model.person.Employee;

public class DutyRosterArrayAdapter extends ArrayAdapter<DutyRosterItem> {
    private final Context mContext;
    private final int mLayout;
    private boolean mIsEditable = false;
    private final DutyRosterAAListener mListener;

    public DutyRosterArrayAdapter(Context context,
                                  int textViewResourceId,
                                  List<DutyRosterItem> objects,
                                  DutyRosterAAListener listener) {
        super(context, R.layout.fragment_duty_roster_widget, textViewResourceId, objects);
        mListener = listener;
        mLayout = R.layout.fragment_duty_roster_widget;
        mContext = context;
    }

    public void setEditable(boolean isEditable) {
        mIsEditable = isEditable;
        notifyDataSetChanged();
    }

    /**
     * This method overrides the default getView method to show a two line view that has a due date. It also controls
     * the displaying of icons such as flags and trophies.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView != null ? convertView : inflater.inflate(mLayout, parent, false);
        TextView weekendText = (TextView) view.findViewById(R.id.weekendTextView);
        TextView friNameText = (TextView) view.findViewById(R.id.friNameTextView);
        ImageButton friButton = (ImageButton) view.findViewById(R.id.friCallButton);
        TextView satNameText = (TextView) view.findViewById(R.id.satNameTextView);
        ImageButton satButton = (ImageButton) view.findViewById(R.id.satCallButton);
        ImageButton editButton = (ImageButton) view.findViewById(R.id.editButton);
        final DutyRosterItem item = super.getItem(position);
        final Employee fridayDuty = item.getFriDuty();
        final Employee saturdayDuty = item.getSatDuty();
        final LocalDate friday = item.getFriday();
        if (mIsEditable) {
            editButton.setVisibility(View.VISIBLE);
            editButton.setBackgroundResource(R.mipmap.ic_edit);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.showEditDialog(item);
                }
            });
        }
        LocalDate sunday = friday.plusDays(2);
        // TODO: extract
        String weekend = friday.dayOfWeek().getAsShortText() + " " +
                         friday.toString("M/d") + " - " +
                         sunday.dayOfWeek().getAsShortText() + " " +
                         sunday.toString("M/d");
        weekendText.setText(weekend);

        friNameText.setText(fridayDuty.getName());
        friNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mContext).switchToProfile(fridayDuty);
            }
        });
        friButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).dialPhoneNumber(fridayDuty.getPhoneNumber());
            }
        });
        satNameText.setText(saturdayDuty.getName());
        satButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).dialPhoneNumber(saturdayDuty.getPhoneNumber());
            }
        });
        satNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mContext).switchToProfile(saturdayDuty);
            }
        });
        view.refreshDrawableState();
        return view;
    }

    public interface DutyRosterAAListener {
        public void showEditDialog(DutyRosterItem item);
    }

}

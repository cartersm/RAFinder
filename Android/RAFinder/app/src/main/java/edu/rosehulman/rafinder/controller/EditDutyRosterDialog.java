package edu.rosehulman.rafinder.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.joda.time.LocalDate;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.R;

public class EditDutyRosterDialog extends DialogFragment {
    private EditDutyRosterDialogListener mListener;
    private LocalDate friday;
    private String friDuty;
    private String satDuty;
    private EditText fri;
    private EditText sat;

    public static EditDutyRosterDialog newInstance(LocalDate friday, String friDuty, String satDuty) {
        EditDutyRosterDialog fragment = new EditDutyRosterDialog();
        Bundle args = new Bundle();
        args.putString("friday", friday.toString(ConfigKeys.formatter));
        args.putString("friDuty", friDuty);
        args.putString("satDuty", satDuty);
        fragment.setArguments(args);
        return fragment;
    }

    public EditDutyRosterDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            friday = LocalDate.parse(getArguments().getString("friday", null), ConfigKeys.formatter);
            friDuty = getArguments().getString("friDuty");
            satDuty = getArguments().getString("satDuty");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_duty_roster, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(createView(view));
        return builder.setIcon(R.drawable.ic_phone)
                .setTitle(getResources().getString(R.string.edit_duty_roster))
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mListener.onEditConfirm(fri.getText().toString(), sat.getText().toString());
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .create();

    }

    private View createView(View view) {
        String date = friday.toString(ConfigKeys.formatter) + " - ";
        LocalDate d2 = friday.plusDays(2);
        date += d2.toString(ConfigKeys.formatter);
        TextView weekend = (TextView) view.findViewById(R.id.weekendLabel);
        weekend.setText(date);
        fri = (EditText) view.findViewById(R.id.friField);
        fri.setText(friDuty);
        sat = (EditText) view.findViewById(R.id.satField);
        sat.setText(satDuty);
        return view;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (EditDutyRosterDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement EditDutyRosterDialogListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface EditDutyRosterDialogListener {
        public void onEditConfirm(String fri, String sat);
    }
}

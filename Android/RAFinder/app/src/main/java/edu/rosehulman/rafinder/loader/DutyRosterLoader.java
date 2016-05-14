package edu.rosehulman.rafinder.loader;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.List;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.model.DutyRoster;
import edu.rosehulman.rafinder.model.person.Employee;

/**
 * A loader for duty rosters.
 */
public class DutyRosterLoader extends Loader {
    private static final String DUTY_ROSTERS = "DutyRosters";
    private final List<Employee> mRAs;
    private final boolean mIsEdit;
    private final LoaderListener mListener;
    private DutyRoster mRoster;
    private LocalDate mDate;

    public DutyRosterLoader(LoaderListener listener, List<Employee> ras, boolean isEdit) {
        mListener = listener;
        mRAs = ras;
        mIsEdit = isEdit;

        loadData(new Firebase(ConfigKeys.FIREBASE_ROOT_URL + "/" + DUTY_ROSTERS));
    }

    protected void loadData(Firebase firebaseRef) {
        Log.d(ConfigKeys.LOG_TAG, "Loading Duty Roster data...");
        firebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRoster = new DutyRoster(dataSnapshot, modifyDate(LocalDate.now()), mRAs);
                Log.d(ConfigKeys.LOG_TAG, "Finished loading Duty Roster data.");
                mListener.onDutyRosterLoadingComplete(mIsEdit);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // ignored
            }
        });
    }

    private LocalDate modifyDate(LocalDate dt) {
        if (dt.getDayOfWeek() == DateTimeConstants.FRIDAY) {
            dt = dt.minusDays(1);
        } else if (dt.getDayOfWeek() == DateTimeConstants.SATURDAY) {
            dt = dt.minusDays(2);
        } else if (dt.getDayOfWeek() == DateTimeConstants.SUNDAY) {
            dt = dt.minusDays(3);
        }
        mDate = dt;
        return dt;
    }

    public LocalDate getDate() {
        return mDate;
    }

    public DutyRoster getDutyRoster() {
        return mRoster;
    }

}

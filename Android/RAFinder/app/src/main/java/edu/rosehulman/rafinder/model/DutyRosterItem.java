package edu.rosehulman.rafinder.model;

import com.firebase.client.DataSnapshot;

import org.joda.time.LocalDate;

import java.util.List;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.model.person.Employee;
import edu.rosehulman.rafinder.model.person.ResidentAssistant;

/**
 * A single item in the Duty Roster.
 */
@SuppressWarnings("unused")
public class DutyRosterItem {
    // DutyRosterItem Firebase keys
    public static final String fridayKey = "friday";
    public static final String saturdayKey = "saturday";
    private final List<Employee> ras;
    private final String url;
    private Employee friDuty;
    private Employee satDuty;
    private LocalDate friday;

    public DutyRosterItem(DataSnapshot ds, List<Employee> ras) {
        url = ConfigKeys.FIREBASE_ROOT_URL + ds.getRef().getPath().toString();
        friday = LocalDate.parse(ds.getKey(), ConfigKeys.formatter);
        this.ras = ras;
        for (DataSnapshot child : ds.getChildren()) {
            if (child.getKey().equals(fridayKey)) {
                friDuty = getRA(child.getValue(String.class));
            } else if (child.getKey().equals(saturdayKey)) {
                satDuty = getRA(child.getValue(String.class));
            }
        }
    }

    public String getURL() {
        return url;
    }

    private Employee getRA(String uid) {
        for (Employee ra : ras) {
            if (ra.getUid().equals(uid)) {
                return ra;
            }
        }
        return new ResidentAssistant("No one", "");
    }

    public Employee getFriDuty() {
        return friDuty;
    }

    public void setFriDuty(Employee friDuty) {
        this.friDuty = friDuty;
    }

    public Employee getSatDuty() {
        return satDuty;
    }

    public void setSatDuty(Employee satDuty) {
        this.satDuty = satDuty;
    }

    public LocalDate getFriday() {
        return friday;
    }

    public void setFriday(LocalDate friday) {
        this.friday = friday;
    }
}




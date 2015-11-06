package edu.rosehulman.rafinder.model;

import com.firebase.client.DataSnapshot;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.model.person.Employee;

public class DutyRoster {
    private final HashMap<LocalDate, DutyRosterItem> roster;

    public DutyRoster(DataSnapshot ds, LocalDate startDate, List<Employee> ras) {
        roster = new HashMap<>();
        for (DataSnapshot child : ds.getChildren()) {
            LocalDate rosterDate = LocalDate.parse(child.getKey(), ConfigKeys.formatter);
            if (!rosterDate.isBefore(startDate)) {
                DutyRosterItem item = new DutyRosterItem(child, ras);
                roster.put(rosterDate, item);
            }
        }
    }

    public Employee getOnDutyNow() {
        LocalDate now = LocalDate.now();
        if (now.getDayOfWeek() != DateTimeConstants.FRIDAY && now.getDayOfWeek() != DateTimeConstants.SATURDAY) {
            return null;
        } else {
            if (now.getDayOfWeek() == DateTimeConstants.SATURDAY) {
                now = now.minusDays(1);
            }
            DutyRosterItem item = roster.get(now);
            if (item == null) {
                return null;
            }
            if (now.getDayOfWeek() == DateTimeConstants.FRIDAY) {
                return item.getFriDuty();
            } else {
                return item.getSatDuty();
            }
        }
    }

    public HashMap<LocalDate, DutyRosterItem> getRoster() {
        return roster;
    }

    public LocalDate getEndDate() {
        Iterator<LocalDate> iter = roster.keySet().iterator();
        LocalDate last = null;
        while (iter.hasNext()) {
            if (last == null) {
                last = iter.next();
            } else {
                LocalDate next = iter.next();
                if (next.isAfter(last)) {
                    last = next;
                }
            }
        }
        return last;
    }
}

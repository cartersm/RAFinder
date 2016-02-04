package edu.rosehulman.rafinder.model;

import com.firebase.client.DataSnapshot;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.model.person.Employee;

public class DutyRoster {
    private final Map<LocalDate, List<DutyRosterItem>> roster;

    public DutyRoster(DataSnapshot ds, LocalDate startDate, List<Employee> ras) {
        this.roster = new HashMap<>();
        for (DataSnapshot rosterSnapshot : ds.getChildren()) {
            LocalDate rosterDate = LocalDate.parse(rosterSnapshot.child("date").getValue(String.class), ConfigKeys.formatter);
//            rosterDate = rosterDate.plusDays(1); // TODO: check me: add one day to correct for UTC error
            if (!rosterDate.isBefore(startDate)) {
                List<DutyRosterItem> items = new ArrayList<>();
                for (DataSnapshot hallSnapshot : rosterSnapshot.child("roster").getChildren()) {
                    DutyRosterItem item = new DutyRosterItem(hallSnapshot, ras);
                    items.add(item);
                }
                this.roster.put(rosterDate, items);
            }
        }
    }

//    public Employee getOnDutyNow() {
//        LocalDate now = LocalDate.now();
//        if (now.getDayOfWeek() != DateTimeConstants.FRIDAY && now.getDayOfWeek() != DateTimeConstants.SATURDAY) {
//            return null;
//        } else {
//            if (now.getDayOfWeek() == DateTimeConstants.SATURDAY) {
//                now = now.minusDays(1);
//            }
//            DutyRosterItem item = roster.get(now);
//            if (item == null) {
//                return null;
//            }
//            if (now.getDayOfWeek() == DateTimeConstants.FRIDAY) {
//                return item.getFriDuty();
//            } else {
//                return item.getSatDuty();
//            }
//        }
//    }

    public Map<LocalDate, List<DutyRosterItem>> getRoster() {
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

    /**
     * Returns the duty roster as a list of lists of duty roster items, sorted by date.
     */
    public List<List<DutyRosterItem>> getRosterAsList() {
        return new ArrayList<>(new TreeMap<>(roster).values());
    }

    /**
     * Returns the duty roster as a list of lists of duty roster items, sorted by date.
     */
    public List<LocalDate> getRosterDatesAsList() {
        ArrayList<LocalDate> localDates = new ArrayList<>(roster.keySet());
        Collections.sort(localDates);
        return localDates;
    }
}

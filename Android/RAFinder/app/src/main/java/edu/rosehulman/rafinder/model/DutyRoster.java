package edu.rosehulman.rafinder.model;

import com.firebase.client.DataSnapshot;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

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
    public static final String DATE = "date";
    public static final String ROSTER = "roster";
    private static final int SEVEN_PM = 19;
    private static final int NOON = 12;
    private static final int EIGHT_AM = 8;
    private final Map<LocalDate, List<DutyRosterItem>> roster;

    public DutyRoster(DataSnapshot ds, LocalDate startDate, List<Employee> ras) {
        this.roster = new HashMap<>();
        for (DataSnapshot rosterSnapshot : ds.getChildren()) {
            LocalDate rosterDate = LocalDate.parse(rosterSnapshot.child(DATE).getValue(String.class), ConfigKeys.formatter);
            if (!rosterDate.isBefore(startDate)) {
                List<DutyRosterItem> items = new ArrayList<>();
                for (DataSnapshot hallSnapshot : rosterSnapshot.child(ROSTER).getChildren()) {
                    DutyRosterItem item = new DutyRosterItem(hallSnapshot, ras);
                    items.add(item);
                }
                this.roster.put(rosterDate, items);
            }
        }
    }

    public List<DutyRosterItem> getOnDutyNow() {
        LocalDateTime now = LocalDateTime.now();
        boolean isWeekend = now.getDayOfWeek() == DateTimeConstants.FRIDAY ||
                            now.getDayOfWeek() == DateTimeConstants.SATURDAY ||
                            now.getDayOfWeek() == DateTimeConstants.SUNDAY;
        if (!isWeekend) {
            return null;
        } else {
            boolean isFriDuty = (now.getDayOfWeek() == DateTimeConstants.FRIDAY && now.getHourOfDay() >= SEVEN_PM) ||
                                (now.getDayOfWeek() == DateTimeConstants.SATURDAY && now.getHourOfDay() < NOON);
            boolean isSatDuty = (now.getDayOfWeek() == DateTimeConstants.SATURDAY && now.getHourOfDay() >= NOON) ||
                                (now.getDayOfWeek() == DateTimeConstants.SUNDAY && now.getHourOfDay() < EIGHT_AM);

            if ((now.getDayOfWeek() == DateTimeConstants.SATURDAY && isFriDuty) ||
                (now.getDayOfWeek() == DateTimeConstants.SUNDAY && isSatDuty)) {
                // we're on the latter day of the duty shift
                now = now.minusDays(1);
            }
            List<DutyRosterItem> items = roster.get(now.toLocalDate());
            if (items == null || items.isEmpty()) {
                return null;
            }
            return items;
        }
    }

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

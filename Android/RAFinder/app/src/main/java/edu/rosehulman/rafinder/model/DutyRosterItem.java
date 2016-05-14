package edu.rosehulman.rafinder.model;

import com.firebase.client.DataSnapshot;

import java.util.List;

import edu.rosehulman.rafinder.model.person.Employee;

/**
 * A single item in the Duty Roster.
 */
public class DutyRosterItem {
    private Employee ra;
    private String hall;
    private String email;
    private String name;
    private String phoneNumber;
    private String uid;

    // CONSIDER whether we can use a POJO here
    public DutyRosterItem(DataSnapshot ds, List<Employee> ras) {
        this.hall = ds.child("hall").getValue(String.class);
        this.email = ds.child("email").getValue(String.class);
        this.name = ds.child("name").getValue(String.class);
        this.phoneNumber = ds.child("phoneNumber").getValue(String.class);
        this.uid = ds.child("uid").getValue(String.class);

        for (Employee ra : ras) {
            if (ra.getUid().equals(this.uid)) {
                this.ra = ra;
                break;
            }
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Employee getRa() {
        return ra;
    }

    public void setRa(Employee ra) {
        this.ra = ra;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }
}




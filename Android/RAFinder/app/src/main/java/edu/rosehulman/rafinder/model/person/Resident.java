package edu.rosehulman.rafinder.model.person;

import com.firebase.client.DataSnapshot;

import edu.rosehulman.rafinder.ConfigKeys;

@SuppressWarnings("unused")
public class Resident {
    private String name;
    private String uid;

    Resident() {
    }

    Resident(String name, String uid) {
        this.name = name;
        this.uid = uid;
    }

    public Resident(DataSnapshot ds) {
        name = ds.child(ConfigKeys.employeeName).getValue(String.class);
        uid = ds.getKey();
    }

    public Resident(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

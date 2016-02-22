package edu.rosehulman.rafinder.model.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.DataSnapshot;

import edu.rosehulman.rafinder.ConfigKeys;

public class Resident {
    private String name;
    @JsonIgnore
    private String uid;

    public Resident() {

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

package edu.rosehulman.rafinder.model.person;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.DataSnapshot;

import edu.rosehulman.rafinder.ConfigKeys;

/**
 * Any authenticated resident or Residence Life Employee.
 */
public class AuthenticatedResident {
    private String name;
    @JsonIgnore
    private String uid;

    AuthenticatedResident() {

    }

    public AuthenticatedResident(DataSnapshot ds) {
        name = ds.child(ConfigKeys.EMPLOYEE_NAME).getValue(String.class);
        uid = ds.getKey();
    }

    AuthenticatedResident(String name) {
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

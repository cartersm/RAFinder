package edu.rosehulman.rafinder.model.person;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Any authenticated resident or Residence Life Employee.
 */
public class AuthenticatedResident {
    private String name;
    @JsonIgnore
    private String uid;

    AuthenticatedResident() {
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

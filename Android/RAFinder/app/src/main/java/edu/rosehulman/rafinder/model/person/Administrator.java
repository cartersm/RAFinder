package edu.rosehulman.rafinder.model.person;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import edu.rosehulman.rafinder.UserType;

/**
 * A Residence Life administrator (an employee of the Office of Residence Life)
 */
@SuppressWarnings("unused")
public class Administrator extends Employee {
    public Administrator(DataSnapshot ds) {
        super(ds);
    }

    public Administrator(String name, String uid) {
        super(name, uid);
    }

    public Administrator(Firebase firebase) {
        super(firebase);
    }

    public Administrator(String name) {
        super(name);
    }

    @Override
    public UserType getEmployeeType() {
        return UserType.ADMINISTRATOR;
    }
}

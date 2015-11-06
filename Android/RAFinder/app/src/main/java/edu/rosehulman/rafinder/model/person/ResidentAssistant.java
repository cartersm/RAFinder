package edu.rosehulman.rafinder.model.person;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import edu.rosehulman.rafinder.UserType;

/**
 * An RA, who has the broadest access to the app.
 */
@SuppressWarnings("unused")
public class ResidentAssistant extends Employee {
    public ResidentAssistant(DataSnapshot ds) {
        super(ds);
    }

    public ResidentAssistant(String name, String uid) {
        super(name, uid);
    }

    public ResidentAssistant(Firebase firebase) {
        super(firebase);
    }

    public ResidentAssistant(String name) {
        super(name);
    }

    @Override
    public UserType getEmployeeType() {
        return UserType.RESIDENT_ASSISTANT;
    }

}

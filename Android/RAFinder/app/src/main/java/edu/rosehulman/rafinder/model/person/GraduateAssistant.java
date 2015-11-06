package edu.rosehulman.rafinder.model.person;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import edu.rosehulman.rafinder.UserType;

/**
 * A GA (Like an RA, but with somewhat limited privileges.
 */
@SuppressWarnings("unused")
public class GraduateAssistant extends Employee {
    public GraduateAssistant(DataSnapshot ds) {
        super(ds);
    }

    public GraduateAssistant(String name, String uid) {
        super(name, uid);
    }

    public GraduateAssistant(Firebase firebase) {
        super(firebase);
    }

    public GraduateAssistant(String name) {
        super(name);
    }

    @Override
    public UserType getEmployeeType() {
        return UserType.GRADUATE_ASSISTANT;
    }
}

package edu.rosehulman.rafinder.model.person;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;

import edu.rosehulman.rafinder.UserType;

/**
 * An SA (like an RA, but with limited privileges).
 */
@SuppressWarnings("unused")
public class SophomoreAdvisor extends Employee {
    public SophomoreAdvisor(DataSnapshot ds) {
        super(ds);
    }

    public SophomoreAdvisor(String name, String uid) {
        super(name, uid);
    }

    public SophomoreAdvisor(Firebase firebase) {
        super(firebase);
    }

    public SophomoreAdvisor(String name) {
        super(name);
    }

    @Override
    public UserType getEmployeeType() {
        return UserType.SOPHOMORE_ADVISOR;
    }
}

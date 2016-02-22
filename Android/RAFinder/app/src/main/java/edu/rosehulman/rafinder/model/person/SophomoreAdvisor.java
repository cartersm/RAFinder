package edu.rosehulman.rafinder.model.person;

import edu.rosehulman.rafinder.UserType;

/**
 * An SA (like an RA, but with limited privileges).
 */
public class SophomoreAdvisor extends Employee {

    public SophomoreAdvisor() {

    }

    public SophomoreAdvisor(String name) {
        super(name);
    }

    public SophomoreAdvisor(String name,
                            String email,
                            int floor,
                            String hall,
                            String phoneNumber,
                            String room,
                            String status,
                            String statusDetail,
                            String profilePicture) {
        super(name, email, floor, hall, phoneNumber, room, status, statusDetail, profilePicture);
    }

    @Override
    public UserType getEmployeeType() {
        return UserType.SOPHOMORE_ADVISOR;
    }
}

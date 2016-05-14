package edu.rosehulman.rafinder.model.person;

import edu.rosehulman.rafinder.UserType;

/**
 * An RA.
 */
public class ResidentAssistant extends Employee {

    public ResidentAssistant() {

    }

    public ResidentAssistant(String name) {
        super(name);
    }

    public ResidentAssistant(String name,
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
        return UserType.RESIDENT_ASSISTANT;
    }

}

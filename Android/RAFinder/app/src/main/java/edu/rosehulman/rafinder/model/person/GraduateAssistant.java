package edu.rosehulman.rafinder.model.person;

import edu.rosehulman.rafinder.UserType;

/**
 * A GA.
 */
@SuppressWarnings("unused") // constructors used by Jackson
public class GraduateAssistant extends Employee {

    public GraduateAssistant() {

    }

    public GraduateAssistant(String name) {
        super(name);
    }

    public GraduateAssistant(String name,
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
        return UserType.GRADUATE_ASSISTANT;
    }
}

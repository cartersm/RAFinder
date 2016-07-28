package edu.rosehulman.rafinder.model.person;

import edu.rosehulman.rafinder.UserType;

/**
 * A Residence Life administrator (an employee of the Office of Residence Life)
 */
@SuppressWarnings("unused") // constructors used by Jackson
public class Administrator extends Employee {

    public Administrator() {

    }

    public Administrator(String name) {
        super(name);
    }

    public Administrator(String name,
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
        return UserType.ADMINISTRATOR;
    }
}

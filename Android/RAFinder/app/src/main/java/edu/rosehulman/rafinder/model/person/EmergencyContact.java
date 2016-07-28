package edu.rosehulman.rafinder.model.person;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Arrays;

import edu.rosehulman.rafinder.UserType;

/**
 * An item in the Emergency Contacts list.
 */
public class EmergencyContact {
    // EmergencyContact Firebase keys
    private static final String EC_EMAIL = "Email";
    private static final String EC_PHONE = "Phone";

    private String name;
    private String email;
    private String phone;
    private UserType userType;
    private Priority priority;

    public EmergencyContact(String firebaseURL) {
        Firebase firebase = new Firebase(firebaseURL);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setName(dataSnapshot.getKey());
                setPriority(Priority.STAFF);
                setUserType(UserType.ADMINISTRATOR);

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals(EC_EMAIL)) {
                        setEmail(child.getValue(String.class));
                    } else if (child.getKey().equals(EC_PHONE)) {
                        setPhone(child.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                // ignored
            }
        });
    }

    public EmergencyContact(Employee employee, boolean isOnDuty) {
        priority = isOnDuty ? Priority.ON_DUTY : Priority.MY_RA;
        userType = employee.getUserType();
        name = employee.getName();
        email = employee.getEmail();
        phone = employee.getPhoneNumber();
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    private void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    public Priority getPriority() {
        return priority;
    }

    private void setPriority(Priority priority) {
        this.priority = priority;
    }

    public UserType getUserType() {
        return userType;
    }

    private void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return Arrays.asList(name,
                email,
                phone,
                userType,
                priority
        ).toString();
    }

    public enum Priority {
        ON_DUTY,
        MY_RA,
        STAFF
    }
}

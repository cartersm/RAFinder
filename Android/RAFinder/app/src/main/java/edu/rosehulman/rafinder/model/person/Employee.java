package edu.rosehulman.rafinder.model.person;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import edu.rosehulman.rafinder.ConfigKeys;
import edu.rosehulman.rafinder.UserType;

/**
 * Any Residence Life employee.
 */
@SuppressWarnings({"MethodOnlyUsedFromInnerClass", "AbstractClassNamingConvention"})
public abstract class Employee extends AuthenticatedResident {
    private String email;
    private int floor;
    private String hall;
    private String phoneNumber;
    private String room;
    private String status;
    private String statusDetail;
    private String profilePicture;
    @JsonIgnore
    private Firebase firebase;
    @JsonIgnore
    private UserType userType;

    Employee() {
        this.userType = getEmployeeType();
    }

    Employee(String name,
             String email,
             int floor,
             String hall,
             String phoneNumber,
             String room,
             String status,
             String statusDetail,
             String profilePicture) {
        super(name);
        this.email = email;
        this.floor = floor;
        this.hall = hall;
        this.phoneNumber = phoneNumber;
        this.room = room;
        this.status = status;
        this.statusDetail = statusDetail;
        this.profilePicture = profilePicture;
        this.userType = getEmployeeType();
    }

    Employee(String name) {
        super(name);
        this.userType = getEmployeeType();
    }

    private static Bitmap convertToBitmap(String image) {
        if (image == null || image.equals("")) {
            // no profile image
            return null;
        }
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            Log.w(ConfigKeys.LOG_TAG, "Error decoding image");
            return null;
        }
    }

    protected abstract UserType getEmployeeType();

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getEmail() {
        return email;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    public int getFloor() {
        return floor;
    }

    private void setFloor(int floor) {
        this.floor = floor;
    }

    public String getHall() {
        return hall;
    }

    private void setHall(String hall) {
        this.hall = hall;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRoom() {
        return room;
    }

    private void setRoom(String room) {
        this.room = room;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public Firebase getFirebase() {
        return firebase;
    }

    public void setFirebase(Firebase firebase) {
        this.firebase = firebase;
        this.firebase.addChildEventListener(new EmployeeChangedListener());
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Bitmap getProfilePictureAsBitmap() {
        return convertToBitmap(this.profilePicture);
    }

    public void setProfilePictureFromBitmap(Bitmap profilePicture) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        profilePicture.compress(Bitmap.CompressFormat.PNG, 100, out);
        byte[] byteArray = out.toByteArray();
        this.profilePicture = Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Employee)) {
            return false;
        }
        Employee e = (Employee) other;
        return getName().equals(e.getName())
                && email.equals(e.getEmail())
                && floor == e.getFloor()
                && hall.equals(e.getHall())
                && phoneNumber.equals(e.getPhoneNumber())
                && room.equals(e.getRoom())
                && status.equals(e.getStatus())
                && statusDetail.equals(e.getStatusDetail())
                && userType == e.getUserType();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + Arrays.asList(
                getUid(),
                getName(),
                email,
                floor,
                hall,
                phoneNumber,
                room,
                status,
                statusDetail,
                userType
        ).toString();
    }

    private class EmployeeChangedListener implements ChildEventListener {
        public void onChildChanged(DataSnapshot arg0, String arg1) {
            switch (arg0.getKey()) {
                case ConfigKeys.EMPLOYEE_EMAIL:
                    setEmail(arg0.getValue(String.class));
                    break;
                case ConfigKeys.EMPLOYEE_FLOOR:
                    setFloor(arg0.getValue(int.class));
                    break;
                case ConfigKeys.EMPLOYEE_HALL:
                    setHall(arg0.getValue(String.class));
                    break;
                case ConfigKeys.EMPLOYEE_PHONE:
                    setPhoneNumber(arg0.getValue(String.class));
                    break;
                case ConfigKeys.EMPLOYEE_ROOM:
                    setRoom(arg0.getValue(String.class));
                    break;
                case ConfigKeys.EMPLOYEE_STATUS:
                    setStatus(arg0.getValue(String.class));
                    break;
                case ConfigKeys.EMPLOYEE_STATUS_DETAIL:
                    setStatusDetail(arg0.getValue(String.class));
                    break;
                case ConfigKeys.EMPLOYEE_NAME:
                    setName(arg0.getValue(String.class));
                    break;
            }
        }

        public void onChildAdded(DataSnapshot arg0, String arg1) {
            // ignored
        }

        public void onChildRemoved(DataSnapshot arg0) {
            // ignored
        }

        public void onChildMoved(DataSnapshot arg0, String arg1) {
            // ignored
        }

        public void onCancelled(FirebaseError arg0) {
            // ignored
        }
    }
}

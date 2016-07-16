package edu.rosehulman.rafinder.model.person;

import android.os.Parcel;
import android.os.Parcelable;

import edu.rosehulman.rafinder.UserType;

/**
 * An RA.
 */
public class ResidentAssistant extends Employee implements Parcelable {

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

    protected ResidentAssistant(Parcel in) {
        super(in.readString(),
                in.readString(),
                in.readInt(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString());
    }

    public static final Creator<ResidentAssistant> CREATOR = new Creator<ResidentAssistant>() {
        @Override
        public ResidentAssistant createFromParcel(Parcel in) {
            return new ResidentAssistant(in);
        }

        @Override
        public ResidentAssistant[] newArray(int size) {
            return new ResidentAssistant[size];
        }
    };

    @Override
    public UserType getEmployeeType() {
        return UserType.RESIDENT_ASSISTANT;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // marshall in order of 9-arg constructor
        dest.writeString(getName());
        dest.writeString(getEmail());
        dest.writeInt(getFloor());
        dest.writeString(getHall());
        dest.writeString(getPhoneNumber());
        dest.writeString(getRoom());
        dest.writeString(getStatus());
        dest.writeString(getStatusDetail());
        dest.writeString(getProfilePicture());
    }
}

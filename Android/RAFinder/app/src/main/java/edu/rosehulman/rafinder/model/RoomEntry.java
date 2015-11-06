package edu.rosehulman.rafinder.model;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.rosehulman.rafinder.controller.HallRosterFragment;
import edu.rosehulman.rafinder.model.person.GraduateAssistant;
import edu.rosehulman.rafinder.model.person.Resident;
import edu.rosehulman.rafinder.model.person.ResidentAssistant;
import edu.rosehulman.rafinder.model.person.SophomoreAdvisor;

/**
 * An entry for one room of the {@link HallRosterFragment}.
 */
@SuppressWarnings("unused")
public class RoomEntry {
    private static final String RESIDENT_ASSISTANT = "Resident Assistant";
    private static final String SOPHOMORE_ADVISOR = "Sophomore Advisor";
    private static final String GRADUATE_ASSISTANT = "Graduate Assistant";
    private static final String RESIDENT = "Resident";

    private List<Resident> residents;
    private String hallName;
    private String roomNumber;

    public RoomEntry(DataSnapshot ds, String hallName) {
        this.hallName = hallName;
        roomNumber = ds.getKey();
        residents = new ArrayList<>();
        for (DataSnapshot child : ds.getChildren()) {
            switch (child.getValue(String.class)) {
            case RESIDENT:
                residents.add(new Resident(child.getKey()));
                break;
            case SOPHOMORE_ADVISOR:
                residents.add(new SophomoreAdvisor(child.getKey()));
                break;
            case RESIDENT_ASSISTANT:
                residents.add(new ResidentAssistant(child.getKey()));
                break;
            case GRADUATE_ASSISTANT:
                residents.add(new GraduateAssistant(child.getKey()));
                break;
            default:
                break;
            }
        }
    }

    private RoomEntry() {
        residents = new ArrayList<>();
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hall) {
        hallName = hall;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Resident[] getResidents() {
        return residents.toArray(new Resident[residents.size()]);
    }

    public void setResidents(Resident[] residents) {
        this.residents = Arrays.asList(residents);
    }

    public static class Lobby extends RoomEntry {
        public Lobby() {
            super.roomNumber = "Lobby";
        }
    }
}

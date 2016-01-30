package edu.rosehulman.rafinder.model;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * An entire floor of {@link RoomEntry} objects.
 */
@SuppressWarnings("unused")
public class Floor {
    private int lobbyAfterRoomNumber;
    private String ordinal;
    private List<RoomEntry> rooms;

    public Floor(DataSnapshot ds, String hallName) {
        ordinal = ds.getKey();
        rooms = new ArrayList<>();
        for (DataSnapshot child : ds.getChildren()) {
            rooms.add(new RoomEntry(child, hallName));
        }
    }

    public List<RoomEntry> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomEntry> rooms) {
        this.rooms = rooms;
    }

    public String getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(String ordinal) {
        this.ordinal = ordinal;
    }

    public int getLobbyAfterRoomNumber() {
        return lobbyAfterRoomNumber;
    }

    public void setLobbyAfterRoomNumber(int lobbyAfterRoomNumber) {
        this.lobbyAfterRoomNumber = lobbyAfterRoomNumber;
    }
}

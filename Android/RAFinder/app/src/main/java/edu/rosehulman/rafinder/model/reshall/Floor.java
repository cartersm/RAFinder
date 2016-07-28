package edu.rosehulman.rafinder.model.reshall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One floor of a Residence Hall.
 */
@SuppressWarnings("unused") // constructors used by Jackson
public class Floor {
    private String floor;
    private Map<String, Room> rooms;

    public Floor() {
        rooms = new HashMap<>();
    }

    public Floor(String floor, Map<String, Room> rooms) {
        this.floor = floor;
        this.rooms = new HashMap<>();
        this.rooms = rooms;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Map<String, Room> getRoomsAsMap() {
        return rooms;
    }

    public List<Room> getRooms() {
        List<Room> list = new ArrayList<>(this.rooms.values());
        Collections.sort(list, (room, room2) -> {
            // order by room number
            return room.getNumber().compareTo(room2.getNumber());
        });
        return list;
    }

    public void setRooms(Map<String, Room> rooms) {
        this.rooms = rooms;
    }
}
package edu.rosehulman.rafinder.model.reshall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ArrayList<Room> list = new ArrayList<>(this.rooms.values());
        Collections.sort(list, new Comparator<Room>() {
            @Override
            public int compare(Room room, Room room2) {
                // order by room number
                return room.getNumber().compareTo(room2.getNumber());
            }
        });
        return list;
    }

    public void setRooms(Map<String, Room> rooms) {
        this.rooms = rooms;
    }
}
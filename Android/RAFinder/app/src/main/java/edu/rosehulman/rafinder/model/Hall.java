package edu.rosehulman.rafinder.model;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A Residence Hall.
 */
@SuppressWarnings("unused")
public class Hall {
    // HallRoster Firebase keys
    private static final String ROSTER = "Roster";
    private final HashMap<String, Floor> floors;
    private String name;

    public Hall(DataSnapshot ds) {
        name = ds.getKey();
        floors = new HashMap<>();
        for (DataSnapshot child : ds.child(ROSTER).getChildren()) {
            floors.put(child.getKey(), new Floor(child, name));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Floor> getFloors() {
        ArrayList<Floor> f = new ArrayList<>();
        f.addAll(floors.values());
        return f;
    }

    public void setFloors(List<Floor> floors) {
        this.floors.clear();
        for (Floor f : floors) {
            this.floors.put(f.getNumber(), f);
        }
    }

    public String[] getFloorNumbers() {
        String[] floorNumbers = new String[floors.size()];
        for (int i = 0; i < floorNumbers.length; i++) {
            floorNumbers[i] = floors.get(String.valueOf(i)).getNumber();
        }
        return floorNumbers;
    }

    public Floor getFloor(String floorName) {
        return floors.get(floorName);
    }
}

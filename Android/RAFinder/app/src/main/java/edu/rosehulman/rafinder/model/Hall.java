package edu.rosehulman.rafinder.model;

import com.firebase.client.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * A Residence Hall.
 */
@SuppressWarnings("unused")
public class Hall {
    // HallRoster Firebase key
    private static final String ROSTER = "Roster";
    private final HashMap<String, Floor> floors;
    private String name;

    public Hall(DataSnapshot ds) {
        name = ds.getKey();
        floors = new HashMap<>();
        for (DataSnapshot child : ds.child(ROSTER).getChildren()) {
            String floorNumber = child.getKey().substring(0, child.getKey().length() - 2);
            floors.put(floorNumber, new Floor(child, name));
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
        f.addAll(new TreeMap<>(floors).values());
        return f;
    }

    public void setFloors(List<Floor> floors) {
        this.floors.clear();
        for (Floor f : floors) {
            this.floors.put(f.getOrdinal(), f);
        }
    }

    public String[] getFloorNumbers() {
        String[] floorNumbers = new String[floors.size()];
        for (int i = 0; i < floorNumbers.length; i++) {
            floorNumbers[i] = floors.get(String.valueOf(i)).getOrdinal();
        }
        return floorNumbers;
    }

    public Floor getFloor(String floorName) {
        return floors.get(floorName);
    }
}

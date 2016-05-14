package edu.rosehulman.rafinder.model.reshall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Residence Hall.
 */
public class ResHall {
    private Map<String, Floor> floors;
    private String hall;

    public ResHall() {
        floors = new HashMap<>();
    }

    public ResHall(Map<String, Floor> floors, String hall) {
        this.floors = new HashMap<>();
        this.floors = floors;
        this.hall = hall;
    }

    public Map<String, Floor> getFloorsAsMap() {
        return floors;
    }

    public List<Floor> getFloors() {
        ArrayList<Floor> list = new ArrayList<>(this.floors.values());
        Collections.sort(list, new Comparator<Floor>() {
            @Override
            public int compare(Floor floor, Floor floor2) {
                // order by floor number
                return floor.getFloor().substring(0, 1).compareTo(floor2.getFloor().substring(0, 1));
            }
        });
        return list;
    }

    public void setFloors(Map<String, Floor> floors) {
        this.floors = floors;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }
}
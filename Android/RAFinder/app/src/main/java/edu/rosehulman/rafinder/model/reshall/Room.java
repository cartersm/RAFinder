package edu.rosehulman.rafinder.model.reshall;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A single room in the hall roster.
 */
public class Room {

    @JsonProperty("number")
    private String number;
    @JsonProperty("residents")
    private Map<String, Resident> residents;

    public Room() {
        residents = new HashMap<>();
    }

    public Room(String number, Map<String, Resident> residents) {
        this.number = number;
        this.residents = new HashMap<>();
        this.residents = residents;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Map<String, Resident> getResidentsAsMap() {
        return residents;
    }

    public List<Resident> getResidents() {
        ArrayList<Resident> list = new ArrayList<>(this.residents.values());
        Collections.sort(list, new Comparator<Resident>() {
            @Override
            public int compare(Resident resident, Resident resident2) {
                // order by resident name
                return resident.getName().compareTo(resident2.getName());
            }
        });
        return list;
    }

    public void setResidents(Map<String, Resident> residents) {
        this.residents = residents;
    }
}
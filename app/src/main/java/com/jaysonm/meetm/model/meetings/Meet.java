package com.jaysonm.meetm.model.meetings;

import android.annotation.SuppressLint;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

public class Meet {

    public String name;
    public String date;
    public String time;
    public String location;
    public String coords;

    public Meet() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    @SuppressLint("NewApi")
    public Meet(String name, String date, String time, String location, String coords) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.location = location;
        this.coords = coords;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public String getCoords() {
        return coords;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("date", date);
        result.put("time", time);
        result.put("location", location);
        result.put("coords", coords);

        return result;
    }
}

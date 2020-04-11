package com.jaysonm.meetm.model.geofencing;

import android.util.Log;

import com.google.android.gms.location.Geofence;

import java.util.ArrayList;

public class GeofenceManager {
    private static final String TAG = GeofenceManager.class.getSimpleName();

    private static GeofenceManager geofenceManagerInstance = null;
    private static ArrayList<Geofence> geofenceList = new ArrayList<>();

    private GeofenceManager() {

    }

    public static GeofenceManager getInstance() {
        if (geofenceManagerInstance == null) { //if there is no instance available... create new one
            geofenceManagerInstance = new GeofenceManager();
        }

        return geofenceManagerInstance;
    }

    public synchronized ArrayList<Geofence> getGeofences() {
        return geofenceList;
    }

    public void addGeofence(Geofence geofence) {
        geofenceList.add(geofence);
        Log.d(TAG, String.valueOf(geofenceList.size()));
    }

    public void clearList() {
        geofenceList.clear();
    }
}
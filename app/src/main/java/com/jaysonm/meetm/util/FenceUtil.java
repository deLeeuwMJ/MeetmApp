package com.jaysonm.meetm.util;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class FenceUtil {

    private static final String TAG = FenceUtil.class.getSimpleName();

    private static final float DEFAULT_RADIUS = 250.0f;
    private static final int DEFAULT_STROKE_WIDTH = 5;
    private static final long DEFAULT_GEO_DURATION = 60 * 60 * 1000;

    public static Circle drawFence(GoogleMap googleMap, LatLng location, String title, String subtitle){
        MarkerUtil.addDefaultMarker(googleMap,location,title,subtitle);

        //create circle on map
        CircleOptions circleOptions = new CircleOptions()
                .center(location)
                .radius( DEFAULT_RADIUS )
                .fillColor(Color.TRANSPARENT)
                .strokeColor(Color.RED)
                .strokeWidth(DEFAULT_STROKE_WIDTH);

        return googleMap.addCircle(circleOptions);
    }

    public static Geofence createGeofence(LatLng latLng, String geoFenceKey) {
        Log.d(TAG, "Creating geofence");
        return new Geofence.Builder()
                .setRequestId(geoFenceKey)
                .setCircularRegion( latLng.latitude, latLng.longitude, DEFAULT_RADIUS)
                .setExpirationDuration( DEFAULT_GEO_DURATION )
                .setTransitionTypes( Geofence.GEOFENCE_TRANSITION_ENTER
                        | Geofence.GEOFENCE_TRANSITION_EXIT )
                .build();
    }
}

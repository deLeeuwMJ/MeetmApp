package com.jaysonm.meetm.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.firebase.geofire.GeoLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.jaysonm.meetm.R;

public class MapUtil {

    private static final String TAG = MapUtil.class.getSimpleName();

    private static final float DEFAULT_CAMERA_ZOOM = 13.0f;

    public static void setMapStyling(Activity activity, GoogleMap googleMap) {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(activity);

        if (sharedPrefs.getBoolean("map_satellite_style", false)){ //satellite view enabled
            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else {

            try {
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                activity, R.raw.google_map_style));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
        }
    }

    public static void clearMap(GoogleMap googleMap) {
        googleMap.clear();
    }

    public static void setMapSettings(GoogleMap googleMap) {
        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);
        settings.setCompassEnabled(true);
        settings.setAllGesturesEnabled(true);

        googleMap.setMyLocationEnabled(true);
    }

    public static LatLng getLatLngFromLocation(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static LatLng getLatLngFromGeoLocation(GeoLocation location) {
        return new LatLng(location.latitude, location.longitude);
    }

    public static LatLng getLatLngFromFirebaseString(String codedString) {
        String[] latLngArr = codedString.split("\\s");
        double latitude = Double.parseDouble(latLngArr[0]);
        double longitude = Double.parseDouble(latLngArr[1]);
        return new LatLng(latitude, longitude);
    }

    public static void moveCamera(GoogleMap googleMap, LatLng latLng) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_CAMERA_ZOOM));
    }
}

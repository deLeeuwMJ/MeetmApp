package com.jaysonm.meetm.model.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaysonm.meetm.controllers.ChangeScreenListener;
import com.jaysonm.meetm.model.FragmentOptions;
import com.jaysonm.meetm.util.NotificationUtil;

import java.util.ArrayList;
import java.util.List;

public class LocationHandler implements GeoFire.CompletionListener {

    private static final String TAG = LocationHandler.class.getSimpleName();

    private static double GEO_FIRE_LOCATION_RADIUS = 5; //Unit kilometers
    private static final int REQUEST_INTERVAL = 10000; //10 seconds
    private static final float LOCATION_REQUEST_MIN_DISTANCE = 10.0f;
    private static final long LOCATION_REQUEST_INTERVAL = 10000; //10 seconds
    private boolean isStopped;

    private LocationManager locationManager;
    private android.location.LocationListener locationListener;
    private Location lastLocation;

    private Activity activity;
    private ChangeScreenListener changeScreenListener;

    public LocationHandler(Activity activity, ChangeScreenListener changeScreenListener) {
        this.activity = activity;
        this.changeScreenListener = changeScreenListener;
    }

    public void onStart() {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(activity);
        GEO_FIRE_LOCATION_RADIUS = Double.parseDouble(sharedPrefs.getString("location_radius", "5"));


        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new android.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastLocation = location;

                if (FirebaseAuth.getInstance().getCurrentUser() == null)
                    return;

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/" + userId);

                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), LocationHandler.this::onComplete);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REQUEST_INTERVAL, LOCATION_REQUEST_MIN_DISTANCE, locationListener);
            startThread();
        }
    }

    public void onStop() {

        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;

        //delete location in visible users
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/visible/");

        GeoFire geoFire = new GeoFire(ref);
        geoFire.removeLocation(userId, this);

        isStopped = true;
    }

    public void requestLocationUpdates() {
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REQUEST_INTERVAL, LOCATION_REQUEST_MIN_DISTANCE, locationListener);
    }

    @Override
    public void onComplete(String key, DatabaseError error) {
        if (error != null) {
            Log.e(TAG, "There was an error with GeoFire: " + error);
        }
    }

    public void startThread() {
        isStopped = false;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!isStopped) {
                        sendRequest();
                        Thread.sleep(REQUEST_INTERVAL); //every 5 seconds send request
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

        thread.start();
    }

    private void sendRequest() {
        if (lastLocation != null) {
            //add request to database

            if (FirebaseAuth.getInstance().getCurrentUser() == null)
                return;

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(activity);

            //update location in db under visible users
            if (sharedPrefs.getBoolean("location_visible", false)) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/visible");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userId, new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), this);
            }

            //get closest member
            getClosestMembers();
        } else {
            Log.wtf(TAG, "Location is null");
        }
    }

    //todo refactor code
    private synchronized void getClosestMembers() {
        List<String> userIdList = new ArrayList<>();
        List<GeoLocation> userGeoLocationList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/visible");

        GeoFire geoFire = new GeoFire(ref);

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lastLocation.getLatitude(), lastLocation.getLongitude()), GEO_FIRE_LOCATION_RADIUS);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                userIdList.add(key);
                userGeoLocationList.add(location);
            }

            @Override
            public void onKeyExited(String key) {
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if (userIdList.size() != userGeoLocationList.size()) {
                    Log.d(TAG, "No users found");
                } else {
                    Log.d(TAG, "Users found with the keys: " + userIdList.toString());
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                    for (int i = 0; i < userIdList.size(); i++) {
                        //check if it isn't the current user and is already in the list
                        if (!userIdList.get(i).equals(currentUser.getUid()) && !CrossedUserIdFetcher.getInstance().getList().contains(userIdList.get(i))) {
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/" + currentUser.getUid() + "/crossed/" + userIdList.get(i));
                            ref.setValue(true);

                            Log.d(TAG, "Updated user: " + userIdList.get(i));
                            NotificationUtil.showNotification(activity);

                            CrossedUserIdFetcher.getInstance().startFetching();
                        } else {
                            Log.d(TAG, "This user is already in the list: " + userIdList.get(i));
                        }
                    }

                    //update ui
                    changeScreenListener.changeToFragment(FragmentOptions.MEET);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.e(TAG, error.getMessage());
            }
        });
    }
}

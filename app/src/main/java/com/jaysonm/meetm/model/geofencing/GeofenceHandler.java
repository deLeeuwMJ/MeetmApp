package com.jaysonm.meetm.model.geofencing;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.util.FenceUtil;
import com.jaysonm.meetm.util.NotificationUtil;

public class GeofenceHandler implements OnCompleteListener<Void> {

    private static final String TAG = FenceUtil.class.getSimpleName();

    private PendingIntent mGeofencePendingIntent;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private Activity activity;
    private GeofencingClient mGeofencingClient;
    private GeofenceManager geofenceManager;

    public GeofenceHandler(Activity activity) {
        this.activity = activity;
        geofenceManager = GeofenceManager.getInstance();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        mGeofencingClient = LocationServices.getGeofencingClient(activity);
    }

    public void onStart() {
        if (!checkPermissions()) {
            requestPermissions();
        }
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(geofenceManager.getGeofences());

        // Return a GeofencingRequest.
        return builder.build();
    }

    private void addGeofences() {
        if (!checkPermissions()) {
            NotificationUtil.showSnackBar(activity, activity.getString(R.string.insufficient_permissions));
            return;
        }

        Log.d(TAG, "Geofences added");
        mGeofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent())
                .addOnCompleteListener(this);
    }

    private void removeGeofences() {
        if (!checkPermissions()) {
            NotificationUtil.showSnackBar(activity, activity.getString(R.string.insufficient_permissions));
            return;
        }

        mGeofencingClient.removeGeofences(getGeofencePendingIntent()).addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            Log.d(TAG, "Task completed");
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(activity, task.getException());
            Log.w(TAG, errorMessage);
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(activity, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        mGeofencePendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    public void newGeofence(LatLng location, String fenceKey) {
        removeGeofences();
        geofenceManager.clearList();
        geofenceManager.addGeofence(FenceUtil.createGeofence(location, fenceKey));
        addGeofences();
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}

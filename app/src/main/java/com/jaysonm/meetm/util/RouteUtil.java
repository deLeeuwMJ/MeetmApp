package com.jaysonm.meetm.util;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.model.route.CustomRouting;
import com.jaysonm.meetm.model.route.CustomRoutingListener;

import java.util.List;

public class RouteUtil {

    private static final String TAG = RouteUtil.class.getSimpleName();

    public static synchronized void routingWaypointRequest(Context context, LatLng start, LatLng end, CustomRoutingListener routingListener) {
        String key = context.getResources().getString(R.string.MEETM_DIRECTIONS_API_KEY);

        Log.d(TAG, "Request started single");

        CustomRouting routing = new CustomRouting.Builder()
                .travelMode(CustomRouting.TravelMode.WALKING)
                .withListener(routingListener)
                .waypoints(start, end)
                .multiple(false)
                .key(key)
                .build();

        if(routing != null)
            routing.execute();
    }

    public static synchronized void routingWaypointsRequest(Context context, List<LatLng> waypointList, CustomRoutingListener routingListener) {
        String key = context.getResources().getString(R.string.MEETM_DIRECTIONS_API_KEY);

        Log.d(TAG, "Request started multiple");

        CustomRouting routing = new CustomRouting.Builder()
                .travelMode(CustomRouting.TravelMode.WALKING)
                .withListener(routingListener)
                .waypoints(waypointList)
                .multiple(true)
                .key(key)
                .build();

        if(routing != null)
            routing.execute();
    }
}

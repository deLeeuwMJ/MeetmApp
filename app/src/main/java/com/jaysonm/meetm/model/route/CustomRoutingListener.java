package com.jaysonm.meetm.model.route;

import com.directions.route.Route;
import com.directions.route.RouteException;

import java.util.ArrayList;

public interface CustomRoutingListener {
    void onRoutingFailure(RouteException var1);

    void onRoutingStart();

    void onRoutingSuccess(ArrayList<Route> var1, int var2, boolean isMultiple);

    void onRoutingCancelled();
}
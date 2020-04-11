package com.jaysonm.meetm.controllers.home;

import com.google.android.gms.maps.model.LatLng;
import com.jaysonm.meetm.model.home.User;

public interface MapInteractionListener {
    void moveCamera(LatLng toLatLng);
    void drawFence(LatLng latLng, String title, String subtitle);
}

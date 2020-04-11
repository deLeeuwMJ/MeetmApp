package com.jaysonm.meetm.model.route;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomRouting extends CustomAbstractRouting {
    private final TravelMode travelMode;
    private final boolean alternativeRoutes;
    private final List<LatLng> waypoints;
    private final int avoidKinds;
    private final boolean optimize;
    private final String language;
    private final String key;
    private final boolean multiple;

    private CustomRouting(Builder builder) {
        super(builder.listener);
        this.travelMode = builder.travelMode;
        this.waypoints = builder.waypoints;
        this.avoidKinds = builder.avoidKinds;
        this.optimize = builder.optimize;
        this.alternativeRoutes = builder.alternativeRoutes;
        this.language = builder.language;
        this.key = builder.key;
        this.multiple = builder.isMultiple;
    }

    @Override
    protected boolean isMultiple() {
        return this.multiple;
    }

    public String constructURL() {
        StringBuilder stringBuilder = new StringBuilder("http://145.48.6.80:3000/directions?");
        LatLng origin = (LatLng)this.waypoints.get(0);
        stringBuilder.append("origin=");
        stringBuilder.append(origin.latitude);
        stringBuilder.append(',');
        stringBuilder.append(origin.longitude);
        LatLng destination = (LatLng)this.waypoints.get(this.waypoints.size() - 1);
        stringBuilder.append("&destination=");
        stringBuilder.append(destination.latitude);
        stringBuilder.append(',');
        stringBuilder.append(destination.longitude);
        stringBuilder.append("&mode=");
        stringBuilder.append(this.travelMode.getValue());
        if (this.waypoints.size() > 2) {
            stringBuilder.append("&waypoints=");
            if (this.optimize) {
                stringBuilder.append("optimize:true|");
            }

            for(int i = 1; i < this.waypoints.size() - 1; ++i) {
                LatLng p = (LatLng)this.waypoints.get(i);
                stringBuilder.append("via:");
                stringBuilder.append(p.latitude);
                stringBuilder.append(",");
                stringBuilder.append(p.longitude);
                stringBuilder.append("|");
            }
        }

        if (this.avoidKinds > 0) {
            stringBuilder.append("&avoid=");
            stringBuilder.append(AvoidKind.getRequestParam(this.avoidKinds));
        }

        if (this.alternativeRoutes) {
            stringBuilder.append("&alternatives=true");
        }

        stringBuilder.append("&sensor=true");
        if (this.language != null) {
            stringBuilder.append("&language=").append(this.language);
        }

        if (this.key != null) {
            stringBuilder.append("&key=").append(this.key);
        }

        return stringBuilder.toString();
    }

    public static class Builder {
        private TravelMode travelMode;
        private boolean alternativeRoutes;
        private List<LatLng> waypoints;
        private int avoidKinds;
        private CustomRoutingListener listener;
        private boolean optimize;
        private String language;
        private String key;
        private boolean isMultiple;

        public Builder() {
            this.travelMode = TravelMode.DRIVING;
            this.alternativeRoutes = false;
            this.waypoints = new ArrayList();
            this.avoidKinds = 0;
            this.listener = null;
            this.optimize = false;
            this.language = null;
            this.isMultiple = false;
            this.key = null;
        }

        public Builder travelMode(TravelMode travelMode) {
            this.travelMode = travelMode;
            return this;
        }

        public Builder alternativeRoutes(boolean alternativeRoutes) {
            this.alternativeRoutes = alternativeRoutes;
            return this;
        }

        public Builder waypoints(LatLng... points) {
            this.waypoints.clear();
            Collections.addAll(this.waypoints, points);
            return this;
        }

        public Builder waypoints(List<LatLng> waypoints) {
            this.waypoints = new ArrayList(waypoints);
            return this;
        }

        public Builder optimize(boolean optimize) {
            this.optimize = optimize;
            return this;
        }

        public Builder multiple(boolean isMultiple) {
            this.isMultiple = isMultiple;
            return this;
        }

        public Builder avoid(AvoidKind... avoids) {
            AvoidKind[] arr$ = avoids;
            int len$ = avoids.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                AvoidKind avoidKind = arr$[i$];
                this.avoidKinds |= avoidKind.getBitValue();
            }

            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder withListener(CustomRoutingListener listener) {
            this.listener = listener;
            return this;
        }

        public CustomRouting build() {
            if (this.waypoints.size() < 2) {
                Log.wtf("BUILDING ERROR","Must supply at least two waypoints to route between.");
//                throw new IllegalArgumentException("Must supply at least two waypoints to route between.");
                return null;
            } else if (this.waypoints.size() <= 2 && this.optimize) {
                Log.wtf("BUILDING ERROR","You need at least three waypoints to enable optimize");
                return null;
//                throw new IllegalArgumentException("You need at least three waypoints to enable optimize");
            } else {
                return new CustomRouting(this);
            }
        }
    }
}


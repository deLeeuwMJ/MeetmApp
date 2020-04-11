package com.jaysonm.meetm.model.route;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Iterator;

import com.directions.route.GoogleParser;
import com.directions.route.Route;
import com.directions.route.RouteException;

public abstract class CustomAbstractRouting extends AsyncTask<Void, Void, ArrayList<Route>> {
    protected ArrayList<CustomRoutingListener> _aListeners = new ArrayList();
    private RouteException mException = null;

    protected CustomAbstractRouting(CustomRoutingListener listener) {
        this.registerListener(listener);
    }

    public void registerListener(CustomRoutingListener mListener) {
        if (mListener != null) {
            this._aListeners.add(mListener);
        }

    }

    protected void dispatchOnStart() {
        Iterator i$ = this._aListeners.iterator();

        while(i$.hasNext()) {
            CustomRoutingListener mListener = (CustomRoutingListener)i$.next();
            mListener.onRoutingStart();
        }

    }

    protected void dispatchOnFailure(RouteException exception) {
        Iterator i$ = this._aListeners.iterator();

        while(i$.hasNext()) {
            CustomRoutingListener mListener = (CustomRoutingListener)i$.next();
            mListener.onRoutingFailure(exception);
        }

    }

    protected void dispatchOnSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        Iterator i$ = this._aListeners.iterator();

        while(i$.hasNext()) {
            CustomRoutingListener mListener = (CustomRoutingListener)i$.next();
            mListener.onRoutingSuccess(route, shortestRouteIndex, isMultiple());
        }

    }

    private void dispatchOnCancelled() {
        Iterator i$ = this._aListeners.iterator();

        while(i$.hasNext()) {
            CustomRoutingListener mListener = (CustomRoutingListener)i$.next();
            mListener.onRoutingCancelled();
        }

    }

    protected ArrayList<Route> doInBackground(Void... voids) {
        ArrayList result = new ArrayList();

        try {
            result = (new GoogleParser(this.constructURL())).parse();
        } catch (RouteException var4) {
            this.mException = var4;
        }

        return result;
    }

    protected abstract boolean isMultiple();

    protected abstract String constructURL();

    protected void onPreExecute() {
        this.dispatchOnStart();
    }

    protected void onPostExecute(ArrayList<Route> result) {
        if (!result.isEmpty()) {
            int shortestRouteIndex = 0;
            int minDistance = 2147483647;

            for(int i = 0; i < result.size(); ++i) {
                PolylineOptions mOptions = new PolylineOptions();
                Route route = (Route)result.get(i);
                if (route.getLength() < minDistance) {
                    shortestRouteIndex = i;
                    minDistance = route.getLength();
                }

                Iterator i$ = route.getPoints().iterator();

                while(i$.hasNext()) {
                    LatLng point = (LatLng)i$.next();
                    mOptions.add(point);
                }

                ((Route)result.get(i)).setPolyOptions(mOptions);
            }

            this.dispatchOnSuccess(result, shortestRouteIndex);
        } else {
            this.dispatchOnFailure(this.mException);
        }

    }

    protected void onCancelled() {
        this.dispatchOnCancelled();
    }

    public static enum AvoidKind {
        TOLLS(1, "tolls"),
        HIGHWAYS(2, "highways"),
        FERRIES(4, "ferries");

        private final String _sRequestParam;
        private final int _sBitValue;

        private AvoidKind(int bit, String param) {
            this._sBitValue = bit;
            this._sRequestParam = param;
        }

        protected int getBitValue() {
            return this._sBitValue;
        }

        protected static String getRequestParam(int bit) {
            String ret = "";
            CustomAbstractRouting.AvoidKind[] arr$ = values();
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                CustomAbstractRouting.AvoidKind kind = arr$[i$];
                if ((bit & kind._sBitValue) == kind._sBitValue) {
                    ret = ret + kind._sRequestParam;
                    ret = ret + "|";
                }
            }

            return ret;
        }
    }

    public static enum TravelMode {
        BIKING("bicycling"),
        DRIVING("driving"),
        WALKING("walking"),
        TRANSIT("transit");

        protected String _sValue;

        private TravelMode(String sValue) {
            this._sValue = sValue;
        }

        protected String getValue() {
            return this._sValue;
        }
    }
}


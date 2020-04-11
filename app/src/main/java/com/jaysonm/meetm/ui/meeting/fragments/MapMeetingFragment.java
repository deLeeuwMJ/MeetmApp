package com.jaysonm.meetm.ui.meeting.fragments;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.model.meetings.Meet;
import com.jaysonm.meetm.model.meetings.PageViewModel;
import com.jaysonm.meetm.model.meetings.SelectedMeeting;
import com.jaysonm.meetm.model.route.CustomRoutingListener;
import com.jaysonm.meetm.util.FenceUtil;
import com.jaysonm.meetm.util.LocationUtil;
import com.jaysonm.meetm.util.MapUtil;
import com.jaysonm.meetm.util.NotificationUtil;
import com.jaysonm.meetm.util.RouteUtil;

import java.util.ArrayList;
import java.util.List;

public class MapMeetingFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        CustomRoutingListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener{

    private static final String TAG = MapMeetingFragment.class.getSimpleName();

    private GoogleMap map;
    private Location location;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private boolean loadingFirstTime;

    public MapMeetingFragment() {
        // Required empty public constructor
    }

    public static MapMeetingFragment newInstance() {
        return new MapMeetingFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        PageViewModel pageViewModel = ViewModelProviders.of(getActivity()).get(PageViewModel.class);

        final Observer<List<Meet>> meetingObserver = new Observer<List<Meet>>() {
            @Override
            public void onChanged(List<Meet> meets) {
                Log.d(TAG, "Meeting size changed");
            }
        };

        pageViewModel.getMeetings().observe(getViewLifecycleOwner(), meetingObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_meet_map, container, false);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.meet_map)).getMapAsync(this);

        return root;
    }

    @Override
    public void onMapLoaded() {
        loadingFirstTime = true;
        map.clear();
        updateFences();
        drawRoute();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        initializeMapClients();

        MapUtil.setMapStyling(getActivity(), map);
        MapUtil.setMapSettings(map);
        map.setOnMapLoadedCallback(this);
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Log.e(TAG, e.getMessage());
    }

    @Override
    public void onRoutingStart() {
        Log.d(TAG, "CustomRouting started!");
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> routeArrayList, int shortestRouteIndex, boolean isMultiple) {
        Log.d(TAG, "CustomRouting succes!");

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(ContextCompat.getColor(getContext(), R.color.routeColor));

        polyOptions.width(15);
        polyOptions.addAll(routeArrayList.get(0).getPoints());
        map.addPolyline(polyOptions);
    }

    @Override
    public void onRoutingCancelled() {
        Log.d(TAG, "CustomRouting Cancelled!");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = LocationUtil.getNewLocationRequest();

        boolean success = LocationUtil.checkLocationPermission(getActivity());

        if (success) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        NotificationUtil.showSnackBar(getActivity(), "Your connection is suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        NotificationUtil.showSnackBar(getActivity(), connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        Log.d(TAG, "New location available: " + location.toString());

        if (loadingFirstTime) {
            MapUtil.moveCamera(map, MapUtil.getLatLngFromLocation(this.location));
            loadingFirstTime = false;
        }
    }

    private void initializeMapClients() {
        boolean success = LocationUtil.checkLocationPermission(getActivity());

        if (success) {
            googleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
    }

    private void drawRoute() {
        SelectedMeeting selectedMeeting = SelectedMeeting.getInstance();
        Meet meet = selectedMeeting.getSelectedMeet();

        if (meet == null)
            return;

        LatLng userLocation = MapUtil.getLatLngFromLocation(this.location);
        LatLng meetingLocation = MapUtil.getLatLngFromFirebaseString(meet.getCoords());

        RouteUtil.routingWaypointRequest(getContext(), userLocation, meetingLocation, this);
    }

    private void updateFences() {
        SelectedMeeting selectedMeeting = SelectedMeeting.getInstance();
        Meet meet = selectedMeeting.getSelectedMeet();

        if (meet == null)
            return;

        LatLng meetingLocation = MapUtil.getLatLngFromFirebaseString(meet.getCoords());
        FenceUtil.drawFence(map, meetingLocation, meet.getName(), meet.getLocation() + ", " + meet.getDate()+ ", " + meet.getTime());
    }
}
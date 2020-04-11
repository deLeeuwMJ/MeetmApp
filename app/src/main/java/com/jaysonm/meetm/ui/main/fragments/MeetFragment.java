package com.jaysonm.meetm.ui.main.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.textview.MaterialTextView;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.home.CrossedUserAdapter;
import com.jaysonm.meetm.controllers.DataAvailableListener;
import com.jaysonm.meetm.controllers.home.MapInteractionListener;
import com.jaysonm.meetm.model.home.CrossedUserFetcher;
import com.jaysonm.meetm.model.home.User;
import com.jaysonm.meetm.util.MapUtil;
import com.jaysonm.meetm.util.MarkerUtil;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import rm.com.longpresspopup.LongPressPopup;
import rm.com.longpresspopup.LongPressPopupBuilder;
import rm.com.longpresspopup.PopupInflaterListener;

public class MeetFragment extends Fragment
        implements
        OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        MapInteractionListener,
        DataAvailableListener {

    private static final String TAG = MeetFragment.class.getSimpleName();

    private GoogleMap map;

    private CrossedUserFetcher crossedUserFetcher;
    private CrossedUserAdapter crossedUserAdapter;
    private ViewPager viewPager;

    public MeetFragment() {
        // Required empty public constructor
    }

    public static MeetFragment newInstance() {
        MeetFragment fragment = new MeetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_meet, container, false);
        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        this.viewPager = view.findViewById(R.id.crossed_user_viewpager);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        MapUtil.setMapSettings(map);
        MapUtil.setMapStyling(getActivity(), map);
        map.setOnMapLoadedCallback(this);
    }

    @Override
    public void moveCamera(LatLng toLatLng) {
        MapUtil.moveCamera(map, toLatLng);
    }

    @Override
    public void drawFence(LatLng latLng, String title, String subtitle) {
        //Not used here
    }

    @Override
    public void onStart() {
        super.onStart();

        //get crossed users
        crossedUserFetcher = CrossedUserFetcher.getInstance();
        crossedUserFetcher.setListener(this::newDataAvailable);
        crossedUserFetcher.startFetching();

        this.crossedUserAdapter = new CrossedUserAdapter(crossedUserFetcher.getUsersCrossed(), getContext(), this);
        this.viewPager.setAdapter(this.crossedUserAdapter);
        this.crossedUserAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMapLoaded() {
        moveCamera(MapUtil.getLatLngFromLocation(map.getMyLocation()));
        updateUserMarkers();
    }

    private void updateUserMarkers() {
        MapUtil.clearMap(map);

        for (User user : crossedUserFetcher.getUsersCrossed()) {
            MarkerUtil.addCustomMarker(map, new LatLng(user.getGeoLocation().latitude, user.getGeoLocation().longitude), user.getName(), user.getJob(), user, MarkerUtil.createCustomMarkerBitmap(getContext(), Uri.parse(user.getPhotoUri())));
        }
    }

    @Override
    public void newDataAvailable() {
        this.crossedUserAdapter.notifyDataSetChanged();
        updateUserMarkers();
    }
}

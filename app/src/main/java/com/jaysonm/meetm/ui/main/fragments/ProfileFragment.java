package com.jaysonm.meetm.ui.main.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.DataAvailableListener;
import com.jaysonm.meetm.model.home.CurrentLoggedUserFetcher;
import com.jaysonm.meetm.model.home.User;
import com.jaysonm.meetm.ui.main.MainActivity;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements DataAvailableListener {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    private MaterialTextView emailTv, nameTv, locationTv, aboutmeTv, jobTv, numberTv, birthdayTv;
    private CircleImageView profileIv;
    private CurrentLoggedUserFetcher currentLoggedUserFetcher;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        currentLoggedUserFetcher = CurrentLoggedUserFetcher.getInstance();
        currentLoggedUserFetcher.setListener(this);
        currentLoggedUserFetcher.startFetching();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileIv = view.findViewById(R.id.profile_user_image);

        aboutmeTv = view.findViewById(R.id.profile_about_me);
        emailTv = view.findViewById(R.id.profile_email);
        nameTv = view.findViewById(R.id.profile_name);
        locationTv = view.findViewById(R.id.profile_user_location);

        numberTv = view.findViewById(R.id.profile_phone_number);
        birthdayTv = view.findViewById(R.id.profile_birthday);
        jobTv = view.findViewById(R.id.profile_job);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        User currentUser = currentLoggedUserFetcher.getUser();

        if (currentUser != null)
            setTvData(currentUser);
            updateSharedPrefences(currentUser);
    }

    @Override
    public void onResume() {
        super.onResume();
        currentLoggedUserFetcher.startFetching();
    }

    @Override
    public void newDataAvailable() {
        User currentUser = currentLoggedUserFetcher.getUser();
        setTvData(currentUser);
    }

    private void setTvData(User currentUser) {
        Picasso.get().load(currentUser.getPhotoUri()).into(profileIv);

        locationTv.setText(currentUser.getLocation());
        aboutmeTv.setText(currentUser.getAboutMe());

        emailTv.setText(currentUser.getEmail());
        nameTv.setText(currentUser.getName());

        numberTv.setText(currentUser.getPhoneNumber());
        jobTv.setText(currentUser.getJob());
        birthdayTv.setText(currentUser.getBirthDay());
    }

    private void updateSharedPrefences(User currentUser) {

        if (currentUser == null)
            return;

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());

        sharedPrefs.edit().putString("location_set", currentUser.getLocation()).apply();
        sharedPrefs.edit().putString("about_me_set", currentUser.getAboutMe()).apply();
        sharedPrefs.edit().putString("profile_url_set", currentUser.getPhotoUri()).apply();
        sharedPrefs.edit().putString("phone_set", currentUser.getPhoneNumber()).apply();
        sharedPrefs.edit().putString("job_set", currentUser.getJob()).apply();

        Log.d(TAG, "Sharedprefs updated");
    }
}

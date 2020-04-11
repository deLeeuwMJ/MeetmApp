package com.jaysonm.meetm.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.ChangeScreenListener;
import com.jaysonm.meetm.controllers.events.WrapApiListener;
import com.jaysonm.meetm.model.events.EventViewModel;
import com.jaysonm.meetm.model.home.CrossedUserIdFetcher;
import com.jaysonm.meetm.model.events.Event;
import com.jaysonm.meetm.model.FragmentOptions;
import com.jaysonm.meetm.model.home.CurrentLoggedUserFetcher;
import com.jaysonm.meetm.model.home.LocationHandler;
import com.jaysonm.meetm.model.home.User;
import com.jaysonm.meetm.model.contacts.FollowedUserIdFetcher;
import com.jaysonm.meetm.ui.login.LoginActivity;
import com.jaysonm.meetm.ui.main.fragments.ContactsFragment;
import com.jaysonm.meetm.ui.main.fragments.EventsFragment;
import com.jaysonm.meetm.ui.main.fragments.HomeFragment;
import com.jaysonm.meetm.ui.main.fragments.MeetFragment;
import com.jaysonm.meetm.ui.main.fragments.ProfileFragment;
import com.jaysonm.meetm.util.EventUtil;
import com.jaysonm.meetm.util.FontUtil;
import com.jaysonm.meetm.util.NotificationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, NavigationView.OnNavigationItemSelectedListener, WrapApiListener, ChangeScreenListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RESULT_SETTINGS = 1;

    private HomeFragment homeFragment;
    private MeetFragment meetFragment;
    private ContactsFragment contactsFragment;
    private EventsFragment eventsFragment;
    private ProfileFragment profileFragment;

    private AppCompatImageButton toolbarButton;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private FirebaseAuth firebaseAuth;
    private LocationHandler locationHandler;

    private EventViewModel eventViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialTextView toolbarTitle = findViewById(R.id.custom_tool_bar_title);
        toolbarTitle.setTypeface(FontUtil.getCustomTypeFace(MainActivity.this, FontUtil.CUSTOM_FONTS.LEMON));

        firebaseAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.main_drawer);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        toolbarButton = findViewById(R.id.custom_tool_bar_button);
        toolbarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(navigationView);
            }
        });

        initializeFragments(savedInstanceState);

        locationHandler = new LocationHandler(MainActivity.this, this);

        //loading events
        eventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);
    }

    private void initializeFragments(Bundle savedInstanceState) {
        if (this.homeFragment == null)
            this.homeFragment = HomeFragment.newInstance();

        if (this.meetFragment == null)
            this.meetFragment = MeetFragment.newInstance();

        if (this.contactsFragment == null)
            this.contactsFragment = ContactsFragment.newInstance();

        if (this.eventsFragment == null)
            this.eventsFragment = EventsFragment.newInstance();

        if (this.profileFragment == null)
            this.profileFragment = ProfileFragment.newInstance();

        if (findViewById(R.id.main_frame_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_frame_container, homeFragment).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationHandler.requestLocationUpdates();
                locationHandler.startThread();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        locationHandler.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        NotificationUtil.createNotificationChannel(MainActivity.this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        eventViewModel.getAllEvents().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                if (events.size() == 0)
                    EventUtil.requestApiEventData(3, MainActivity.this, MainActivity.this);
            }
        });

        //get user follower id list
        FollowedUserIdFetcher.getInstance().startFetching();

        //get user follower id list
        CrossedUserIdFetcher.getInstance().startFetching();

        //get logged user info
        CurrentLoggedUserFetcher.getInstance().startFetching();

        locationHandler.onStart();
    }

    private void updateSettingsToDb(){
        CurrentLoggedUserFetcher currentLoggedUserFetcher = CurrentLoggedUserFetcher.getInstance();
        User currentUser = currentLoggedUserFetcher.getUser();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/" + userId);

        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        try {

            String location = sharedPrefs.getString("location_set", currentUser.getLocation() == null ? "" : currentUser.getLocation());
            String aboutMe = sharedPrefs.getString("about_me_set", currentUser.getAboutMe());
            String profileImageUrl = sharedPrefs.getString("profile_url_set", currentUser.getPhotoUri());
            String phoneNumber = sharedPrefs.getString("phone_set", currentUser.getPhoneNumber());
            String job = sharedPrefs.getString("job_set", currentUser.getJob());

            User tempUser = new User(
                    currentUser.getName(),
                    currentUser.getEmail(),
                    phoneNumber,
                    aboutMe,
                    currentUser.getBirthDay(),
                    profileImageUrl,
                    location,
                    job
            );

            currentLoggedUserFetcher.setUser(tempUser);

            Map<String, Object> userInformation = tempUser.toMap();

            ref.updateChildren(userInformation);
        } catch (NullPointerException e){
            Log.d(TAG, e.getMessage());
        }
    }

    private void performFragmentTransaction(Fragment fragment) {
        Fragment newFragment = fragment;
        Bundle args = new Bundle();
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.main_frame_container, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottomNavigationHomeMenuId:
                if (userHasCrossed()) {
                    meetFragment = MeetFragment.newInstance();
                    performFragmentTransaction(meetFragment);
                } else {
                    performFragmentTransaction(homeFragment);
                }
                break;
            case R.id.bottomNavigationContactsMenuId:
                performFragmentTransaction(contactsFragment);
                break;
            case R.id.bottomNavigationEventsMenuId:
                performFragmentTransaction(eventsFragment);
                break;
            case R.id.bottomNavigationProfileMenuId:
                performFragmentTransaction(profileFragment);
                break;
            case R.id.ToolbarAboutUsMenuId:
                drawerLayout.closeDrawer(navigationView);
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                break;
            case R.id.ToolbarSettingsMenuId:
                drawerLayout.closeDrawer(navigationView);
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(intent, RESULT_SETTINGS);
                break;
            case R.id.ToolbarLogoutMenuId:
                drawerLayout.closeDrawer(navigationView);

                //logout current user
                firebaseAuth.signOut();

                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                break;
            default:
                return false;
        }
        return true;
    }

    private boolean userHasCrossed() {
        List<String> crossedList = CrossedUserIdFetcher.getInstance().getList();
        Log.d(TAG, crossedList.toString());
        return crossedList.size() != 0;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                meetFragment = MeetFragment.newInstance(); //to update map
                updateSettingsToDb();
                break;
        }
    }

    @Override
    public void onApiStart() {
        Log.i(TAG, "Api is starting");
    }

    @Override
    public void onApiSuccess(ArrayList<Event> events) {
        Log.i(TAG, "Api success");

        for (Event event : events) {
            eventViewModel.insert(event);
        }
    }

    @Override
    public void onApiCancelled() {
        Log.i(TAG, "Api is canceled");
    }

    @Override
    public void onApiFailure(Exception e) {
        Log.wtf(TAG, e.getMessage());
    }

    @Override
    public void changeToActivity(Class c) {
        startActivity(new Intent(MainActivity.this, c));
        finish();
    }

    @Override
    public void changeToFragment(FragmentOptions option) {
        if (!meetFragment.isVisible() && homeFragment.isVisible()) {
            switch (option) {
                case MEET:
                    performFragmentTransaction(meetFragment);
                    break;
                case HOME:
                    performFragmentTransaction(homeFragment);
                    break;
            }
        }
    }
}

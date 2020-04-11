package com.jaysonm.meetm.ui.meeting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.ChangeScreenListener;
import com.jaysonm.meetm.controllers.meeting.FirebaseMeetingListener;
import com.jaysonm.meetm.controllers.meeting.MeetPagerAdapter;
import com.jaysonm.meetm.model.FragmentOptions;
import com.jaysonm.meetm.model.geofencing.GeofenceHandler;
import com.jaysonm.meetm.model.meetings.SelectedMeeting;
import com.jaysonm.meetm.model.meetings.SelectedUser;
import com.jaysonm.meetm.model.home.User;
import com.jaysonm.meetm.model.meetings.Meet;
import com.jaysonm.meetm.model.meetings.PageViewModel;
import com.jaysonm.meetm.util.FontUtil;
import com.jaysonm.meetm.util.MapUtil;

import java.util.Map;

public class MeetActivity extends AppCompatActivity implements FirebaseMeetingListener, ChangeScreenListener {

    private static final String TAG = MeetActivity.class.getSimpleName();

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private MeetPagerAdapter meetPagerAdapter;
    private PageViewModel pageViewModel;
    private User selectedUser;

    private GeofenceHandler geofenceHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meet);

        selectedUser = (User) getIntent().getSerializableExtra("selectedUser");

        SelectedUser selectedUserInstance = SelectedUser.getInstance();
        selectedUserInstance.setSelectedUser(selectedUser);

        MaterialTextView toolbarTitle = findViewById(R.id.custom_tool_bar_title);
        toolbarTitle.setTypeface(FontUtil.getCustomTypeFace(MeetActivity.this, FontUtil.CUSTOM_FONTS.LEMON));

        AppCompatImageButton toolbarButton = findViewById(R.id.custom_tool_bar_button);
        toolbarButton.setImageResource(R.drawable.ic_close_custom_24dp);

        toolbarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);

        meetPagerAdapter = new MeetPagerAdapter(this, getSupportFragmentManager());

        viewPager = findViewById(R.id.meet_viewpager);
        tabLayout = findViewById(R.id.tabs);

        viewPager.setAdapter(meetPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        geofenceHandler = new GeofenceHandler(MeetActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SelectedMeeting selectedMeeting = SelectedMeeting.getInstance();
        selectedMeeting.setSelectedMeet(null);

        geofenceHandler.onStart();
    }

    @Override
    public void addMeeting(Meet meet) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userMeetingRef = FirebaseDatabase.getInstance().getReference("members/" + user.getUid() + "/meetings/" + selectedUser.getUserId());
        DatabaseReference destMeetingRef = FirebaseDatabase.getInstance().getReference("members/" + selectedUser.getUserId() + "/meetings/" + user.getUid());

        Map<String, Object> meetingInformation = meet.toMap();

        userMeetingRef.push().setValue(meetingInformation);
        destMeetingRef.push().setValue(meetingInformation);
    }

    @Override
    public void removeMeeting(Meet meet) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userMeetingRef = FirebaseDatabase.getInstance().getReference("members/" + user.getUid() + "/meetings/" + selectedUser.getUserId());
        DatabaseReference destMeetingRef = FirebaseDatabase.getInstance().getReference("members/" + selectedUser.getUserId() + "/meetings/" + user.getUid());

        destMeetingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Meet meetingWithUser = ds.getValue(Meet.class);

                    if (meetingWithUser == null)
                        return;


                    if (meet.getName().equals(meetingWithUser.getName())) {
                        Log.d(TAG, ds.getKey());
                        destMeetingRef.child(ds.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userMeetingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Meet meetingWithUser = ds.getValue(Meet.class);

                    if (meetingWithUser == null)
                        return;

                    if (meet.getName().equals(meetingWithUser.getName())) {
                        userMeetingRef.child(ds.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void changeToActivity(Class c) {
        //not important in this activity
    }

    @Override
    public void changeToFragment(FragmentOptions option) {
        switch (option){
            case SHOW_MEETINGS:
                viewPager.setCurrentItem(0);
                break;
            case SHOW_MEETING_MAP:
                SelectedMeeting selectedMeeting = SelectedMeeting.getInstance();
                Meet meet = selectedMeeting.getSelectedMeet();

                geofenceHandler.newGeofence(MapUtil.getLatLngFromFirebaseString(meet.getCoords()), meet.getName());
                viewPager.setCurrentItem(2);
                break;
        }
        if (option.equals(FragmentOptions.SHOW_MEETING_MAP)) {

        }
    }
}

package com.jaysonm.meetm.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.model.contacts.FollowedUserIdFetcher;
import com.jaysonm.meetm.model.home.User;
import com.jaysonm.meetm.ui.meeting.MeetActivity;
import com.jaysonm.meetm.util.FontUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    MaterialTextView name, location, email, aboutMe, number, birthday, job;
    MaterialButton follow, meet, message;
    User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        MaterialTextView toolbarTitle = findViewById(R.id.custom_tool_bar_title);
        toolbarTitle.setTypeface(FontUtil.getCustomTypeFace(ProfileActivity.this, FontUtil.CUSTOM_FONTS.LEMON));

        AppCompatImageButton toolbarButton = findViewById(R.id.custom_tool_bar_button);
        toolbarButton.setImageResource(R.drawable.ic_close_custom_24dp);

        toolbarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });

        selectedUser = (User) getIntent().getSerializableExtra("selectedUser");

        if (selectedUser == null)
            finish();

        CircleImageView profileImage = findViewById(R.id.profile_user_image);
        name = findViewById(R.id.profile_name);
        location = findViewById(R.id.profile_user_location);

        email = findViewById(R.id.profile_email);
        aboutMe = findViewById(R.id.profile_about_me);

        follow = findViewById(R.id.profile_follow_button);
        meet = findViewById(R.id.profile_meet_button);
        message = findViewById(R.id.profile_message_button);

        number = findViewById(R.id.profile_phone_number);
        birthday = findViewById(R.id.profile_birthday);
        job = findViewById(R.id.profile_job);

        Picasso.get().load(selectedUser.getPhotoUri()).into(profileImage);
        name.setText(selectedUser.getName());
        location.setText(selectedUser.getLocation());

        email.setText(selectedUser.getEmail());
        aboutMe.setText(selectedUser.getAboutMe());

        number.setText(selectedUser.getPhoneNumber());
        job.setText(selectedUser.getJob());
        birthday.setText(selectedUser.getBirthDay());

        follow.setOnClickListener(this);
        meet.setOnClickListener(this);
        message.setOnClickListener(this);

        List<String> userFollowingId = FollowedUserIdFetcher.getInstance().getList();

        if (userFollowingId.contains(selectedUser.getUserId())) {
            follow.setText(getResources().getString(R.string.user_following));
        } else {
            follow.setText(getResources().getString(R.string.user_follow));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_follow_button:
                handleFollowing();
                break;
            case R.id.profile_meet_button:
                handleMeet();
                break;
            case R.id.profile_message_button:
                handleMessage();
                break;
        }
    }

    private void handleMessage() {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("selectedUserId", selectedUser.getUserId());
        startActivity(intent);
    }

    private void handleMeet() {
        Intent intent = new Intent(this, MeetActivity.class);
        intent.putExtra("selectedUser", selectedUser);
        startActivity(intent);
    }

    private void handleFollowing() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List<String> userFollowingId = FollowedUserIdFetcher.getInstance().getList();

        //ref to follower list
        DatabaseReference userFollowingRef = FirebaseDatabase.getInstance().getReference("members/" + user.getUid() + "/following/" + selectedUser.getUserId());
        DatabaseReference destFollowingRef = FirebaseDatabase.getInstance().getReference("members/" + selectedUser.getUserId() + "/following/" + user.getUid());

        //ref chat
        DatabaseReference userChatRef = FirebaseDatabase.getInstance().getReference("members/" + user.getUid() + "/chats/" + selectedUser.getUserId());
        DatabaseReference destChatRef = FirebaseDatabase.getInstance().getReference("members/" + selectedUser.getUserId() + "/chats/" + user.getUid());

        if (!userFollowingId.contains(selectedUser.getUserId())) {
            follow.setText("Following");

            //add db followed
            userFollowingRef.setValue(true);
            destFollowingRef.setValue(true);

            //add db chat id
            String roomId = selectedUser.getUserId().substring(7) + user.getUid().substring(7);
            userChatRef.setValue(roomId);
            destChatRef.setValue(roomId);
        } else {
            follow.setText("Follow");
            userFollowingRef.removeValue();
            destFollowingRef.removeValue();
            userChatRef.removeValue();
            destChatRef.removeValue();
        }
    }
}

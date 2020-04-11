package com.jaysonm.meetm.model.meetings;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaysonm.meetm.controllers.DataAvailableListener;
import com.jaysonm.meetm.model.FirebaseFetcher;
import com.jaysonm.meetm.model.home.CurrentLoggedUserFetcher;
import com.jaysonm.meetm.model.home.User;

import java.util.ArrayList;
import java.util.List;

public class UserMeetingsFetcher extends FirebaseFetcher {

    private static final String TAG = UserMeetingsFetcher.class.getSimpleName();

    private static UserMeetingsFetcher userMeetingsFetcherInstance = null;
    private static List<Meet> meetArrayList;

    private UserMeetingsFetcher() {
        meetArrayList = new ArrayList<>();
    }

    public static UserMeetingsFetcher getInstance() {
        if (userMeetingsFetcherInstance == null) { //if there is no instance available... create new one
            userMeetingsFetcherInstance = new UserMeetingsFetcher();
        }

        return userMeetingsFetcherInstance;
    }

    @Override
    public void startFetching() {
        Log.d(TAG, "Started fetching data");
        meetArrayList.clear();
        getFetchData();
    }

    @Override
    public void getFetchData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        User selectedUser = SelectedUser.getInstance().getSelectedUser();

        Log.d(TAG, "Fetching data from " + selectedUser.getName());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/" + currentUser.getUid() + "/meetings/" + selectedUser.getUserId());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Meet meetingWithUser = ds.getValue(Meet.class);

                    if (meetingWithUser == null)
                        return;

                    Log.d(TAG, meetingWithUser.getName());

                    meetArrayList.add(meetingWithUser);

                    if (UserMeetingsFetcher.super.getDataAvailableListener() != null)
                        UserMeetingsFetcher.super.getDataAvailableListener().newDataAvailable();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    @Override
    public void setListener(DataAvailableListener dataAvailableListener) {
        UserMeetingsFetcher.super.setDataAvailableListener(dataAvailableListener);
    }

    public synchronized List<Meet> getMeetList() {
        return meetArrayList;
    }
}

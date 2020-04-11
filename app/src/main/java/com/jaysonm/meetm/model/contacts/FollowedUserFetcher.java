package com.jaysonm.meetm.model.contacts;

import android.util.Log;

import androidx.annotation.NonNull;

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

public class FollowedUserFetcher extends FirebaseFetcher {

    private static final String TAG = FollowedUserFetcher.class.getSimpleName();

    private static FollowedUserFetcher followedUserFetcherInstance = null;
    private static ArrayList<User> followedUsers;

    private FollowedUserFetcher() {
        followedUsers = new ArrayList<>();
    }

    public static FollowedUserFetcher getInstance() {
        if (followedUserFetcherInstance == null) { //if there is no instance available... create new one
            followedUserFetcherInstance = new FollowedUserFetcher();
        }

        return followedUserFetcherInstance;
    }

    @Override
    public void startFetching() {
        Log.d(TAG, "Started fetching data");
        followedUsers.clear();
        getFetchData();
    }

    @Override
    public void getFetchData() {
        List<String> userFollowingIdList = FollowedUserIdFetcher.getInstance().getList();

        for (String id : userFollowingIdList) {
            Log.d(TAG, id);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/" + id);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User followedUser = dataSnapshot.getValue(User.class);
                    followedUser.setUserId(id);
                    followedUsers.add(followedUser);
                    if (FollowedUserFetcher.super.getDataAvailableListener() != null)
                        FollowedUserFetcher.super.getDataAvailableListener().newDataAvailable();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage());
                }
            });
        }
    }

    @Override
    public void setListener(DataAvailableListener dataAvailableListener) {
        FollowedUserFetcher.super.setDataAvailableListener(dataAvailableListener);
    }


    public synchronized ArrayList<User> getList() {
        return followedUsers;
    }
}
package com.jaysonm.meetm.model.contacts;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.jaysonm.meetm.controllers.DataAvailableListener;
import com.jaysonm.meetm.model.FirebaseFetcher;

import java.util.ArrayList;

public class FollowedUserIdFetcher extends FirebaseFetcher {

    private static final String TAG = FollowedUserIdFetcher.class.getSimpleName();

    private static FollowedUserIdFetcher followedUserIdFetcherInstance = null;
    private static ArrayList<String> listFollowing;

    private FollowedUserIdFetcher() {
        listFollowing = new ArrayList<>();
    }

    public static FollowedUserIdFetcher getInstance() {
        if (followedUserIdFetcherInstance == null) { //if there is no instance available... create new one
            followedUserIdFetcherInstance = new FollowedUserIdFetcher();
        }

        return followedUserIdFetcherInstance;
    }

    @Override
    public void startFetching() {
        Log.d(TAG, "Started fetching data");
        listFollowing.clear();
        getFetchData();
    }

    @Override
    public void getFetchData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/" + user.getUid() + "/following");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String uid = dataSnapshot.getRef().getKey();
                    if (uid != null && !listFollowing.contains(uid)) {
                        listFollowing.add(uid);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String uid = dataSnapshot.getRef().getKey();
                    if (uid != null) {
                        listFollowing.remove(uid);
                    }
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void setListener(DataAvailableListener dataAvailableListener) {
        FollowedUserIdFetcher.super.setDataAvailableListener(dataAvailableListener);
    }

    public synchronized ArrayList<String> getList() {
        return listFollowing;
    }
}

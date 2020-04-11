package com.jaysonm.meetm.model.home;

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

public class CrossedUserIdFetcher extends FirebaseFetcher {

    private static final String TAG = CrossedUserIdFetcher.class.getSimpleName();

    private static CrossedUserIdFetcher crossedUserIdFetcherInstance = null;
    private static ArrayList<String> crossedIdList;

    private CrossedUserIdFetcher() {
        crossedIdList = new ArrayList<>();
    }

    public static CrossedUserIdFetcher getInstance() {
        if (crossedUserIdFetcherInstance == null) { //if there is no instance available... create new one
            crossedUserIdFetcherInstance = new CrossedUserIdFetcher();
        }

        return crossedUserIdFetcherInstance;
    }

    @Override
    public void startFetching() {
        Log.d(TAG, "Started fetching data");
        crossedIdList.clear();
        getFetchData();
    }

    @Override
    public void getFetchData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/" + user.getUid() + "/crossed");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String uid = dataSnapshot.getRef().getKey();
                    if (uid != null && !crossedIdList.contains(uid) && !uid.equals(user.getUid())) {
                        Log.d(TAG, "CROSSED ADDED: " + uid);
                        crossedIdList.add(uid);
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
                        crossedIdList.remove(uid);
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
        CrossedUserIdFetcher.super.setDataAvailableListener(dataAvailableListener);
    }

    public synchronized ArrayList<String> getList() {
        return crossedIdList;
    }
}

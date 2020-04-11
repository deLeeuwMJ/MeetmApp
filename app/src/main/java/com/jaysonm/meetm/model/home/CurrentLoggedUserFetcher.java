package com.jaysonm.meetm.model.home;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaysonm.meetm.controllers.DataAvailableListener;
import com.jaysonm.meetm.model.FirebaseFetcher;

import java.util.List;

public class CurrentLoggedUserFetcher extends FirebaseFetcher {

    private static final String TAG = CurrentLoggedUserFetcher.class.getSimpleName();

    private static CurrentLoggedUserFetcher currentLoggedUserInstance = null;
    private static User user;

    private CurrentLoggedUserFetcher() {
    }

    public static CurrentLoggedUserFetcher getInstance() {
        if (currentLoggedUserInstance == null) { //if there is no instance available... create new one
            currentLoggedUserInstance = new CurrentLoggedUserFetcher();
        }

        return currentLoggedUserInstance;
    }

    @Override
    public void startFetching() {
        Log.d(TAG, "Started fetching data");
        user = null;
        getFetchData();
    }

    @Override
    public void getFetchData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/" + userId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User currentUser = dataSnapshot.getValue(User.class);
                currentUser.setUserId(userId);

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/" + userId + "/" + userId + "/l");
                ref.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                List<Double> latLng = (List<Double>) dataSnapshot.getValue();

                                if (latLng == null)
                                    return;

                                GeoLocation geoLocation = new GeoLocation(latLng.get(0), latLng.get(1));
                                currentUser.setGeoLocation(geoLocation);

                                setUser(currentUser);

                                if (CurrentLoggedUserFetcher.super.getDataAvailableListener() != null)
                                    CurrentLoggedUserFetcher.super.getDataAvailableListener().newDataAvailable();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.d(TAG, databaseError.getMessage());
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    @Override
    public void setListener(DataAvailableListener dataAvailableListener) {
        super.setDataAvailableListener(dataAvailableListener);
    }

    public synchronized User getUser() {
        return user;
    }

    public void setUser(User user) {
        CurrentLoggedUserFetcher.user = user;
    }
}

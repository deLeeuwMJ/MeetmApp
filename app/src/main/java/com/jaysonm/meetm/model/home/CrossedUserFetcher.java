package com.jaysonm.meetm.model.home;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaysonm.meetm.controllers.DataAvailableListener;
import com.jaysonm.meetm.model.FirebaseFetcher;

import java.util.ArrayList;
import java.util.List;

public class CrossedUserFetcher extends FirebaseFetcher {

    private static final String TAG = CrossedUserFetcher.class.getSimpleName();

    private static CrossedUserFetcher crossedUserFetcherInstance = null;
    private static ArrayList<User> crossedUsers;

    private CrossedUserFetcher() {
        crossedUsers = new ArrayList<>();
    }

    public static CrossedUserFetcher getInstance() {
        if (crossedUserFetcherInstance == null) { //if there is no instance available... create new one
            crossedUserFetcherInstance = new CrossedUserFetcher();
        }

        return crossedUserFetcherInstance;
    }

    @Override
    public void startFetching() {
        Log.d(TAG, "Started fetching data");
        crossedUsers.clear();
        getFetchData();
    }

    @Override
    public void getFetchData() {
        List<String> userCrossedIdList = CrossedUserIdFetcher.getInstance().getList();

        for (String id : userCrossedIdList) {
            Log.d(TAG, id);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/" + id);

            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User crossedUser = dataSnapshot.getValue(User.class);
                    crossedUser.setUserId(id);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/" + id + "/" + id + "/l");
                    ref.addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    List<Double> latLng = (List<Double>) dataSnapshot.getValue();

                                    GeoLocation geoLocation = new GeoLocation(latLng.get(0), latLng.get(1));
                                    crossedUser.setGeoLocation(geoLocation);

                                    crossedUsers.add(crossedUser);
                                    if (CrossedUserFetcher.super.getDataAvailableListener() != null)
                                        CrossedUserFetcher.super.getDataAvailableListener().newDataAvailable();
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
    }

    @Override
    public void setListener(DataAvailableListener dataAvailableListener) {
        CrossedUserFetcher.super.setDataAvailableListener(dataAvailableListener);
    }

    public synchronized ArrayList<User> getUsersCrossed() {
        return crossedUsers;
    }
}

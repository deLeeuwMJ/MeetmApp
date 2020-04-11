package com.jaysonm.meetm.model.meetings;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jaysonm.meetm.controllers.DataAvailableListener;
import com.jaysonm.meetm.model.home.User;

import java.util.ArrayList;
import java.util.List;

public class PageViewModel extends ViewModel implements DataAvailableListener {

    private String TAG = PageViewModel.class.getSimpleName();

    private MutableLiveData<List<Meet>> meetingList;
    private UserMeetingsFetcher userMeetingsFetcher;

    public LiveData<List<Meet>> getMeetings() {
        if (meetingList == null) {
            meetingList = new MutableLiveData<>();
            fetchMeetings();
        }
        return meetingList;
    }

    private void fetchMeetings() {
        List<Meet> meetList = new ArrayList<>();

        userMeetingsFetcher = UserMeetingsFetcher.getInstance();
        userMeetingsFetcher.startFetching();
        userMeetingsFetcher.setListener(this);

        meetingList.setValue(meetList);
    }

    public void addMeeting(Meet meet) {
        List<Meet> meetList = meetingList.getValue();
        meetList.add(meet);
        meetingList.postValue(meetList);
    }

    @Override
    public void newDataAvailable() {
        Log.d(TAG, "New data available");

        for(Meet meet : userMeetingsFetcher.getMeetList()){
            if (!getMeetings().getValue().contains(meet)) {
                addMeeting(meet);
            }
        }
    }
}
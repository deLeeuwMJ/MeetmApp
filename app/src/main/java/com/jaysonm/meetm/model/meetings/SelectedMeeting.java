package com.jaysonm.meetm.model.meetings;

public class SelectedMeeting {

    private static SelectedMeeting userInstance = null;
    private static Meet selectedMeet;

    public static SelectedMeeting getInstance() {
        if (userInstance == null) { //if there is no instance available... create new one
            userInstance = new SelectedMeeting();
        }

        return userInstance;
    }

    public Meet getSelectedMeet() {
        return selectedMeet;
    }

    public void setSelectedMeet(Meet selectedMeet) {
        SelectedMeeting.selectedMeet = selectedMeet;
    }
}

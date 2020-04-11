package com.jaysonm.meetm.model.meetings;

import com.jaysonm.meetm.model.home.User;

public class SelectedUser {

    private static SelectedUser userInstance = null;
    private static User selectedUser;

    public static SelectedUser getInstance() {
        if (userInstance == null) { //if there is no instance available... create new one
            userInstance = new SelectedUser();
        }

        return userInstance;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        SelectedUser.selectedUser = selectedUser;
    }
}

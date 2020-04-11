package com.jaysonm.meetm.controllers.meeting;

import com.jaysonm.meetm.model.meetings.Meet;

public interface FirebaseMeetingListener {
    void addMeeting(Meet meet);
    void removeMeeting(Meet meet);
}

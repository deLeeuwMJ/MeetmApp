package com.jaysonm.meetm.controllers.events;

import com.jaysonm.meetm.model.events.Event;

import java.util.ArrayList;

public interface WrapApiListener {

    void onApiStart();

    void onApiSuccess(ArrayList<Event> events);

    void onApiCancelled();

    void onApiFailure(Exception e);
}

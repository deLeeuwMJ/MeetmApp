package com.jaysonm.meetm.model.events;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class EventViewModel extends AndroidViewModel {
    private EventRepository repository;
    private LiveData<List<Event>> allEvents;

    public EventViewModel(@NonNull Application application) {
        super(application);
        repository = new EventRepository(application);
        allEvents = repository.getAllEvents();
    }

    public void insert(Event event) {
        repository.insert(event);
    }

    public void update(Event event) {
        repository.update(event);
    }

    public void delete(Event event) {
        repository.delete(event);
    }

    public void deleteAllEvents() {
        repository.deleteAllEvents();
    }

    public LiveData<List<Event>> getAllEvents() {
        return allEvents;
    }
}

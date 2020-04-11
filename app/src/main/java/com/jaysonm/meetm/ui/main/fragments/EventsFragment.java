package com.jaysonm.meetm.ui.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.events.EventAdapter;
import com.jaysonm.meetm.model.events.Event;
import com.jaysonm.meetm.model.events.EventViewModel;

import java.util.List;

public class EventsFragment extends Fragment {

    private static final String TAG = EventsFragment.class.getSimpleName();

    private EventAdapter eventAdapter;
    private RecyclerView recyclerView;
    private EventViewModel eventViewModel;

    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance() {
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);

        this.recyclerView = view.findViewById(R.id.events_recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        eventViewModel = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        eventViewModel.getAllEvents().observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                eventAdapter.submitList(events);
                eventAdapter.notifyDataSetChanged();
            }
        });

        this.eventAdapter = new EventAdapter(getContext());
        this.recyclerView.setAdapter(this.eventAdapter);
    }
}

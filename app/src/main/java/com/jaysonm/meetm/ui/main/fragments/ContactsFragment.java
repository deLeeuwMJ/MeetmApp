package com.jaysonm.meetm.ui.main.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.contacts.ContactsAdapter;
import com.jaysonm.meetm.controllers.DataAvailableListener;
import com.jaysonm.meetm.model.contacts.FollowedUserFetcher;

public class ContactsFragment extends Fragment implements DataAvailableListener {

    private static final String TAG = ContactsFragment.class.getSimpleName();

    private FollowedUserFetcher followedUserFetcher;
    private ContactsAdapter contactsAdapter;
    private RecyclerView recyclerView;

    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment newInstance() {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        this.recyclerView = view.findViewById(R.id.contact_recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        //get followed user
        followedUserFetcher = FollowedUserFetcher.getInstance();
        followedUserFetcher.setListener(this);
        followedUserFetcher.startFetching();

        this.contactsAdapter = new ContactsAdapter(followedUserFetcher.getList(), getActivity());
        this.recyclerView.setAdapter(this.contactsAdapter);
    }

    @Override
    public void newDataAvailable() {
        this.contactsAdapter.notifyDataSetChanged();
    }
}

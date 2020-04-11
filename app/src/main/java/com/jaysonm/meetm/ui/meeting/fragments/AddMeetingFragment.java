package com.jaysonm.meetm.ui.meeting.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialAutoCompleteTextView;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.ChangeScreenListener;
import com.jaysonm.meetm.controllers.meeting.FirebaseMeetingListener;
import com.jaysonm.meetm.model.FragmentOptions;
import com.jaysonm.meetm.model.meetings.Meet;
import com.jaysonm.meetm.model.meetings.PageViewModel;
import com.jaysonm.meetm.util.NotificationUtil;

import java.util.List;

public class AddMeetingFragment extends Fragment {

    private static final String TAG = AddMeetingFragment.class.getSimpleName();

    private PageViewModel pageViewModel;
    private TextInputEditText name, time, date,location;
    private MaterialButton add;
    private FirebaseMeetingListener firebaseMeetingListener;
    private ChangeScreenListener changeScreenListener;

    public AddMeetingFragment() {
        // Required empty public constructor
    }

    public static AddMeetingFragment newInstance() {
        return new AddMeetingFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pageViewModel = ViewModelProviders.of(getActivity()).get(PageViewModel.class);

        final Observer<List<Meet>> meetingObserver = new Observer<List<Meet>>() {
            @Override
            public void onChanged(List<Meet> meets) {
                Log.d(TAG, "Meeting size changed");
            }
        };

        pageViewModel.getMeetings().observe(getViewLifecycleOwner(), meetingObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_meet_add, container, false);

        name = root.findViewById(R.id.meeting_name);
        time = root.findViewById(R.id.meeting_time);
        date = root.findViewById(R.id.meeting_date);
        location = root.findViewById(R.id.meeting_location);

        add = root.findViewById(R.id.meeting_add_button);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTextEmpty()) {
                    NotificationUtil.showSnackBar(getActivity(), "You didn't fill everything in yet!");
                    return;
                }

                Meet tempMeeting = new Meet(
                        name.getText().toString(),
                        time.getText().toString(),
                        date.getText().toString(),
                        location.getText().toString(),
                        "51.5948 4.7299"
                );

                pageViewModel.addMeeting(tempMeeting);
                firebaseMeetingListener.addMeeting(tempMeeting);
                clearInputFields();
                changeScreenListener.changeToFragment(FragmentOptions.SHOW_MEETINGS);
            }
        });

        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            firebaseMeetingListener = (FirebaseMeetingListener) context;
            changeScreenListener = (ChangeScreenListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnListItemSelectedListener");
        }
    }

    private void clearInputFields() {
        name.getText().clear();
        time.getText().clear();
        date.getText().clear();
        location.getText().clear();
    }

    private boolean isTextEmpty() {
        return name.getText().length() == 0 || time.getText().length() == 0 || date.getText().length() == 0 || location.getText().length() == 0;
    }
}
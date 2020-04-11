package com.jaysonm.meetm.ui.meeting.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.ChangeScreenListener;
import com.jaysonm.meetm.controllers.home.CrossedUserAdapter;
import com.jaysonm.meetm.controllers.meeting.FirebaseMeetingListener;
import com.jaysonm.meetm.controllers.meeting.MeetAdapter;
import com.jaysonm.meetm.model.FragmentOptions;
import com.jaysonm.meetm.model.meetings.Meet;
import com.jaysonm.meetm.model.meetings.PageViewModel;
import com.jaysonm.meetm.model.meetings.SelectedMeeting;
import com.jaysonm.meetm.util.NotificationUtil;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import rm.com.longpresspopup.LongPressPopup;
import rm.com.longpresspopup.LongPressPopupBuilder;
import rm.com.longpresspopup.PopupInflaterListener;

import static com.google.android.material.color.MaterialColors.ALPHA_FULL;

public class MeetingsFragment extends Fragment {

    private static final String TAG = MeetingsFragment.class.getSimpleName();

    private PageViewModel pageViewModel;
    private MeetAdapter meetAdapter;
    private RecyclerView recyclerView;
    private FirebaseMeetingListener firebaseMeetingListener;
    private ChangeScreenListener changeScreenListener;

    public MeetingsFragment() {
        // Required empty public constructor
    }

    public static MeetingsFragment newInstance() {
        return new MeetingsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pageViewModel = ViewModelProviders.of(getActivity()).get(PageViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_meet_show, container, false);

        this.recyclerView = root.findViewById(R.id.meetings_recycler_view);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
                    + " must implement Listener");
        }
    }


    @Override
    public void onStart() {
        super.onStart();

        final Observer<List<Meet>> meetingObserver = new Observer<List<Meet>>() {
            @Override
            public void onChanged(List<Meet> meets) {
                Log.d(TAG, "Meeting size changed");
                meetAdapter.notifyDataSetChanged();
            }
        };

        pageViewModel.getMeetings().observe(getViewLifecycleOwner(), meetingObserver);

        this.meetAdapter = new MeetAdapter(pageViewModel.getMeetings().getValue(), getContext());
        this.recyclerView.setAdapter(this.meetAdapter);
        this.meetAdapter.notifyDataSetChanged();

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.map_blue))
                        .addSwipeLeftActionIcon(R.drawable.ic_map_white_24dp)
                        .addSwipeLeftLabel("Go to")
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.remove_red))
                        .addSwipeRightActionIcon(R.drawable.ic_delete_sweep_white_24dp)
                        .addSwipeRightLabel("Remove")
                        .setSwipeRightLabelColor(Color.WHITE)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Meet meet = meetAdapter.getDataSet().get(position);

                if (direction == ItemTouchHelper.LEFT) { //open map
                    SelectedMeeting selectedMeeting = SelectedMeeting.getInstance();
                    selectedMeeting.setSelectedMeet(meet);

                    changeScreenListener.changeToFragment(FragmentOptions.SHOW_MEETING_MAP);
                    meetAdapter.notifyItemChanged(position);

                } else if (direction == ItemTouchHelper.RIGHT) { //remove meeting
                    firebaseMeetingListener.removeMeeting(meet);
                    meetAdapter.removeMeeting(position);
                    meetAdapter.notifyItemRemoved(position);
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }
}
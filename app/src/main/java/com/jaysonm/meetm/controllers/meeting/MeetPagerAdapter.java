package com.jaysonm.meetm.controllers.meeting;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.jaysonm.meetm.R;
import com.jaysonm.meetm.ui.meeting.fragments.AddMeetingFragment;
import com.jaysonm.meetm.ui.meeting.fragments.MapMeetingFragment;
import com.jaysonm.meetm.ui.meeting.fragments.MeetingsFragment;

public class MeetPagerAdapter extends FragmentStatePagerAdapter {

    private Context context;

    public MeetPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return MeetingsFragment.newInstance();
            case 1:
                return AddMeetingFragment.newInstance();
            case 2:
                return MapMeetingFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.meet_schedule);
            case 1:
                return context.getString(R.string.meet_add);
            case 2:
                return context.getString(R.string.meet_map);
            default:
                return null;
        }
    }
}
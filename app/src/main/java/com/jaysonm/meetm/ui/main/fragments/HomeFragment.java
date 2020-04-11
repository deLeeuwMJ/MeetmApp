package com.jaysonm.meetm.ui.main.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.ChangeScreenListener;
import com.jaysonm.meetm.model.home.CrossedUserIdFetcher;
import com.jaysonm.meetm.model.FragmentOptions;
import com.jaysonm.meetm.util.NotificationUtil;

import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private MaterialButton reloadBttn;
    private ChangeScreenListener changeScreenListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        reloadBttn = view.findViewById(R.id.home_reload_button);

        reloadBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userHasCrossed()){
                    changeScreenListener.changeToFragment(FragmentOptions.MEET);
                } else {
                    NotificationUtil.showSnackBar(getActivity(),"Didn't cross anyone yet!");
                }
            }
        });

        return view;
    }

    private boolean userHasCrossed() {
        List<String> crossedList = CrossedUserIdFetcher.getInstance().getList();
        Log.d(TAG, String.valueOf(crossedList.size()));
        return crossedList.size() != 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ChangeScreenListener) {
            changeScreenListener = (ChangeScreenListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ChangeScreenListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(changeScreenListener != null){
            changeScreenListener = null;
        }
    }
}

package com.jaysonm.meetm.controllers;

import com.jaysonm.meetm.model.FragmentOptions;

public interface ChangeScreenListener {
    void changeToActivity(Class c);
    void changeToFragment(FragmentOptions option);
}

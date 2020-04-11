package com.jaysonm.meetm.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.ChangeScreenListener;
import com.jaysonm.meetm.model.FragmentOptions;
import com.jaysonm.meetm.ui.login.fragments.LoginFragment;
import com.jaysonm.meetm.ui.login.fragments.RegisterFragment;
import com.jaysonm.meetm.ui.main.MainActivity;
import com.jaysonm.meetm.util.FontUtil;

public class LoginActivity extends AppCompatActivity implements ChangeScreenListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private FirebaseAuth firebaseAuth;

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MaterialTextView toolbarTitle = findViewById(R.id.custom_tool_bar_title);
        toolbarTitle.setTypeface(FontUtil.getCustomTypeFace(LoginActivity.this, FontUtil.CUSTOM_FONTS.LEMON));

        AppCompatImageButton toolbarButton = findViewById(R.id.custom_tool_bar_button);
        toolbarButton.setVisibility(View.INVISIBLE);

        firebaseAuth = FirebaseAuth.getInstance();

        if (this.loginFragment == null)
            this.loginFragment = LoginFragment.newInstance();

        if (this.registerFragment == null)
            this.registerFragment = RegisterFragment.newInstance();

        if (findViewById(R.id.login_frame_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.login_frame_container, loginFragment).commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //check if user is signed in
        if (firebaseAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));

            //make sure user can't go back to this activity
            finish();
        }
    }

    private void performFragmentTransaction(Fragment fragment) {
        Fragment newFragment = fragment;
        Bundle args = new Bundle();
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.login_frame_container, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public void changeToActivity(Class c) {
        startActivity(new Intent(LoginActivity.this, c));
        finish();
    }

    @Override
    public void changeToFragment(FragmentOptions option) {
        switch (option){
            case LOGIN:
                performFragmentTransaction(loginFragment);
                break;
            case REGISTER:
                performFragmentTransaction(registerFragment);
                break;
        }
    }
}

package com.jaysonm.meetm.ui.login.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.ChangeScreenListener;
import com.jaysonm.meetm.model.FragmentOptions;
import com.jaysonm.meetm.ui.main.MainActivity;
import com.jaysonm.meetm.util.NotificationUtil;

import java.util.Objects;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = LoginFragment.class.getSimpleName();

    private ChangeScreenListener changeScreenListener;
    private MaterialButton loginButton, registerButton;
    private TextInputEditText emailText, passwordText;

    private FirebaseAuth firebaseAuth;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginButton = view.findViewById(R.id.login_button);
        registerButton = view.findViewById(R.id.register_button);

        emailText = view.findViewById(R.id.email_edit_text);
        passwordText = view.findViewById(R.id.password_edit_text);

        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        return view;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                Log.d(TAG, "Login button clicked");

                firebaseAuth.signInWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                        .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    changeScreenListener.changeToActivity(MainActivity.class);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    NotificationUtil.showSnackBar(Objects.requireNonNull(getActivity()),"Authentication failed.");
                                }
                            }
                        });
                break;
            case R.id.register_button:
                Log.d(TAG, "Register button clicked");
                changeScreenListener.changeToFragment(FragmentOptions.REGISTER);
                break;
            default:
                break;
        }
    }
}

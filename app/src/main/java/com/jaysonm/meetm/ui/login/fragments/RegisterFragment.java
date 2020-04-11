package com.jaysonm.meetm.ui.login.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaysonm.meetm.R;
import com.jaysonm.meetm.controllers.ChangeScreenListener;
import com.jaysonm.meetm.model.FragmentOptions;
import com.jaysonm.meetm.model.home.User;
import com.jaysonm.meetm.ui.main.MainActivity;
import com.jaysonm.meetm.util.NotificationUtil;

import java.util.Map;
import java.util.Objects;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = RegisterFragment.class.getSimpleName();

    private ChangeScreenListener changeScreenListener;
    private MaterialButton cancelButton, registerButton;
    private TextInputEditText nameText, emailText, passwordText, birthdayText;

    private FirebaseAuth firebaseAuth;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        cancelButton = view.findViewById(R.id.cancel_button_rf);
        registerButton = view.findViewById(R.id.register_button_rf);

        nameText = view.findViewById(R.id.name_edit_text);
        emailText = view.findViewById(R.id.email_edit_text);
        passwordText = view.findViewById(R.id.password_edit_text);
        birthdayText = view.findViewById(R.id.birthday_edit_text);

        cancelButton.setOnClickListener(this);
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
        if (changeScreenListener != null) {
            changeScreenListener = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_button_rf:
                Log.d(TAG, "Cancel button clicked");
                changeScreenListener.changeToFragment(FragmentOptions.LOGIN);
                break;
            case R.id.register_button_rf:
                Log.d(TAG, "Register button clicked");

                firebaseAuth.createUserWithEmailAndPassword(emailText.getText().toString(), passwordText.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");

                                    addUserInformationToDB();
                                    changeScreenListener.changeToActivity(MainActivity.class);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    NotificationUtil.showSnackBar(Objects.requireNonNull(getActivity()), "Authentication failed.");
                                }
                            }
                        });

                break;
            default:
                break;
        }
    }

    private void addUserInformationToDB() {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        String userId = user.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("members/" + userId);

        String defaultImage = "https://www.pixelite.co.nz/content/images/2019/09/mateo-avila-chinchilla-x_8oJhYU31k-unsplash.jpg";

        User tempUser = new User(
                nameText.getText().toString(),
                emailText.getText().toString(),
                null,
                null,
                birthdayText.getText().toString(),
                defaultImage,
                null,
                null
        );

        Map<String, Object> userInformation = tempUser.toMap();
        ref.updateChildren(userInformation);

    }
}

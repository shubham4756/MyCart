package com.example.mymall;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends Fragment {

    public ResetPasswordFragment() {
        // Required empty public constructor
    }

    private EditText registeredEmail;
    private Button resetPasswordbtn;
    private TextView goBack, emailIconText;
    private FrameLayout parentFrameLayout;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private ViewGroup emailIconContainer;
    private ImageView emailIcon, tempIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        registeredEmail = view.findViewById(R.id.forgot_pwd_email);
        resetPasswordbtn = view.findViewById(R.id.btn_reset_password);
        goBack = view.findViewById(R.id.forgot_password_go_back);
        parentFrameLayout = getActivity().findViewById(R.id.register_framelayout);

        progressBar = view.findViewById(R.id.forgot_password_progressBar);
        emailIcon = view.findViewById(R.id.forgot_password_mail_box);
        emailIconText = view.findViewById(R.id.forgot_password_mail_recive);
        emailIconContainer = view.findViewById(R.id.forgot_password_email_icon_container);
        tempIcon = view.findViewById(R.id.Temp_email_box);

        firebaseAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SigninFragment());
            }
        });
        resetPasswordbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIconText.setVisibility(View.GONE);
                emailIcon.setVisibility(View.GONE);

                TransitionManager.beginDelayedTransition(emailIconContainer);
                tempIcon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                resetPasswordbtn.setEnabled(false);
                if (isValidMail(registeredEmail.getText().toString())) {
                    firebaseAuth.sendPasswordResetEmail(registeredEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                emailIconText.setText("Recovery email sent successfully ! Check your Inbox");
                                emailIconText.setTextColor(getResources().getColor(R.color.successGreen));
                                emailIcon.setColorFilter(getResources().getColor(R.color.successGreen));
                            } else {
                                String error = task.getException().getMessage();
                                emailIconText.setText(error);
                                emailIconText.setTextColor(getResources().getColor(R.color.successred));
                                emailIcon.setColorFilter(getResources().getColor(R.color.successred));
                                resetPasswordbtn.setEnabled(true);
                            }
                            tempIcon.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            TransitionManager.beginDelayedTransition(emailIconContainer);
                            emailIcon.setVisibility(View.VISIBLE);
                            emailIconText.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    resetPasswordbtn.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "email is not added properly!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

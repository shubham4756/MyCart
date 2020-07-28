package com.example.mymall;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class SigninFragment extends Fragment {

    public SigninFragment() {
        // Required empty public constructor
    }

    private TextView dontHaveAnAccount, forgotPassword;
    private FrameLayout parentFramelayout;
    private EditText email, password;
    private Button signin;
    private ImageButton closesignin;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    public static boolean disableCloseBtn=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        dontHaveAnAccount = view.findViewById(R.id.tv_dont_have_an_account);
        parentFramelayout = getActivity().findViewById(R.id.register_framelayout);
        forgotPassword = view.findViewById(R.id.sign_in_forgot_password);
        email = view.findViewById(R.id.sign_in_email);
        password = view.findViewById(R.id.sign_in_password);
        signin = view.findViewById(R.id.sign_in_btn);
        progressBar = view.findViewById(R.id.sign_in_progressBar);
        closesignin = view.findViewById(R.id.sign_in_close_btn);

        firebaseAuth = FirebaseAuth.getInstance();

        if(disableCloseBtn){
            closesignin.setVisibility(View.GONE);
        }
        else{
            closesignin.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SignUpFragment());
            }
        });

        closesignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainintent();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //below line for accessing global variable
                RegisterActivity.onResetPasswordFragment = true;
                setFragment(new ResetPasswordFragment());
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                if (validation()) {
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mainintent();
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                signin.setEnabled(true);
                                String error = task.getException().getMessage();
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    signin.setEnabled(true);
                    Toast.makeText(getActivity(), "Email id or password Invalid !!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFramelayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void mainintent() {
        if(disableCloseBtn){
            disableCloseBtn=false;
        }
        else {
            Intent mainIntent = new Intent(getActivity(), homePageActivity.class);
            startActivity(mainIntent);
        }
        getActivity().finish();
    }

    private boolean validation() {
        if (!isValidMail(email.getText().toString()))
            return false;
        if (TextUtils.isEmpty(password.getText()))
            return false;
        if (password.getText().length() <= 5)
            return false;
        return true;
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

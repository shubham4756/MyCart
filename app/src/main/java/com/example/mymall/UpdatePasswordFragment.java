package com.example.mymall;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UpdatePasswordFragment extends Fragment {


    public UpdatePasswordFragment() {
        // Required empty public constructor
    }


    private EditText oldPassword,newPassword,confirmNewPassword;
    private Button updateBtn;
    private Dialog loadingDialog;
    private String email;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_update_password, container, false);
        oldPassword=view.findViewById(R.id.old_password);
        newPassword=view.findViewById(R.id.new_password);
        confirmNewPassword=view.findViewById(R.id.confirm_new_password);
        updateBtn=view.findViewById(R.id.update_password_btn);


        ////loading dialong for set data of product details
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ///////end of loading dialong

        email=getArguments().getString("Email");

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBtn.setEnabled(false);
                if(formvalidation()){
                    //update in firebase
                    //reauthentication
                    loadingDialog.show();
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential credential = EmailAuthProvider.getCredential(email,oldPassword.getText().toString());

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    oldPassword.setText("");
                                                    newPassword.setText("");
                                                    confirmNewPassword.setText("");
                                                    Toast.makeText(getContext(),"Password Updated SuccessFully !! ",Toast.LENGTH_SHORT).show();
                                                    getActivity().finish();
                                                } else {
                                                    String error=task.getException().getMessage();
                                                    Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                                                }
                                                loadingDialog.dismiss();
                                                updateBtn.setEnabled(true);
                                            }
                                        });
                                    } else {
                                        loadingDialog.dismiss();
                                        updateBtn.setEnabled(true);
                                        String error=task.getException().getMessage();
                                        Toast.makeText(getContext(),error,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    updateBtn.setEnabled(true);
                }
            }
        });

        return view;
    }
    private boolean formvalidation() {
        if (TextUtils.isEmpty(oldPassword.getText())) {
            oldPassword.setError("password is not added!!");
            return false;
        }
        if (TextUtils.isEmpty(newPassword.getText())) {
            newPassword.setError("password is not added!!");
            return false;
        }
        if (TextUtils.isEmpty(confirmNewPassword.getText())) {
            confirmNewPassword.setError("Confirm password is not added properly !!");
            return false;
        }
        if(oldPassword.getText().length()<=5){
            oldPassword.setError("Password should be atleast 6 Characters !!");
            return false;
        }
        if(newPassword.getText().length()<=5){
            newPassword.setError("Password should be atleast 6 Characters !!");
            return false;
        }
        if(confirmNewPassword.getText().length()<=5){
            confirmNewPassword.setError("Password should be atleast 6 Characters !!");
            return false;
        }
        if (!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
            confirmNewPassword.setError("Confirm password is not matched !!");
            return false;
        }
        return true;
    }
}
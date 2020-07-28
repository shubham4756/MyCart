package com.example.mymall;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class SignUpFragment extends Fragment {

    public SignUpFragment() {
        // Required empty public constructor
    }
    private TextView alreadyHaveAnAccount;
    private FrameLayout parentFrameLayout;
    private EditText fname,lname,address,email,passsword,confirmPassword,mobileNo;
    private ImageButton closeBtn;
    private Button signUpBtn;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private static final String  TAG="773";
    public static boolean disableCloseBtn=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view=inflater.inflate(R.layout.fragment_sign_up, container, false);
         alreadyHaveAnAccount = view.findViewById(R.id.already_have_an_account);
         parentFrameLayout=getActivity().findViewById(R.id.register_framelayout);

         fname=view.findViewById(R.id.sign_up_fname);
         lname=view.findViewById(R.id.sign_up_lname);
         address=view.findViewById(R.id.sign_up_address);
         mobileNo=view.findViewById(R.id.sign_up_phone);
         email=view.findViewById(R.id.sign_up_email);
         passsword=view.findViewById(R.id.sign_up_password);
         confirmPassword=view.findViewById(R.id.sign_up_confirm_password);
         closeBtn=view.findViewById(R.id.sign_up_close_btn);
         signUpBtn=view.findViewById(R.id.btn_sign_up);
         progressBar=view.findViewById(R.id.sign_up_progressBar);

         firebaseAuth=FirebaseAuth.getInstance();
         firebaseFirestore=FirebaseFirestore.getInstance();

        if(disableCloseBtn){
            closeBtn.setVisibility(View.GONE);
        }
        else{
            closeBtn.setVisibility(View.VISIBLE);
        }
         return view;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new SigninFragment());
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainintent();
            }
        });


        signUpBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //todo send data to firebase
                signUpBtn.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                if(formvalidation()){
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),passsword.getText().toString())
                            .addOnCompleteListener(getActivity(),new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()){
                               Map<String,Object> userdata=new HashMap<>();
                               userdata.put("email",email.getText().toString());
                               userdata.put("profile","");
                               userdata.put("fullname",fname.getText().toString());
                               userdata.put("lastname",lname.getText().toString());
                               userdata.put("address",address.getText().toString());
                               userdata.put("mobileno",mobileNo.getText().toString());
                               firebaseFirestore.collection("USERS").document(firebaseAuth.getUid())
                                       .set(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful()){
                                           CollectionReference userDataReference=firebaseFirestore.collection("USERS").document(firebaseAuth.getUid()).collection("USER_DATA");
                                           //TODO: MAPS
                                           Map<String,Object> wishListMap=new HashMap<>();
                                           wishListMap.put("list_size",(long)0);

                                           Map<String,Object> ratingsMap=new HashMap<>();
                                           ratingsMap.put("list_size",(long)0);

                                           Map<String,Object> cartMap=new HashMap<>();
                                           cartMap.put("list_size",(long)0);

                                           Map<String,Object> myAddressesMap=new HashMap<>();
                                           myAddressesMap.put("list_size",(long)0);

                                           Map<String,Object> notificationsMap=new HashMap<>();
                                           notificationsMap.put("list_size",(long)0);

                                           final List<String> documentNames=new ArrayList<>();
                                           documentNames.add("MY_WISHLIST");
                                           documentNames.add("MY_RATINGS");
                                           documentNames.add("MY_CART");
                                           documentNames.add("MY_ADDRESSES");
                                           documentNames.add("MY_NOTIFICATIONS");

                                           List<Map<String,Object> > documentFields=new ArrayList<>();
                                           documentFields.add(wishListMap);
                                           documentFields.add(ratingsMap);
                                           documentFields.add(cartMap);
                                           documentFields.add(myAddressesMap);
                                           documentFields.add(notificationsMap);

                                           //TODO: User all data
                                           for(int x=0;x<documentNames.size();x++){
                                               final int finalX = x;
                                               userDataReference.document(documentNames.get(x))
                                                       .set(documentFields.get(x)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       if(task.isSuccessful()){
                                                           if(finalX == documentNames.size()-1) {
                                                               //TODO: finally user account is created
                                                               mainintent();
                                                           }
                                                       }else{
                                                           progressBar.setVisibility(View.INVISIBLE);
                                                           signUpBtn.setEnabled(true);
                                                           String error=task.getException().getMessage();
                                                           Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
                                                       }
                                                   }
                                               });
                                           }
                                           ////////////end....................

                                       }else{
                                           String error=task.getException().getMessage();
                                           Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });
                           }else{
                               progressBar.setVisibility(View.INVISIBLE);
                               signUpBtn.setEnabled(true);
                               String error=task.getException().getMessage();
                               Toast.makeText(getActivity(),error,Toast.LENGTH_SHORT).show();
                           }
                        }
                    });
                } else{
                    progressBar.setVisibility(View.INVISIBLE);
                    signUpBtn.setEnabled(true);
                }
            }
        });
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
    private void mainintent(){
        if(disableCloseBtn){
            disableCloseBtn=false;
        }
        else {
            Intent mainIntent = new Intent(getActivity(), homePageActivity.class);
            startActivity(mainIntent);
        }
        getActivity().finish();
    }

    private boolean formvalidation() {
        if (TextUtils.isEmpty(fname.getText())) {
            fname.setError("first name is not added!!");
            return false;
        }
        if (TextUtils.isEmpty(lname.getText())) {
            lname.setError("last name is not added !!");
            return false;
        }
        if (TextUtils.isEmpty(address.getText())) {
            address.setError("address is not added !!");
            return false;
        }
        if (!isValidMobile(mobileNo.getText().toString())) {
            mobileNo.setError("mobile number is not added properly !!");
            return false;
        }
        if (!isValidMail(email.getText().toString())) {
            email.setError("email is not added properly !!");
            return false;
        }
        if (TextUtils.isEmpty(passsword.getText())) {
            passsword.setError("password is not added!!");
            return false;
        }
        if (TextUtils.isEmpty(confirmPassword.getText())) {
            confirmPassword.setError("Confirm password is not added properly !!");
            return false;
        }
        if (passsword.getText().length() <= 5) {
            passsword.setError("Password should be atleast 6 Characters !!");
            return false;
        }
        if (!passsword.getText().toString().equals(confirmPassword.getText().toString())) {
            confirmPassword.setError("Confirm password is not matched !!");
            return false;
        }
        return true;
    }

    private boolean isValidMobile(String phone) {
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() > 6 && phone.length() <= 13;
        }
        return false;
    }
    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

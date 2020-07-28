package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(MainActivity.this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }
        // SystemClock.sleep(1);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .update("Last seen", FieldValue.serverTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Intent loginIntent=new Intent(MainActivity.this,homePageActivity.class);
                        startActivity(loginIntent);
                        finish();
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(MainActivity.this,error,Toast.LENGTH_SHORT).show();
                    }
                }});
        }else{
            Intent mainIntent = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(mainIntent);
            finish();

        }
    }
}
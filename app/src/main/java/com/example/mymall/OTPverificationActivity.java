package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OTPverificationActivity extends AppCompatActivity {

    private TextView phoneNo;
    private EditText otp;
    private Button verifyBtn;
    private String userNo;

    private static final String SMS_API_KEY = BuildConfig.SMS_API_KEY;
    private static final String OTP_SMS_ID = BuildConfig.OTP_SMS_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_pverification);
        phoneNo=findViewById(R.id.phone_no);
        otp=findViewById(R.id.otp);
        verifyBtn=findViewById(R.id.verify_btn);
        userNo=getIntent().getStringExtra("mobileNo");
        phoneNo.setText("Verification code has been sent to +91 "+userNo);

        Random random=new Random();
        final int OTPNumber=random.nextInt(999999-111111)+111111;
        String SMS_API = "https://www.fast2sms.com/dev/bulk";

        StringRequest stringRequest=new StringRequest(Request.Method.POST,SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                verifyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(otp.getText().toString().equals(String.valueOf(OTPNumber))){

                            Map<String, Object> updateStatus = new HashMap<>();
                            updateStatus.put("Order Status", "Ordered");
                            final String OrderID=getIntent().getStringExtra("OrderID");
                            FirebaseFirestore.getInstance().collection("ORDERS").document(OrderID).update(updateStatus)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Map<String,Object>  userOrder=new HashMap<>();
                                                userOrder.put("order_id",OrderID);
                                                userOrder.put("time", FieldValue.serverTimestamp());
                                                FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS")
                                                        .document(OrderID).set(userOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()){
                                                            DeliveryActivity.codOrderConfirmed=true;
                                                            finish();
                                                        } else {
                                                            Toast.makeText(OTPverificationActivity.this,"failed to update user's OrderList",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(OTPverificationActivity.this, "Order CANCELLED", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else{
                            Toast.makeText(OTPverificationActivity.this,"incorrect OTP !",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(OTPverificationActivity.this,"failed to send the OTP verification code",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<>();
                headers.put("authorization",SMS_API_KEY);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> body = new HashMap<>();

                body.put("sender_id","FSTSMS");
                body.put("language","english");
                body.put("route","qt");
                body.put("numbers",userNo);
                body.put("message",OTP_SMS_ID);
                body.put("variables","{#BB#}");
                body.put("variables_values",String.valueOf(OTPNumber));

                return body;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue= Volley.newRequestQueue(OTPverificationActivity.this);
        requestQueue.add(stringRequest);
    }
}
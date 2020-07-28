package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.mymall.Adapter.CartAdapter;
import com.example.mymall.Model.CartItemModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity {

    private RecyclerView deliveryRecyclerView;
    private Button changeOrNewAddAddress;
    private TextView totalAmount;
    private TextView fullname;
    private String name,mobileNo;
    private TextView fullAddress;
    private TextView pincode;
    private Button contineBtn;
    public static Dialog loadingDialog;
    private Dialog paymenyMethodDialog;
    private ImageButton paytm;
    private ImageButton cod;
    private TextView codTitle;
    private View divider;
    private String paymentMethod="PAYTM";
    private ConstraintLayout orderConfirmationLayout;
    private TextView orderId;

    private static final String SMS_API_KEY = BuildConfig.SMS_API_KEY;
    private static final String CONFIRMATION_SMS_ID = BuildConfig.CONFIRMATION_SMS_ID;
    private static final String PAYTM_API_KEY = BuildConfig.PAYTM_API_KEY;

    public static CartAdapter cartAdapter;

    private ImageButton continueShoppintbtn;
    private boolean successResponse=false;

    public static boolean fromCart;

    private String order_id;

    public static boolean codOrderConfirmed=false;
    public static boolean getQtyIDs=true;

    private FirebaseFirestore firebaseFirestore;
    public static final int SELECT_ADDRESS = 0;
    public  static List<CartItemModel> cartItemModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final Toolbar toolbar = findViewById(R.id.toolbarDeliveryActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fullname=findViewById(R.id.fullname);
        fullAddress=findViewById(R.id.address);
        pincode=findViewById(R.id.pincode);
        changeOrNewAddAddress = findViewById(R.id.changeOrAddAddresBtn);
        changeOrNewAddAddress.setVisibility(View.VISIBLE);

        deliveryRecyclerView= findViewById(R.id.deliveryRecyclerVeiw);
        totalAmount=findViewById(R.id.total_cart_amount);
        contineBtn=findViewById(R.id.deliveryContinueButton);
        orderConfirmationLayout=findViewById(R.id.order_conformation_layout);
        orderId=findViewById(R.id.order_id);
        continueShoppintbtn=findViewById(R.id.continue_shopping_btn);

        ////loading dialong for set data of product details
        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ///////end of loading dialong

        ////loading dialong for set data of product details
        paymenyMethodDialog = new Dialog(DeliveryActivity.this);
        paymenyMethodDialog.setContentView(R.layout.payment_method);
        paymenyMethodDialog.setCancelable(true);
        paymenyMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymenyMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ///////end of loading dialong

        paytm=paymenyMethodDialog.findViewById(R.id.paytm_btn);
        cod=paymenyMethodDialog.findViewById(R.id.cod_btn);
        order_id= UUID.randomUUID().toString().substring(0,28);      //we manually generating order_id
        codTitle=paymenyMethodDialog.findViewById(R.id.cod_btn_title);
        divider=paymenyMethodDialog.findViewById(R.id.divider19);
        firebaseFirestore=FirebaseFirestore.getInstance();
        getQtyIDs=true;
        successResponse=false;

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        deliveryRecyclerView.setLayoutManager(layoutManager);

        //we first used Dquerires.cartItemModelList but that list also have product which is out of stock so
        // we create new list which we are seting in my_cart_fragment continue button and ProductDetailsActivity buy now btn
        cartAdapter=new CartAdapter(cartItemModelList,totalAmount,false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        changeOrNewAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQtyIDs=false;
                Intent intent  = new Intent(DeliveryActivity.this,MyAddressesActivity.class);
                intent.putExtra("MODE",SELECT_ADDRESS);
                startActivity(intent);
            }
        });

        contineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean allProductAvailable=true;
                for(CartItemModel cartItemModel:cartItemModelList){
                    if(cartItemModel.isQtyError()){
                        allProductAvailable=false;
                        break;
                    }
                    if(cartItemModel.getType()==CartItemModel.CART_ITEM) {
                        if (!cartItemModel.isCOD()) {
                            cod.setEnabled(false);
                            cod.setAlpha(0.5f);
                            codTitle.setAlpha(0.5f);
                            divider.setVisibility(View.GONE);
                            break;
                        } else {
                            cod.setEnabled(true);
                            cod.setAlpha(1f);
                            codTitle.setAlpha(1f);
                            divider.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if(allProductAvailable) {
                    paymenyMethodDialog.show();
                }
            }
        });

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod="COD";
                placeOrderDetails();
            }
        });

        paytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentMethod="PAYTM";
                placeOrderDetails();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //accessing quantity
        if(getQtyIDs) {
            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {

                for(int y=0;y<cartItemModelList.get(x).getProductQuantity();y++){

                    final String quantitiyDocumentName=UUID.randomUUID().toString().substring(0,20);
                    Map<String,Object> timeStamp=new HashMap<>();
                    timeStamp.put("time", FieldValue.serverTimestamp());

                    final int finalX = x;
                    final int finalY = y;

                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY")
                            .document(quantitiyDocumentName).set(timeStamp).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                cartItemModelList.get(finalX).getQtyIDs().add(quantitiyDocumentName);

                                if(finalY +1==cartItemModelList.get(finalX).getProductQuantity()){

                                    firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID()).collection("QUANTITY")
                                            .orderBy("time", Query.Direction.ASCENDING).limit(cartItemModelList.get(finalX)
                                            .getStockQuantity()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){

                                                List<String> serverQuantity=new ArrayList<>();
                                                for(QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){
                                                    serverQuantity.add(queryDocumentSnapshot.getId());
                                                }

                                                long availableQuantity=0;
                                                boolean noLongerAvailable=true;
                                                for(String qtyId:cartItemModelList.get(finalX).getQtyIDs()){

                                                    if(!serverQuantity.contains(qtyId)){
                                                        //Todo: set error for that number of quantity not available
                                                        cartItemModelList.get(finalX).setQtyError(false);
                                                        if(noLongerAvailable){
                                                            cartItemModelList.get(finalX).setInStock(false);
                                                        } else {
                                                            cartItemModelList.get(finalX).setQtyError(true);
                                                            cartItemModelList.get(finalX).setMaxQuantity(availableQuantity);
                                                            Toast.makeText(DeliveryActivity.this, "Sorry ! all products may not be available in required quantity" , Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        availableQuantity++;
                                                        noLongerAvailable=false;
                                                    }

                                                }
                                                cartAdapter.notifyDataSetChanged();
                                            }else{
                                                String error=task.getException().getMessage();
                                                Toast.makeText(DeliveryActivity.this,error,Toast.LENGTH_SHORT).show();
                                            }
                                            loadingDialog.dismiss();
                                        }
                                    });
                                }
                            } else {
                                loadingDialog.dismiss();
                                String error=task.getException().getMessage();
                                Toast.makeText(DeliveryActivity.this,error,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        }else {
            getQtyIDs=true;
        }
        //accessing quantity



        name=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMoblieNo();
        if(!DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMoblieNo().equals("")){
            mobileNo=mobileNo+" or "+DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMoblieNo();
        }
        fullname.setText(name+" - "+mobileNo);
        String flatNo=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlatNo();
        String locality=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String city=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String state=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getState();

        if(landmark.equals("")) {
            fullAddress.setText(flatNo + " " + locality + " " + city + " " + state);
        } else {
            fullAddress.setText(flatNo + " " + locality + " " + landmark + " " + city + " " + state);
        }
        pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPincode());

        if(codOrderConfirmed){
            showConfirmationLayout();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home) {
            Toast.makeText(DeliveryActivity.this,"in option selection",Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        //make product again available
        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {
                if(!successResponse) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY")
                                .document(qtyID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                  //checking is this productQTyid  is last ID
                                if(qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size()-1))){
                                    cartItemModelList.get(finalX).getQtyIDs().clear();
                                }
                            }
                        });
                    }
                }else {
                    //if response is not succeed
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(successResponse){
            finish();
            return ;
        }
        super.onBackPressed();
    }

    private void showConfirmationLayout(){
        successResponse=true;
        codOrderConfirmed=false;
        getQtyIDs=false;

        //make product again available
        for(int x=0;x<cartItemModelList.size()-1;x++) {
            for (String qtyID : cartItemModelList.get(x).getQtyIDs()) {
                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID()).collection("QUANTITY")
                        .document(qtyID).update("user_ID", FirebaseAuth.getInstance().getUid());

            }
        }
        if(homePageActivity.mainActivity!=null){
            homePageActivity.mainActivity.finish();
            homePageActivity.mainActivity=null;
            homePageActivity.showCart=false;
        }else{
            homePageActivity.resetMainActivity=true;
        }
        if(ProductsDetailsActivity.productDetailsActivity!=null){
            ProductsDetailsActivity.productDetailsActivity.finish();
            ProductsDetailsActivity.productDetailsActivity=null;
        }



//        sent confirmation SMS
        String SMS_API = "https://www.fast2sms.com/dev/bulk";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, SMS_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ///nothing
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //nothing
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
                body.put("numbers",mobileNo);
                body.put("message",CONFIRMATION_SMS_ID);
                body.put("variables","{#FF#}");
                body.put("variables_values",String.valueOf(order_id));

                return body;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue= Volley.newRequestQueue(DeliveryActivity.this);
        requestQueue.add(stringRequest);
//        sent confirmation SMS


        if(fromCart){
            loadingDialog.show();
            Map<String,Object> updateCartList=new HashMap<>();
            long cartListSize=0;
            final List<Integer> indexList=new ArrayList<>();
            for(int x=0;x<DBqueries.cartList.size();x++){
                if(!cartItemModelList.get(x).isInStock()){
                    updateCartList.put("product_ID_"+cartListSize,cartItemModelList.get(x).getProductID());
                    cartListSize++;
                }
                else{
                    indexList.add(x);
                }
            }
            updateCartList.put("list_size",cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        DBqueries.cartItemModelList.remove(DBqueries.cartItemModelList.size()-1);
                        for(int x=0;x<indexList.size();x++){
                            DBqueries.cartList.remove(indexList.get(x).intValue());
                            DBqueries.cartItemModelList.remove(indexList.get(x).intValue());
                        }
                        if (my_cart_Fragment.cartAdapter != null) {
                            my_cart_Fragment.cartAdapter.notifyDataSetChanged();
                        }
                    }else{
                        String error = task.getException().getMessage();
                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            });
        }

        contineBtn.setEnabled(false);
        changeOrNewAddAddress.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        orderId.setText("Order ID "+order_id);
        orderConfirmationLayout.setVisibility(View.VISIBLE);
        continueShoppintbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void placeOrderDetails(){
        String userID=FirebaseAuth.getInstance().getUid();
        loadingDialog.show();
        for(CartItemModel cartItemModel:cartItemModelList) {
            if(cartItemModel.getType()==CartItemModel.CART_ITEM) {

                Map<String,Object> orderDetails=new HashMap<>();
                orderDetails.put("ORDER ID",order_id);
                orderDetails.put("Product Id",cartItemModel.getProductID());
                orderDetails.put("Product Image",cartItemModel.getProductImage());
                orderDetails.put("Product Title",cartItemModel.getProductTitle());
                orderDetails.put("User Id",userID);
                orderDetails.put("Product Quantity",cartItemModel.getProductQuantity());
                if(cartItemModel.getCuttedPrice()!=null) {
                    orderDetails.put("Cutted Price", cartItemModel.getCuttedPrice());
                } else {
                    orderDetails.put("Cutted Price","");
                }
                orderDetails.put("Product Price",cartItemModel.getProductPrice());
                if(cartItemModel.getSelectedCoupenId()!=null) {
                    orderDetails.put("Coupen Id", cartItemModel.getSelectedCoupenId());
                } else {
                    orderDetails.put("Coupen Id","");
                }
                if(cartItemModel.getDiscountedPrice()!=null) {
                    orderDetails.put("Discounted Price", cartItemModel.getDiscountedPrice());
                } else {
                    orderDetails.put("Discounted Price", "");
                }

                orderDetails.put("Ordered date",FieldValue.serverTimestamp());
                orderDetails.put("Packed date",FieldValue.serverTimestamp());
                orderDetails.put("Shipped date",FieldValue.serverTimestamp());
                orderDetails.put("Delivered date",FieldValue.serverTimestamp());
                orderDetails.put("Cancelled date",FieldValue.serverTimestamp());

                orderDetails.put("Order Status","Ordered");
                orderDetails.put("Payment Method",paymentMethod);
                orderDetails.put("Address",fullAddress.getText());
                orderDetails.put("FullName",fullname.getText());
                orderDetails.put("Pincode",pincode.getText());
                orderDetails.put("Free Coupens",cartItemModel.getFreeCoupens());
                orderDetails.put("Delivery Price",cartItemModelList.get(cartItemModelList.size()-1).getDeliveryPrice());
                orderDetails.put("Cancellation requested",false);

                firebaseFirestore.collection("ORDERS").document(order_id).collection("OrderItems").document(cartItemModel.getProductID())
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(!task.isSuccessful()){
                            String error=task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this,error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Map<String,Object> orderDetails=new HashMap<>();
                orderDetails.put("Total Items",cartItemModel.getTotalItems());
                orderDetails.put("Total Items Price",cartItemModel.getTotalItemPrice());
                orderDetails.put("Delivery Price",cartItemModel.getDeliveryPrice());
                orderDetails.put("Total Amount",cartItemModel.getTotalAmount());
                orderDetails.put("Saved Amount",cartItemModel.getSavedAmount());
                orderDetails.put("Payment Status","not Paid");
                orderDetails.put("Order Status","Cancelled");
                firebaseFirestore.collection("ORDERS").document(order_id)
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(paymentMethod.equals("PAYTM")){
                                paytm();
                            }else{
                                cod();
                            }
                        } else {
                            String error=task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this,error,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void paytm(){
        getQtyIDs=false;
        paymenyMethodDialog.dismiss();
        loadingDialog.show();
        if (ContextCompat.checkSelfPermission(DeliveryActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DeliveryActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }

        final String M_id = PAYTM_API_KEY;
        final String customer_id= FirebaseAuth.getInstance().getUid();
        String url = "https://somatic-challenge.000webhostapp.com/paytm/generateChecksum.php";
        final String callBackUrl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

        RequestQueue requestQueue= Volley.newRequestQueue(DeliveryActivity.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    if(jsonObject.has("CHECKSUMHASH")){
                        String CHECKSUMHASH=jsonObject.getString("CHECKSUMHASH");

                        PaytmPGService paytmPGService=PaytmPGService.getStagingService();
                        Map<String, String> paramMap = new HashMap<String,String>();
                        paramMap.put( "MID" , M_id);
                        paramMap.put( "ORDER_ID" , order_id);
                        paramMap.put( "CUST_ID" , customer_id);
                        paramMap.put( "CHANNEL_ID" , "WAP");
                        paramMap.put( "TXN_AMOUNT" , totalAmount.getText().toString().substring(3,totalAmount.getText().length()-2));
                        paramMap.put( "WEBSITE" , "WEBSTAGING");
                        paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                        paramMap.put( "CALLBACK_URL",callBackUrl);
                        paramMap.put("CHECKSUMHASH",CHECKSUMHASH);

                        PaytmOrder order=new PaytmOrder((HashMap<String, String>) paramMap);
                        paytmPGService.initialize(order,null);
                        paytmPGService.startPaymentTransaction(DeliveryActivity.this, true, true,
                                new PaytmPaymentTransactionCallback() {
                                    @Override
                                    public void onTransactionResponse(Bundle inResponse) {
                                        //Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                                        if(inResponse.getString("STATUS").equals("TXN_SUCCESS")) {
                                            Map<String, Object> updateStatus = new HashMap<>();
                                            updateStatus.put("Payment Status", "Paid");
                                            updateStatus.put("Order Status", "Ordered");
                                            firebaseFirestore.collection("ORDERS").document(order_id).update(updateStatus)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Map<String,Object>  userOrder=new HashMap<>();
                                                                userOrder.put("order_id",order_id);
                                                                userOrder.put("time",FieldValue.serverTimestamp());
                                                                firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS")
                                                                        .document(order_id).set(userOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){
                                                                            showConfirmationLayout();
                                                                        } else {
                                                                            Toast.makeText(DeliveryActivity.this,"failed to update user's OrderList",Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(DeliveryActivity.this, "Order CANCELLED", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void networkNotAvailable() {
                                        Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void clientAuthenticationFailed(String inErrorMessage) {
                                        Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void someUIErrorOccurred(String inErrorMessage) {
                                        Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage , Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                                        Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onBackPressedCancelTransaction() {
                                        Toast.makeText(getApplicationContext(), "Transaction cancelled" , Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                        Toast.makeText(getApplicationContext(), "Transaction cancelled" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();
                Toast.makeText(DeliveryActivity.this,"Something went wrong!",Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramMap = new HashMap<String,String>();
                paramMap.put( "MID" , M_id);
                paramMap.put( "ORDER_ID" , order_id);
                paramMap.put( "CUST_ID" , customer_id);
                paramMap.put( "CHANNEL_ID" , "WAP");
                paramMap.put( "TXN_AMOUNT" , totalAmount.getText().toString().substring(3,totalAmount.getText().length()-2));
                paramMap.put( "WEBSITE" , "WEBSTAGING");
                paramMap.put( "INDUSTRY_TYPE_ID" , "Retail");
                paramMap.put( "CALLBACK_URL",callBackUrl);
                return paramMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void cod(){
        getQtyIDs=false;
        paymenyMethodDialog.dismiss();
        Intent otpIntent=new Intent(DeliveryActivity.this,OTPverificationActivity.class);
        otpIntent.putExtra("mobileNo",mobileNo.substring(0,10));
        otpIntent.putExtra("OrderID",order_id);
        startActivity(otpIntent);
    }

}

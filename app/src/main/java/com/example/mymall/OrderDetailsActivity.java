package com.example.mymall;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.mymall.Model.MyOrderItemModel;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.nio.channels.UnresolvedAddressException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {

    private int position;
    private TextView title,price,quantity;
    private ImageView productImage,orderedIndicator,packedIndicator,shippedIndicator,deliveredIndicator;
    private ProgressBar o_p_progress,p_s_progress,s_d_progress;
    private TextView orderedTitle,packedTitle,shippedTitle,deliveredTitle;
    private TextView orderedDate,packedDate,shippedDate,deliveredDate;
    private TextView orderedBody,packedBody,shippedBody,deliveredBody;
    private LinearLayout rateNowContainer;
    private int rating;
    private TextView fullName,address,pincode;
    private TextView totalItems,totalItemsPrice,deliveryPrice,totalAmount,savedAmount;
    private Dialog loadingDialog,cancelDialog;
    private SimpleDateFormat simpleDateFormat;
    private Button cancelOrderBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        Toolbar toolbar = findViewById(R.id.order_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Order details");

        ////loading dialong for set data of product details
        loadingDialog = new Dialog(OrderDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ///////end of loading dialong

        ////cancel dialong for set data of product details
        cancelDialog = new Dialog(OrderDetailsActivity.this);
        cancelDialog.setContentView(R.layout.order_cancel_dialog);
        cancelDialog.setCancelable(true);
        cancelDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
//        cancelDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ///////end of cancel dialong

        position=getIntent().getIntExtra("Position",-1);
        final MyOrderItemModel model=DBqueries.myOrderItemModelList.get(position);
        title=findViewById(R.id.product_title);
        price=findViewById(R.id.product_price);
        quantity=findViewById(R.id.product_quantity);

        productImage=findViewById(R.id.product_image);

        orderedIndicator=findViewById(R.id.ordered_indicator);
        packedIndicator=findViewById(R.id.packed_indicator);
        shippedIndicator=findViewById(R.id.shipping_indicator);
        deliveredIndicator=findViewById(R.id.delivered_indicator);

        o_p_progress=findViewById(R.id.ordered_packed_progress);
        p_s_progress=findViewById(R.id.packed_shipping_progress);
        s_d_progress=findViewById(R.id.shipping_delivered_progress);

        orderedTitle=findViewById(R.id.ordered_title);
        packedTitle=findViewById(R.id.packed_title);
        shippedTitle=findViewById(R.id.shipping_title);
        deliveredTitle=findViewById(R.id.delivered_title);

        orderedDate=findViewById(R.id.ordered_date);
        packedDate=findViewById(R.id.packed_date);
        shippedDate=findViewById(R.id.shipping_date);
        deliveredDate=findViewById(R.id.delivered_date);

        orderedBody = findViewById(R.id.ordered_body);
        packedBody=findViewById(R.id.packed_body);
        shippedBody=findViewById(R.id.shipping_body);
        deliveredBody=findViewById(R.id.delivered_body);

        rateNowContainer=findViewById(R.id.rate_now_container);

        fullName=findViewById(R.id.fullname);
        address=findViewById(R.id.address);
        pincode=findViewById(R.id.pincode);

        totalItems=findViewById(R.id.total_items);
        totalItemsPrice=findViewById(R.id.total_item_price);
        deliveryPrice=findViewById(R.id.delivery_price);
        totalAmount=findViewById(R.id.total_price);
        savedAmount=findViewById(R.id.saved_amount);

        cancelOrderBtn=findViewById(R.id.cancel_btn);

        title.setText(model.getProductTitle());
        if(!model.getDiscountedPrice().equals("")) {
            price.setText("Rs."+model.getDiscountedPrice()+"/-");
        } else {
            price.setText("Rs."+model.getProductPrice()+"/-");
        }
        quantity.setText("Qty :"+String.valueOf(model.getProductQuantity()));
        Glide.with(this).load(model.getProductImage()).into(productImage);


        simpleDateFormat=new SimpleDateFormat("EEE,DD MMM YYYY hh:mm aa");

        switch (model.getOrderStatus()){
            case "Ordered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                o_p_progress.setVisibility(View.GONE);
                p_s_progress.setVisibility(View.GONE);
                s_d_progress.setVisibility(View.GONE);

                packedIndicator.setVisibility(View.GONE);
                packedBody.setVisibility(View.GONE);
                packedDate.setVisibility(View.GONE);
                packedTitle.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                break;

            case "Packed":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                o_p_progress.setProgress(100);

                p_s_progress.setVisibility(View.GONE);
                s_d_progress.setVisibility(View.GONE);

                shippedIndicator.setVisibility(View.GONE);
                shippedBody.setVisibility(View.GONE);
                shippedDate.setVisibility(View.GONE);
                shippedTitle.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);

                break;

            case "Shipped":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                o_p_progress.setProgress(100);
                p_s_progress.setProgress(100);

                s_d_progress.setVisibility(View.GONE);

                deliveredIndicator.setVisibility(View.GONE);
                deliveredBody.setVisibility(View.GONE);
                deliveredDate.setVisibility(View.GONE);
                deliveredTitle.setVisibility(View.GONE);
                break;
            case "Out for Delivery":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));

                o_p_progress.setProgress(100);
                p_s_progress.setProgress(100);
                s_d_progress.setProgress(100);

                deliveredTitle.setText("Out for Delivery");
                deliveredBody.setText("Your order is out for delivery.");
                break;
            case "Delivered":
                orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getDeliveredDate())));

                o_p_progress.setProgress(100);
                p_s_progress.setProgress(100);
                s_d_progress.setProgress(100);

                break;

            case "Cancelled":
                if(model.getPackedDate().after(model.getOrderedDate())){

                    if(model.getShippedDate().after(model.getPackedDate())){

                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getShippedDate())));

                        deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successred)));
                        deliveredDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                        deliveredTitle.setText("Cancelled");
                        deliveredBody.setText("Your order has been cancelled.");

                        o_p_progress.setProgress(100);
                        p_s_progress.setProgress(100);
                        s_d_progress.setProgress(100);

                    } else {
                        orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                        packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                        packedDate.setText(String.valueOf(simpleDateFormat.format(model.getPackedDate())));

                        shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successred)));
                        shippedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                        shippedTitle.setText("Cancelled");
                        shippedBody.setText("Your order has been cancelled.");

                        o_p_progress.setProgress(100);
                        p_s_progress.setProgress(100);

                        s_d_progress.setVisibility(View.GONE);

                        deliveredIndicator.setVisibility(View.GONE);
                        deliveredBody.setVisibility(View.GONE);
                        deliveredDate.setVisibility(View.GONE);
                        deliveredTitle.setVisibility(View.GONE);
                    }

                } else {
                    orderedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                    orderedDate.setText(String.valueOf(simpleDateFormat.format(model.getOrderedDate())));

                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successred)));
                    packedDate.setText(String.valueOf(simpleDateFormat.format(model.getCancelledDate())));
                    packedTitle.setText("Cancelled");
                    packedBody.setText("Your order has been cancelled.");

                    o_p_progress.setProgress(100);

                    p_s_progress.setVisibility(View.GONE);
                    s_d_progress.setVisibility(View.GONE);

                    shippedIndicator.setVisibility(View.GONE);
                    shippedBody.setVisibility(View.GONE);
                    shippedDate.setVisibility(View.GONE);
                    shippedTitle.setVisibility(View.GONE);

                    deliveredIndicator.setVisibility(View.GONE);
                    deliveredBody.setVisibility(View.GONE);
                    deliveredDate.setVisibility(View.GONE);
                    deliveredTitle.setVisibility(View.GONE);
                }
                break;
        }

        ///rating layout
        rating=model.getRatings();
        setRating(rating);
        for(int x=0;x<rateNowContainer.getChildCount();x++){
            final int starPosition=x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadingDialog.show();
                    setRating(starPosition);
                    final DocumentReference documentReference= FirebaseFirestore.getInstance().collection("PRODUCTS").document(model.getProductId());
                    FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Object>() {
                        @Nullable
                        @Override
                        public Object apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                            DocumentSnapshot documentSnapshot=transaction.get(documentReference);
                            if(rating!=0){
                                Long increase=documentSnapshot.getLong(starPosition+1+"_star")+1;
                                Long decrease=documentSnapshot.getLong(rating+1+"_star")-1;
                                transaction.update(documentReference,starPosition+1+"_star",increase);
                                transaction.update(documentReference,rating+1+"_star",decrease);
                            } else {
                                Long increase=documentSnapshot.getLong(starPosition+1+"_star")+1;
                                transaction.update(documentReference,starPosition+1+"_star",increase);
                            }
                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Object>() {
                        @Override
                        public void onSuccess(Object o) {
                            Map<String, Object> myRating = new HashMap<>();
                            //   Toast.makeText(ProductsDetailsActivity.this, " ======= after first query run intial reating " + (tempintial + 1), Toast.LENGTH_SHORT).show();
                            if (DBqueries.myRatedIds.contains(model.getProductId())) {
                                myRating.put("rating_" + DBqueries.myRatedIds.indexOf(model.getProductId()), starPosition+1);
                            } else {
                                myRating.put("list_size", (long) DBqueries.myRatedIds.size() + 1);
                                myRating.put("product_ID_" + DBqueries.myRatedIds.size(), model.getProductId());
                                myRating.put("rating_" + DBqueries.myRatings.size(), (long) starPosition + 1);
                            }

                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        DBqueries.myOrderItemModelList.get(position).setRatings(starPosition);
                                        if (DBqueries.myRatedIds.contains(model.getProductId())) {
                                            DBqueries.myRatings.set(DBqueries.myRatedIds.indexOf(model.getProductId()), Long.parseLong(String.valueOf(starPosition + 1)));
                                        } else {
                                            DBqueries.myRatedIds.add(model.getProductId());
                                            DBqueries.myRatings.add(Long.parseLong(String.valueOf(starPosition + 1)));
                                        }
                                    } else {
                                        String error=task.getException().getMessage();
                                        Toast.makeText(OrderDetailsActivity.this,error,Toast.LENGTH_SHORT).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingDialog.dismiss();
                        }
                    });
                }
            });
        }
        ///rating layout

        if(model.isCancellationRequested()){
            cancelOrderBtn.setVisibility(View.VISIBLE);
            cancelOrderBtn.setEnabled(false);
            cancelOrderBtn.setText("Cancellation in process.");
            cancelOrderBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
            cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
        } else {
            if(model.getOrderStatus().equals("Ordered") || model.getOrderStatus().equals("Packed")){
                cancelOrderBtn.setVisibility(View.VISIBLE);
                cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancelDialog.findViewById(R.id.no_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                            }
                        });

                        cancelDialog.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelDialog.dismiss();
                                loadingDialog.show();
                                Map<String,Object> map=new HashMap<>();
                                map.put("Order Id",model.getOrderID());
                                map.put("product Id",model.getProductId());
                                map.put("Order  Cancelled",false);
                                FirebaseFirestore.getInstance().collection("CANCELLED ORDERS").document()
                                        .set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            FirebaseFirestore.getInstance().collection("ORDERS").document(model.getOrderID())
                                                    .collection("OrderItems").document(model.getProductId()).update("Cancellation requested",true)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                model.setCancellationRequested(true);
                                                                cancelOrderBtn.setVisibility(View.VISIBLE);
                                                                cancelOrderBtn.setEnabled(false);
                                                                cancelOrderBtn.setText("Cancellation in process.");
                                                                cancelOrderBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
                                                                cancelOrderBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                                            } else {
                                                                String error=task.getException().getMessage();
                                                                Toast.makeText(OrderDetailsActivity.this,error,Toast.LENGTH_SHORT).show();
                                                            }
                                                            loadingDialog.dismiss();
                                                        }
                                                    });
                                        } else {
                                            loadingDialog.dismiss();
                                            String error=task.getException().getMessage();
                                            Toast.makeText(OrderDetailsActivity.this,error,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                        cancelDialog.show();
                    }
                });
            }
        }


        fullName.setText(model.getFullName());
        address.setText(model.getAddress());
        pincode.setText(model.getPincode());

        totalItems.setText("Price("+model.getProductQuantity()+" items)");

        Long totalItemsPriceValue;
        if(model.getDiscountedPrice().equals("")){
            totalItemsPriceValue=model.getProductQuantity()*Long.valueOf(model.getProductPrice());
            totalItemsPrice.setText("Rs."+totalItemsPriceValue+"/-");
        } else {
            totalItemsPriceValue=model.getProductQuantity()*Long.valueOf(model.getDiscountedPrice());
            totalItemsPrice.setText("Rs."+totalItemsPriceValue+"/-");
        }
        if(model.getDeliveryPrice().equals("FREE")){
            deliveryPrice.setText(model.getDeliveryPrice());
            totalAmount.setText(totalItemsPrice.getText());
        } else {
            deliveryPrice.setText("Rs." + model.getDeliveryPrice() + "/-");
            totalAmount.setText("Rs."+(totalItemsPriceValue+Long.valueOf(model.getDeliveryPrice()))+"/-");
        }
        if(!model.getCuttedPrice().equals("")){
            if(!model.getDiscountedPrice().equals("")){
                savedAmount.setText("You saved Rs."+model.getProductQuantity()*(Long.valueOf(model.getCuttedPrice())-Long.valueOf(model.getDiscountedPrice()))+"/- on this order");
            } else {
                savedAmount.setText("You saved Rs."+model.getProductQuantity()*(Long.valueOf(model.getCuttedPrice())-Long.valueOf(model.getProductPrice()))+"/- on this order");
            }
        } else {
            if(!model.getDiscountedPrice().equals("")){
                savedAmount.setText("You saved Rs."+model.getProductQuantity()*(Long.valueOf(model.getProductPrice())-Long.valueOf(model.getDiscountedPrice()))+"/- on this order");
            } else {
                savedAmount.setText("You saved Rs.0/- on this order");
            }
        }
    }


    private void setRating(int starPosition){
        for(int x=0;x<rateNowContainer.getChildCount();x++){
            ImageView starBtn=(ImageView)rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#817E7E")));
            if(x<=starPosition){
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F88D01")));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.mymall.Adapter;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mymall.DBqueries;
import com.example.mymall.DeliveryActivity;
import com.example.mymall.MainActivity;
import com.example.mymall.Model.CartItemModel;
import com.example.mymall.Model.RewardModel;
import com.example.mymall.ProductsDetailsActivity;
import com.example.mymall.R;
import com.example.mymall.homePageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.MoreObjects;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.view.View.GONE;

public class CartAdapter extends RecyclerView.Adapter {
    private List<CartItemModel> cartItemModelList;
    private TextView cartTotalAmount;
    private boolean showDeleteBtn;
    private int lastpostion=-1;
    public CartAdapter(List<CartItemModel> cartItemModelList,TextView cartTotalAmount,boolean showDeleteBtn) {
        this.cartItemModelList = cartItemModelList;
        this.cartTotalAmount=cartTotalAmount;
        this.showDeleteBtn=showDeleteBtn;
    }

    @Override
    public int getItemViewType(int position) {
        switch(cartItemModelList.get(position).getType()){
            case 0:
                return CartItemModel.CART_ITEM;
            case 1:
                return CartItemModel.TOTAL_AMOUNT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch(viewType){
            case CartItemModel.CART_ITEM:
                View cartItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                return new CartItemViewHolder(cartItemView);
            case CartItemModel.TOTAL_AMOUNT:
                View cartTotalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_total_amount_layout,parent,false);
                return new CartTotalAmountViewholder(cartTotalView);
            default:return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(cartItemModelList.get(position).getType()){
            case CartItemModel.CART_ITEM:
                String productID=cartItemModelList.get(position).getProductID();
                String resource=cartItemModelList.get(position).getProductImage();
                String title=cartItemModelList.get(position).getProductTitle();
                Long freeCoupens=cartItemModelList.get(position).getFreeCoupens();
                String productPrice=cartItemModelList.get(position).getProductPrice();
                String cuttedPrice=cartItemModelList.get(position).getCuttedPrice();
                Long offersApplied=cartItemModelList.get(position).getOffersApplied();
                boolean inStock=cartItemModelList.get(position).isInStock();
                Long productQuantity=cartItemModelList.get(position).getProductQuantity();
                Long maxQuantity=cartItemModelList.get(position).getMaxQuantity();
                boolean qtyError=cartItemModelList.get(position).isQtyError();
                List<String> qtyIds=cartItemModelList.get(position).getQtyIDs();
                long stockQty=cartItemModelList.get(position).getStockQuantity();
                boolean COD=cartItemModelList.get(position).isCOD();
                ((CartItemViewHolder)holder).setItemDetails(productID,resource,title,freeCoupens,productPrice,cuttedPrice,offersApplied,position,inStock,String.valueOf(productQuantity),maxQuantity,qtyError,qtyIds,stockQty,COD);
                break;
            case CartItemModel.TOTAL_AMOUNT:
                int totalItems=0;
                int totalItemPrice=0;
                String deliveryPrice;
                int totalAmount;
                int savedAmount=0;
                for(int x=0;x<cartItemModelList.size();x++){

                    if(cartItemModelList.get(x).getType()==CartItemModel.CART_ITEM && cartItemModelList.get(x).isInStock()){
                        int quantity=Integer.parseInt(String.valueOf(cartItemModelList.get(x).getProductQuantity()));
                        totalItems=totalItems+quantity;
                        if(TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupenId())) {
                            totalItemPrice += Integer.parseInt(cartItemModelList.get(x).getProductPrice())*quantity;
                        } else {
                            totalItemPrice += Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice())*quantity;
                        }
                        if(!TextUtils.isEmpty(cartItemModelList.get(x).getCuttedPrice())){
                            savedAmount+=(Integer.parseInt(cartItemModelList.get(x).getCuttedPrice())-Integer.parseInt(cartItemModelList.get(x).getProductPrice()))*quantity;
                        }
                        if(!TextUtils.isEmpty(cartItemModelList.get(x).getSelectedCoupenId())){
                            savedAmount+=(Integer.parseInt(cartItemModelList.get(x).getProductPrice())-Integer.parseInt(cartItemModelList.get(x).getDiscountedPrice()))*quantity;
                        }

                    }

                }
                if(totalItemPrice>500){
                    deliveryPrice="FREE";
                    totalAmount=totalItemPrice;
                }
                else{
                    deliveryPrice="60";
                    totalAmount=totalItemPrice+60;
                }
                cartItemModelList.get(position).setTotalItems(totalItems);
                cartItemModelList.get(position).setTotalItemPrice(totalItemPrice);
                cartItemModelList.get(position).setDeliveryPrice(deliveryPrice);
                cartItemModelList.get(position).setTotalAmount(totalAmount);
                cartItemModelList.get(position).setSavedAmount(savedAmount);
                ((CartTotalAmountViewholder)holder).setTotalAmount(totalItems,totalItemPrice,deliveryPrice,totalAmount,savedAmount);
                break;
            default:
        }

        ///fade in animation
        if(lastpostion<=position){
            Animation animation= AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastpostion=position;
        }

    }

    @Override
    public int getItemCount() {
        return cartItemModelList.size();
    }
    class CartItemViewHolder extends RecyclerView.ViewHolder{

        private ImageView productImage,freeCoupenIcon;
        private TextView productTitle,freeCoupens,productPrice,cuttedPrice,offersApplied,coupenApplied,productQuantity;
        private LinearLayout coupenRedemptionLayout;
        private LinearLayout deleteBtn;
        private Button redeemBtn;
        private TextView coupenRedemptionBody;
        private ImageView codIndicator;

        ///coupendialog
        private RecyclerView coupensRecyclerView;
        private LinearLayout selectedCoupen;

        private TextView coupenTitle;
        private TextView coupenExpiryDate;
        private TextView coupenBody;
        private LinearLayout applyORremoveBtnContainer;
        private TextView footerText;
        private Button removeCoupenBtn,applyCoupenBtn;
        private String productOriginalPrice;
        private TextView originalPrice;
        private TextView discountedPrice;
        ///coupendialog

        public CartItemViewHolder(View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.product_image);
            productTitle=itemView.findViewById(R.id.product_title);
            freeCoupenIcon=itemView.findViewById(R.id.free_coupen_icon);
            freeCoupens=itemView.findViewById(R.id.tv_free_coupen);
            productPrice=itemView.findViewById(R.id.product_price);
            cuttedPrice=itemView.findViewById(R.id.cuteed_price);
            offersApplied=itemView.findViewById(R.id.offers_applied);
            coupenApplied=itemView.findViewById(R.id.coupens_applied);
            productQuantity=itemView.findViewById(R.id.product_quantity);
            deleteBtn=itemView.findViewById(R.id.remove_item_btn);
            coupenRedemptionBody=itemView.findViewById(R.id.tv_coupon_redeem);
            codIndicator=itemView.findViewById(R.id.cod_indicator);

            coupenRedemptionLayout=itemView.findViewById(R.id.coupen_redeem_layout);
            redeemBtn=itemView.findViewById(R.id.coupen_redeem_btn);
        }
        private void setItemDetails(final String productID, String resource, String title, long freeCoupensNo, final String productpricetext, String cuttedpricetext, long offersAppliedNo, final int position, boolean inStock, final String quantity, final Long maxQuantity, boolean qtyError, final List<String> qtyIds, final long stockQty,final  boolean COD) {
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.home_logo_icon)).into(productImage);
            productTitle.setText(title);

            coupenApplied.setVisibility(View.INVISIBLE);  //by me added

            final Dialog checkCoupenPriceDailog = new Dialog(itemView.getContext());
            checkCoupenPriceDailog.setContentView(R.layout.coupen_redeem_dialog);
            checkCoupenPriceDailog.setCancelable(false);
            checkCoupenPriceDailog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            if(COD){
                codIndicator.setVisibility(View.VISIBLE);
            } else {
                codIndicator.setVisibility(View.INVISIBLE);
            }

            if(inStock) {
                if (freeCoupensNo > 0) {
                    freeCoupenIcon.setVisibility(View.VISIBLE);
                    freeCoupens.setVisibility(View.VISIBLE);
                    if (freeCoupensNo == 1) {
                        freeCoupens.setText("free" + freeCoupensNo + "Coupen");

                    } else {
                        freeCoupens.setText("free" + freeCoupensNo + "Coupens");
                    }
                } else {
                    freeCoupenIcon.setVisibility(View.INVISIBLE);
                    freeCoupens.setVisibility(View.INVISIBLE);
                }
                coupenRedemptionLayout.setVisibility(View.VISIBLE);
                productPrice.setText("Rs."+productpricetext+"/-");
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setText("Rs."+cuttedpricetext+"/-");

                //////// coupen Dialog

                //discounted declare and original Price in upper scope in case of error

                ImageView toggleRecyclerView = checkCoupenPriceDailog.findViewById(R.id.toggle_recyclerview);

                coupensRecyclerView = checkCoupenPriceDailog.findViewById(R.id.coupens_recycler_view);
                selectedCoupen = checkCoupenPriceDailog.findViewById(R.id.selected_coupen);
                coupenTitle = checkCoupenPriceDailog.findViewById(R.id.coupen_title);
                coupenExpiryDate = checkCoupenPriceDailog.findViewById(R.id.coupen_validity);
                coupenBody = checkCoupenPriceDailog.findViewById(R.id.coupen_body);

                removeCoupenBtn=checkCoupenPriceDailog.findViewById(R.id.remove_btn);
                applyCoupenBtn=checkCoupenPriceDailog.findViewById(R.id.apply_btn);
                footerText=checkCoupenPriceDailog.findViewById(R.id.footer_text);
                applyORremoveBtnContainer=checkCoupenPriceDailog.findViewById(R.id.apply_or_remove_btns_container);

                footerText.setVisibility(View.GONE);
                applyORremoveBtnContainer.setVisibility(View.VISIBLE);


                originalPrice = checkCoupenPriceDailog.findViewById(R.id.original_price);
                discountedPrice = checkCoupenPriceDailog.findViewById(R.id.discounted_price);


                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext());
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                coupensRecyclerView.setLayoutManager(layoutManager);

                //Coupon dialog
                originalPrice.setText(productPrice.getText());
                productOriginalPrice = productpricetext;
                MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(position,DBqueries.rewardModelList, true, coupensRecyclerView, selectedCoupen, productOriginalPrice, coupenTitle, coupenExpiryDate, coupenBody, discountedPrice,cartItemModelList);
                coupensRecyclerView.setAdapter(myRewardsAdapter);
                myRewardsAdapter.notifyDataSetChanged();
                //coupen dialog

                applyCoupenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupenId())) {
                            for (RewardModel rewardModel : DBqueries.rewardModelList) {
                                if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())) {
                                    rewardModel.setAlreadyUsed(true);
                                    coupenRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gradient_background));
                                    coupenRedemptionBody.setText(rewardModel.getCoupenBody());
                                    redeemBtn.setText("Coupen");
                                }
                            }
                            cartItemModelList.get(position).setDiscountedPrice(discountedPrice.getText().toString().substring(3, discountedPrice.getText().length() - 2));
                            productPrice.setText(discountedPrice.getText());
                            String offerDiscountedAmt = String.valueOf(Long.valueOf(productpricetext) - Long.valueOf(discountedPrice.getText().toString().substring(3, discountedPrice.getText().length() - 2)));
                            coupenApplied.setText("Coupen applied -Rs." + offerDiscountedAmt + "/-");
                            coupenApplied.setVisibility(View.VISIBLE);
                            notifyItemChanged(cartItemModelList.size()-1);
                            checkCoupenPriceDailog.dismiss();
                        }
                    }
                });
                removeCoupenBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(RewardModel rewardModel:DBqueries.rewardModelList){
                            if(rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())){
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                        coupenTitle.setText("Coupen");
                        coupenExpiryDate.setText("validity");
                        coupenBody.setText("Tap the icon on the top right corner to select your coupen.");
                        coupenApplied.setVisibility(View.INVISIBLE);
                        coupenRedemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.coupenRed));
                        coupenRedemptionBody.setText("Apply your coupen here.");
                        redeemBtn.setText("Redeem");
                        cartItemModelList.get(position).setSelectedCoupenId(null);
                        productPrice.setText("Rs."+productpricetext+"/-");
                        notifyItemChanged(cartItemModelList.size()-1);
                        checkCoupenPriceDailog.dismiss();
                    }
                });

                toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialogRecyclerView();
                    }
                });

                if(!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupenId())) {
                    for (RewardModel rewardModel : DBqueries.rewardModelList) {
                        if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())) {
                            coupenRedemptionLayout.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.reward_gradient_background));
                            coupenRedemptionBody.setText(rewardModel.getCoupenBody());
                            redeemBtn.setText("Coupen");

                            coupenBody.setText(rewardModel.getCoupenBody());
                            if(rewardModel.getType().equals("Discount")){
                                coupenTitle.setText(rewardModel.getType());
                            }else{
                                coupenTitle.setText("FLAT Rs."+rewardModel.getDisORamt()+" OFF");
                            }
                            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM YYYY");
                            coupenExpiryDate.setTextColor(itemView.getContext().getResources().getColor(R.color.coupenPurple));
                            coupenExpiryDate.setText("till " + simpleDateFormat.format(rewardModel.getTimestamp()));
                        }
                    }
                    discountedPrice.setText("Rs."+cartItemModelList.get(position).getDiscountedPrice()+"/-");
                    productPrice.setText("Rs."+cartItemModelList.get(position).getDiscountedPrice()+"/-");
                    String offerDiscountedAmt = String.valueOf(Long.valueOf(productpricetext) - Long.valueOf(cartItemModelList.get(position).getDiscountedPrice()));
                    coupenApplied.setText("Coupen applied -Rs." + offerDiscountedAmt + "/-");
                    coupenApplied.setVisibility(View.VISIBLE);
                }else{
                    coupenApplied.setVisibility(View.INVISIBLE);
                    coupenRedemptionLayout.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.coupenRed));
                    coupenRedemptionBody.setText("Apply your coupen here.");
                    redeemBtn.setText("Redeem");
                }
                ///coupen dialog

                productQuantity.setText("Qty: "+quantity);
                if(!showDeleteBtn) {
                    if (qtyError) {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.holo_red_dark)));
                    } else {
                        productQuantity.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                        productQuantity.setBackgroundTintList(ColorStateList.valueOf(itemView.getContext().getResources().getColor(android.R.color.black)));
                    }
                }
                productQuantity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog quantityDialog = new Dialog(itemView.getContext());
                        quantityDialog.setContentView(R.layout.quantity_dialog);
                        quantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        quantityDialog.setCancelable(false);
                        final EditText quantityNo = quantityDialog.findViewById(R.id.quantity_number);
                        Button cancelBtn = quantityDialog.findViewById(R.id.cancel_btn);
                        Button okBtn = quantityDialog.findViewById(R.id.ok_btn);
                        quantityNo.setHint("Max "+String.valueOf(maxQuantity));

                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quantityDialog.dismiss();
                            }
                        });


                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(!quantityNo.getText().equals("") && Long.valueOf(quantityNo.getText().toString())>0 && Long.valueOf(quantityNo.getText().toString())<=maxQuantity) {

                                    if(itemView.getContext() instanceof homePageActivity){
                                        cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                    }
                                    else {
                                        if (DeliveryActivity.fromCart) {
                                            cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                        } else {
                                            DeliveryActivity.cartItemModelList.get(position).setProductQuantity(Long.valueOf(quantityNo.getText().toString()));
                                        }
                                    }
                                    productQuantity.setText("Qty: " + quantityNo.getText());
                                    notifyItemChanged(cartItemModelList.size()-1);
                                    if(!showDeleteBtn){
                                        DeliveryActivity.loadingDialog.show();
                                        DeliveryActivity.cartItemModelList.get(position).setQtyError(false);
                                        /////Todo: for cheking quantity copied form delivery activity
                                        final int intialQty=Integer.parseInt(quantity);
                                        final int finalQty=Integer.parseInt(quantityNo.getText().toString());
                                        final FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
                                        Toast.makeText(itemView.getContext(),intialQty+" -- "+finalQty,Toast.LENGTH_SHORT).show();
                                        if(finalQty>intialQty) {
                                            //Todo: only add product in firebase
                                            for (int y = 0; y < finalQty-intialQty; y++) {

                                                final String quantitiyDocumentName = UUID.randomUUID().toString().substring(0, 20);
                                                Map<String, Object> timeStamp = new HashMap<>();
                                                timeStamp.put("time", FieldValue.serverTimestamp());

                                                final int finalY = y;

                                                firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY")
                                                        .document(quantitiyDocumentName).set(timeStamp).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        qtyIds.add(quantitiyDocumentName);

                                                        if (finalY + 1 == finalQty-intialQty) {
                                                            firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY")
                                                                    .orderBy("time", Query.Direction.ASCENDING).limit(stockQty).get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {

                                                                        List<String> serverQuantity = new ArrayList<>();
                                                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                                            serverQuantity.add(queryDocumentSnapshot.getId());
                                                                        }
                                                                        long availableQuantity = 0;
                                                                        for (String qtyId : qtyIds) {
                                                                            if (!serverQuantity.contains(qtyId)) {
                                                                                //Todo: set error for that number of quantity not available
                                                                                Toast.makeText(itemView.getContext(),qtyId,Toast.LENGTH_SHORT).show();
                                                                                DeliveryActivity.cartItemModelList.get(position).setQtyError(true);
                                                                                DeliveryActivity.cartItemModelList.get(position).setMaxQuantity(availableQuantity);
                                                                                Toast.makeText(itemView.getContext(),"Sorry ! all products may not be available in required quantity", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            else{
                                                                                availableQuantity++;
                                                                            }
                                                                        }
                                                                       // Toast.makeText(itemView.getContext(),"in adapter greater than "+stockQty,Toast.LENGTH_SHORT).show();
                                                                        DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                                    } else {
                                                                        String error = task.getException().getMessage();
                                                                        Toast.makeText(itemView.getContext(), error, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                    DeliveryActivity.loadingDialog.dismiss();
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        } else if(intialQty>finalQty){
                                            //Todo: delete products
                                            for (int x=0;x<intialQty-finalQty;x++) {
                                                final String qtyID=qtyIds.get(qtyIds.size()-1-x);
                                                final int finalX = x;
                                                firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY")
                                                        .document(qtyID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        qtyIds.remove(qtyID);
                                                       // Toast.makeText(itemView.getContext(),qtyIds.size()-1- finalX,Toast.LENGTH_SHORT).show();
                                                        DeliveryActivity.cartAdapter.notifyDataSetChanged();
                                                        if(finalX+1==intialQty-finalQty){
                                                            DeliveryActivity.loadingDialog.dismiss();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                                else{
                                    Toast.makeText(itemView.getContext(),"Max Quantity: "+maxQuantity, Toast.LENGTH_SHORT).show();
                                }
                                quantityDialog.dismiss();
                            }
                        });
                        quantityDialog.show();
                    }
                });
                ///done

                if (offersAppliedNo > 0) {
                    offersApplied.setVisibility(View.VISIBLE);
                    String offerDiscountedAmt=String.valueOf(Long.valueOf(cuttedpricetext)-Long.valueOf(productpricetext));
                    offersApplied.setText("Offer applied -Rs."+offerDiscountedAmt+"/-");
                } else {
                    offersApplied.setVisibility(View.INVISIBLE);
                }

            } else {
                coupenRedemptionLayout.setVisibility(View.GONE);
                productPrice.setText("Out of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimaryDark));
                cuttedPrice.setText("");

                //todo: handle product quantity selection drop down
                productQuantity.setVisibility(View.INVISIBLE);
                freeCoupens.setVisibility(View.INVISIBLE);
                coupenApplied.setVisibility(View.INVISIBLE);
                offersApplied.setVisibility(View.INVISIBLE);
                freeCoupenIcon.setVisibility(View.INVISIBLE);
            }

            if(showDeleteBtn){
                deleteBtn.setVisibility(View.VISIBLE);
            }
            else{
                deleteBtn.setVisibility(View.GONE);
            }



            redeemBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (RewardModel rewardModel : DBqueries.rewardModelList) {
                        if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())) {
                            rewardModel.setAlreadyUsed(false);
                        }
                    }
                    checkCoupenPriceDailog.show();
                }
            });


            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!TextUtils.isEmpty(cartItemModelList.get(position).getSelectedCoupenId())) {
                        for (RewardModel rewardModel : DBqueries.rewardModelList) {
                            if (rewardModel.getCoupenId().equals(cartItemModelList.get(position).getSelectedCoupenId())) {
                                rewardModel.setAlreadyUsed(false);
                            }
                        }
                    }
                    if(!ProductsDetailsActivity.runningCart_query){
                        ProductsDetailsActivity.runningCart_query=true;
                        DBqueries.removeFromCart(position,itemView.getContext(),cartTotalAmount);
                    }
                }
            });
        }
        private void showDialogRecyclerView() {
            if (coupensRecyclerView.getVisibility() == View.GONE) {
                coupensRecyclerView.setVisibility(View.VISIBLE);
                selectedCoupen.setVisibility(View.GONE);
            } else {
                coupensRecyclerView.setVisibility(View.GONE);
                selectedCoupen.setVisibility(View.VISIBLE);
            }
        }
    }


    class CartTotalAmountViewholder extends RecyclerView.ViewHolder {
        private TextView totalItems,totalItemPrice,deliveryPrice,totalAmount,savedAmount;
        public CartTotalAmountViewholder(@NonNull View itemView) {
            super(itemView);

            totalItems=itemView.findViewById(R.id.total_items);
            totalItemPrice=itemView.findViewById(R.id.total_item_price);
            totalAmount = itemView.findViewById(R.id.total_price);
            deliveryPrice=itemView.findViewById(R.id.delivery_price);
            savedAmount=itemView.findViewById(R.id.saved_amount);
        }
        private void setTotalAmount(int totalItemText,int totalItemPriceText,String deliveryPriceText,int totalAmountText,int savedAmountText){
            totalItems.setText("Price("+totalItemText+" items)");
            totalItemPrice.setText("Rs."+totalItemPriceText+"/-");
            if (deliveryPriceText.equals("FREE")){
                deliveryPrice.setText(deliveryPriceText);
            }
            else{
                deliveryPrice.setText("Rs."+deliveryPriceText+"/-");
            }
            totalAmount.setText("Rs."+totalAmountText+"/-");
            cartTotalAmount.setText("Rs."+totalAmountText+"/-");
            savedAmount.setText("You saved Rs."+savedAmountText+"/- on this order");
//         error cheking   Toast.makeText(itemView.getContext(),"here === "+totalItemText+" "+totalItemPriceText,Toast.LENGTH_SHORT).show();
            LinearLayout parent=(LinearLayout) cartTotalAmount.getParent().getParent();
            if(totalItemPriceText==0){
                if(DeliveryActivity.fromCart) {
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                    DeliveryActivity.cartItemModelList.remove(DeliveryActivity.cartItemModelList.size()-1);
                }
                if(showDeleteBtn){
                    cartItemModelList.remove(cartItemModelList.size() - 1);
                }
                //todo: hide cartfragment linearlayout
             // error cheking   Toast.makeText(itemView.getContext(),"here --- "+totalItemText+" "+totalItemPriceText,Toast.LENGTH_SHORT).show();
                parent.setVisibility(View.GONE);
            } else{
                parent.setVisibility(View.VISIBLE);
            }
        }
    }
}

package com.example.mymall;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mymall.Adapter.CartAdapter;
import com.example.mymall.Adapter.CategoryAdapter;
import com.example.mymall.Adapter.HomePageAdapter;
import com.example.mymall.Adapter.MyOrderAdapter;
import com.example.mymall.Model.AddressesModel;
import com.example.mymall.Model.CartItemModel;
import com.example.mymall.Model.CategoryModel;
import com.example.mymall.Model.HomePageModel;
import com.example.mymall.Model.HorizontalProductScrollModel;
import com.example.mymall.Model.MyOrderItemModel;
import com.example.mymall.Model.NotificationModel;
import com.example.mymall.Model.RewardModel;
import com.example.mymall.Model.SliderModel;
import com.example.mymall.Model.WishlistModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;

public class DBqueries {

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static String email,fullname,profile;

    public static List<CategoryModel> categoryModelList = new ArrayList<>();

    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadedCatgoriesNames = new ArrayList<>();
    //for user wishlist
    public static List<String> wishList = new ArrayList<>();
    public static List<WishlistModel> wishlistModelList = new ArrayList<>();
    //for user ratings
    public static List<String> myRatedIds = new ArrayList<>();
    public static List<Long> myRatings = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    public static int selectedAddress=-1;
    public static List<AddressesModel> addressesModelList=new ArrayList<>();

    public static List<RewardModel> rewardModelList=new ArrayList<>();

    public static List<MyOrderItemModel> myOrderItemModelList=new ArrayList<>();

    public static List<NotificationModel> notificationModelList= new ArrayList<>();
    private static ListenerRegistration registration;

    //bcz we create fake list so we using recyclerview as a parameter
    public static void loadCategories(final RecyclerView categoryRecyclerView, final Context context) {
        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(), documentSnapshot.get("categoryName").toString()));
                            }
                            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                            categoryRecyclerView.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    ///for make fake list we use parameter recyclerview and position
    public static void loadFragmentData(final RecyclerView homePageRecyclerView, final Context context, final int index, String categoryName) {

        firebaseFirestore.collection("CATEGORIES")
                .document(categoryName.toUpperCase())
                .collection("TOP_DEALS").orderBy("index").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        long type = (long) documentSnapshot.get("view_type");
                        if (type == 0) {
                            //for banner
                            List<SliderModel> sliderModelList = new ArrayList<>();
                            long no_of_banners = (long) documentSnapshot.get("no_of_banners");
                            for (int x = 1; x <= no_of_banners; x++) {
                                sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString(), "#FFA751"));
                            }
                            lists.get(index).add(new HomePageModel(0, sliderModelList));
                        } else if (type == 1) {
                            //for horizontal product view
                            List<WishlistModel> viewAllProductList = new ArrayList<>();
                            List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();

                            long no_of_product = (long) documentSnapshot.get("no_of_products");
                            for (int x = 1; x <= no_of_product; x++) {
                                horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString(),
                                        documentSnapshot.get("product_image_" + x).toString(), documentSnapshot.get("product_title_" + x).toString(),
                                        documentSnapshot.get("product_subtitle_" + x).toString(), documentSnapshot.get("product_price_" + x).toString()));

                                viewAllProductList.add(new WishlistModel(documentSnapshot.get("product_ID_" + x).toString(), documentSnapshot.get("product_image_" + x).toString(),
                                        documentSnapshot.get("product_full_title_" + x).toString(),
                                        (long) documentSnapshot.get("free_coupens_" + x),
                                        documentSnapshot.get("average_rating_" + x).toString(),
                                        (long) documentSnapshot.get("total_ratings_" + x),
                                        documentSnapshot.get("product_price_" + x).toString(),
                                        documentSnapshot.get("cutted_price_" + x).toString(),
                                        (boolean) documentSnapshot.get("COD_" + x),(boolean)documentSnapshot.get("in_stock_"+x)));
                            }
                            lists.get(index).add(new HomePageModel(1, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductScrollModelList, viewAllProductList));

                        } else if (type == 2) {
                            //for grid product view
                            List<HorizontalProductScrollModel> gridProductScrollModelList = new ArrayList<>();

                            long no_of_product = (long) documentSnapshot.get("no_of_product");
                            for (int x = 1; x <= no_of_product; x++) {
                                gridProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString(),
                                        documentSnapshot.get("product_image_" + x).toString(), documentSnapshot.get("product_title_" + x).toString(),
                                        documentSnapshot.get("product_subtitle_" + x).toString(), documentSnapshot.get("product_price_" + x).toString()));
                            }
                            lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), gridProductScrollModelList));

                        } else {
                            return;
                        }
                    }
                    HomePageAdapter homePageAdapter = new HomePageAdapter(lists.get(index));
                    homePageRecyclerView.setAdapter(homePageAdapter);
                    homePageAdapter.notifyDataSetChanged();
                    HomeFragment.swipeRefreshLayout.setRefreshing(false);
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void loadWishList(final Context context, final Dialog dialog, final boolean loadProductData) {
        wishList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                .document("MY_WISHLIST").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        wishList.add(task.getResult().get("product_ID_" + x).toString());

                        if (DBqueries.wishList.contains(ProductsDetailsActivity.productID)) {
                            ProductsDetailsActivity.ALREADY_ADDED_TO_WISH_LIST = true;
                            if (ProductsDetailsActivity.addToWishlistBtn != null) {
                                ProductsDetailsActivity.addToWishlistBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.successred));
                            }
                        } else {
                            if (ProductsDetailsActivity.addToWishlistBtn != null) {
                                ProductsDetailsActivity.addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                            }
                            ProductsDetailsActivity.ALREADY_ADDED_TO_WISH_LIST = false;
                        }

                        if (loadProductData) {
                            wishlistModelList.clear();
                            final String prodcutId = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(prodcutId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        //Todo: cheak that much products are available or not
                                        final DocumentSnapshot documentSnapshot=task.getResult();
                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(prodcutId).collection("QUANTITY")
                                                .orderBy("time", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    if(task.getResult().getDocuments().size()<(long)documentSnapshot.get("stock_quantity")){
                                                        //that much products are available
                                                        wishlistModelList.add(new WishlistModel(prodcutId, documentSnapshot.get("product_image_1").toString(),
                                                                documentSnapshot.get("product_title").toString(),
                                                                (long) documentSnapshot.get("free_coupens"),
                                                                documentSnapshot.get("average_rating").toString(),
                                                                (long) documentSnapshot.get("total_ratings"),
                                                                documentSnapshot.get("product_price").toString(),
                                                                documentSnapshot.get("cutted_price").toString(),
                                                                (boolean) documentSnapshot.get("COD"),
                                                                true));
                                                    }else{
                                                        //that much products are not available
                                                        wishlistModelList.add(new WishlistModel(prodcutId, documentSnapshot.get("product_image_1").toString(),
                                                                documentSnapshot.get("product_title").toString(),
                                                                (long) documentSnapshot.get("free_coupens"),
                                                                documentSnapshot.get("average_rating").toString(),
                                                                (long) documentSnapshot.get("total_ratings"),
                                                                documentSnapshot.get("product_price").toString(),
                                                                documentSnapshot.get("cutted_price").toString(),
                                                                (boolean) documentSnapshot.get("COD"),
                                                                false));
                                                    }
                                                    MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
                                                }else{
                                                    String error=task.getException().getMessage();
                                                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        ///////Todo: DONE cheak that much products are available or not


                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                    ProductsDetailsActivity.runningWishList_query = false;
                                }
                            });
                        }
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromWishList(final int index, final Context context) {
        final String removedProductId=wishList.get(index);
        wishList.remove(index);
        Map<String, Object> updateWishList = new HashMap<>();
        for (int x = 0; x < wishList.size(); x++) {
            updateWishList.put("product_ID_" + x, wishList.get(x));
        }
        updateWishList.put("list_size", (long) wishList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST")
                .set(updateWishList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (wishlistModelList.size() != 0) {
                        wishlistModelList.remove(index);
                        MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
                    }
                    ProductsDetailsActivity.ALREADY_ADDED_TO_WISH_LIST = false;
                    if (ProductsDetailsActivity.addToWishlistBtn != null) {
                        ProductsDetailsActivity.addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                    }
                    Toast.makeText(context, "Removed successfully!!", Toast.LENGTH_SHORT).show();
                } else {
                    wishList.add(index,removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                ProductsDetailsActivity.runningWishList_query = false;
            }
        });
    }

    public static void loadRatingList(final Context context) {
        if (!ProductsDetailsActivity.runningRating_query) {
            ProductsDetailsActivity.runningRating_query = true;
            myRatedIds.clear();
            myRatings.clear();
            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_RATINGS")
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> orderProductsIds=new ArrayList<>();
                        for(int x=0;x<myOrderItemModelList.size();x++){
                            orderProductsIds.add(myOrderItemModelList.get(x).getProductId());
                        }
                        for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                            myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
                            myRatings.add((long) task.getResult().get("rating_" + x));
                            if (task.getResult().get("product_ID_" + x).toString().equals(ProductsDetailsActivity.productID) && ProductsDetailsActivity.rateNowContainer != null) {
                                ProductsDetailsActivity.intialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                                if(ProductsDetailsActivity.rateNowContainer!=null) {
                                    ProductsDetailsActivity.setRating(ProductsDetailsActivity.intialRating);
                                }
                            }
                            if(orderProductsIds.contains(task.getResult().get("product_ID_" + x).toString())){
                                myOrderItemModelList.get(orderProductsIds.indexOf(task.getResult().get("product_ID_" + x).toString())).setRatings(Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1);
                            }

//                            if(orderProductsIds.contains(task.getResult().get("product_ID_"+x).toString())){
//                                for(MyOrderItemModel myOrderItemModel:myOrderItemModelList){
//                                    if(myOrderItemModel.getProductId().equals(task.getResult().get("product_ID_"+x))){
//                                        myOrderItemModel.setRatings(Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))));
//                                    }
//                                }
//                            }
                        }
                        if(MyOrdersFragment.myOrderAdapter!=null){
                            MyOrdersFragment.myOrderAdapter.notifyDataSetChanged();
                        }
                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    ProductsDetailsActivity.runningRating_query=false;
                }
            });
        }
    }

    public static void loadCartList(final Context context, final Dialog dialog, final boolean loadProductData, final TextView badgeCount,final TextView cartTotalAmount) {
        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                .document("MY_CART").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        cartList.add(task.getResult().get("product_ID_" + x).toString());

                        if (DBqueries.cartList.contains(ProductsDetailsActivity.productID)) {
                            ProductsDetailsActivity.ALREADY_ADDED_TO_CART = true;
                        } else {
                            ProductsDetailsActivity.ALREADY_ADDED_TO_CART = false;
                        }
                        if (loadProductData) {
                            cartItemModelList.clear();
                            final String prodcutId = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(prodcutId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        //Todo: cheak that much products are available or not
                                        final DocumentSnapshot documentSnapshot=task.getResult();
                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(prodcutId).collection("QUANTITY")
                                                .orderBy("time", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    int index=0;
                                                    if(cartList.size()>=2){
                                                        index=cartList.size()-2;
                                                    }

                                                    if(task.getResult().getDocuments().size()<(long)documentSnapshot.get("stock_quantity")){
                                                        //that much products are available
                                                        //index bcz we add product befor total amount
                                                        cartItemModelList.add(index,new CartItemModel(documentSnapshot.getBoolean("COD"),
                                                                CartItemModel.CART_ITEM,
                                                                prodcutId,
                                                                documentSnapshot.get("product_image_1").toString(),
                                                                documentSnapshot.get("product_title").toString(),
                                                                (long) documentSnapshot.get("free_coupens"),
                                                                documentSnapshot.get("product_price").toString(),
                                                                documentSnapshot.get("cutted_price").toString(),
                                                                (long) 1,
                                                                (long) documentSnapshot.get("offers_applied"),
                                                                (long) 0,
                                                                true,
                                                                (long)documentSnapshot.get("max-quantity"),
                                                                (long)documentSnapshot.get("stock_quantity")));
                                                    }else{
                                                        //that much products are not available
                                                        //index bcz we add product befor total amount
                                                        cartItemModelList.add(index,new CartItemModel(documentSnapshot.getBoolean("COD"),
                                                                CartItemModel.CART_ITEM, prodcutId,
                                                                documentSnapshot.get("product_image_1").toString(),
                                                                documentSnapshot.get("product_title").toString(),
                                                                (long) documentSnapshot.get("free_coupens"),
                                                                documentSnapshot.get("product_price").toString(),
                                                                documentSnapshot.get("cutted_price").toString(),
                                                                (long) 1,
                                                                (long) documentSnapshot.get("offers_applied"),
                                                                (long) 0,
                                                                false,
                                                                (long)documentSnapshot.get("max-quantity"),
                                                                (long)documentSnapshot.get("stock_quantity")));
                                                    }

                                                    ///arrange cartlist
                                                    LinearLayout parent=(LinearLayout) cartTotalAmount.getParent().getParent();
                                                    if(cartItemModelList.size()==1){
                                                        cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                                        parent.setVisibility(View.VISIBLE);
                                                    }
                                                    else{
                                                        parent.setVisibility(View.GONE);
                                                    }
                                                    if(cartList.size()==0){
                                                        cartItemModelList.clear();
                                                    }
                                                    // Toast.makeText(context,"in DBquires load cart data",Toast.LENGTH_SHORT).show();
                                                    my_cart_Fragment.cartAdapter.notifyDataSetChanged();
                                                    ///end arrangement of cartlist


                                                }else{
                                                    String error=task.getException().getMessage();
                                                    Toast.makeText(context,error,Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        ///////Todo: DONE cheak that much products are available or not



                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                    if(cartList.size()!=0){
                        badgeCount.setVisibility(View.VISIBLE);
                    }
                    else{
                        badgeCount.setVisibility(View.INVISIBLE);
                    }
                    if(DBqueries.cartList.size()>99){
                        badgeCount.setText("99");
                    } else {
                        badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                    }
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromCart(final int index, final Context context, final TextView cartTotalAmount) {
        final String removedProductId=cartList.get(index);
        cartList.remove(index);
        Map<String, Object> updateCartList = new HashMap<>();
        for (int x = 0; x < cartList.size(); x++) {
            updateCartList.put("product_ID_" + x, cartList.get(x));
        }
        updateCartList.put("list_size", (long) cartList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_CART")
                .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (cartItemModelList.size() != 0) {
                        cartItemModelList.remove(index);
                        my_cart_Fragment.cartAdapter.notifyDataSetChanged();
                    }
                    if(cartList.size()==0){
                        LinearLayout parent=(LinearLayout) cartTotalAmount.getParent().getParent();
                        parent.setVisibility(View.GONE);
                        cartItemModelList.clear();
                    }
                   // ProductsDetailsActivity.ALREADY_ADDED_TO_CART = false;
                    Toast.makeText(context, "Removed successfully!!", Toast.LENGTH_SHORT).show();
                } else {
                    cartList.add(index,removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                ProductsDetailsActivity.runningCart_query = false;
            }
        });
    }

    public static void loadAddresses(final Context context, final Dialog loadingDialog, final boolean gotoDeliveryActivity){
        addressesModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Intent deliveryIntent=null;
                    if((long)task.getResult().get("list_size")==0){
                        deliveryIntent = new Intent(context,AddAddressActivity.class);
                        deliveryIntent.putExtra("INTENT","deliveryIntent");
                    } else{
                        for(long x=1;x<(long)task.getResult().get("list_size")+1;x++){
                            addressesModelList.add(new AddressesModel(task.getResult().getBoolean("selected_"+x)
                                    ,task.getResult().getString("city_"+x)
                                    ,task.getResult().getString("locality_"+x)
                                    ,task.getResult().getString("flat_no_"+x)
                                    ,task.getResult().getString("pincode_"+x)
                                    ,task.getResult().getString("landmark_"+x)
                                    ,task.getResult().getString("name_"+x)
                                    ,task.getResult().getString("mobile_no_"+x)
                                    ,task.getResult().getString("alternate_mobile_no_"+x)
                                    ,task.getResult().getString("state_"+x)));

                            if((boolean)task.getResult().get("selected_"+x)){
                                selectedAddress=Integer.parseInt(String.valueOf(x-1));
                            }
                        }
                        if(gotoDeliveryActivity) {
                            deliveryIntent = new Intent(context, DeliveryActivity.class);
                        }
                    }
                    if(gotoDeliveryActivity) {
                        context.startActivity(deliveryIntent);
                    }
                }else{
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });
    }

    public static void loadRewards(final Context context, final Dialog loadingDialog,final boolean onRewardFragment){
        rewardModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            final Date lastSeenDate=task.getResult().getDate("Last seen");

                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_REWARDS").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                                    if (documentSnapshot.get("type").toString().equals("Discount") && lastSeenDate.before(documentSnapshot.getDate("validity"))) {
                                                        rewardModelList.add(new RewardModel(documentSnapshot.getId()
                                                                ,documentSnapshot.get("type").toString()
                                                                , documentSnapshot.get("lower_limit").toString()
                                                                , documentSnapshot.get("upper_limit").toString()
                                                                , documentSnapshot.get("percentage").toString()
                                                                , documentSnapshot.get("body").toString()
                                                                , documentSnapshot.getTimestamp("validity").toDate()
                                                                , (boolean)documentSnapshot.get("already_used")));
                                                    } else if(documentSnapshot.get("type").toString().equals("Flat Rs.* OFF") && lastSeenDate.before(documentSnapshot.getDate("validity"))){
                                                        rewardModelList.add(new RewardModel(documentSnapshot.getId()
                                                                ,documentSnapshot.get("type").toString()
                                                                , documentSnapshot.get("lower_limit").toString()
                                                                , documentSnapshot.get("upper_limit").toString()
                                                                , documentSnapshot.get("amount").toString()
                                                                , documentSnapshot.get("body").toString()
                                                                , documentSnapshot.getTimestamp("validity").toDate()
                                                                , (boolean)documentSnapshot.get("already_used")));
                                                    }
                                                }
                                                if(onRewardFragment) {
                                                    MyRewardsFragment.myRewardsAdapter.notifyDataSetChanged();
                                                }
                                           //debug     Toast.makeText(context,"in queries "+rewardModelList.size(),Toast.LENGTH_SHORT).show();
                                            } else{
                                                String error = task.getException().getMessage();
                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                            }
                                            loadingDialog.dismiss();
                                        }
                                    });

                        }else{
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public static void loadOrders(final Context context, @Nullable final MyOrderAdapter myOrderAdapter, final Dialog loadingDialog){
        myOrderItemModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_ORDERS").orderBy("time", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                        firebaseFirestore.collection("ORDERS").document(documentSnapshot.getString("order_id"))
                                .collection("OrderItems").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()) {
                                            for (DocumentSnapshot orderItems : task.getResult().getDocuments()) {

                                                final MyOrderItemModel myOrderItemModel = new MyOrderItemModel(orderItems.getString("Product Id")
                                                        , orderItems.getString("Order Status"), orderItems.getString("Address"), orderItems.getString("Coupen Id")
                                                        , orderItems.getString("Cutted Price"), orderItems.getDate("Ordered date"), orderItems.getDate("Packed date")
                                                        , orderItems.getDate("Shipped date"), orderItems.getDate("Delivered date"), orderItems.getDate("Cancelled date")
                                                        , orderItems.getString("Discounted Price"), orderItems.getLong("Free Coupens"), orderItems.getString("FullName")
                                                        , orderItems.getString("ORDER ID"), orderItems.getString("Payment Method"), orderItems.getString("Pincode")
                                                        , orderItems.getString("Product Price"), orderItems.getLong("Product Quantity"), orderItems.getString("User Id")
                                                        , orderItems.getString("Product Image"), orderItems.getString("Product Title"),orderItems.getString("Delivery Price")
                                                        ,orderItems.getBoolean("Cancellation requested"));
                                                myOrderItemModelList.add(myOrderItemModel);
                                            }
                                            loadRatingList(context);
                                            if(myOrderAdapter!=null) {
                                                myOrderAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                        }
                                        loadingDialog.dismiss();
                                    }
                                });
                    }
                } else {
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void checkNotifications(boolean remove,@Nullable final TextView notifyCount) {

        if (remove) {
            registration.remove();
        } else {
            registration = firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_NOTIFICATIONS")
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (documentSnapshot != null && documentSnapshot.exists()) {
                                notificationModelList.clear();
                                int unread=0;
                                for (long x = 0; x < (long) documentSnapshot.get("list_size"); x++) {
                                    notificationModelList.add(0,new NotificationModel(documentSnapshot.get("Image_" + x).toString()
                                            , documentSnapshot.get("Body_" + x).toString()
                                            , documentSnapshot.getBoolean("Readed_" + x)));

                                    if(!documentSnapshot.getBoolean("Readed_" + x)){
                                        unread++;
                                        if(notifyCount!=null){
                                            if(unread>0) {
                                                notifyCount.setVisibility(View.VISIBLE);
                                                if (unread > 99) {
                                                    notifyCount.setText("99");
                                                } else {
                                                    notifyCount.setText(String.valueOf(unread));
                                                }
                                            }else {
                                                notifyCount.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    }

                                }
                                if(unread==0){
                                    notifyCount.setVisibility(View.INVISIBLE);
                                }
                                if (NotificationActivity.adapter != null) {
                                    NotificationActivity.adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }
    }

    public static void clearData() {
        categoryModelList.clear();
        lists.clear();
        loadedCatgoriesNames.clear();
        wishList.clear();
        wishlistModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        myRatedIds.clear();
        myRatings.clear();
        addressesModelList.clear();
        rewardModelList.clear();
        myOrderItemModelList.clear();
    }
}
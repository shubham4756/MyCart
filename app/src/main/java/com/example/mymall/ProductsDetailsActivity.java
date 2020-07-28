package com.example.mymall;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.mymall.Adapter.MyRewardsAdapter;
import com.example.mymall.Adapter.ProductDetailsAdapter;
import com.example.mymall.Adapter.ProductimagesAdapter;
import com.example.mymall.Model.CartItemModel;
import com.example.mymall.Model.ProductSpecificationModel;
import com.example.mymall.Model.WishlistModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mymall.RegisterActivity.setSignUpFragment;
import static com.example.mymall.homePageActivity.showCart;

public class ProductsDetailsActivity extends AppCompatActivity {

    public static boolean runningWishList_query = false;
    public static boolean runningRating_query = false;
    public static boolean runningCart_query = false;

    public static boolean fromSearch=false;

    private ViewPager productImageViewPager;
    private TabLayout viewPageIndicator;

    private TextView productTitle;
    private TextView averageRatingMinView;
    private TextView totalRatingMiniView;
    private TextView productPrice;
    private TextView cuttedPrice;
    private ImageView cod_indicator;
    private TextView tv_cod_indicator;

    private LinearLayout coupenRedemtionLayout;
    private Button coupenRedeemBtn;
    private TextView rewardTitle;
    private TextView rewardBody;

    ///product description
    private ConstraintLayout productDetailsOnlyContainer;
    private ConstraintLayout productDetailsTabsContainer;
    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTablayout;
    private TextView productOnlyDescriptionBody;
    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();
    private String productDescription;
    private String productOtherDetails;
    ///product description

    ///rating layout
    public static int intialRating;
    public static LinearLayout rateNowContainer;
    private TextView totalRatings;
    private LinearLayout ratingsNumberContainer;
    private TextView totalRatingsFigure;
    private LinearLayout ratingsProgressBarContainer;
    private TextView averageRating;
    ///rating layout

    private Button buyNowBtn;
    private LinearLayout addToCartBtn;
    public static MenuItem cartItem;

    public static Activity productDetailsActivity;

    private boolean inStock = false;

    ///coupendialog
    private RecyclerView coupensRecyclerView;
    private LinearLayout selectedCoupen;

    private TextView coupenTitle;
    private TextView coupenExpiryDate;
    private TextView coupenBody;
    ///coupendialog

    //sign in dialog popup
    private Dialog signInDialog;
    private FirebaseUser currentUser;

    private Dialog loadingDialog;
    private TextView badgeCount;

    //variable for store product id
    public static String productID;

    public static FloatingActionButton addToWishlistBtn;
    public static boolean ALREADY_ADDED_TO_WISH_LIST = false;
    public static boolean ALREADY_ADDED_TO_CART = false;

    private FirebaseFirestore firebaseFirestore;
    private DocumentSnapshot documentSnapshot;

    private String productOriginalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_products_details);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productTitle = findViewById(R.id.product_title);
        averageRatingMinView = findViewById(R.id.tv_product_rating_miniview);
        totalRatingMiniView = findViewById(R.id.total_ratings_mini_view);
        productPrice = findViewById(R.id.product_price);
        cuttedPrice = findViewById(R.id.cutted_price);
        cod_indicator = findViewById(R.id.cod_indicator_img_view);
        tv_cod_indicator = findViewById(R.id.tv_cod_indicator);
        rewardTitle = findViewById(R.id.tv_reward_title);
        rewardBody = findViewById(R.id.tv_reward_body);
        productImageViewPager = findViewById(R.id.product_imges_viewpager);
        viewPageIndicator = findViewById(R.id.viewpager_indicator);
        addToWishlistBtn = findViewById(R.id.add_to_wishlist_btn);       //Todo: sign in requird to click
        productDetailsViewPager = findViewById(R.id.product_details_viewpager);
        productDetailsTablayout = findViewById(R.id.product_details_tablayout);
        buyNowBtn = findViewById(R.id.buy_now_btn);           //Todo: sign in requird to click
        coupenRedeemBtn = findViewById(R.id.coupen_redeem_btn);
        productDetailsTabsContainer = findViewById(R.id.products_details_tab_container);
        productDetailsOnlyContainer = findViewById(R.id.product_details_container);
        productOnlyDescriptionBody = findViewById(R.id.product_details_body);
        totalRatings = findViewById(R.id.total_ratings);
        ratingsNumberContainer = findViewById(R.id.ratings_numbers_container);
        totalRatingsFigure = findViewById(R.id.total_ratings_figure);
        ratingsProgressBarContainer = findViewById(R.id.ratings_progressbar_container);
        averageRating = findViewById(R.id.average_rating);
        addToCartBtn = findViewById(R.id.add_to_cart_btn);
        coupenRedemtionLayout = findViewById(R.id.coupen_redeem_layout);


        intialRating = -1;


        ////loading dialong for set data of product details
        loadingDialog = new Dialog(ProductsDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ///////end of loading dialong

        //////// coupen Dialog

        //discounted declare and original Price in upper scope in case of error

        final Dialog checkCoupenPriceDailog = new Dialog(ProductsDetailsActivity.this);
        checkCoupenPriceDailog.setContentView(R.layout.coupen_redeem_dialog);
        checkCoupenPriceDailog.setCancelable(true);
        checkCoupenPriceDailog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final ImageView toggleRecyclerView = checkCoupenPriceDailog.findViewById(R.id.toggle_recyclerview);

        coupensRecyclerView = checkCoupenPriceDailog.findViewById(R.id.coupens_recycler_view);
        selectedCoupen = checkCoupenPriceDailog.findViewById(R.id.selected_coupen);
        coupenTitle = checkCoupenPriceDailog.findViewById(R.id.coupen_title);
        coupenExpiryDate = checkCoupenPriceDailog.findViewById(R.id.coupen_validity);
        coupenBody = checkCoupenPriceDailog.findViewById(R.id.coupen_body);

        final TextView originalPrice = checkCoupenPriceDailog.findViewById(R.id.original_price);
        final TextView discountedPrice = checkCoupenPriceDailog.findViewById(R.id.discounted_price);


        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductsDetailsActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        coupensRecyclerView.setLayoutManager(layoutManager);


        toggleRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogRecyclerView();
            }
        });

        //Todo:load images from firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        final List<String> productImages = new ArrayList<>();

        productID = getIntent().getStringExtra("PRODUCT_ID");

        firebaseFirestore.collection("PRODUCTS").document(productID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    documentSnapshot = task.getResult();

                    firebaseFirestore.collection("PRODUCTS").document(productID)
                            .collection("QUANTITY")
                            .orderBy("time", Query.Direction.ASCENDING)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {

                                        for (long x = 1; x <= (long) documentSnapshot.get("no_of_product_images"); x++) {
                                            productImages.add(documentSnapshot.get("product_image_" + x).toString());
                                        }
                                        ProductimagesAdapter productimagesAdapter = new ProductimagesAdapter(productImages);
                                        productImageViewPager.setAdapter(productimagesAdapter);
                                        productTitle.setText(documentSnapshot.get("product_title").toString());
                                        averageRatingMinView.setText(documentSnapshot.get("average_rating").toString());
                                        totalRatingMiniView.setText("(" + (long) documentSnapshot.get("total_ratings") + ")ratings");
                                        productPrice.setText("Rs." + (long) documentSnapshot.get("product_price") + "/-");

                                        productOriginalPrice = documentSnapshot.get("product_price").toString();

                                        //Coupon dialog
                                        originalPrice.setText(productPrice.getText());
                                        MyRewardsAdapter myRewardsAdapter = new MyRewardsAdapter(DBqueries.rewardModelList, true, coupensRecyclerView, selectedCoupen, productOriginalPrice, coupenTitle, coupenExpiryDate, coupenBody, discountedPrice);
                                        coupensRecyclerView.setAdapter(myRewardsAdapter);
                                        myRewardsAdapter.notifyDataSetChanged();
                                        //Coupon dialog

                                        cuttedPrice.setText("Rs." + (long) documentSnapshot.get("cutted_price") + "/-");


                                        if ((boolean) documentSnapshot.get("COD")) {
                                            cod_indicator.setVisibility(View.VISIBLE);
                                            tv_cod_indicator.setVisibility(View.VISIBLE);
                                        } else {
                                            cod_indicator.setVisibility(View.INVISIBLE);
                                            tv_cod_indicator.setVisibility(View.INVISIBLE);
                                        }
                                        rewardTitle.setText((long) documentSnapshot.get("free_coupens") + documentSnapshot.get("free_coupen_title").toString());
                                        rewardBody.setText(documentSnapshot.get("free_coupne_body").toString());
                                        if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                            productDetailsTabsContainer.setVisibility(View.VISIBLE);
                                            productDetailsOnlyContainer.setVisibility(View.GONE);
                                            productDescription = documentSnapshot.get("product_description").toString();
                                            productOtherDetails = documentSnapshot.get("product_other_details").toString();
                                            //Todo:add all specificaion in list
                                            for (long x = 1; x <= (long) documentSnapshot.get("total_spec_title"); x++) {
                                                productSpecificationModelList.add(new ProductSpecificationModel(0, documentSnapshot.get("spec_title_" + x).toString()));
                                                for (long y = 1; y <= (long) documentSnapshot.get("spec_title_" + x + "_total_fields"); y++) {
                                                    productSpecificationModelList.add(
                                                            new ProductSpecificationModel(1, documentSnapshot.get("spec_title_" + x + "_field_" + y + "_name").toString(),
                                                                    documentSnapshot.get("spec_title_" + x + "_field_" + y + "_value").toString()));
                                                }
                                            }
                                        } else {
                                            productDetailsTabsContainer.setVisibility(View.GONE);
                                            productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                                            productOnlyDescriptionBody.setText(documentSnapshot.get("product_description").toString());
                                        }
                                        totalRatings.setText((long) documentSnapshot.get("total_ratings") + " ratings");
                                        for (int x = 0; x < 5; x++) {
                                            TextView rating = (TextView) ratingsNumberContainer.getChildAt(x);
                                            rating.setText(String.valueOf(documentSnapshot.get((5 - x) + "_star")));

                                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                            int maxProgress = Integer.parseInt(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                            progressBar.setMax(maxProgress);
                                            progressBar.setProgress(Integer.parseInt(String.valueOf((long) documentSnapshot.get((5 - x) + "_star"))));
                                        }
                                        totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings")));
                                        averageRating.setText(documentSnapshot.get("average_rating").toString());
                                        productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTablayout.getTabCount(), productDescription, productOtherDetails, productSpecificationModelList));

                                        //TODO: any data of this product is exist or not
                                        if (currentUser != null) {

                                            if (DBqueries.rewardModelList.size() == 0) {
                                                DBqueries.loadRewards(ProductsDetailsActivity.this, loadingDialog, false);
                                            }

                                            if (DBqueries.myRatings.size() == 0) {
                                                DBqueries.loadRatingList(ProductsDetailsActivity.this);
                                            }

                                            if (DBqueries.cartList.size() == 0) {
                                                DBqueries.loadCartList(ProductsDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductsDetailsActivity.this));
                                            }

                                            if (DBqueries.wishList.size() == 0) {
                                                DBqueries.loadWishList(ProductsDetailsActivity.this, loadingDialog, false);
                                            }

                                            if (DBqueries.rewardModelList.size() != 0 && DBqueries.myRatings.size() != 0 && DBqueries.cartList.size() != 0 && DBqueries.wishList.size() != 0) {
                                                loadingDialog.dismiss();
                                            }


                                        } else {
                                            loadingDialog.dismiss();
                                        }

                                        if (DBqueries.cartList.contains(ProductsDetailsActivity.productID)) {
                                            ALREADY_ADDED_TO_CART = true;
                                        } else {
                                            ALREADY_ADDED_TO_WISH_LIST = false;
                                        }

                                        if (DBqueries.wishList.contains(ProductsDetailsActivity.productID)) {
                                            ALREADY_ADDED_TO_WISH_LIST = true;
                                            addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.successred));
                                        } else {
                                            addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                            ALREADY_ADDED_TO_WISH_LIST = false;
                                        }

                                        if (DBqueries.myRatedIds.contains(productID)) {
                                            // Toast.makeText(ProductsDetailsActivity.this, " ---- in loading " + (intialRating + 1), Toast.LENGTH_SHORT).show();
                                            int idx = DBqueries.myRatedIds.indexOf(productID);
                                            averageRatingMinView.setText(String.valueOf(calculateAverageRating(0, false)));
                                            averageRating.setText(String.valueOf(calculateAverageRating(0, false)));
                                            intialRating = (int) (DBqueries.myRatings.get(idx) - 1);
                                            setRating((int) (DBqueries.myRatings.get(idx) - 1));
                                        }


                                        if (task.getResult().getDocuments().size() < (long) documentSnapshot.get("stock_quantity")) {
                                            inStock = true;
                                            buyNowBtn.setVisibility(View.VISIBLE);
                                            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    if (currentUser == null) {
                                                        signInDialog.show();
                                                    } else {
                                                        if (!runningCart_query) {
                                                            runningCart_query = true;
                                                            if (ALREADY_ADDED_TO_CART && DBqueries.cartList.contains(productID)) {
                                                                runningCart_query = false;
                                                                Toast.makeText(ProductsDetailsActivity.this, "Already Added to cart", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                //TODO: add this product in user wishlist
                                                                Map<String, Object> addProduct = new HashMap<>();
                                                                addProduct.put("product_ID_" + String.valueOf(DBqueries.cartList.size()), productID);
                                                                addProduct.put("list_size", (long) DBqueries.cartList.size() + 1);

                                                                firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_CART")
                                                                        .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            //To change wish list size in firebase
                                                                            Map<String, Object> updateListSize = new HashMap<>();

                                                                            if (DBqueries.cartItemModelList.size() != 0) {
                                                                                DBqueries.cartItemModelList.add(0, new CartItemModel(documentSnapshot.getBoolean("COD"),
                                                                                        CartItemModel.CART_ITEM,
                                                                                        productID,
                                                                                        documentSnapshot.get("product_image_1").toString(),
                                                                                        documentSnapshot.get("product_title").toString(),
                                                                                        (long) documentSnapshot.get("free_coupens"),
                                                                                        documentSnapshot.get("product_price").toString(),
                                                                                        documentSnapshot.get("cutted_price").toString(),
                                                                                        (long) 1,
                                                                                        (long) documentSnapshot.get("offers_applied"),
                                                                                        (long) 0, inStock,
                                                                                        (long) documentSnapshot.get("max-quantity"),
                                                                                        (long) documentSnapshot.get("stock_quantity")));
                                                                            }
                                                                            ALREADY_ADDED_TO_CART = true;
                                                                            DBqueries.cartList.add(productID);
                                                                            Toast.makeText(ProductsDetailsActivity.this, "Added to cart successfully!!", Toast.LENGTH_SHORT).show();
                                                                            invalidateOptionsMenu();
                                                                            runningCart_query = false;
                                                                        } else {
                                                                            runningCart_query = false;
                                                                            String error = task.getException().getMessage();
                                                                            Toast.makeText(ProductsDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    }
                                                }
                                            });

                                        } else {
                                            inStock = false;
                                            buyNowBtn.setVisibility(View.GONE);
                                            TextView outOfStock = (TextView) addToCartBtn.getChildAt(0);
                                            outOfStock.setText("Out Of Stock");
//                        outOfStock.setTextColor(getResources().getColor(R.color.colorAccent));
                                            outOfStock.setCompoundDrawables(null, null, null, null);
                                        }
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductsDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                    /////////end
                } else {
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(ProductsDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });


        viewPageIndicator.setupWithViewPager(productImageViewPager, true);

        addToWishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    signInDialog.show();
                    ;
                } else {
                    if (!runningWishList_query) {
                        runningWishList_query = true;
                        if (ALREADY_ADDED_TO_WISH_LIST) {
                            //TODO: removing this product wish list and product data from  wishlistmodellist
                            int index = DBqueries.wishList.indexOf(productID);
                            addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                            DBqueries.removeFromWishList(index, ProductsDetailsActivity.this);
                        } else {
                            //TODO: add this product in user wishlist
                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID_" + String.valueOf(DBqueries.wishList.size()), productID);
                            addProduct.put("list_size", (long) DBqueries.wishList.size() + 1);

                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_WISHLIST")
                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //To change wish list size in firebase
                                        if (DBqueries.wishlistModelList.size() != 0) {
                                            DBqueries.wishlistModelList.add(new WishlistModel(productID, documentSnapshot.get("product_image_1").toString(),
                                                    documentSnapshot.get("product_title").toString(),
                                                    (long) documentSnapshot.get("free_coupens"),
                                                    documentSnapshot.get("average_rating").toString(),
                                                    (long) documentSnapshot.get("total_ratings"),
                                                    documentSnapshot.get("product_price").toString(),
                                                    documentSnapshot.get("cutted_price").toString(),
                                                    (boolean) documentSnapshot.get("COD"),
                                                    (boolean) inStock));
                                        }
                                        ALREADY_ADDED_TO_WISH_LIST = true;
                                        addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.successred));
                                        DBqueries.wishList.add(productID);
                                        Toast.makeText(ProductsDetailsActivity.this, "Added to wishlist successfully!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductsDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    runningWishList_query = false;
                                }
                            });
                        }
                    }
                }
            }
        });


        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTablayout));
        productDetailsTablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                productDetailsViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        ///rating layout
        rateNowContainer = findViewById(R.id.rate_now_container);
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            final int starPosition = x;
            rateNowContainer.getChildAt(x).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        if (starPosition != intialRating) {
                            final int tempintial = intialRating;
                            intialRating = starPosition;
                            //Toast.makeText(ProductsDetailsActivity.this, " ---- intial reating " + (tempintial + 1), Toast.LENGTH_SHORT).show();
                            if (!runningRating_query) {
                                runningRating_query = true;
                                setRating(starPosition);
                                Map<String, Object> updateRating = new HashMap<>();
                                if (DBqueries.myRatedIds.contains(productID)) {
                                    TextView oldRating = (TextView) ratingsNumberContainer.getChildAt(4 - tempintial);
                                    TextView finalRating = (TextView) ratingsNumberContainer.getChildAt(4 - starPosition);
                                    updateRating.put(tempintial + 1 + "_star", Integer.parseInt(oldRating.getText().toString()) - 1);
                                    updateRating.put(starPosition + 1 + "_star", Integer.parseInt(finalRating.getText().toString()) + 1);
                                    updateRating.put("average_rating", String.valueOf(calculateAverageRating(starPosition - tempintial, false)));
                                } else {
                                    TextView finalRating = (TextView) ratingsNumberContainer.getChildAt(4 - starPosition);
                                    updateRating.put(starPosition + 1 + "_star", Integer.parseInt(finalRating.getText().toString()) + 1);
                                    updateRating.put("average_rating", String.valueOf(calculateAverageRating(starPosition + 1, true)));
                                    updateRating.put("total_ratings", (long) documentSnapshot.get("total_ratings") + 1);
                                }
                                // Toast.makeText(ProductsDetailsActivity.this, " ***** after first if else intial reating " + (tempintial + 1), Toast.LENGTH_SHORT).show();
                                firebaseFirestore.collection("PRODUCTS").document(productID)
                                        .update(updateRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, Object> myRating = new HashMap<>();
                                            //   Toast.makeText(ProductsDetailsActivity.this, " ======= after first query run intial reating " + (tempintial + 1), Toast.LENGTH_SHORT).show();
                                            if (DBqueries.myRatedIds.contains(productID)) {
                                                myRating.put("rating_" + DBqueries.myRatedIds.indexOf(productID), starPosition + 1);
                                            } else {
                                                myRating.put("list_size", (long) DBqueries.myRatedIds.size() + 1);
                                                myRating.put("product_ID_" + DBqueries.myRatedIds.size(), productID);
                                                myRating.put("rating_" + DBqueries.myRatings.size(), (long) starPosition + 1);
                                            }

                                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_RATINGS")
                                                    .update(myRating).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        //   Toast.makeText(ProductsDetailsActivity.this, " ^^^^^^^^^^^^^ in second query intial reating " + (tempintial + 1), Toast.LENGTH_SHORT).show();
                                                        if (DBqueries.myRatedIds.contains(productID)) {
                                                            DBqueries.myRatings.set(DBqueries.myRatedIds.indexOf(productID), (long) starPosition + 1);

                                                            TextView oldRating = (TextView) ratingsNumberContainer.getChildAt(4 - tempintial);
                                                            TextView finalRating = (TextView) ratingsNumberContainer.getChildAt(4 - starPosition);
                                                            // Toast.makeText(ProductsDetailsActivity.this, "old rating box " + (tempintial + 1) + " rating " + Integer.parseInt(oldRating.getText().toString()) + " decreasing ", Toast.LENGTH_SHORT).show();


                                                            oldRating.setText(String.valueOf(Integer.parseInt(oldRating.getText().toString()) - 1));
                                                            finalRating.setText(String.valueOf(Integer.parseInt(finalRating.getText().toString()) + 1));
                                                        } else {
                                                            DBqueries.myRatedIds.add(productID);
                                                            DBqueries.myRatings.add((long) starPosition + 1);

                                                            TextView rating = (TextView) ratingsNumberContainer.getChildAt(4 - starPosition);
                                                            rating.setText(String.valueOf(Integer.parseInt(rating.getText().toString()) + 1));
                                                            totalRatingMiniView.setText("(" + ((long) documentSnapshot.get("total_ratings") + 1) + ")ratings");
                                                            totalRatings.setText(((long) documentSnapshot.get("total_ratings") + 1) + " ratings");
                                                            totalRatingsFigure.setText(String.valueOf((long) documentSnapshot.get("total_ratings") + 1));
                                                            Toast.makeText(ProductsDetailsActivity.this, "Thank you!! for rating", Toast.LENGTH_SHORT).show();
                                                        }

                                                        for (int x = 0; x < 5; x++) {
                                                            TextView ratingfigures = (TextView) ratingsNumberContainer.getChildAt(x);
                                                            ProgressBar progressBar = (ProgressBar) ratingsProgressBarContainer.getChildAt(x);
                                                            int maxProgress = Integer.parseInt(totalRatingsFigure.getText().toString());
                                                            progressBar.setMax(maxProgress);
                                                            progressBar.setProgress(Integer.parseInt(ratingfigures.getText().toString()));
                                                        }
                                                        averageRatingMinView.setText(String.valueOf(calculateAverageRating(0, false)));
                                                        averageRating.setText(String.valueOf(calculateAverageRating(0, false)));
                                                        if (DBqueries.wishList.contains(productID) && DBqueries.wishlistModelList.size() != 0) {
                                                            int idx = DBqueries.wishList.indexOf(productID);
                                                            DBqueries.wishlistModelList.get(idx).setRating(averageRating.getText().toString());
                                                            DBqueries.wishlistModelList.get(idx).setTotalRatings(Long.parseLong(totalRatingsFigure.getText().toString()));
                                                            MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();
                                                        }
                                                    } else {
                                                        setRating(tempintial);
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ProductsDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                    runningRating_query = false;
                                                }
                                            });
                                        } else {
                                            runningRating_query = false;
                                            setRating(tempintial);
                                            String error = task.getException().getMessage();
                                            Toast.makeText(ProductsDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
        ///rating layout

        buyNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    DeliveryActivity.fromCart = false;
                    loadingDialog.show();
                    productDetailsActivity = ProductsDetailsActivity.this;
                    DeliveryActivity.cartItemModelList = new ArrayList<>();

                    DeliveryActivity.cartItemModelList.add(new CartItemModel(documentSnapshot.getBoolean("COD"),
                            CartItemModel.CART_ITEM,
                            productID,
                            documentSnapshot.get("product_image_1").toString(),
                            documentSnapshot.get("product_title").toString(),
                            (long) documentSnapshot.get("free_coupens"),
                            documentSnapshot.get("product_price").toString(),
                            documentSnapshot.get("cutted_price").toString(),
                            (long) 1,
                            (long) documentSnapshot.get("offers_applied"),
                            (long) 0,
                            inStock,
                            (long) documentSnapshot.get("max-quantity"),
                            (long) documentSnapshot.get("stock_quantity")));

                    DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));

                    if (DBqueries.addressesModelList.size() == 0) {
                        DBqueries.loadAddresses(ProductsDetailsActivity.this, loadingDialog,true);
                    } else {
                        loadingDialog.dismiss();
                        Intent deliveryIntent = new Intent(ProductsDetailsActivity.this, DeliveryActivity.class);
                        startActivity(deliveryIntent);
                    }

                }
            }
        });

        coupenRedeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCoupenPriceDailog.show();
            }
        });


        //Todo: sign in dialog created
        signInDialog = new Dialog(ProductsDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.sign_up_btn);
        final Intent registerIntent = new Intent(ProductsDetailsActivity.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SigninFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = false;
                startActivity(registerIntent);
                // finish(); //ToOverCome Loop

            }
        });
        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SigninFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;
                signInDialog.dismiss();
                setSignUpFragment = true;
                startActivity(registerIntent);
                // finish(); //ToOverCome Loop
            }
        });
        //end of sign in dialog creation

    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            coupenRedemtionLayout.setVisibility(View.GONE);
        } else {
            coupenRedemtionLayout.setVisibility(View.VISIBLE);
        }


        if (currentUser != null) {
//            if (DBqueries.rewardModelList.size() == 0) {
//                DBqueries.loadRewards(ProductsDetailsActivity.this, loadingDialog, false);
//            }
            if (DBqueries.myRatings.size() == 0) {
                DBqueries.loadRatingList(ProductsDetailsActivity.this);
            }

            if (DBqueries.wishList.size() == 0) {
                DBqueries.loadWishList(ProductsDetailsActivity.this, loadingDialog, false);
            }

            if (DBqueries.rewardModelList.size() != 0 && DBqueries.myRatings.size() != 0 && DBqueries.cartList.size() != 0 && DBqueries.wishList.size() != 0) {
                loadingDialog.dismiss();
            }

        } else {
            loadingDialog.dismiss();
        }

        if (DBqueries.cartList.contains(ProductsDetailsActivity.productID)) {
            ALREADY_ADDED_TO_CART = true;
        } else {
            ALREADY_ADDED_TO_WISH_LIST = false;
        }

        if (DBqueries.wishList.contains(ProductsDetailsActivity.productID)) {
            ALREADY_ADDED_TO_WISH_LIST = true;
            addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.successred));
        } else {
            addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
            ALREADY_ADDED_TO_WISH_LIST = false;
        }
        if (DBqueries.myRatedIds.contains(productID)) {
            int idx = DBqueries.myRatedIds.indexOf(productID);
            averageRatingMinView.setText(String.valueOf(calculateAverageRating(0, false)));
            averageRating.setText(String.valueOf(calculateAverageRating(0, false)));
            intialRating = (int) (DBqueries.myRatings.get(idx) - 1);
            setRating((int) (DBqueries.myRatings.get(idx) - 1));

        }
        invalidateOptionsMenu();
        /////////end
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

    public static void setRating(int starPosition) {
        //intialRating=starPosition;
        for (int x = 0; x < rateNowContainer.getChildCount(); x++) {
            ImageView starBtn = (ImageView) rateNowContainer.getChildAt(x);
            starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#817E7E")));
            if (x <= starPosition) {
                starBtn.setImageTintList(ColorStateList.valueOf(Color.parseColor("#F88D01")));
            }
        }
    }

    private double calculateAverageRating(long currentUserRating, boolean flag) {
        long totalStars = 0;
        for (int x = 1; x <= 5; x++) {
            TextView ratingNo = (TextView) ratingsNumberContainer.getChildAt(5 - x);
            totalStars += Long.parseLong(ratingNo.getText().toString()) * x;
        }
        totalStars += currentUserRating;
        long total = Long.parseLong(totalRatingsFigure.getText().toString());
        DecimalFormat df = new DecimalFormat("#.#");
        if (flag) {
            total++;
        }
        return Double.parseDouble(df.format((totalStars * 1.0) / total));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_and_cart_icon, menu);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        cartItem = menu.findItem(R.id.home_my_cart);
        cartItem.setActionView(R.layout.badge_layout);

        ImageView badgeIcon = (ImageView) cartItem.getActionView().findViewById(R.id.badge_icon);
        badgeIcon.setImageResource(R.drawable.white_cart);
        badgeCount = (TextView) cartItem.getActionView().findViewById(R.id.badge_count);

        if (currentUser != null) {
            if (DBqueries.cartList.size() == 0) {
                DBqueries.loadCartList(ProductsDetailsActivity.this, loadingDialog, false, badgeCount, new TextView(ProductsDetailsActivity.this));
            } else {
                badgeCount.setVisibility(View.VISIBLE);

                if (DBqueries.cartList.size() < 99) {
                    badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                } else {
                    badgeCount.setText(String.valueOf("99"));
                }
            }
        }

        cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUser == null) {
                    signInDialog.show();
                } else {
                    Intent cartIntent = new Intent(ProductsDetailsActivity.this, homePageActivity.class);
                    showCart = true;
                    startActivity(cartIntent);
                }
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Todo: add toolbars options here

        if (item.getItemId() == android.R.id.home) {
            productDetailsActivity = null;
            finish();
            return true;
        } else if (item.getItemId() == R.id.home_search_icon) {
            if(fromSearch){
             finish();
            } else {
                Intent searchIntent = new Intent(this, SearchActivity.class);
                startActivity(searchIntent);
            }
            return true;
        } else if (item.getItemId() == R.id.home_my_cart) {
            if (currentUser == null) {
                signInDialog.show();
            } else {
                Intent cartIntent = new Intent(ProductsDetailsActivity.this, homePageActivity.class);
                showCart = true;
                startActivity(cartIntent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fromSearch=false;
    }

    @Override
    public void onBackPressed() {
        productDetailsActivity = null;
        super.onBackPressed();
    }
}

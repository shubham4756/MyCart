package com.example.mymall;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.mymall.RegisterActivity.setSignUpFragment;

public class homePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FrameLayout frameLayout;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private static final int HOME_FRAGMENT = 0;
    private static final int CART_FRAGMENT = 3;    //1
    private static final int ORDERS_FRAGMENT = 1;   //2
    private static final int WISHLIST_FRAGMENT = 4;  //3
    private static final int REWARDS_FRAGMENT = 2;   //4
    private static final int ACCOUNT_FRAGMENT = 5;   //5
    private static final int SIGN_OUT=6;
    public static Boolean showCart = false;
    public static Activity mainActivity=null;
    public static boolean resetMainActivity=false;
    private int currentFragment = -1;

    NavigationView navigationView;
    private ActionBar actionBar;
    private Dialog signInDialog;
    private FirebaseUser currentUser;
    private TextView badgeCount;

    private CircleImageView profileView;
    private TextView fullname,email;
    private ImageView addProfileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        frameLayout = findViewById(R.id.mainFrameLayout);
        Toolbar toolbar = findViewById(R.id.toolbarHomeActivity);
        setSupportActionBar(toolbar);

//        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
//        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer = findViewById(R.id.draw_layout);
        navigationView = findViewById(R.id.nav_view);

        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);

        profileView=navigationView.getHeaderView(0).findViewById(R.id.nav_profile_pic);
        fullname=navigationView.getHeaderView(0).findViewById(R.id.nav_user_name);
        email=navigationView.getHeaderView(0).findViewById(R.id.nav_emali_address);
        addProfileIcon=navigationView.getHeaderView(0).findViewById(R.id.add_profile_icon);

        if (showCart) {
            mainActivity=homePageActivity.this;
            //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            gotoFragment("My Cart", new my_cart_Fragment(), CART_FRAGMENT);
        } else {
            toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            gotoFragment("Home", new HomeFragment(), HOME_FRAGMENT);
        }

        //Todo: show sign in dialog
        signInDialog = new Dialog(homePageActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.sign_up_btn);
        final Intent registerIntent = new Intent(homePageActivity.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SigninFragment.disableCloseBtn=true;
                SignUpFragment.disableCloseBtn=true;
                signInDialog.dismiss();
                setSignUpFragment = false;
                startActivity(registerIntent);
                //finish(); //ToOverCome Loop
            }
        });

        dialogSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SigninFragment.disableCloseBtn=true;
                SignUpFragment.disableCloseBtn=true;
                signInDialog.dismiss();
                setSignUpFragment = true;
                startActivity(registerIntent);
                //finish(); //ToOverCome Loop
            }
        });
        ///end
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Todo:check current user is log in or not
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null){
            navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(false);
        }
        else{


            if(DBqueries.email==null) {
                FirebaseFirestore.getInstance().collection("USERS").document(currentUser.getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DBqueries.fullname = task.getResult().getString("fullname");
                            DBqueries.email = task.getResult().getString("email");
                            DBqueries.profile = task.getResult().getString("profile");

                            fullname.setText(DBqueries.fullname);
                            email.setText(DBqueries.email);
                            if (DBqueries.profile.equals("")) {
                                addProfileIcon.setVisibility(View.VISIBLE);
                            } else {
                                addProfileIcon.setVisibility(View.INVISIBLE);
                                Glide.with(homePageActivity.this).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.mipmap.profile_placeholder_foreground)).into(profileView);
                            }
                        } else {
                            String error = task.getException().getMessage();
                            Toast.makeText(homePageActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                fullname.setText(DBqueries.fullname);
                email.setText(DBqueries.email);
                if (DBqueries.profile.equals("")) {
                    profileView.setImageResource(R.mipmap.profile_placeholder_foreground);
                    addProfileIcon.setVisibility(View.VISIBLE);
                } else {
                    addProfileIcon.setVisibility(View.INVISIBLE);
                    Glide.with(homePageActivity.this).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.mipmap.profile_placeholder_foreground)).into(profileView);
                }
            }
            navigationView.getMenu().getItem(navigationView.getMenu().size()-1).setEnabled(true);
        }
        if(resetMainActivity){
            resetMainActivity=false;
            gotoFragment("Home", new HomeFragment(), HOME_FRAGMENT);
        }
        homePageActivity.this.invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(currentUser!=null) {
            DBqueries.checkNotifications(true, null);
        }
    }


    ////Part 45 sooo many changes
    private void setFragment(Fragment fragment, int fragmentNo) {
        if (currentFragment != fragmentNo) {
            currentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }

    private void gotoFragment(String title, Fragment fragment, int fragmentNo) {
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        invalidateOptionsMenu();
        setFragment(fragment, fragmentNo);
        if (fragmentNo == CART_FRAGMENT) {
            navigationView.getMenu().getItem(3).setChecked(true);
        } else if (fragmentNo == ORDERS_FRAGMENT) {
            navigationView.getMenu().getItem(1).setChecked(true);
        } else if (fragmentNo == HOME_FRAGMENT) {
            navigationView.getMenu().getItem(0).setChecked(true);
        } else if (fragmentNo == WISHLIST_FRAGMENT) {
            navigationView.getMenu().getItem(4).setChecked(true);
        } else if (fragmentNo == REWARDS_FRAGMENT) {
            navigationView.getMenu().getItem(2).setChecked(true);
        }else if(fragmentNo==ACCOUNT_FRAGMENT){
            navigationView.getMenu().getItem(5).setChecked(true);
        } else if(fragmentNo==SIGN_OUT){
            navigationView.getMenu().getItem(6).setChecked(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
//        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }
    MenuItem menuItem;
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //Todo:add option list switch logic here
        drawer.closeDrawer(GravityCompat.START);
        menuItem=item;

        if(currentUser!=null) {
            drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    int id = menuItem.getItemId();
                    if (id == R.id.nav_my_mall) {
                        gotoFragment("Home", new HomeFragment(), HOME_FRAGMENT);
                    } else {
                        navigationView.getMenu().getItem(0).setChecked(false);
                    }
                    if (id == R.id.nav_my_cart) {
                        gotoFragment("My Cart", new my_cart_Fragment(), CART_FRAGMENT);
                    } else {
                        navigationView.getMenu().getItem(3).setChecked(false);
                    }
                    if (id == R.id.nav_my_order) {
                        gotoFragment("My Orders", new MyOrdersFragment(), ORDERS_FRAGMENT);
                    } else {
                        navigationView.getMenu().getItem(1).setChecked(false);
                    }
                    if (id == R.id.nav_my_wishlist) {
                        gotoFragment("My Wishlist", new MyWishlistFragment(), WISHLIST_FRAGMENT);
                    } else {
                        navigationView.getMenu().getItem(4).setChecked(false);
                    }
                    if (id == R.id.nav_my_rewards) {
                        gotoFragment("My Rewards", new MyRewardsFragment(), REWARDS_FRAGMENT);
                    } else {
                        navigationView.getMenu().getItem(2).setChecked(false);
                    }
                    if (id == R.id.nav_my_account) {
                        gotoFragment("My Account", new MyAccountFragment(), ACCOUNT_FRAGMENT);
                    } else {
                        navigationView.getMenu().getItem(5).setChecked(false);
                    }
                    if (id == R.id.nav_sign_out) {
                        FirebaseAuth.getInstance().signOut();
                        DBqueries.clearData();
                        SignUpFragment.disableCloseBtn=false;
                        SigninFragment.disableCloseBtn=false;
                        Intent registerIntent=new Intent(homePageActivity.this,RegisterActivity.class);
                        startActivity(registerIntent);
                        finish();
                    } else {
                        navigationView.getMenu().getItem(6).setChecked(false);
                    }
                    drawer.removeDrawerListener(this);
                }
            });
            //////////////////////// ||
            return true;
        }
        else{
            signInDialog.show();
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (currentFragment == HOME_FRAGMENT) {
            /////////TODO check
            //getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.home_page, menu);
            //TODO see how many product in cart using badge label
            MenuItem cartItem = menu.findItem(R.id.home_my_cart);
            cartItem.setActionView(R.layout.badge_layout);
            ImageView badgeIcon = cartItem.getActionView().findViewById(R.id.badge_icon);
            badgeIcon.setImageResource(R.drawable.white_cart);
            badgeCount = cartItem.getActionView().findViewById(R.id.badge_count);
            if(currentUser != null) {
                if (DBqueries.cartList.size() == 0) {
                    DBqueries.loadCartList(homePageActivity.this, new Dialog(homePageActivity.this), false, badgeCount,new TextView(homePageActivity.this));
                }else {
                    badgeCount.setVisibility(View.VISIBLE);
                    if (DBqueries.cartList.size() > 99) {
                        badgeCount.setText("99");
                    } else {
                        badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                    }
                }
            }

            cartItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentUser == null) {
                        signInDialog.show();
                    } else {
                        navigationView.getMenu().getItem(3).setChecked(true);
                        gotoFragment("My Cart", new my_cart_Fragment(), CART_FRAGMENT);
                    }
                }
            });

            MenuItem notifyItem = menu.findItem(R.id.home_notification_logo);
            notifyItem.setActionView(R.layout.badge_layout);
            ImageView notifyIcon = notifyItem.getActionView().findViewById(R.id.badge_icon);
            notifyIcon.setImageResource(R.drawable.notifications_logo);
            TextView notifyCount = notifyItem.getActionView().findViewById(R.id.badge_count);

            if(currentUser!=null){
                DBqueries.checkNotifications(false,notifyCount);
            }

            notifyItem.getActionView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent notificationIntent=new Intent(homePageActivity.this,NotificationActivity.class);
                    startActivity(notificationIntent);
                }
            });
        }
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Todo: add toolbars options here
        if (!showCart) {
            if (toggle.onOptionsItemSelected(item)) {
                return true;
           }
        }

        if (item.getItemId() == R.id.home_search_icon) {

            Intent searchIntent=new Intent(this,SearchActivity.class);
            startActivity(searchIntent);

            return true;
        } else if (item.getItemId() == R.id.home_notification_logo) {

            Intent notificationIntent=new Intent(this,NotificationActivity.class);
            startActivity(notificationIntent);

            return true;
        } else if (item.getItemId() == R.id.home_my_cart) {
             //Todo: for open cart first cheking user is already signin or not
            if(currentUser==null) {
                signInDialog.show();
            }
            else {
                navigationView.getMenu().getItem(3).setChecked(true);
                gotoFragment("My Cart", new my_cart_Fragment(), CART_FRAGMENT);
            }
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            if (showCart) {
                mainActivity=null;
                showCart = false;
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == HOME_FRAGMENT) {
                currentFragment = -1;
                super.onBackPressed();
            } else {
                if (showCart) {
                    mainActivity=null;
                    showCart = false;
                    finish();
                } else {
                    gotoFragment("Home", new HomeFragment(), HOME_FRAGMENT);
                }
            }
        }
    }
}
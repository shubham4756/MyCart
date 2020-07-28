package com.example.mymall;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mymall.Adapter.CategoryAdapter;
import com.example.mymall.Adapter.GridProductLayoutAdapter;
import com.example.mymall.Adapter.HomePageAdapter;
import com.example.mymall.Adapter.HorizontalProductScrollAdapter;
import com.example.mymall.Adapter.SliderAdapter;
import com.example.mymall.Model.CategoryModel;
import com.example.mymall.Model.HomePageModel;
import com.example.mymall.Model.HorizontalProductScrollModel;
import com.example.mymall.Model.SliderModel;
import com.example.mymall.Model.WishlistModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.mymall.DBqueries.categoryModelList;
import static com.example.mymall.DBqueries.firebaseFirestore;
import static com.example.mymall.DBqueries.lists;
import static com.example.mymall.DBqueries.loadCategories;
import static com.example.mymall.DBqueries.loadFragmentData;
import static com.example.mymall.DBqueries.loadedCatgoriesNames;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private RecyclerView homePageRecycleView;
    private HomePageAdapter homePageAdapter;
    private ImageView noInternetConnection;
    private LinearLayoutManager linearLayout;
    public static SwipeRefreshLayout swipeRefreshLayout;
    private List<CategoryModel> categoryModelFakeList=new ArrayList<>();
    private List<HomePageModel> homePageModelFakeList=new ArrayList<>();
    private Button retryBtn;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_home, container, false);
        retryBtn=view.findViewById(R.id.retry_btn);
        noInternetConnection = view.findViewById(R.id.no_internet);
        swipeRefreshLayout=view.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.colorPrimary),getContext().getResources().getColor(R.color.colorPrimary),getContext().getResources().getColor(R.color.colorPrimary));

        //Todo: set category recycler view
        categoryRecyclerView = view.findViewById(R.id.catogeryRecyclerView);
        linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryRecyclerView.setLayoutManager(linearLayout);


        //Todo: set all other recyclerview
        //todo: load images for horizontal layout and grid layout
        homePageRecycleView = view.findViewById(R.id.homePageRecyclerView);
        LinearLayoutManager linearLayoutManagerxyz = new LinearLayoutManager(getContext());
        linearLayoutManagerxyz.setOrientation(LinearLayoutManager.VERTICAL);
        homePageRecycleView.setLayoutManager(linearLayoutManagerxyz);


        /////category fake list
        categoryModelFakeList.add(new CategoryModel("null",""));
        categoryModelFakeList.add(new CategoryModel("null",""));
        categoryModelFakeList.add(new CategoryModel("null",""));
        categoryModelFakeList.add(new CategoryModel("null",""));
        categoryModelFakeList.add(new CategoryModel("null",""));
        categoryModelFakeList.add(new CategoryModel("null",""));
        categoryModelFakeList.add(new CategoryModel("null",""));
        categoryModelFakeList.add(new CategoryModel("null",""));
        categoryModelFakeList.add(new CategoryModel("null",""));
        /////category fake list

        /////home page fake list
        List<SliderModel> sliderModelFakeList=new ArrayList<>();
        sliderModelFakeList.add(new SliderModel("null","#FFFFFF"));
        sliderModelFakeList.add(new SliderModel("null","#FFFFFF"));
        sliderModelFakeList.add(new SliderModel("null","#FFFFFF"));
        sliderModelFakeList.add(new SliderModel("null","#FFFFFF"));
        sliderModelFakeList.add(new SliderModel("null","#FFFFFF"));

        List<HorizontalProductScrollModel> horizontalProductScrollModelFakeList=new ArrayList<>();
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("","","","",""));

        homePageModelFakeList.add(new HomePageModel(0,sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(2,"","#FFFFFF",horizontalProductScrollModelFakeList));
        homePageModelFakeList.add(new HomePageModel(1,"","#FFFFFF",horizontalProductScrollModelFakeList,new ArrayList<WishlistModel>()));
        /////home page fake list


        //Todo: set fake list first when refreshing and main data fetching list in the DB queries -> loadCategories
        categoryAdapter = new CategoryAdapter(categoryModelFakeList);

        //Todo: set fake list of homepagelists
        homePageAdapter = new HomePageAdapter(homePageModelFakeList);

        //Todo: check for internet connection is established or not
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
           // homePageActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnection.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homePageRecycleView.setVisibility(View.VISIBLE);

            //Todo:use firebase date for show categorys
            if (categoryModelList.size() == 0) {
                loadCategories(categoryRecyclerView, getContext());
            } else {
                categoryAdapter = new CategoryAdapter(categoryModelList);
                categoryAdapter.notifyDataSetChanged();
            }
            categoryRecyclerView.setAdapter(categoryAdapter);

            if (lists.size() == 0) {
                loadedCatgoriesNames.add("HOME");
                lists.add(new ArrayList<HomePageModel>());
                loadFragmentData(homePageRecycleView, getContext(), 0, "HOME");
            } else {
                homePageAdapter = new HomePageAdapter(lists.get(0));
                homePageAdapter.notifyDataSetChanged();
            }
            homePageRecycleView.setAdapter(homePageAdapter);
        } else {
           // homePageActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecycleView.setVisibility(View.GONE);
            retryBtn.setVisibility(View.VISIBLE);
            noInternetConnection.setVisibility(View.VISIBLE);
        }

        //Todo:refresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                reloadPage();
            }
        });
        //Done refresh

        //Todo:retry button
        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });
        //Done retry button

        return view;
    }


    private void reloadPage(){
//        categoryModelList.clear();
//        lists.clear();
//        loadedCatgoriesNames.clear();
        DBqueries.clearData();
        networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
           // homePageActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            noInternetConnection.setVisibility(View.GONE);
            retryBtn.setVisibility(View.GONE);
            categoryRecyclerView.setVisibility(View.VISIBLE);
            homePageRecycleView.setVisibility(View.VISIBLE);

            categoryAdapter = new CategoryAdapter(categoryModelFakeList);
            homePageAdapter =  new HomePageAdapter(homePageModelFakeList);
            //Fake list adapter
            categoryRecyclerView.setAdapter(categoryAdapter);
            homePageRecycleView.setAdapter(homePageAdapter);

            loadCategories(categoryRecyclerView, getContext());
            loadedCatgoriesNames.add("HOME");
            lists.add(new ArrayList<HomePageModel>());
            loadFragmentData(homePageRecycleView, getContext(), 0, "HOME");
        } else {
         //   homePageActivity.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            Toast.makeText(getContext(),"No Internet Connection!",Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            categoryRecyclerView.setVisibility(View.GONE);
            homePageRecycleView.setVisibility(View.GONE);
            retryBtn.setVisibility(View.VISIBLE);
            noInternetConnection.setVisibility(View.VISIBLE);
        }
    }
}

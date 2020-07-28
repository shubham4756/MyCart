package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mymall.Adapter.CategoryAdapter;
import com.example.mymall.Adapter.HomePageAdapter;
import com.example.mymall.Model.CategoryModel;
import com.example.mymall.Model.HomePageModel;
import com.example.mymall.Model.HorizontalProductScrollModel;
import com.example.mymall.Model.SliderModel;
import com.example.mymall.Model.WishlistModel;

import java.util.ArrayList;
import java.util.List;

import static com.example.mymall.DBqueries.lists;
import static com.example.mymall.DBqueries.loadFragmentData;
import static com.example.mymall.DBqueries.loadedCatgoriesNames;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoryRecyclerView;
    private List<HomePageModel> homePageModelFakeList=new ArrayList<>();
    private HomePageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbarCategoryActivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String title = getIntent().getStringExtra("categoryName");
        getSupportActionBar().setTitle(title);

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

        categoryRecyclerView = findViewById(R.id.categoryRecyclerView);
        LinearLayoutManager testingLayoutManger=new LinearLayoutManager(this);
        testingLayoutManger.setOrientation(RecyclerView.VERTICAL);
        categoryRecyclerView.setLayoutManager(testingLayoutManger);

        adapter = new HomePageAdapter(homePageModelFakeList);

        int listPosition=0;
        for(int x=0;x<loadedCatgoriesNames.size();x++){
            if(loadedCatgoriesNames.get(x).equals(title.toUpperCase())){
                listPosition=x;
            }
        }
        if(listPosition==0){
            loadedCatgoriesNames.add(title.toUpperCase());
            lists.add(new ArrayList<HomePageModel>());
            //this adapter is not required bcz we doing this proceess in loadfragmentdata method
            //adapter = new HomePageAdapter(lists.get(loadedCatgoriesNames.size()-1));
            loadFragmentData(categoryRecyclerView,this,loadedCatgoriesNames.size()-1,title);
        }else{
            adapter = new HomePageAdapter(lists.get(listPosition));
        }

        categoryRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_icon,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id ==  R.id.searchCategoryMenuIcon) {
            Intent searchIntent=new Intent(this,SearchActivity.class);
            startActivity(searchIntent);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

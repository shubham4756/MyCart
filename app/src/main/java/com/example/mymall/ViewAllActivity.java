package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.example.mymall.Adapter.GridProductLayoutAdapter;
import com.example.mymall.Adapter.WishlistAdapter;
import com.example.mymall.Model.HorizontalProductScrollModel;
import com.example.mymall.Model.WishlistModel;

import java.util.ArrayList;
import java.util.List;

import static com.example.mymall.DBqueries.loadFragmentData;

public class ViewAllActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridView gridView;
    public static List<HorizontalProductScrollModel> horizontalProductScrollModelList;
    public static  List<WishlistModel> wishlistModelList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        Toolbar toolbar = findViewById(R.id.toolbarViewAll);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("title"));

        recyclerView = findViewById(R.id.recyclerViewViewAll);
        gridView = findViewById(R.id.gridViewViewAll);

        int layout_code = getIntent().getIntExtra("layout_code",-1);

        if(layout_code == 0) {

            gridView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            WishlistAdapter wishlistAdapter = new WishlistAdapter(wishlistModelList, false);
            recyclerView.setAdapter(wishlistAdapter);
            wishlistAdapter.notifyDataSetChanged();
        } else if (layout_code == 1) {

            recyclerView.setVisibility(View.GONE);
            gridView.setVisibility(View.VISIBLE);

            GridProductLayoutAdapter gridProductLayoutAdapter = new GridProductLayoutAdapter(horizontalProductScrollModelList);
            gridView.setAdapter(gridProductLayoutAdapter);
            gridProductLayoutAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

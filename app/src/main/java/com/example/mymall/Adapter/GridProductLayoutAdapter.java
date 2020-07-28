package com.example.mymall.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mymall.Model.HorizontalProductScrollModel;
import com.example.mymall.ProductsDetailsActivity;
import com.example.mymall.R;

import java.util.List;

import io.grpc.Context;

public class GridProductLayoutAdapter extends BaseAdapter {

    List<HorizontalProductScrollModel> models;

    public GridProductLayoutAdapter(List<HorizontalProductScrollModel> models) {
        this.models = models;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {

        View view1;
        if(view == null) {
            view1 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_scroll_item_layout,null);
            view1.setElevation(0);
            view1.setBackgroundColor(Color.parseColor("#ffffff"));

            view1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent startProductDetailsIntent = new Intent(viewGroup.getContext(), ProductsDetailsActivity.class);
                    startProductDetailsIntent.putExtra("PRODUCT_ID",models.get(i).getProductId());
                    viewGroup.getContext().startActivity(startProductDetailsIntent);
                }
            });

            ImageView imageView = view1.findViewById(R.id.hspImage);
            TextView textView1 = view1.findViewById(R.id.hspName);
            TextView textView2 = view1.findViewById(R.id.hspDes);
            TextView textView3 = view1.findViewById(R.id.hspPrice);

            Glide.with(viewGroup.getContext()).load(models.get(i).getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.home_logo_icon)).into(imageView);
            textView1.setText(models.get(i).getProductName());
            textView2.setText(models.get(i).getProductDes());
            textView3.setText("Rs."+models.get(i).getProductPrice()+"/-");
        } else {
            view1 = view;
        }

        return view1;
    }
}

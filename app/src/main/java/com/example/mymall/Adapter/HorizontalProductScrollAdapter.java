package com.example.mymall.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mymall.Model.HorizontalProductScrollModel;
import com.example.mymall.ProductsDetailsActivity;
import com.example.mymall.R;

import java.util.List;

public class HorizontalProductScrollAdapter extends RecyclerView.Adapter<HorizontalProductScrollAdapter.ViewHolder> {

    private List<HorizontalProductScrollModel> models;

    public HorizontalProductScrollAdapter(List<HorizontalProductScrollModel> models) {
        this.models = models;
    }

    @NonNull
    @Override
    public HorizontalProductScrollAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalProductScrollAdapter.ViewHolder holder, int position) {
        String proId=models.get(position).getProductId();
        String res = models.get(position).getProductImage();
        String name = models.get(position).getProductName();
        String des = models.get(position).getProductDes();
        String price = models.get(position).getProductPrice();

        holder.setData(proId,res,name,des,price);
    }

    @Override
    public int getItemCount() {

        if(models.size() > 8) {
            return 8;
        }
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView1,textView2,textView3;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.hspImage);
            textView1 = itemView.findViewById(R.id.hspName);
            textView2 = itemView.findViewById(R.id.hspDes);
            textView3 = itemView.findViewById(R.id.hspPrice);

        }

        private void setData(final String productId, String r, String name, String des, String price){
            Glide.with(itemView.getContext()).load(r).apply(new RequestOptions().placeholder(R.mipmap.home_logo_icon)).into(imageView);
            textView1.setText(name);
            textView2.setText(des);
            textView3.setText("Rs."+price+"/-");

            if(!textView1.equals("")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(itemView.getContext(), ProductsDetailsActivity.class);
                        intent.putExtra("PRODUCT_ID",productId);
                        itemView.getContext().startActivity(intent);
                    }
                });
            }
        }
    }
}

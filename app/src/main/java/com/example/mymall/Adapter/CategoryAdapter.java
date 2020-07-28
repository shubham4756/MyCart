package com.example.mymall.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mymall.CategoryActivity;
import com.example.mymall.Model.CategoryModel;
import com.example.mymall.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {


    private List<CategoryModel> categoryModel;
    private int lastpostion=-1;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.categoryIcon);
            textView = itemView.findViewById(R.id.categoryName);
        }

        private void setCategoryIcon(String iconUrl) {
            if (!iconUrl.equals("null")) {
                Glide.with(itemView.getContext()).load(iconUrl).apply(new RequestOptions().placeholder(R.mipmap.home_logo_icon)).into(imageView);
            }
            else {
                imageView.setImageResource(R.mipmap.home_logo_icon);
            }
        }

        private void setCategory(final String name,final int position) {
            textView.setText(name);

            if(!name.equals("")) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (position != 0) {
                            Intent categoryIntent = new Intent(itemView.getContext(), CategoryActivity.class);
                            categoryIntent.putExtra("categoryName", name);
                            imageView.getContext().startActivity(categoryIntent);
                        }

                    }
                });
            }
        }
    }

    public CategoryAdapter(List<CategoryModel> categoryModel) {
        this.categoryModel = categoryModel;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        String icon = categoryModel.get(position).getCategoryIconLink();
        String name = categoryModel.get(position).getCategoryName();
        holder.setCategory(name,position);
        holder.setCategoryIcon(icon);


        if(lastpostion!=position){
            Animation animation= AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastpostion=position;
        }
    }

    @Override
    public int getItemCount() {
        return categoryModel.size();
    }
}

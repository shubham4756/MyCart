package com.example.mymall.Adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mymall.Model.SliderModel;
import com.example.mymall.R;

import java.util.List;

public class SliderAdapter extends PagerAdapter {

    private List<SliderModel> sliderModel;

    public SliderAdapter(List<SliderModel> sliderModel) {
        this.sliderModel = sliderModel;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.slider_layout, container, false);
        ConstraintLayout bannerContainer = view.findViewById(R.id.bannerContainer);
        bannerContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(sliderModel.get(position).getBackgroundColor())));
        ImageView banner = view.findViewById(R.id.bannerSlider);
        //Todo:set banner from firebase
        Glide.with(container.getContext()).load(sliderModel.get(position).getBanner()).apply(new RequestOptions().placeholder(R.mipmap.home_logo_icon)).into(banner);

        container.addView(view, 0);
        return view;
    }

    @Override
    public int getCount() {
        return sliderModel.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}

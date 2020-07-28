package com.example.mymall.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mymall.Model.HomePageModel;
import com.example.mymall.Model.HorizontalProductScrollModel;
import com.example.mymall.Model.SliderModel;
import com.example.mymall.Model.WishlistModel;
import com.example.mymall.ProductsDetailsActivity;
import com.example.mymall.R;
import com.example.mymall.ViewAllActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomePageAdapter extends RecyclerView.Adapter {
    public static final int BANNER_SLIDER = 0;

    private int lastpostion=-1;
    private List<HomePageModel> homePageModelList;
    private RecyclerView.RecycledViewPool recycledViewPool;

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public int getItemViewType(int position) {
        switch (homePageModelList.get(position).getType()) {
            case 0:
                return BANNER_SLIDER;
            case 1:
                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;
            case 2:
                return HomePageModel.GRID_PRODUCT_VIEW;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case BANNER_SLIDER:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliding_ad_layout, parent, false);
                return new BannerSliderViewHolder(view);
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                View horizontalView = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_scroll_layout, parent, false);
                return new HorizontalProductViewHolder(horizontalView);
            case HomePageModel.GRID_PRODUCT_VIEW:
                View gridView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_product_layout, parent, false);
                return new GridProductViewHolder(gridView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (homePageModelList.get(position).getType()) {
            case BANNER_SLIDER:
                List<SliderModel> sliderModelList = homePageModelList.get(position).getSliderModels();
                ((BannerSliderViewHolder) holder).setBannerSlider(sliderModelList);
                break;
            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:
                String color=homePageModelList.get(position).getBackGroundColor();
                String title = homePageModelList.get(position).getTitle();
                //for showing images in viewall activitys
                List<WishlistModel> viewAllProductList= homePageModelList.get(position).getViewAllProductList();
                List<HorizontalProductScrollModel> horizontalProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                ((HorizontalProductViewHolder) holder).setHorizontalProductLayout(horizontalProductScrollModelList, title,color,viewAllProductList);
                break;
            case HomePageModel.GRID_PRODUCT_VIEW:
                String title1 = homePageModelList.get(position).getTitle();
                String gridColor=homePageModelList.get(position).getBackGroundColor();
                List<HorizontalProductScrollModel> gridProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();
                ((GridProductViewHolder) holder).setGridLayout(gridProductScrollModelList, title1,gridColor);
                break;
            default:
        }
        if(lastpostion<=position){
            Animation animation= AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastpostion=position;
        }
    }

    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }

    public static class BannerSliderViewHolder extends RecyclerView.ViewHolder {

        private ViewPager bannerSliderViewPager;
        private int currentPage;
        private Timer timer;
        final private long delayTime = 3000, periodTime = 3000;
        List<SliderModel> arrangedList;

        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerSliderViewPager = itemView.findViewById(R.id.viewPagerBannerSlider);
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setBannerSlider(final List<SliderModel> sliderModels) {
            currentPage = 2;
            if(timer != null) {
                timer.cancel();
            }

            arrangedList = new ArrayList<>(sliderModels);

            arrangedList.set(0,sliderModels.get(sliderModels.size() - 2));
            arrangedList.set(1,sliderModels.get(sliderModels.size() - 1));
            arrangedList.add(sliderModels.get(0));
            arrangedList.add(sliderModels.get(1));

            SliderAdapter sliderAdapter = new SliderAdapter(arrangedList);
            bannerSliderViewPager.setAdapter(sliderAdapter);
            bannerSliderViewPager.setClipToPadding(false);
            bannerSliderViewPager.setPageMargin(20);

            bannerSliderViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    currentPage = position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_IDLE) {
                        pageLooper(arrangedList);
                    }
                }
            });
            sartBannerSlideShow(arrangedList);

            bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    pageLooper(arrangedList);
                    stopBannerSlideShow();

                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        sartBannerSlideShow(arrangedList);
                    }
                    return false;
                }
            });
        }

        private void pageLooper(List<SliderModel> sliderModels) {
            if (currentPage == sliderModels.size() - 2) {
                currentPage = 2;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }

            if (currentPage == 1) {
                currentPage = sliderModels.size() - 3;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }
        }

        private void sartBannerSlideShow(final List<SliderModel> sliderModels) {
            final Handler handler = new Handler();
            final Runnable update = new Runnable() {
                @Override
                public void run() {
                    if (currentPage >= sliderModels.size()) {
                        currentPage = 1;
                    }
                    bannerSliderViewPager.setCurrentItem(currentPage++, true);
                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, delayTime, periodTime);
        }

        private void stopBannerSlideShow() {
            timer.cancel();
        }
    }

    public class HorizontalProductViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout container;
        private TextView horizonatalTextView;
        private Button horizontalViewAll;
        private RecyclerView horizontalProductRecycler;

        public HorizontalProductViewHolder(@NonNull View itemView) {
            super(itemView);
            container=itemView.findViewById(R.id.container);
            horizonatalTextView = itemView.findViewById(R.id.horizontalScrollLayoutTitle);
            horizontalViewAll = itemView.findViewById(R.id.horizontalScrollLayoutButton);
            horizontalProductRecycler = itemView.findViewById(R.id.horizontalScrollLayoutRecyclerView);
            horizontalProductRecycler.setRecycledViewPool(recycledViewPool);
        }

        private void setHorizontalProductLayout(List<HorizontalProductScrollModel> list, final String title, String color, final List<WishlistModel> viewAllProductList) {
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            horizonatalTextView.setText(title);

            if (list.size() > 8) {
                horizontalViewAll.setVisibility(View.VISIBLE);
                horizontalViewAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Todo:image loading for showing all products from horizontal recycler view
                        ViewAllActivity.wishlistModelList=viewAllProductList;
                        Intent viewAll = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAll.putExtra("layout_code",0);
                        viewAll.putExtra("title",title);
                        itemView.getContext().startActivity(viewAll);
                    }
                });
            } else {
                horizontalViewAll.setVisibility(View.INVISIBLE);
            }
            HorizontalProductScrollAdapter horizontalProductScrollAdapter = new HorizontalProductScrollAdapter(list);
            final LinearLayoutManager linearLayoutabc = new LinearLayoutManager(itemView.getContext());
            linearLayoutabc.setOrientation(LinearLayoutManager.HORIZONTAL);
            horizontalProductRecycler.setLayoutManager(linearLayoutabc);
            horizontalProductRecycler.setAdapter(horizontalProductScrollAdapter);
            horizontalProductScrollAdapter.notifyDataSetChanged();
        }
    }

    public class GridProductViewHolder extends RecyclerView.ViewHolder {

        private TextView gridLayoutTitle;
        private Button gridLayoutViewAllButton;
        private ConstraintLayout container;
        private GridLayout gridProductLayout;

        public GridProductViewHolder(@NonNull View itemView) {
            super(itemView);
            container=itemView.findViewById(R.id.gridProductLayout);
            gridLayoutTitle = itemView.findViewById(R.id.gridProductLayoutTitle);
            gridLayoutViewAllButton = itemView.findViewById(R.id.gridProductLayoutViewAllbtn);
            gridProductLayout = itemView.findViewById(R.id.gridProductLayoutGridView);

        }

        private void setGridLayout(final List<HorizontalProductScrollModel> horizontalProductScrollModelList, final String title, String color) {
            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            gridLayoutTitle.setText(title);
            GridProductLayoutAdapter gridProductLayoutAdapter = new GridProductLayoutAdapter(horizontalProductScrollModelList);

            for (int i = 0; i < 4; i++) {
                ImageView productImage = gridProductLayout.getChildAt(i).findViewById(R.id.hspImage);
                TextView productTitle = gridProductLayout.getChildAt(i).findViewById(R.id.hspName);
                TextView productDes = gridProductLayout.getChildAt(i).findViewById(R.id.hspDes);
                TextView productPrice = gridProductLayout.getChildAt(i).findViewById(R.id.hspPrice);

                Glide.with(itemView.getContext()).load(horizontalProductScrollModelList.get(i).getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.home_logo_icon)).into(productImage);
                productTitle.setText(horizontalProductScrollModelList.get(i).getProductName());
                productDes.setText(horizontalProductScrollModelList.get(i).getProductDes());
                productPrice.setText("Rs."+horizontalProductScrollModelList.get(i).getProductPrice()+"/-");

                gridProductLayout.getChildAt(i).setBackgroundColor(Color.parseColor("#ffffff"));

                if(!title.equals("")) {
                    final int finalI = i;
                    gridProductLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(itemView.getContext(), ProductsDetailsActivity.class);
                            //for send product id to homepage activity
                            intent.putExtra("PRODUCT_ID",horizontalProductScrollModelList.get(finalI).getProductId());
                            itemView.getContext().startActivity(intent);
                        }
                    });
                }
            }
            gridProductLayoutAdapter.notifyDataSetChanged();

            if(!title.equals("")) {
                gridLayoutViewAllButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ViewAllActivity.horizontalProductScrollModelList = horizontalProductScrollModelList;
                        Intent viewAll = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAll.putExtra("layout_code", 1);
                        viewAll.putExtra("title", title);
                        itemView.getContext().startActivity(viewAll);
                    }
                });
            }
        }
    }
}

package com.example.mymall.Model;

import java.util.List;

public class HomePageModel {

    public static final int BANNER_SLIDER = 0;
    public static final int HORIZONTAL_PRODUCT_VIEW = 1;
    public static final int GRID_PRODUCT_VIEW = 2;


    private int type;
    //banner
    private List<SliderModel> sliderModels;
    public HomePageModel(int type, List<SliderModel> sliderModels) {
        this.type = type;
        this.sliderModels = sliderModels;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<SliderModel> getSliderModels() {
        return sliderModels;
    }

    public void setSliderModels(List<SliderModel> sliderModels) {
        this.sliderModels = sliderModels;
    }
    //banner

    //horizontal
    private String title;
    private String backGroundColor;
    private List<HorizontalProductScrollModel> horizontalProductScrollModelList;
    private List<WishlistModel> viewAllProductList;
    //Todo: show all product in recyclerview
    public HomePageModel(int type, String title,String backGroundColor, List<HorizontalProductScrollModel> horizontalProductScrollModelList,List<WishlistModel> viewAllProductList) {
        this.type = type;
        this.title = title;
        this.backGroundColor=backGroundColor;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
        this.viewAllProductList=viewAllProductList;
    }

    public List<WishlistModel> getViewAllProductList() {
        return viewAllProductList;
    }

    public void setViewAllProductList(List<WishlistModel> viewAllProductList) {
        this.viewAllProductList = viewAllProductList;
    }
    ////////Done

    //Todo: show products only in horizontal recyclerview
    public HomePageModel(int type, String title,String backGroundColor, List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.type = type;
        this.title = title;
        this.backGroundColor=backGroundColor;
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }
    /////////Done
    public String getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(String backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HorizontalProductScrollModel> getHorizontalProductScrollModelList() {
        return horizontalProductScrollModelList;
    }

    public void setHorizontalProductScrollModelList(List<HorizontalProductScrollModel> horizontalProductScrollModelList) {
        this.horizontalProductScrollModelList = horizontalProductScrollModelList;
    }

    //horizontal

    //grid
    //same Horizontal
    //grid



}

package com.example.mymall.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mymall.Model.ProductSpecificationModel;
import com.example.mymall.ProductDescriptionFragment;
import com.example.mymall.ProductSpecificationFragment;

import java.util.List;

public class ProductDetailsAdapter extends FragmentPagerAdapter {
    private int totalTabs;
    private String productDescription;
    private String productOtherDetails;
    private List<ProductSpecificationModel> productSpecificationModelList;

    public ProductDetailsAdapter(@NonNull FragmentManager fm, int totalTabs ,String productDescription, String productOtherDetails, List<ProductSpecificationModel> productSpecificationModelList) {
        super(fm);
        this.totalTabs=totalTabs;
        this.productDescription = productDescription;
        this.productOtherDetails = productOtherDetails;
        this.productSpecificationModelList = productSpecificationModelList;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
             ProductDescriptionFragment productDescriptionFragment1=new ProductDescriptionFragment();
                productDescriptionFragment1.body=productDescription;
             return productDescriptionFragment1;
            case 1:
             ProductSpecificationFragment productSpecificationFragment=new ProductSpecificationFragment();
             ProductSpecificationFragment.productSpecificationModelList=productSpecificationModelList;
             return productSpecificationFragment;
            case 2:
                ProductDescriptionFragment productDescriptionFragment2=new ProductDescriptionFragment();
                productDescriptionFragment2.body=productOtherDetails;
                return productDescriptionFragment2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}

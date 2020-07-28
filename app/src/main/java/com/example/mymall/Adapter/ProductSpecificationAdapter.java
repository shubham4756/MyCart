package com.example.mymall.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymall.Model.ProductSpecificationModel;
import com.example.mymall.R;

import java.util.List;

public class ProductSpecificationAdapter  extends RecyclerView.Adapter<ProductSpecificationAdapter.ViewHolder> {
    private List<ProductSpecificationModel> productSpecificationModelList;

    public ProductSpecificationAdapter(List<ProductSpecificationModel> productSpecificationModelList) {
        this.productSpecificationModelList = productSpecificationModelList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (productSpecificationModelList.get(position).getType()){
            case 0:
                return ProductSpecificationModel.SPECIFICAION_TITLE;
            case 1:
                return ProductSpecificationModel.SPECIFICATION_BODY;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public ProductSpecificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch(viewType){
            case ProductSpecificationModel.SPECIFICAION_TITLE:
                //Todo:here text View is created using java code
                TextView title=new TextView(parent.getContext());
                title.setTypeface(null, Typeface.BOLD);
                title.setTextColor(Color.parseColor("#000000"));
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(setdp(16,parent.getContext()),setdp(16,parent.getContext()),setdp(16,parent.getContext()),setdp(8,parent.getContext()));
                title.setLayoutParams(layoutParams);
                return new ViewHolder(title);

            case ProductSpecificationModel.SPECIFICATION_BODY:
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_specification_item_layout,parent,false);
                return new ViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ProductSpecificationAdapter.ViewHolder holder, int position) {

        switch (productSpecificationModelList.get(position).getType()){
            case ProductSpecificationModel.SPECIFICAION_TITLE:
                holder.setTitle(productSpecificationModelList.get(position).getTitle());
                break;
            case ProductSpecificationModel.SPECIFICATION_BODY:
                String featureTitle=productSpecificationModelList.get(position).getFeatureName();
                String featureDetail=productSpecificationModelList.get(position).getFeatureValue();
                holder.setFeatures(featureTitle,featureDetail);
                break;
            default:
                return;
        }
    }

    @Override
    public int getItemCount() {
        return productSpecificationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView featureName,featureValue,title;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        private void setTitle(String titleText){
            title = (TextView) itemView;
            title.setText(titleText);
        }
        private void setFeatures(String featureTitle,String featureDetail){
            featureName=itemView.findViewById(R.id.feature_name);
            featureValue=itemView.findViewById(R.id.feature_value);
            featureName.setText(featureTitle);
            featureValue.setText(featureDetail);
        }
    }

    private int setdp(int dp,Context context){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.getResources().getDisplayMetrics());
    }
}

package com.example.mymall.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mymall.DBqueries;
import com.example.mymall.Model.WishlistModel;
import com.example.mymall.ProductsDetailsActivity;
import com.example.mymall.R;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private List<WishlistModel> wishlistModelList;
    private Boolean wishList;
    private int lastpostion=-1;
    private boolean fromSearch;

    public List<WishlistModel> getWishlistModelList() {
        return wishlistModelList;
    }

    public void setWishlistModelList(List<WishlistModel> wishlistModelList) {
        this.wishlistModelList = wishlistModelList;
    }

    public boolean isFromSearch() {
        return fromSearch;
    }

    public void setFromSearch(boolean fromSearch) {
        this.fromSearch = fromSearch;
    }

    public WishlistAdapter(List<WishlistModel> wishlistModelList, Boolean wishList) {
        this.wishlistModelList = wishlistModelList;
        this.wishList = wishList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String productId=wishlistModelList.get(position).getProductId();
        String resource=wishlistModelList.get(position).getProductImage();
        String title=wishlistModelList.get(position).getProductTitle();
        long freeCoupens=wishlistModelList.get(position).getFreeCoupens();
        String rating=wishlistModelList.get(position).getRating();
        long totalRatings=wishlistModelList.get(position).getTotalRatings();
        String productPrice=wishlistModelList.get(position).getProductPrice();
        String cuttedPrice=wishlistModelList.get(position).getCuttedPrice();
        boolean paymentMethod=wishlistModelList.get(position).isCOD();
        boolean inStock=wishlistModelList.get(position).isInStock();
        holder.setData(productId,resource,title,freeCoupens,rating,totalRatings,productPrice,cuttedPrice,paymentMethod,position,inStock);


        if(lastpostion<=position){
            Animation animation= AnimationUtils.loadAnimation(holder.itemView.getContext(),R.anim.fade_in);
            holder.itemView.setAnimation(animation);
            lastpostion=position;
        }
    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView productImage;
        private TextView productTitle;
        private TextView freeCoupens;
        private ImageView coupenIcon;
        private TextView rating;
        private TextView totalRatings;
        private View priceCut;
        private TextView productPrice;
        private TextView cuttedPrice;
        private TextView paymentMethod;
        private ImageButton deleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.product_image);
            productTitle=itemView.findViewById(R.id.product_title);
            freeCoupens=itemView.findViewById(R.id.free_coupen);
            coupenIcon=itemView.findViewById(R.id.coupen_icon);
            rating=itemView.findViewById(R.id.tv_product_rating_miniview);
            totalRatings=itemView.findViewById(R.id.total_ratings);
            priceCut=itemView.findViewById(R.id.price_cut);
            productPrice=itemView.findViewById(R.id.product_price);
            cuttedPrice=itemView.findViewById(R.id.cutted_price);
            paymentMethod=itemView.findViewById(R.id.payment_method);
            deleteBtn=itemView.findViewById(R.id.delete_btn);
        }
        private void setData(final String productId, String resource, String title, long freeCoupensNo, String averageRate, long totalRatingsNo, String price, String cuttedPriceValue, boolean payMethod, final int index,boolean inStock){
            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.mipmap.home_logo_icon)).into(productImage);
            productTitle.setText(title);
            if(freeCoupensNo!=0 && inStock){
                if (freeCoupensNo==1) {
                    coupenIcon.setVisibility(View.VISIBLE);
                    freeCoupens.setText("free " + freeCoupensNo + " coupen");
                }
                else{
                    freeCoupens.setText("free " + freeCoupensNo + " coupens");
                }
            }else{
                coupenIcon.setVisibility(View.INVISIBLE);
                freeCoupens.setVisibility(View.INVISIBLE);
            }
            LinearLayout linearLayout=(LinearLayout) rating.getParent();
            if(inStock) {
                linearLayout.setVisibility(View.VISIBLE);
                rating.setVisibility(View.VISIBLE);
                totalRatings.setVisibility(View.VISIBLE);
                productPrice.setTextColor(Color.parseColor("#000000"));
                cuttedPrice.setVisibility(View.VISIBLE);
                rating.setText(averageRate);
                totalRatings.setText("(" + totalRatingsNo + ")ratings");
                productPrice.setText("Rs." + price + "/-");
                cuttedPrice.setText("Rs." + cuttedPriceValue + "/-");
                if (payMethod) {
                    paymentMethod.setVisibility(View.VISIBLE);
                } else {
                    paymentMethod.setVisibility(View.INVISIBLE);
                }
            }else{
                linearLayout.setVisibility(View.INVISIBLE);
                rating.setVisibility(View.INVISIBLE);
                totalRatings.setVisibility(View.INVISIBLE);
                productPrice.setText("Out of Stock");
                productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
                cuttedPrice.setVisibility(View.INVISIBLE);
                paymentMethod.setVisibility(View.INVISIBLE);
            }
            if(wishList) {
                deleteBtn.setVisibility(View.VISIBLE);
            } else {
                deleteBtn.setVisibility(View.GONE);
            }

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!ProductsDetailsActivity.runningWishList_query) {
                        ProductsDetailsActivity.runningWishList_query = true;
                        DBqueries.removeFromWishList(index, itemView.getContext());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(fromSearch){
                        ProductsDetailsActivity.fromSearch=true;
                    }

                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductsDetailsActivity.class);
                    productDetailsIntent.putExtra("PRODUCT_ID",productId);
                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }
    }
}

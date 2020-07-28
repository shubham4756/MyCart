package com.example.mymall;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymall.Adapter.CartAdapter;
import com.example.mymall.Adapter.WishlistAdapter;
import com.example.mymall.Model.CartItemModel;
import com.example.mymall.Model.RewardModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class my_cart_Fragment extends Fragment {

    public my_cart_Fragment() {
        // Required empty public constructor
    }
    private RecyclerView cartItemsRecylcerView;
    private Button btnContinue;
    private TextView totalAmount;
    private Dialog loadingDialog;
    private LinearLayout amountLayout;
    public static CartAdapter cartAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_my_cart_, container, false);
        btnContinue = view.findViewById(R.id.cart_continue_btn);
        totalAmount=view.findViewById(R.id.total_cart_amount);
        amountLayout=(LinearLayout) totalAmount.getParent().getParent();
        ////loading dialong for set data of product details
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ///////end of loading dialong

        cartItemsRecylcerView=view.findViewById(R.id.cart_item_recyclerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        cartItemsRecylcerView.setLayoutManager(layoutManager);



        cartAdapter=new CartAdapter(DBqueries.cartItemModelList,totalAmount,true);
        cartItemsRecylcerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        //already cheked any product in stock or not
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delevery Intent start
                DeliveryActivity.fromCart=true;
                loadingDialog.show();
                DeliveryActivity.cartItemModelList=new ArrayList<>();
                for(int x=0;x<DBqueries.cartItemModelList.size();x++){
                    CartItemModel cartItemModel=DBqueries.cartItemModelList.get(x);
                    if(cartItemModel.isInStock()){
                        DeliveryActivity.cartItemModelList.add(cartItemModel);
                    }
                }
                DeliveryActivity.cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                if (DBqueries.addressesModelList.size()==0) {
                    DBqueries.loadAddresses(getContext(), loadingDialog,true);
                }else {
                    loadingDialog.dismiss();
                    Intent deliveryIntent = new Intent(getContext(), DeliveryActivity.class);
                    startActivity(deliveryIntent);
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cartAdapter.notifyDataSetChanged();
      //  Toast.makeText(getContext(),"in onStart method"+DBqueries.cartItemModelList.size(),Toast.LENGTH_SHORT).show();

        if(DBqueries.rewardModelList.size()==0){
            loadingDialog.show();
            DBqueries.loadRewards(getContext(),loadingDialog,false);
        }
        if(DBqueries.cartItemModelList.size()==0){
            DBqueries.cartList.clear();
            DBqueries.loadCartList(getContext(),loadingDialog,true,new TextView(getContext()),totalAmount);

            long totAmount=0;
            for(int i=0;i<DBqueries.cartList.size();i++){
                if(!DBqueries.cartItemModelList.get(i).isInStock()) {
                    totAmount += Long.parseLong(DBqueries.cartItemModelList.get(i).getProductPrice());
                }
            }
            if(totAmount==0){
                amountLayout.setVisibility(View.GONE);
            }else{
                totalAmount.setText("Rs."+totAmount+"/-");
                amountLayout.setVisibility(View.VISIBLE);
            }
        }
        else {
            LinearLayout parent=(LinearLayout) totalAmount.getParent().getParent();
            if(DBqueries.cartItemModelList.get(DBqueries.cartItemModelList.size()-1).getType()==CartItemModel.TOTAL_AMOUNT){
                parent.setVisibility(View.VISIBLE);
            }else{
                parent.setVisibility(View.GONE);
            }
            loadingDialog.dismiss();
        }
       // Toast.makeText(getContext(),"in onStart method **** "+DBqueries.cartItemModelList.size(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        for (CartItemModel cartItemModel : DBqueries.cartItemModelList) {
            if (!TextUtils.isEmpty(cartItemModel.getSelectedCoupenId())) {
                for (RewardModel rewardModel : DBqueries.rewardModelList) {
                    if (rewardModel.getCoupenId().equals(cartItemModel.getSelectedCoupenId())) {
                        rewardModel.setAlreadyUsed(false);
                    }
                    cartItemModel.setSelectedCoupenId(null);
                    if(MyRewardsFragment.myRewardsAdapter!=null){
                        MyRewardsFragment.myRewardsAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }
}

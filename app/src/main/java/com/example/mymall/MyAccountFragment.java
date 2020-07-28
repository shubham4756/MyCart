package com.example.mymall;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mymall.Model.MyOrderItemModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    public MyAccountFragment() {
        // Required empty public constructor
    }

    public static final int MANAGE_ADDRESS = 1;
    private Button viewAllAddressesBtn,signOutBtn;
    private CircleImageView profileView,currentOrderImage;
    private TextView name,email,tvCurrentOrderStatus;
    private LinearLayout layoutContainer,recentOrdersContainer;
    private Dialog loadingDialog;
    private ImageView orderIndicator,packedIndicator,shippedIndicator,deliveredIndicator;
    private ProgressBar o_p_progress,p_s_progress,s_d_progress;
    private TextView yourRecentOrdersTitle;
    private TextView addressName,address,pincode;
    private FloatingActionButton settingsBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        ////loading dialong for set data of product details
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ///////end of loading dialong

        profileView=view.findViewById(R.id.profileImageProfile);
        name=view.findViewById(R.id.userNameProfile);
        email=view.findViewById(R.id.userEmailProile);
        layoutContainer=view.findViewById(R.id.layout_container);
        currentOrderImage=view.findViewById(R.id.currentOrderImage);
        tvCurrentOrderStatus=view.findViewById(R.id.currentOrderStatus);

        orderIndicator=view.findViewById(R.id.ordereedIndicator);
        packedIndicator=view.findViewById(R.id.packedIndicator);
        shippedIndicator=view.findViewById(R.id.shippedIndicator);
        deliveredIndicator=view.findViewById(R.id.deliverdIndicator);
        o_p_progress=view.findViewById(R.id.orderPackedProgress);
        p_s_progress=view.findViewById(R.id.packedShippedProgress);
        s_d_progress=view.findViewById(R.id.shippedDeliveredProgress);
        yourRecentOrdersTitle=view.findViewById(R.id.your_recent_order_title);
        recentOrdersContainer=view.findViewById(R.id.recentOrderContainer);
        addressName=view.findViewById(R.id.fullNameAddresses);
        address=view.findViewById(R.id.addressAddresses);
        pincode=view.findViewById(R.id.pincodeAddressTextview);
        signOutBtn=view.findViewById(R.id.sign_out_btn);
        settingsBtn=view.findViewById(R.id.settingFloatingButtonProfile);


        layoutContainer.getChildAt(1).setVisibility(View.GONE);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                for(MyOrderItemModel orderItemModel:DBqueries.myOrderItemModelList){
                    if(!orderItemModel.isCancellationRequested()){
                        if(!(orderItemModel.getOrderStatus().equals("Delivered") || orderItemModel.getOrderStatus().equals("Cancelled"))) {
                            layoutContainer.getChildAt(1).setVisibility(View.VISIBLE);
                            Glide.with(getContext()).load(orderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.home_logo_icon_foreground)).into(currentOrderImage);
                            tvCurrentOrderStatus.setText(orderItemModel.getOrderStatus());

                            switch (orderItemModel.getOrderStatus()) {
                                case "Ordered":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    break;
                                case "Packed":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    o_p_progress.setProgress(100);
                                    break;
                                case "Shipped":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    o_p_progress.setProgress(100);
                                    p_s_progress.setProgress(100);
                                    break;
                                case "Out for Delivery":
                                    orderIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    packedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    shippedIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    deliveredIndicator.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.successGreen)));
                                    o_p_progress.setProgress(100);
                                    p_s_progress.setProgress(100);
                                    s_d_progress.setProgress(100);
                                    break;
                            }
                        }
                    }
                }
                int i = 0;
                for (MyOrderItemModel myOrderItemModel : DBqueries.myOrderItemModelList) {
                    if (i >= 4)
                        break;
                    if (myOrderItemModel.getOrderStatus().equals("Delivered")) {
                        Glide.with(getContext()).load(myOrderItemModel.getProductImage()).apply(new RequestOptions().placeholder(R.mipmap.home_logo_icon_foreground)).into((CircleImageView) recentOrdersContainer.getChildAt(i));
                        i++;
                    }
                }
                if (i == 0) {
                    yourRecentOrdersTitle.setText("No recent Orders.");
                }
                if (i < 3) {
                    for (int x = i; x < 4; x++) {
                        recentOrdersContainer.getChildAt(x).setVisibility(View.GONE);
                    }
                }
                loadingDialog.show();
                loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loadingDialog.setOnDismissListener(null);
                        if(DBqueries.addressesModelList.size()==0){
                            addressName.setText("No Address");
                            address.setText("-");
                            pincode.setText("-");
                        }
                        else{
                            setAddress();
                        }
                    }
                });
                DBqueries.loadAddresses(getContext(),loadingDialog,false);
            }
        });

        ////added by me
        if(!loadingDialog.isShowing()){
            if(DBqueries.addressesModelList.size()==0){
                addressName.setText("No Address");
                address.setText("-");
                pincode.setText("-");
            }
            else{
                setAddress();
            }
        }
        ///added by me

        DBqueries.loadOrders(getContext(),null,loadingDialog);

        viewAllAddressesBtn = view.findViewById(R.id.viewAllAddressesButton);
        viewAllAddressesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(getContext(),MyAddressesActivity.class);
                intent.putExtra("MODE",MANAGE_ADDRESS);
                startActivity(intent);
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                DBqueries.clearData();
                SignUpFragment.disableCloseBtn=false;
                SigninFragment.disableCloseBtn=false;
                Intent registerIntent=new Intent(getContext(),RegisterActivity.class);
                startActivity(registerIntent);
                getActivity().finish();
            }
        });


        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateUserInfo=new Intent(getContext(),UpdateUserInfoActivity.class);
                updateUserInfo.putExtra("Name",name.getText());
                updateUserInfo.putExtra("Email",email.getText());
                updateUserInfo.putExtra("Photo",DBqueries.profile);
                startActivity(updateUserInfo);
            }
        });
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();

        name.setText(DBqueries.fullname);
        email.setText(DBqueries.email);
        if(!DBqueries.profile.equals("")){
            Glide.with(getContext()).load(DBqueries.profile).apply(new RequestOptions().placeholder(R.mipmap.profile_placeholder_foreground)).into(profileView);
        } else {
            profileView.setImageResource(R.mipmap.profile_placeholder_foreground);
        }

        if(!loadingDialog.isShowing()){
            if(DBqueries.addressesModelList.size()==0){
                addressName.setText("No Address");
                address.setText("-");
                pincode.setText("-");
            }
            else{
                setAddress();
            }
        }
    }

    private void setAddress(){
        String nameText,mobileNo;
        nameText=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getName();
        mobileNo=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMoblieNo();
        if(!DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMoblieNo().equals("")){
            mobileNo=mobileNo+" or "+DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAlternateMoblieNo();
        }
        addressName.setText(nameText+" - "+mobileNo);
        String flatNo=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFlatNo();
        String locality=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLocality();
        String landmark=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getLandmark();
        String city=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getCity();
        String state=DBqueries.addressesModelList.get(DBqueries.selectedAddress).getState();

        if(landmark.equals("")) {
            address.setText(flatNo + " " + locality + " " + city + " " + state);
        } else {
            address.setText(flatNo + " " + locality + " " + landmark + " " + city + " " + state);
        }
        pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPincode());

    }
}

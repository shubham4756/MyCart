package com.example.mymall.Adapter;

import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymall.AddAddressActivity;
import com.example.mymall.DBqueries;
import com.example.mymall.Model.AddressesModel;
import com.example.mymall.MyAddressesActivity;
import com.example.mymall.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mymall.MyAddressesActivity.previousAddress;
import static com.example.mymall.MyAddressesActivity.refreshItem;

public class AddressesAdapter extends RecyclerView.Adapter<AddressesAdapter.ViewHolder> {

    List<AddressesModel> modelList;
    public static final int SELECT_ADDRESS = 0;
    public static final int MANAGE_ADDRESS = 1;
    private int modeset;
    private int preSelectedPosition;
    private boolean refresh=false;
    private Dialog loadingDialog;

    public AddressesAdapter(List<AddressesModel> modelList, int modeset,Dialog loadingDialog) {
        this.modelList = modelList;
        this.modeset = modeset;
        preSelectedPosition = DBqueries.selectedAddress;
        this.loadingDialog = loadingDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.addresses_item_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String city=modelList.get(position).getCity();
        String locality=modelList.get(position).getLocality();
        String flatNo=modelList.get(position).getFlatNo();
        String pincode=modelList.get(position).getPincode();
        String landmark=modelList.get(position).getLandmark();
        String name=modelList.get(position).getName();
        String mobileNo=modelList.get(position).getMoblieNo();
        String alternateMobileNo=modelList.get(position).getAlternateMoblieNo();
        String state=modelList.get(position).getState();
        boolean selected=modelList.get(position).isSelected();

        holder.setData(name,city,pincode,selected,position,mobileNo,alternateMobileNo,flatNo,locality,state,landmark);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvfullname,tvaddresses,tvpincode;
        private ImageView iconView;
        private LinearLayout optionContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvfullname = itemView.findViewById(R.id.nameAddressItem);
            tvaddresses = itemView.findViewById(R.id.addressesAddressItem);
            tvpincode = itemView.findViewById(R.id.pincodeAddressItem);
            iconView = itemView.findViewById(R.id.iconViewAddressLayout);
            optionContainer = itemView.findViewById(R.id.optionContainerAddressLayout);
        }

        private void setData(String userName, String city, String userPincode, boolean selected, final int position, String mobileNo, String alternateMobileNo, String flatNo, final String locality, String state, String landmark) {
            if(!alternateMobileNo.equals("")){
                mobileNo=mobileNo+" or "+alternateMobileNo;
            }
            tvfullname.setText(userName+" - "+mobileNo);
            if(landmark.equals("")) {
                tvaddresses.setText(flatNo + " " + locality + " " + city + " " + state);
            } else {
                tvaddresses.setText(flatNo + " " + locality + " " + landmark + " " + city + " " + state);
            }
            tvpincode.setText(userPincode);


            if(modeset == SELECT_ADDRESS) {
                iconView.setImageResource(R.drawable.check_icon);
                if(selected) {
                    iconView.setVisibility(View.VISIBLE);
                    preSelectedPosition = position;
                } else {
                    iconView.setVisibility(View.GONE);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(preSelectedPosition != position) {
                            modelList.get(position).setSelected(true);
                            modelList.get(preSelectedPosition).setSelected(false);
                            refreshItem(preSelectedPosition, position);
                            preSelectedPosition = position;
                            DBqueries.selectedAddress=position;
                        }
                    }
                });

            } else if (modeset == MANAGE_ADDRESS) {
                optionContainer.setVisibility(View.GONE);
                optionContainer.getChildAt(0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { ///edit address
                        Intent addAddressIntent=new Intent(itemView.getContext(), AddAddressActivity.class);
                        addAddressIntent.putExtra("INTENT","update_address");
                        addAddressIntent.putExtra("index",position);
                        itemView.getContext().startActivity(addAddressIntent);
                        refresh=false;
                    }
                });
                optionContainer.getChildAt(1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //remove address
                        loadingDialog.show();
                        Map<String,Object> addresses=new HashMap<>();
                        int x=0;
                        int selected=-1;
                        for(int i=0;i<modelList.size();i++){
                            if(i!=position){
                                x++;
                                addresses.put("city_"+x,modelList.get(i).getCity());
                                addresses.put("locality_"+x,modelList.get(i).getLocality());
                                addresses.put("flat_no_"+x,modelList.get(i).getFlatNo());
                                addresses.put("pincode_"+x,modelList.get(i).getPincode());
                                addresses.put("landmark_"+x,modelList.get(i).getLandmark());
                                addresses.put("name_"+x,modelList.get(i).getName());
                                addresses.put("mobile_no_"+x,modelList.get(i).getMoblieNo());
                                addresses.put("alternate_mobile_no_"+x,modelList.get(i).getAlternateMoblieNo());
                                addresses.put("state_"+x,modelList.get(i).getState());

                                if(modelList.get(position).isSelected()){
                                    if(position-1>=0){
                                        if(x==position){
                                            addresses.put("selected_"+x,true);
                                            selected=x;
                                        } else {
                                            addresses.put("selected_"+x,modelList.get(i).isSelected());
                                        }
                                    } else {
                                        if(x==1){
                                            addresses.put("selected_"+x,true);
                                            selected=x;
                                        } else {
                                            addresses.put("selected_"+x,modelList.get(i).isSelected());
                                        }
                                    }
                                } else {
                                    addresses.put("selected_"+x,modelList.get(i).isSelected());
                                    if(modelList.get(i).isSelected()){
                                        selected=x;
                                    }
                                }
                            }
                        }
                        addresses.put("list_size",x);

                        final int finalSelected = selected;
                        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                                .set(addresses).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    DBqueries.addressesModelList.remove(position);
                                    if(finalSelected!=-1) {
                                        DBqueries.selectedAddress = finalSelected - 1;
                                        DBqueries.addressesModelList.get(finalSelected - 1).setSelected(true);
                                    } else if(DBqueries.addressesModelList.size()==0){
                                        DBqueries.selectedAddress=-1;
                                    }
                                    notifyDataSetChanged();
                                } else {
                                    String error=task.getException().getMessage();
                                    Toast.makeText(itemView.getContext(),error,Toast.LENGTH_SHORT).show();
                                }
                                loadingDialog.dismiss();
                            }
                        });
                        refresh=false;
                    }
                });
                iconView.setImageResource(R.drawable.three_verticat_dot);
                iconView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        optionContainer.setVisibility(View.VISIBLE);
                        if(refresh) {
                            refreshItem(preSelectedPosition, preSelectedPosition);
                        } else {
                            refresh=true;
                        }
                        preSelectedPosition = position;
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refreshItem(preSelectedPosition,preSelectedPosition);
                        preSelectedPosition = -1;
                    }
                });
            }

        }
    }
}

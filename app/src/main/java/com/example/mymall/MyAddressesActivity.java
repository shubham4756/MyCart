package com.example.mymall;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.mymall.Adapter.AddressesAdapter;
import com.example.mymall.Model.AddressesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.mymall.Adapter.AddressesAdapter.MANAGE_ADDRESS;
import static com.example.mymall.Adapter.AddressesAdapter.SELECT_ADDRESS;

public class MyAddressesActivity extends AppCompatActivity {

    private RecyclerView addressRecyclerView;
    private static AddressesAdapter addressesAdapter;
    private Button deliveryButton;
    private TextView addressesSaved;
    LinearLayout addNewAddressBtn;
    public static int previousAddress;
    private Dialog loadingDialog;
    private int mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_addresses);


        ///Also from delivery Part 40
        Toolbar toolbar = findViewById(R.id.toolbarMyAddressActivity);
        deliveryButton   =  findViewById(R.id.deliverHereButton);
        addNewAddressBtn=findViewById(R.id.addNewAddressBtn);
        addressesSaved=findViewById(R.id.addressSaved);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("MY Addresses");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ////loading dialong for set data of product details
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(this.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                addressesSaved.setText(String.valueOf(DBqueries.addressesModelList.size())+" saved addresses");
            }
        });
        ///////end of loading dialong

        previousAddress=DBqueries.selectedAddress;
        /////fetch data from intent
        //Mode from delivery selected Address

        //int mode fo now 0

        mode = getIntent().getIntExtra("MODE",-1);

        addressRecyclerView = findViewById(R.id.addressesRecyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        addressRecyclerView.setLayoutManager(linearLayoutManager);

        if(mode == SELECT_ADDRESS){
            deliveryButton.setVisibility(View.VISIBLE);
        } else if (mode == MANAGE_ADDRESS) {
            deliveryButton.setVisibility(View.GONE);
        }

        deliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DBqueries.selectedAddress!=previousAddress){
                    loadingDialog.show();
                    final int previousAddressIndex=previousAddress;
                    previousAddress=DBqueries.selectedAddress;

                    Map<String,Object> updateSelection= new HashMap<>();
                    updateSelection.put("selected_"+String.valueOf(previousAddressIndex+1),false);
                    updateSelection.put("selected_"+String.valueOf(DBqueries.selectedAddress+1),true);

                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                            .update(updateSelection).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                finish();
                            }else{
                                previousAddress=previousAddressIndex;
                                String error = task.getException().getMessage();
                                Toast.makeText(MyAddressesActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                }else{
                    finish();
                }
            }
        });
        addressesAdapter = new AddressesAdapter(DBqueries.addressesModelList,mode,loadingDialog);
        addressRecyclerView.setAdapter(addressesAdapter);

        ((SimpleItemAnimator)addressRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        addressesAdapter.notifyDataSetChanged();


        addNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addAddressIntent=new Intent(MyAddressesActivity.this,AddAddressActivity.class);
                if(mode!=SELECT_ADDRESS){
                    addAddressIntent.putExtra("INTENT", "manage");
                } else {
                    addAddressIntent.putExtra("INTENT", "null");
                }
                startActivity(addAddressIntent);
            }
        });
        /////////
        ////// Start From Delivery activity from intent from continue button
        ///// delivery activity to donot finish activity
        ////////

    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(MyAddressesActivity.this,DBqueries.addressesModelList.size()+" - - "+DBqueries.selectedAddress+" in this ",Toast.LENGTH_SHORT).show();
        addressesAdapter.notifyDataSetChanged();
        addressesSaved.setText(String.valueOf(DBqueries.addressesModelList.size())+" saved addresses");
    }

    public static void refreshItem(int deselect, int select){
        addressesAdapter.notifyItemChanged(deselect);
        addressesAdapter.notifyItemChanged(select);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if(mode==SELECT_ADDRESS) {
                    if (DBqueries.selectedAddress != previousAddress) {
                        DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                        DBqueries.addressesModelList.get(previousAddress).setSelected(true);
                        DBqueries.selectedAddress = previousAddress;
                    }
                }
                finish();
                return true;
            default :
        }

        return super.onOptionsItemSelected(item);
    }

    public  void onBackPressed(){
        if(mode==SELECT_ADDRESS) {
            if (DBqueries.selectedAddress != previousAddress) {
                DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                DBqueries.addressesModelList.get(previousAddress).setSelected(true);
                DBqueries.selectedAddress = previousAddress;
            }
        }
        super.onBackPressed();
    }

}

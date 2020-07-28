package com.example.mymall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mymall.Model.AddressesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddAddressActivity extends AppCompatActivity {

    private EditText city;
    private EditText locality;
    private EditText flatNo;
    private EditText pincode;
    private EditText landmark;
    private EditText name;
    private EditText moblieNo;
    private EditText alternateMoblieNo;
    private Spinner stateSpinner;
    private Button saveButton;
    private Dialog loadingDialog;

    private String[] stateList;
    private String selectedState;

    private boolean updateAddress=false;
    private AddressesModel addressesModel;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        Toolbar toolbar = findViewById(R.id.toolbarAddress);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Add a new Address");

        ////loading dialong for set data of product details
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ///////end of loading dialong

        stateList=getResources().getStringArray(R.array.india_states);

        //cart fragment (39) --> delivery Activity
        city=findViewById(R.id.city);
        locality=findViewById(R.id.locality);
        flatNo=findViewById(R.id.flatNo);
        pincode=findViewById(R.id.pincode);
        landmark=findViewById(R.id.landmark);
        name=findViewById(R.id.name);
        moblieNo=findViewById(R.id.mobileNo);
        alternateMoblieNo=findViewById(R.id.alternateMobileNo);
        stateSpinner=findViewById(R.id.state_spinner);
        saveButton = findViewById(R.id.btnAddSave);

        ArrayAdapter spinnerAdapter=new ArrayAdapter(this,android.R.layout.simple_spinner_item,stateList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(spinnerAdapter);

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedState=stateList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(getIntent().getStringExtra("INTENT").equals("update_address")){
            updateAddress=true;
            position=getIntent().getIntExtra("index",-1);
            addressesModel=DBqueries.addressesModelList.get(position);

            city.setText(addressesModel.getCity());
            locality.setText(addressesModel.getLocality());
            flatNo.setText(addressesModel.getFlatNo());
            landmark.setText(addressesModel.getLandmark());
            name.setText(addressesModel.getName());
            moblieNo.setText(addressesModel.getMoblieNo());
            alternateMoblieNo.setText(addressesModel.getAlternateMoblieNo());
            pincode.setText(addressesModel.getPincode());

            for(int i=0;i<stateList.length;i++){
                if(stateList[i].equals(addressesModel.getState())) {
                    stateSpinner.setSelection(i);
                }
            }
            saveButton.setText("Update");
        } else {
            position=(int)DBqueries.addressesModelList.size();
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send to delivery Activity
                //TODO: check some important fields are filled or not contidions in video no 71
                if(!TextUtils.isEmpty(city.getText()) && !TextUtils.isEmpty(locality.getText()) &&
                        !TextUtils.isEmpty(flatNo.getText()) && !TextUtils.isEmpty(pincode.getText()) && pincode.getText().length()==6 &&
                        !TextUtils.isEmpty(name.getText()) && !TextUtils.isEmpty(moblieNo.getText()) && moblieNo.getText().length()==10){
                      loadingDialog.show();
                    Map<String,Object> addAddress = new HashMap();
                    addAddress.put("city_"+String.valueOf(position+1),city.getText().toString());
                    addAddress.put("locality_"+String.valueOf(position+1),locality.getText().toString());
                    addAddress.put("flat_no_"+String.valueOf(position+1),flatNo.getText().toString());
                    addAddress.put("pincode_"+String.valueOf(position+1),pincode.getText().toString());
                    addAddress.put("landmark_"+String.valueOf(position+1),landmark.getText().toString());
                    addAddress.put("name_"+String.valueOf(position+1),name.getText().toString());
                    addAddress.put("mobile_no_"+String.valueOf(position+1),moblieNo.getText().toString());
                    addAddress.put("alternate_mobile_no_"+String.valueOf(position+1),alternateMoblieNo.getText().toString());
                    addAddress.put("state_"+String.valueOf(position+1),selectedState.toString());
                    if(!updateAddress) {
                        addAddress.put("list_size", (long) DBqueries.addressesModelList.size() + 1);
                        if (getIntent().getStringExtra("INTENT").equals("manage")) {
                           // if (DBqueries.addressesModelList.size() == 0) {
                                addAddress.put("selected_" + String.valueOf(position + 1), true);
//                             } else {
//                                addAddress.put("selected_" + String.valueOf(position + 1), false);
//                            }
                        } else {
                            addAddress.put("selected_" + String.valueOf(position + 1), true);
                        }
                        if (DBqueries.addressesModelList.size() > 0) {
                        //    Toast.makeText(AddAddressActivity.this, String.valueOf(DBqueries.selectedAddress + 1) + "  *** in the false  ", Toast.LENGTH_SHORT).show();
                            addAddress.put("selected_" + String.valueOf(DBqueries.selectedAddress + 1), false);
                        }
                    }


                    FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_ADDRESSES")
                            .update(addAddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (!updateAddress) {
                                    if (DBqueries.addressesModelList.size() > 0) {
                                     //    Toast.makeText(AddAddressActivity.this,DBqueries.selectedAddress+" in first condition ",Toast.LENGTH_SHORT).show();
                                        DBqueries.addressesModelList.get(DBqueries.selectedAddress).setSelected(false);
                                    }
                                    DBqueries.addressesModelList.add(new AddressesModel(true, city.getText().toString(), locality.getText().toString()
                                            , flatNo.getText().toString(), pincode.getText().toString(), landmark.getText().toString(), name.getText().toString()
                                            , moblieNo.getText().toString(), alternateMoblieNo.getText().toString(), selectedState.toString()));

                                    if (getIntent().getStringExtra("INTENT").equals("manage")){
                                      //  if(DBqueries.addressesModelList.size()==1){
                                            DBqueries.selectedAddress = position;//DBqueries.addressesModelList.size()-1;
                                     // }
                                    } else {
                                        Toast.makeText(AddAddressActivity.this,DBqueries.selectedAddress+" in else ",Toast.LENGTH_SHORT).show();;
                                        DBqueries.selectedAddress =position;
                                    }
                                } else {
                                    DBqueries.addressesModelList.set(position,new AddressesModel(true, city.getText().toString(), locality.getText().toString()
                                            , flatNo.getText().toString(), pincode.getText().toString(), landmark.getText().toString(), name.getText().toString()
                                            , moblieNo.getText().toString(), alternateMoblieNo.getText().toString(), selectedState.toString()));

                                }

                                if (getIntent().getStringExtra("INTENT").equals("deliveryIntent")) {
                                    Intent intent = new Intent(AddAddressActivity.this, DeliveryActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(AddAddressActivity.this,String.valueOf(DBqueries.selectedAddress)+" *** "+String.valueOf(DBqueries.addressesModelList.size()-1),Toast.LENGTH_SHORT).show();
                                    MyAddressesActivity.refreshItem(DBqueries.selectedAddress, DBqueries.addressesModelList.size() - 1);
                                }
                                MyAddressesActivity.previousAddress = DBqueries.selectedAddress;
                                finish();
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(AddAddressActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });
                } else{
                    if(TextUtils.isEmpty(city.getText())){
                        city.requestFocus();
                    }else if(TextUtils.isEmpty(locality.getText())){
                        locality.requestFocus();
                    }else if(TextUtils.isEmpty(flatNo.getText())){
                        flatNo.requestFocus();
                    }else if(TextUtils.isEmpty(pincode.getText()) || pincode.getText().length()!=6){
                        pincode.requestFocus();
                        Toast.makeText(AddAddressActivity.this,"Please provide valid pincode",Toast.LENGTH_SHORT).show();
                    }else if(TextUtils.isEmpty(name.getText())){
                        name.requestFocus();
                    }else if(TextUtils.isEmpty(moblieNo.getText()) || moblieNo.getText().length()!=10){
                        moblieNo.requestFocus();
                        Toast.makeText(AddAddressActivity.this,"Please provide valid moblie No.",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
        }

        return super.onOptionsItemSelected(item);
    }
}

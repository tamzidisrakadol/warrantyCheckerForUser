package com.example.warrantycheckerforuser.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.warrantycheckerforuser.R;
import com.example.warrantycheckerforuser.databinding.ActivityMainBinding;
import com.example.warrantycheckerforuser.model.CustomerModel;
import com.example.warrantycheckerforuser.repos.CaptureAct;
import com.example.warrantycheckerforuser.utlity.Constraints;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding activityMainBinding;
    String barcodeValue;
    Boolean isBarcodeScanned = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        getSupportActionBar().hide();

        activityMainBinding.barcodeTV.setOnClickListener(v -> {
            scanBarcode();
        });

        activityMainBinding.searchbutton.setOnClickListener(v -> {
            if (isBarcodeScanned){
                activityMainBinding.cardView.setVisibility(View.VISIBLE);
                fetchCustomerDetails();
            }
        });

    }

    private void fetchCustomerDetails(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.customer_battery_details, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    CustomerModel customerModel = new CustomerModel();
                    customerModel.setCustomerName(jsonObject.getString("customerName"));
                    customerModel.setPurchaseDate(jsonObject.getString("purchaseDate"));
                    customerModel.setExpireDate(jsonObject.getString("ExpireDate"));
                    activityMainBinding.customerNameTV.setText(customerModel.getCustomerName());
                    activityMainBinding.purchaseDateTV.setText(customerModel.getPurchaseDate());
                    String expireDate = customerModel.getExpireDate();
                    daysLeft(expireDate);
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }, error -> {
            Log.d("Etag",error.getMessage());
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("batteryBarcode",barcodeValue);
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void scanBarcode() {
        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setPrompt("Volume up to flash on");
        scanOptions.setBeepEnabled(true);
        scanOptions.setOrientationLocked(true);
        scanOptions.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(scanOptions);
    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
           activityMainBinding.barcodeTV.setText(result.getContents());
            barcodeValue = result.getContents();
            isBarcodeScanned = true;
        }
    });

    private void daysLeft(String expireDate){
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formatCurrentDate = sdf.format(currentDate);


        try {
            Date firstDate = sdf.parse(formatCurrentDate);
            Date secondDate = sdf.parse(expireDate);

            Long difference = Math.abs(firstDate.getTime()-secondDate.getTime()) ;
            Long differenceToDay = difference/(24*60*60*1000);
            if (differenceToDay >= 0){
               activityMainBinding.expireDateTV.setText(String.valueOf(differenceToDay)+" days left");
            }else{
               activityMainBinding.expireDateTV.setText("0 days left");
            }


        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
package com.example.warrantycheckerforuser.views;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Loading....");
        progressDialog.setCancelable(false);
        getSupportActionBar().hide();

        activityMainBinding.barcodeTV.setOnClickListener(v -> {
            scanBarcode();
        });

        activityMainBinding.searchbutton.setOnClickListener(v -> {
            if (isBarcodeScanned){

                fetchCustomerDetails();
            }
        });

    }

    private void fetchCustomerDetails(){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constraints.BASE_API, response -> {
            activityMainBinding.searchbutton.setEnabled(false);
           if (!response.equals("0")){
               progressDialog.dismiss();
               activityMainBinding.cardView.setVisibility(View.VISIBLE);
               try {
                   JSONObject jsonObject=new JSONObject(response);
                   int id=jsonObject.getInt("id");
                   int retailerID=jsonObject.getInt("retailer_id");
                   String sellDate=jsonObject.getString("sell_date");
                   String expireDate=jsonObject.getString("end_time");
                   String user_name=jsonObject.getString("user_name");
                   String user_address=jsonObject.getString("user_address");
                   String user_phone=jsonObject.getString("user_phone");
                   activityMainBinding.sellDateTV.setText("Sell :"+sellDate);
                   activityMainBinding.nameTV.setText("Name : "+user_name);
                   activityMainBinding.addressTV.setText("Address : "+user_address);
                   daysLeft(expireDate);
               } catch (JSONException e) {
                   throw new RuntimeException(e);
               }
           }else{
               progressDialog.dismiss();
               Toast.makeText(this, "Product not found !", Toast.LENGTH_SHORT).show();
           }
        }, error -> {
            Log.d("Etag",error.getMessage());
            progressDialog.dismiss();
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> map = new HashMap<>();
                map.put("code",barcodeValue);
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
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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
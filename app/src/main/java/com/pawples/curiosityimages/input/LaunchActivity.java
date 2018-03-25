package com.pawples.curiosityimages.input;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pawples.curiosityimages.MainActivity;
import com.pawples.curiosityimages.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        String rover = getIntent().getExtras().getString("ROVER");

        String url = "https://api.nasa.gov/mars-photos/api/v1/rovers/" + rover + "/?api_key=BldvqDsBvxhlFq4w3x1kFgijM4lR2nGE1L3uqdDM";

        Log.i("TRACE","ROVER: " + rover);

        if (rover.equals("curiosity")) {
            loadCuriosity(url);
            Log.i("TRACE","URL: " + url);
        } else if (rover.equals("opportunity")) {
            loadOpportunity(url);
            Log.i("TRACE","URL: " + url);
        }

        final ProgressDialog dialog = new ProgressDialog(LaunchActivity.this);
        dialog.setMessage("Getting latest images");
        dialog.setCancelable(false);
        dialog.show();

    }

    private void loadCuriosity (String url) {
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject rover = jsonObject.getJSONObject("rover");
                    final String max_date = rover.getString("max_date");
                    final String max_sol = rover.getString("max_sol");
                    final String status = rover.getString("status");
                    final int total_photos_int = rover.getInt("total_photos");
                    final String total_photos = String.valueOf(total_photos_int);
                    final String landing_date = rover.getString("landing_date");
                    final String launch_date = rover.getString("launch_date");

                    Intent intent = new Intent(LaunchActivity.this,MainActivity.class);
                    intent.putExtra("MAX_DATE",max_date);
                    intent.putExtra("ROVER","curiosity");

                    intent.putExtra("MAX_SOL",max_sol);
                    intent.putExtra("STATUS",status);
                    intent.putExtra("TOTAL_PHOTOS",total_photos);
                    intent.putExtra("LANDING_DATE",landing_date);
                    intent.putExtra("LAUNCH_DATE",launch_date);

                    startActivity(intent);

                } catch (JSONException | NullPointerException e) {
                    Log.e("E","Error: ", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("E","Volley error: ", error);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(LaunchActivity.this);
        requestQueue.add(stringRequest);
    }

    private void loadOpportunity (String url) {
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject rover = jsonObject.getJSONObject("rover");
                    final String max_date = rover.getString("max_date");
                    final String max_sol = rover.getString("max_sol");
                    final String status = rover.getString("status");
                    final int total_photos_int = rover.getInt("total_photos");
                    final String total_photos = String.valueOf(total_photos_int);
                    final String landing_date = rover.getString("landing_date");
                    final String launch_date = rover.getString("launch_date");

                    Intent intent = new Intent(LaunchActivity.this,MainActivity.class);
                    intent.putExtra("MAX_DATE",max_date);
                    intent.putExtra("ROVER","opportunity");

                    intent.putExtra("MAX_SOL",max_sol);
                    intent.putExtra("STATUS",status);
                    intent.putExtra("TOTAL_PHOTOS",total_photos);
                    intent.putExtra("LANDING_DATE",landing_date);
                    intent.putExtra("LAUNCH_DATE",launch_date);

                    startActivity(intent);

                } catch (JSONException | NullPointerException e) {
                    Log.e("E","Error: ", e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("E","Volley error: ", error);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(LaunchActivity.this);
        requestQueue.add(stringRequest);
    }

}

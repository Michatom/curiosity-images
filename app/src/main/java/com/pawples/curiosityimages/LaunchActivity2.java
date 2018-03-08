package com.pawples.curiosityimages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class LaunchActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch2);
        String url = "https://api.nasa.gov/mars-photos/api/v1/rovers/opportunity/?api_key=BldvqDsBvxhlFq4w3x1kFgijM4lR2nGE1L3uqdDM";
        new processJson().execute(url);
    }
    private class processJson extends AsyncTask<String, Void, String> {

        private final ProgressDialog dialog = new ProgressDialog(LaunchActivity2.this);

        @Override
        protected void onPreExecute(){
            this.dialog.setMessage("Getting latest images");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        protected String doInBackground(String... strings) {
            String stream = null;
            String urlString = strings[0];
            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);
            return stream;
        }

        protected void onPostExecute(String stream) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            try {

                JSONObject jsonObject = new JSONObject(stream);
                JSONObject rover = jsonObject.getJSONObject("rover");
                final String max_date = rover.getString("max_date");
                final String max_sol = rover.getString("max_sol");
                final String status = rover.getString("status");
                final int total_photos_int = rover.getInt("total_photos");
                final String total_photos = String.valueOf(total_photos_int);
                final String landing_date = rover.getString("landing_date");
                final String launch_date = rover.getString("launch_date");

                Intent intent = new Intent(LaunchActivity2.this,MainActivity.class);
                intent.putExtra("MAX_DATE",max_date);
                intent.putExtra("ROVER","opportunity");
                //for rover status
                intent.putExtra("MAX_SOL",max_sol);
                intent.putExtra("STATUS",status);
                intent.putExtra("TOTAL_PHOTOS",total_photos);
                intent.putExtra("LANDING_DATE",landing_date);
                intent.putExtra("LAUNCH_DATE",launch_date);
                startActivity(intent);

            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
}

package com.pawples.curiosityimages;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        String url = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/?api_key=BldvqDsBvxhlFq4w3x1kFgijM4lR2nGE1L3uqdDM";
        new processJson().execute(url);
    }
    private class processJson extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(LaunchActivity.this);

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
                Intent intent = new Intent(LaunchActivity.this,MainActivity.class);
                intent.putExtra("MAX_DATE",max_date);
                intent.putExtra("ROVER","curiosity");
                startActivity(intent);

            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
}

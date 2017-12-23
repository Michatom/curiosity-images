package com.pawples.curiosityimages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar_actionbar);
        toolbar.setTitle("Curiosity Images");
        setSupportActionBar(toolbar);

        TimeZone tz = TimeZone.getTimeZone("GMT00:00");
        Calendar cal = Calendar.getInstance(tz);
        Date currentTime = Calendar.getInstance(tz).getTime();
        cal.setTime(currentTime);
        cal.add(Calendar.DATE, -1);
        Date dayBefore = cal.getTime();
        String date = DateFormat.format("yyyy-MM-dd", dayBefore).toString();

        String urlJson = "https://api.nasa.gov/mars-photos/api/v1/rovers/curiosity/photos?earth_date="+date+ "&api_key=BldvqDsBvxhlFq4w3x1kFgijM4lR2nGE1L3uqdDM";
        new processJSON().execute(urlJson);
    }

    private class processJSON extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute(){
            this.dialog.setMessage("Loading");
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

            List<DataJSON> data=new ArrayList<>();

            try {

                JSONObject jsonObject = new JSONObject(stream);
                JSONArray jsonArray = jsonObject.getJSONArray("photos");

                for(int i=0;i<jsonArray.length();i++){
                    DataJSON dataJSON = new DataJSON();
                    dataJSON.img = jsonArray.getJSONObject(i).getString("img_src");
                    dataJSON.img_id = jsonArray.getJSONObject(i).getString("id");
                    dataJSON.date = jsonArray.getJSONObject(i).getString("earth_date");
                    dataJSON.sol = jsonArray.getJSONObject(i).getString("sol");
                    JSONObject cameraObject = jsonArray.getJSONObject(i).getJSONObject("camera");
                    dataJSON.camera_name = cameraObject.getString("full_name");
                    data.add(dataJSON);

                    RecyclerView recyclerView = findViewById(R.id.recycler);
                    AdapterJSON jsonAdapter = new AdapterJSON(MainActivity.this, data);
                    recyclerView.setAdapter(jsonAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
            catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
    class AdapterJSON extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private LayoutInflater inflater;
        List<DataJSON> data = Collections.emptyList();

        public AdapterJSON(Context context, List<DataJSON> data){
            this.context=context;
            inflater= LayoutInflater.from(context);
            this.data=data;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=inflater.inflate(R.layout.cardview_img, parent,false);
            return new MyHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final MyHolder myHolder = (MyHolder) holder;
            final DataJSON current = data.get(position);
            myHolder.textDate.setText("Earth date · " + current.date);
            myHolder.textId.setText("Image ID · " + current.img_id);
            myHolder.textSol.setText("Martian sol · " + current.sol);
            myHolder.textCamera.setText("Instrument · " + current.camera_name);

            final RequestOptions options = new RequestOptions();
            options.centerCrop();

            Glide.with(context)
                    .load(current.img)
                    .apply(options)
                    .into(myHolder.imageView);

            ViewCompat.setTransitionName(myHolder.imageView, current.img_id);

            myHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(view.getContext(), OpenImage.class)
                            .putExtra("STRING_URL",current.img)
                            .putExtra("TRANSITION_NAME", ViewCompat.getTransitionName(myHolder.imageView));

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)view.getContext(),myHolder.imageView,ViewCompat.getTransitionName(myHolder.imageView));
                    startActivity(i, optionsCompat.toBundle());
                }
            });


        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        class MyHolder extends RecyclerView.ViewHolder{

            ImageView imageView;
            TextView textId;
            TextView textDate;
            TextView textCamera;
            TextView textSol;
            CardView cardView;

            public MyHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.squareImg);
                textId = itemView.findViewById(R.id.textId);
                textCamera = itemView.findViewById(R.id.textCamera);
                textSol = itemView.findViewById(R.id.textSol);
                textDate = itemView.findViewById(R.id.textDate);
                cardView = itemView.findViewById(R.id.card);
            }

        }

    }
}

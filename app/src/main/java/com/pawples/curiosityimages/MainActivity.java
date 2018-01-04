package com.pawples.curiosityimages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
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
import java.util.Objects;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String max_date;
        String rover;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                max_date = null;
                rover = null;
            } else {
                max_date = extras.getString("MAX_DATE");
                rover = extras.getString("ROVER");
            }
        } else {
            max_date = (String) savedInstanceState.getSerializable("MAX_DATE");
            rover = (String) savedInstanceState.getSerializable("ROVER");
        }

        Toolbar toolbar = findViewById(R.id.toolbar_actionbar);
        if (Objects.equals(rover, "curiosity")){
            toolbar.setTitle("Curiosity Images");
        } else if (Objects.equals(rover,"opportunity")){
            toolbar.setTitle("Opportunity Images");
        }

        setSupportActionBar(toolbar);

        String urlJson = "https://api.nasa.gov/mars-photos/api/v1/rovers/" + rover + "/photos?earth_date=" + max_date + "&api_key=BldvqDsBvxhlFq4w3x1kFgijM4lR2nGE1L3uqdDM";
        new processJSON().execute(urlJson);
    }

    @SuppressLint("StaticFieldLeak")
    private class processJSON extends AsyncTask<String, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute(){
            this.dialog.setMessage("Loading images");
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
                    runLayoutAnimation(recyclerView);
                }



            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

        }
    }
    class AdapterJSON extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private Context context;
        private LayoutInflater inflater;
        List<DataJSON> data = Collections.emptyList();

        AdapterJSON(Context context, List<DataJSON> data){
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
            myHolder.textId.setText("Image ID · " + current.img_id);

            ViewCompat.setTransitionName(myHolder.imageView, current.img_id);

            Glide.with(context)
                    .asBitmap()
                    .load(current.img)
                    .apply(new RequestOptions()
                            .centerCrop())
                    .transition(GenericTransitionOptions.with(R.anim.img_animation))
                    .into(myHolder.imageView);

            myHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent i = new Intent(view.getContext(), OpenImage.class)
                            .putExtra("STRING_URL",current.img)
                            .putExtra("TRANSITION_NAME", ViewCompat.getTransitionName(myHolder.imageView));

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)view.getContext(),myHolder.imageView,ViewCompat.getTransitionName(myHolder.imageView));
                    startActivity(i, optionsCompat.toBundle());
                }
            });

            myHolder.infoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog alertDialog = new AlertDialog.Builder(view.getContext()).create();
                    alertDialog.setTitle("About this image");
                    alertDialog.setMessage("Camera - " + current.camera_name + "\nDate - " + current.date + " (sol " + current.sol + ")");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            });

            myHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share
                            .setType("text/plain")
                            .putExtra(Intent.EXTRA_SUBJECT, "Image from Mars")
                            .putExtra(Intent.EXTRA_TEXT, current.img);
                    startActivity(Intent.createChooser(share,"Share link"));
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
            CardView cardView;
            ImageButton infoButton;
            ImageButton shareButton;

            MyHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.squareImg);
                textId = itemView.findViewById(R.id.textId);
                cardView = itemView.findViewById(R.id.card);
                infoButton = itemView.findViewById(R.id.icon_info);
                shareButton = itemView.findViewById(R.id.icon_share);
            }

        }

    }
    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent i = new Intent(MainActivity.this, RoverActivity.class);
        startActivity(i);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_weather) {
            String urlWeather = "http://marsweather.ingenology.com/v1/latest/?format=json";
            new processWeather().execute(urlWeather);
            return true;
        }
        if (id == R.id.action_info) {
            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
            dialog.setTitle("About");
            dialog.setMessage("Libraries used in this application - Glide by Bumptech, PhotoView by Chris Banes, RxDownloader by esafirm, Dexter by Karumi and Palette, CardView, RecyclerView and Design support libraries by Google.\n\nImages from Curiosity and Opportunity rovers are NASA's property. Two images of the rovers were made by NASA.");
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }
    @SuppressLint("StaticFieldLeak")
    private class processWeather extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            String stream = null;
            String urlString = strings[0];
            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);
            return stream;
        }

        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute(){
            this.dialog.setMessage("Loading weather");
            this.dialog.show();
        }

        protected void onPostExecute(String stream) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            try {

                JSONObject jsonObject = new JSONObject(stream);

                final JSONObject report = jsonObject.getJSONObject("report");
                final String terrestrial_date = report.get("terrestrial_date").toString();
                final String sol = report.get("sol").toString();
                final String min_temp = report.get("min_temp").toString();
                final String min_temp_fahrenheit = report.get("min_temp_fahrenheit").toString();
                final String max_temp = report.get("max_temp").toString();
                final String max_temp_fahrenheit = report.get("max_temp_fahrenheit").toString();
                final String pressure = report.get("pressure").toString();
                final String atmo_opacity = report.get("atmo_opacity").toString();
                final String season = report.get("season").toString();
                final String sunrise = report.get("sunrise").toString();
                final String sunset = report.get("sunset").toString();

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
                dialog.setTitle("Weather on Mars");
                dialog.setMessage("Weather measurement date · " + terrestrial_date + " (sol " + sol + ")" +
                        "\n\nMax temperature · " + max_temp + "°C (" + max_temp_fahrenheit + "°F)" +
                        "\nMinimal temperature · " + min_temp + "°C (" + min_temp_fahrenheit + "°F)" +
                        "\nAtmospheric conditions · " + atmo_opacity +
                        "\nPressure · " + pressure + " pascals" +
                        "\nSeason · " + season +
                        "\nSunrise · " + sunrise +
                        "\nSunset · " + sunset);
                dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog.show();
            } catch (JSONException | NullPointerException e) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                e.printStackTrace();
            }
        }
    }
}

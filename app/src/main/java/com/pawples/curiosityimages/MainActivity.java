package com.pawples.curiosityimages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pawples.curiosityimages.input.RoverActivity;
import com.pawples.curiosityimages.utils.DataJSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener dateSetListener;

    String max_date;
    String rover;
    String max_sol;
    String status;
    String total_photos;
    String landing_date;
    String launch_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                max_date = null;
                rover = null;
                max_sol = null;
                status = null;
                total_photos = null;
                landing_date = null;
                launch_date = null;
            } else {
                max_date = extras.getString("MAX_DATE");
                rover = extras.getString("ROVER");
                max_sol = extras.getString("MAX_SOL");
                status = extras.getString("STATUS");
                total_photos = extras.getString("TOTAL_PHOTOS");
                landing_date = extras.getString("LANDING_DATE");
                launch_date = extras.getString("LAUNCH_DATE");
            }
        } else {
            max_date = (String) savedInstanceState.getSerializable("MAX_DATE");
            rover = (String) savedInstanceState.getSerializable("ROVER");
            max_sol = (String) savedInstanceState.getSerializable("MAX_SOL");
            status = (String) savedInstanceState.getSerializable("STATUS");
            total_photos = (String) savedInstanceState.getSerializable("TOTAL_PHOTOS");
            landing_date = (String) savedInstanceState.getSerializable("LANDING_DATE");
            launch_date = (String) savedInstanceState.getSerializable("LAUNCH_DATE");
        }

        Log.i("TRACE","ROVER: " + rover);

        Toolbar toolbar = findViewById(R.id.toolbar_actionbar);
        if (Objects.equals(rover, "curiosity")){
            toolbar.setTitle("Curiosity Images");
        } else if (Objects.equals(rover,"opportunity")){
            toolbar.setTitle("Opportunity Images");
        }

        setSupportActionBar(toolbar);

        String urlJson = "https://api.nasa.gov/mars-photos/api/v1/rovers/" + rover + "/photos?earth_date=" + max_date + "&api_key=BldvqDsBvxhlFq4w3x1kFgijM4lR2nGE1L3uqdDM";
        Log.i("TRACE","URL: " + urlJson);
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Intent i = new Intent(MainActivity.this,DateActivity.class);
                i.putExtra("DATE",year+"-" + month + "-" + day);
                i.putExtra("ROVER",rover);
                startActivity(i);
            }
        };

        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading images");
        dialog.setCancelable(false);
        dialog.show();

        StringRequest stringRequest = new StringRequest(urlJson, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                List<DataJSON> data = new ArrayList<>();

                try {

                    JSONObject jsonObject = new JSONObject(response);
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

                        Log.i("TRACE","IMG_URL: " + dataJSON.img);

                        RecyclerView recyclerView = findViewById(R.id.recycler);
                        AdapterJSON jsonAdapter = new AdapterJSON(MainActivity.this, data);
                        recyclerView.setAdapter(jsonAdapter);
                        runLayoutAnimation(recyclerView);
                    }

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
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);

    }

    class AdapterJSON extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final Context context;
        private final LayoutInflater inflater;
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

            if (Build.VERSION.SDK_INT >= 21) {
                ViewCompat.setTransitionName(myHolder.imageView, current.img_id);
            }

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

                    if (Build.VERSION.SDK_INT >= 21) {
                        Intent i = new Intent(view.getContext(), OpenImage.class)
                                .putExtra("STRING_URL",current.img)
                                .putExtra("TRANSITION_NAME", ViewCompat.getTransitionName(myHolder.imageView));

                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)view.getContext(),myHolder.imageView,ViewCompat.getTransitionName(myHolder.imageView));
                        startActivity(i, optionsCompat.toBundle());
                    } else {
                        Intent i = new Intent(view.getContext(), OpenImage.class)
                                .putExtra("STRING_URL",current.img);
                        startActivity(i);
                    }
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

            final ImageView imageView;
            final TextView textId;
            final CardView cardView;
            final ImageButton infoButton;
            final ImageButton shareButton;

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

        if (id == R.id.action_date) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,dateSetListener,year,month,day);
            datePickerDialog.show();
        }

        if (id == R.id.action_rover_info) {
            Intent i = new Intent(MainActivity.this,AboutRoverActivity.class);
            i.putExtra("ROVER",rover);
            i.putExtra("MAX_SOL",max_sol);
            i.putExtra("STATUS",status);
            i.putExtra("TOTAL_PHOTOS",total_photos);
            i.putExtra("LANDING_DATE",landing_date);
            i.putExtra("LAUNCH_DATE",launch_date);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}

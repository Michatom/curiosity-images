package com.pawples.curiosityimages;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;

import java.util.Calendar;

public class RoverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rover);
        CardView cardCuriosity = findViewById(R.id.card);
        CardView cardOpportunity = findViewById(R.id.card2);
        TextView textView = findViewById(R.id.roverText);
        ImageView imgCuriosity = findViewById(R.id.squareImg);
        ImageView imgOpportunity = findViewById(R.id.squareImg2);

        Glide.with(this)
                .load(R.drawable.curiosity_rover)
                .transition(GenericTransitionOptions.with(R.anim.img_rover))
                .into(imgCuriosity);
        Glide.with(this)
                .load(R.drawable.opportunity_rover)
                .transition(GenericTransitionOptions.with(R.anim.img_rover))
                .into(imgOpportunity);

        cardCuriosity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent curiosity = new Intent(RoverActivity.this,LaunchActivity.class);
                startActivity(curiosity);
            }
        });

        cardOpportunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent opportunity = new Intent(RoverActivity.this,LaunchActivity2.class);
                startActivity(opportunity);
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(RoverActivity.this).create();
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
        });
    }
    @Override
    public void onBackPressed(){
        finishAffinity();
        super.onBackPressed();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rover_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_about_app) {
            AlertDialog dialog = new AlertDialog.Builder(RoverActivity.this).create();
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
}

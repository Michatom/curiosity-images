package com.pawples.curiosityimages;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.esafirm.rxdownloader.RxDownloader;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import rx.Subscriber;
import uk.co.senab.photoview.PhotoViewAttacher;

public class OpenImage extends AppCompatActivity {

    PhotoViewAttacher attacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);
        supportPostponeEnterTransition();

        FloatingActionButton floatingActionButton = findViewById(R.id.fabDownload);

        final String urlString;
        final String transition;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                urlString = null;
                transition = null;
            } else {
                urlString = extras.getString("STRING_URL");
                transition = extras.getString("TRANSITION_NAME");
            }
        } else {
            urlString = (String) savedInstanceState.getSerializable("STRING_URL");
            transition = (String) savedInstanceState.getSerializable("TRANSITION_NAME");
        }

        final ImageView img = findViewById(R.id.openImageView);

        img.setTransitionName(transition);

        Glide.with(this)
                .asBitmap()
                .load(urlString)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        supportStartPostponedEnterTransition();
                        final RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
                        Palette.from(resource).maximumColorCount(16).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch muted = palette.getMutedSwatch();
                                if (muted != null) {
                                    relativeLayout.setBackgroundColor(muted.getRgb());
                                }
                            }
                        });
                        attacher = new PhotoViewAttacher(img);
                        return false;
                    }
                })
                .into(img);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(OpenImage.this)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                RxDownloader.getInstance(OpenImage.this)
                                        .download(urlString,transition + ".JPG", "image/jpg")
                                        .subscribe(new Subscriber<String>() {
                                            @Override
                                            public void onCompleted() {

                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Toast.makeText(OpenImage.this,"Error: " + e, Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onNext(String s) {

                                            }
                                        });
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Toast.makeText(OpenImage.this,"Can't download the image.",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        })
                        .check();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                attacher.cleanup();
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed(){
        supportFinishAfterTransition();
        attacher.cleanup();
    }
}

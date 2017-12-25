package com.pawples.curiosityimages;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import uk.co.senab.photoview.PhotoViewAttacher;

public class OpenImage extends AppCompatActivity {

    PhotoViewAttacher attacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_image);
        supportPostponeEnterTransition();

        String urlString;
        String transition;
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

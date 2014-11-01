package com.madisp.trails;

import android.app.Application;

import com.madisp.trails.recordings.ThumbDownloader;
import com.squareup.picasso.Picasso;

public class App extends Application {
    public static Picasso picasso;

    @Override
    public void onCreate() {
        super.onCreate();
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.indicatorsEnabled(BuildConfig.DEBUG);
        builder.downloader(new ThumbDownloader());
        picasso = builder.build();
    }
}

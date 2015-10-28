package com.zagros.quiver.sample;

import android.app.Application;

import com.zagros.quiver.sample.comms.flickr.FlickrManager;

public class QuiverApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlickrManager.instance().init(getApplicationContext());
    }
}

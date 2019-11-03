package com.intuisoft.moderncalc.omdbapi;

import android.app.Application;
import android.content.Context;

public class Omdb extends Application {

    static Context context;
    public static String BASE_URI = "http://www.omdbapi.com/";
    public static String IMAGES_BASE_URI = "http://img.omdbapi.com/";
    public static String API_KEY = "56fcff9c";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }


    public static Context getContext() {
        return context;
    }
}

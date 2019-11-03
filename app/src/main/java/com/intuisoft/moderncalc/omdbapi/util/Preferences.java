package com.intuisoft.moderncalc.omdbapi.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.intuisoft.moderncalc.omdbapi.Omdb;
import com.intuisoft.moderncalc.omdbapi.R;

public class Preferences {

    public static SharedPreferences get() {
        return PreferenceManager.getDefaultSharedPreferences(Omdb.getContext());
    }

    public static SharedPreferences.Editor edit() {
        return get().edit();
    }

    public static boolean getBool(int id, boolean def) {
        return get().getBoolean(Omdb.getContext().getString(id), def);
    }

    public static boolean hasMoviesFilter() {
        return getBool(R.string.pref_movie_filter, false);
    }

    public static boolean hasTvSeriesFilter() {
        return getBool(R.string.pref_tv_series_filter, false);
    }

    public static void setMovieFilterEnabled(boolean enabled) {
        edit().putBoolean(Omdb.getContext().getString(R.string.pref_movie_filter), enabled).commit();
    }

    public static void setTvSeriesFilterEnabled(boolean enabled) {
        edit().putBoolean(Omdb.getContext().getString(R.string.pref_tv_series_filter), enabled).commit();
    }
}


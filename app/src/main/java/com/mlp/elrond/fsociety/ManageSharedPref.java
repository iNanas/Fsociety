package com.mlp.elrond.fsociety;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManageSharedPref {
    private static final String SHOW_FILTER = "show_filter";
    private static final String SH_PREF = "my_pref";
    private static final String SHOWS_JSON = "shows_json";

    public static String getShowFilter(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(SHOW_FILTER, null);
    }
    public static void setShowFilter(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(SHOW_FILTER, query)
                .apply();
    }

    public void storeTvShows(Context context, List tv_shows) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(SH_PREF,Context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonTvShows = gson.toJson(tv_shows);
        editor.putString(SHOWS_JSON, jsonTvShows);
        editor.commit();
    }
    public ArrayList loadTvShows(Context context) {
        SharedPreferences settings;
        List TvShows = new ArrayList();
        settings = context.getSharedPreferences(SH_PREF,Context.MODE_PRIVATE);
        if (settings.contains(SHOWS_JSON)) {
            String jsonTvShows = settings.getString(SHOWS_JSON, null);
            Gson gson = new Gson();
            TvShows[] showsItems = gson.fromJson(jsonTvShows,TvShows[].class);
            TvShows = Arrays.asList(showsItems);
            TvShows = new ArrayList(TvShows);
        } else{
            return (ArrayList) TvShows;
        }
        return (ArrayList) TvShows;
    }
    public void addTvShow(Context context, TvShows new_tvShow) {
        List TvShows = loadTvShows(context);
        if (TvShows == null)
            TvShows = new ArrayList();
        TvShows.add(new_tvShow);
        storeTvShows(context, TvShows);
    }
    public void removeTvShow(Context context, TvShows new_tvShow) {
        ArrayList<TvShows> TvShows = loadTvShows(context);
        if (TvShows != null) {
            for(int i = 0; i < TvShows.size(); i++){
                if(TvShows.get(i).getShowId().equals(new_tvShow.getShowId())){
                    TvShows.remove(TvShows.get(i));
                    storeTvShows(context, TvShows);
                    break;
                }
            }
        }
    }
}

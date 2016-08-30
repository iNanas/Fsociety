package com.mlp.elrond.fsociety;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TMDB {
    private static final String API_KEY = "1472c14c2fbac881e50dd848fd38fd56";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<TvShows> getJson(String starting_url){
        List<TvShows> showsList = new ArrayList<>();
        String url_main = starting_url;

        try {
            String url = Uri.parse(url_main).buildUpon()
                .appendQueryParameter("api_key", API_KEY).build().toString();
            String jsonString = getUrlString(url);

            JSONObject jsonMain = new JSONObject(jsonString);
            parseJson(showsList, jsonMain);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return showsList;
    }

    private void parseJson(List<TvShows> shows_list, JSONObject jsonMain) throws JSONException {
        JSONArray j_array = jsonMain.getJSONArray("results");

        for (int i = 0; i < j_array.length(); i++){
            JSONObject show_object = j_array.getJSONObject(i);
            TvShows show = new TvShows();
            show.setName(show_object.getString("original_name"));
            show.setPosterUrl(show_object.getString("poster_path"));
            show.setBackgroundUrl(show_object.getString("backdrop_path"));
            show.setOverview(show_object.getString("overview"));
            show.setRatings(show_object.getString("vote_average"));
            show.setFAD(show_object.getString("first_air_date"));
            show.setShowId(show_object.getString("id"));

            shows_list.add(show);
        }

    }

    public String parseYTid(String show_id){
        String YT_id = "";
        try {
            String url = Uri.parse("http://api.themoviedb.org/3/tv/" + show_id + "/videos?")
                    .buildUpon()
                    .appendQueryParameter("api_key", API_KEY).build().toString();
            String jsonIdString = getUrlString(url);

            JSONObject jsonTrailer = new JSONObject(jsonIdString);
            JSONArray j_array = jsonTrailer.getJSONArray("results");
            JSONObject id_object = j_array.getJSONObject(0);
            YT_id = id_object.getString("key");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return YT_id;
    }

}

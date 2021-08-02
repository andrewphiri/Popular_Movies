package com.example.popularmoviesretrofit.utils;

import android.net.Uri;

import com.example.popularmoviesretrofit.database.MoviesRepository;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkUtils {
    //this requires an API_KEY from TMDB
    private static String TAG = NetworkUtils.class.getSimpleName();
    public static String BASE_URL = "http://api.themoviedb.org/3/";
    public static String API_KEY = "provide own key";
    private static String API_PARAM = "api_key";
    private static String   LANGUAGE = "en-US";
    private static String LANG_PARAM = "language";
    private static String ID_PARAM = "movie_id";


    public static String IMAGE_URL ="http://image.tmdb.org/t/p/";
    public static String SIZE = "w500";

    public static String TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";
    public static final String VND_YOUTUBE = "vnd.youtube";


    /**
     * Build image URl
     */
    public static URL imageUrl(String imageUrl){
        Uri builtImageUrl = Uri.parse(IMAGE_URL).buildUpon()
                .appendEncodedPath(SIZE)
                .appendEncodedPath(imageUrl)
                .build();

        URL url = null;

        try {
            url = new URL(builtImageUrl.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

}

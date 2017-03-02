package com.example.joanabeleza.popularmovies.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    final static String MOVIE_DB_BASE_URL =
            "http://api.themoviedb.org/3/movie/";

    final static String MOVIE_IMAGE_BASE_URL =
            "http://image.tmdb.org/t/p/";

    final static String IMAGE_SIZE = "w185";

    final static String API_QUERY = "api_key";

    final static String API_KEY = "";

    public static URL buildUrl(String movieSearchQuery) {
        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL + movieSearchQuery).buildUpon()
                .appendQueryParameter(API_QUERY, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static Uri buildImageUri(String imageUrl) {
        Uri builtUri = Uri.parse(MOVIE_IMAGE_BASE_URL + IMAGE_SIZE + imageUrl).buildUpon()
                .build();

        return builtUri;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String[] getSimpleMoviesInfoFromJson(String moviesJsonStr)
            throws JSONException {

        final String MOVIE_ID = "id";

        final String MOVIE_IMAGE_PATH = "poster_path";

        final String MOVIE_TITLE = "original_title";

        final String MOVIE_PLOT_SYNOPSIS = "overview";

        final String MOVIE_USER_RATING = "vote_average";

        final String MOVIE_RELEASE_DATE = "release_date";

        final String RESULTS = "results";

        final String ERROR_CODE = "status_code";

        final String ERROR_MESSAGE = "status_message";

        String[] parsedMovieData;

        JSONObject movieDBJson = new JSONObject(moviesJsonStr);

        /* Check if HTTP request returned an error in JSON */
        if (movieDBJson.has(ERROR_CODE)) {
            String[] error = new String[2];
            String errorMessage = movieDBJson.getString(ERROR_MESSAGE);

            error[0] = "error";
            error[1] = errorMessage;
            Log.d("ERROR", errorMessage);

            return error;
        }

        JSONArray movieArray = movieDBJson.getJSONArray(RESULTS);

        parsedMovieData = new String[movieArray.length()];

        for (int i = 0; i < movieArray.length(); i++) {
            int id;
            String title;
            String imageUrl;
            String overview;
            Double vote_average;
            String release_date;

            /* Get the JSON object representing the movie */
            JSONObject movieObject = movieArray.getJSONObject(i);

            id = movieObject.getInt(MOVIE_ID);
            title = movieObject.getString(MOVIE_TITLE);
            imageUrl = movieObject.getString(MOVIE_IMAGE_PATH);
            overview = movieObject.getString(MOVIE_PLOT_SYNOPSIS);
            vote_average = movieObject.getDouble(MOVIE_USER_RATING);
            release_date = movieObject.getString(MOVIE_RELEASE_DATE);

            parsedMovieData[i] = id + "__" + title + "__" + imageUrl + "__" + overview + "__" + vote_average + "__" + release_date;
        }

        return parsedMovieData;
    }

}

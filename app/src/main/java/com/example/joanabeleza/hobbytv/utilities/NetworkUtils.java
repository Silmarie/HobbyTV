package com.example.joanabeleza.hobbytv.utilities;

import android.net.Uri;
import android.support.annotation.StringDef;
import android.text.TextUtils;
import android.util.Log;

import com.example.joanabeleza.hobbytv.Models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import static java.util.Arrays.asList;

public class NetworkUtils {

    public static final String MOVIE = "Movie";
    public static final String TV_SHOW = "Tv Show";

    // Declare the @ StringDef for these constants:
    @StringDef({MOVIE, TV_SHOW})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MediaType {}


    private final static String MOVIE_DB_BASE_URL =
            "https://api.themoviedb.org/3/movie/";

    private final static String MOVIE_DB_GENRES_BASE_URL =
            "https://api.themoviedb.org/3/genre/movie/list";

    private final static String TV_SHOW_DB_BASE_URL =
            "https://api.themoviedb.org/3/tv/";

    private final static String IMAGE_BASE_URL =
            "https://image.tmdb.org/t/p/";

    public final static String IMAGE_SIZE_W185 = "w185";
    public final static String IMAGE_SIZE_W500 = "w500";

    private final static String API_QUERY = "api_key";

    private final static String API_KEY = "";

    public static URL buildUrl(String type, String searchQuery) {
        Uri builtUri = Uri.parse((type.equals(MOVIE) ? MOVIE_DB_BASE_URL : TV_SHOW_DB_BASE_URL) + searchQuery).buildUpon()
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

    public static Uri buildImageUri(String imageUrl, String imageSize) {

        return Uri.parse(IMAGE_BASE_URL + imageSize + imageUrl).buildUpon()
                .build();
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

    public static String getMovieDetails(String movieDetailsJsonStr) throws JSONException {
        final String MOVIE_RUNTIME = "runtime";

        final String MOVIE_GENRES = "genres";

        final String MOVIE_BACKDROP_PATH = "backdrop_path";

        final String ERROR_CODE = "status_code";

        final String ERROR_MESSAGE = "status_message";

        JSONObject movieDBJson = new JSONObject(movieDetailsJsonStr);

        /* Check if HTTP request returned an error in JSON */
        if (movieDBJson.has(ERROR_CODE)) {
            String[] error = new String[2];
            String errorMessage = movieDBJson.getString(ERROR_MESSAGE);

            error[0] = "error";
            error[1] = errorMessage;
            Log.d("ERROR", errorMessage);

            return Arrays.toString(error);
        }

        // TODO include link to "homepage" to be activated from movie poster
        JSONArray genresArray = movieDBJson.getJSONArray(MOVIE_GENRES);

        String[] genres = new String[genresArray.length()];
        for (int i = 0; i < genresArray.length(); i++) {
                try {
                    JSONObject genre = genresArray.getJSONObject(i);
                    genres[i] = genre.getString("name");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

        String backdrop_path = movieDBJson.getString(MOVIE_BACKDROP_PATH);

        return movieDBJson.getString(MOVIE_RUNTIME) + "__" + TextUtils.join(", ", genres) + (!backdrop_path.isEmpty() ? "__" + backdrop_path : "");
    }

    public static ArrayList getMovieTrailers(String movieTrailersJsonStr) throws JSONException {
        final String VIDEO_TYPE = "type";

        final String VIDEO_LINK = "key";

        final String RESULTS = "results";

        final String ERROR_CODE = "status_code";

        final String ERROR_MESSAGE = "status_message";

        JSONObject movieDBJson = new JSONObject(movieTrailersJsonStr);

        ArrayList parsedMovieData;

        /* Check if HTTP request returned an error in JSON */
        if (movieDBJson.has(ERROR_CODE)) {
            String[] error = new String[2];
            String errorMessage = movieDBJson.getString(ERROR_MESSAGE);

            error[0] = "error";
            error[1] = errorMessage;
            Log.d("ERROR", errorMessage);

            return new ArrayList<>(asList(error));
        }

        JSONArray movieArray = movieDBJson.getJSONArray(RESULTS);

        parsedMovieData = new ArrayList();

        for (int i = 0, j = 0; i < movieArray.length(); i++, j++) {
            String type;
            String key;

            /* Get the JSON object representing the movie */
            JSONObject movieObject = movieArray.getJSONObject(i);

            type = movieObject.getString(VIDEO_TYPE);

            if (type.equals("Trailer")) {
                key = movieObject.getString(VIDEO_LINK);
                parsedMovieData.add(key);
            }
        }

        return parsedMovieData;
    }

    public static ArrayList<Review> getMovieReviews(String movieReviewsJsonStr) throws JSONException {
        final String REVIEW_AUTHOR = "author";

        final String REVIEW_CONTENT = "content";

        final String RESULTS = "results";

        final String ERROR_CODE = "status_code";

        final String ERROR_MESSAGE = "status_message";

        JSONObject movieDBJson = new JSONObject(movieReviewsJsonStr);

        ArrayList<Review> parsedMovieData;

        /* Check if HTTP request returned an error in JSON */
        if (movieDBJson.has(ERROR_CODE)) {
            String[] error = new String[2];
            String errorMessage = movieDBJson.getString(ERROR_MESSAGE);

            error[0] = "error";
            error[1] = errorMessage;
            Log.d("ERROR", errorMessage);

            return new ArrayList<>(Collections.singletonList(new Review(error[0], error[1])));
        }

        JSONArray movieArray = movieDBJson.getJSONArray(RESULTS);

        parsedMovieData = new ArrayList<>();

        for (int i = 0, j = 0; i < movieArray.length(); i++, j++) {

            /* Get the JSON object representing the movie */
            JSONObject movieObject = movieArray.getJSONObject(i);

            Review r = new Review(movieObject.getString(REVIEW_AUTHOR), movieObject.getString(REVIEW_CONTENT));
            parsedMovieData.add(r);
        }

        return parsedMovieData;
    }


    /* TV SHOWS */
    public static String[] getSimpleTvShowsInfoFromJson(String jsonTvShowsResponse)
            throws JSONException {

        final String TV_SHOW_ID = "id";

        final String TV_SHOW_IMAGE_PATH = "poster_path";

        final String TV_SHOW_NAME = "original_name";

        final String TV_SHOW_PLOT_SYNOPSIS = "overview";

        final String TV_SHOW_USER_RATING = "vote_average";

        final String TV_SHOW_RELEASE_DATE = "first_air_date";

        final String RESULTS = "results";

        final String ERROR_CODE = "status_code";

        final String ERROR_MESSAGE = "status_message";

        String[] parsedTvShowData;

        JSONObject tvShowDBJson = new JSONObject(jsonTvShowsResponse);

        /* Check if HTTP request returned an error in JSON */
        if (tvShowDBJson.has(ERROR_CODE)) {
            String[] error = new String[2];
            String errorMessage = tvShowDBJson.getString(ERROR_MESSAGE);

            error[0] = "error";
            error[1] = errorMessage;
            Log.d("ERROR", errorMessage);

            return error;
        }

        JSONArray tvShowArray = tvShowDBJson.getJSONArray(RESULTS);

        parsedTvShowData = new String[tvShowArray.length()];

        for (int i = 0; i < tvShowArray.length(); i++) {
            int id;
            String name;
            String release_date;
            Double vote_average;
            String overview;
            String imageUrl;

            /* Get the JSON object representing the tv show */
            JSONObject movieObject = tvShowArray.getJSONObject(i);

            id = movieObject.getInt(TV_SHOW_ID);
            name = movieObject.getString(TV_SHOW_NAME);
            release_date = movieObject.getString(TV_SHOW_RELEASE_DATE);
            vote_average = movieObject.getDouble(TV_SHOW_USER_RATING);
            overview = movieObject.getString(TV_SHOW_PLOT_SYNOPSIS);
            imageUrl = movieObject.getString(TV_SHOW_IMAGE_PATH);

            parsedTvShowData[i] = id + "__" + name + "__" + release_date + "__" + vote_average + "__" + overview + "__" + imageUrl;
        }

        return parsedTvShowData;
    }

    public static String getTvShowDetails(String tvShowDetailsJsonStr) throws JSONException {
        final String TV_SHOW_GENRES = "genres";

        final String TV_SHOW_STATUS = "status";

        final String TV_SHOW_SEASON_NUMBER = "number_of_seasons";

        final String TV_SHOW_BACKDROP_PATH = "backdrop_path";

        final String ERROR_CODE = "status_code";

        final String ERROR_MESSAGE = "status_message";

        JSONObject tvShowDBJson = new JSONObject(tvShowDetailsJsonStr);

        /* Check if HTTP request returned an error in JSON */
        if (tvShowDBJson.has(ERROR_CODE)) {
            String[] error = new String[2];
            String errorMessage = tvShowDBJson.getString(ERROR_MESSAGE);

            error[0] = "error";
            error[1] = errorMessage;
            Log.d("ERROR", errorMessage);

            return Arrays.toString(error);
        }

        // TODO include link to "homepage" to be activated from tv show poster
        JSONArray genresArray = tvShowDBJson.getJSONArray(TV_SHOW_GENRES);

        String[] genres = new String[genresArray.length()];
        for (int i = 0; i < genresArray.length(); i++) {
            try {
                JSONObject genre = genresArray.getJSONObject(i);
                genres[i] = genre.getString("name");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String status = tvShowDBJson.getString(TV_SHOW_STATUS);
        int seasonNumber = tvShowDBJson.getInt(TV_SHOW_SEASON_NUMBER);
        String backdrop_path = tvShowDBJson.getString(TV_SHOW_BACKDROP_PATH);

        return TextUtils.join(", ", genres) + "__" + status + "__" + seasonNumber + (!backdrop_path.isEmpty() ? "__" + backdrop_path : "");
    }

    public static String[] getSimilarTvShowsInfoFromJson(String jsonTvShowsResponse)
            throws JSONException {

        final String TV_SHOW_ID = "id";

        final String TV_SHOW_IMAGE_PATH = "poster_path";

        final String TV_SHOW_NAME = "original_name";

        final String TV_SHOW_PLOT_SYNOPSIS = "overview";

        final String TV_SHOW_USER_RATING = "vote_average";

        final String TV_SHOW_RELEASE_DATE = "first_air_date";

        final String RESULTS = "results";

        final String ERROR_CODE = "status_code";

        final String ERROR_MESSAGE = "status_message";

        String[] parsedTvShowData;

        JSONObject tvShowDBJson = new JSONObject(jsonTvShowsResponse);

        /* Check if HTTP request returned an error in JSON */
        if (tvShowDBJson.has(ERROR_CODE)) {
            String[] error = new String[2];
            String errorMessage = tvShowDBJson.getString(ERROR_MESSAGE);

            error[0] = "error";
            error[1] = errorMessage;
            Log.d("ERROR", errorMessage);

            return error;
        }

        JSONArray tvShowArray = tvShowDBJson.getJSONArray(RESULTS);

        parsedTvShowData = new String[tvShowArray.length()];

        for (int i = 0; i < tvShowArray.length(); i++) {
            int id;
            String name;
            String release_date;
            Double vote_average;
            String overview;
            String imageUrl;

            /* Get the JSON object representing the tv show */
            JSONObject movieObject = tvShowArray.getJSONObject(i);

            id = movieObject.getInt(TV_SHOW_ID);
            name = movieObject.getString(TV_SHOW_NAME);
            release_date = movieObject.getString(TV_SHOW_RELEASE_DATE);
            vote_average = movieObject.getDouble(TV_SHOW_USER_RATING);
            overview = movieObject.getString(TV_SHOW_PLOT_SYNOPSIS);
            imageUrl = movieObject.getString(TV_SHOW_IMAGE_PATH);

            parsedTvShowData[i] = id + "__" + name + "__" + release_date + "__" + vote_average + "__" + overview + "__" + imageUrl;
        }

        return parsedTvShowData;
    }

    public static ArrayList getTvShowVideos(String tvShowVideosJsonStr) throws JSONException {
        final String VIDEO_TYPE = "type";

        final String VIDEO_LINK = "key";

        final String RESULTS = "results";

        final String ERROR_CODE = "status_code";

        final String ERROR_MESSAGE = "status_message";

        JSONObject tvShowDBJson = new JSONObject(tvShowVideosJsonStr);

        ArrayList parsedTvShowData;

        /* Check if HTTP request returned an error in JSON */
        if (tvShowDBJson.has(ERROR_CODE)) {
            String[] error = new String[2];
            String errorMessage = tvShowDBJson.getString(ERROR_MESSAGE);

            error[0] = "error";
            error[1] = errorMessage;
            Log.d("ERROR", errorMessage);

            return new ArrayList<>(asList(error));
        }

        JSONArray movieArray = tvShowDBJson.getJSONArray(RESULTS);

        parsedTvShowData = new ArrayList();

        for (int i = 0, j = 0; i < movieArray.length(); i++, j++) {
            String type;
            String key;

            /* Get the JSON object representing the tv show video */
            JSONObject tvShowVideoObject = movieArray.getJSONObject(i);

            type = tvShowVideoObject.getString(VIDEO_TYPE);

            if (type.equals("Trailer")) {
                key = tvShowVideoObject.getString(VIDEO_LINK);
                parsedTvShowData.add("https://www.youtube.com/watch?v=" + key);
            }
        }

        return parsedTvShowData;
    }

    /*public static URL buildGenresUrl() {
        Uri builtUri = Uri.parse(MOVIE_DB_GENRES_BASE_URL).buildUpon()
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

    public static String buildStringUrl(String movieSearchQuery) {
        return MOVIE_DB_BASE_URL + movieSearchQuery + "?" + API_QUERY + "=" + API_KEY;
    }
    private static String getGenresName(JSONArray movieGenresArray, JSONArray genres) {
        String[] result = new String[genres.length()];
        for (int i = 0; i < genres.length(); i++) {
            for (int j = 0; j < movieGenresArray.length(); j++) {

                try {
                    JSONObject genreToCompareObject = movieGenresArray.getJSONObject(j);
                    int genreId = genres.optInt(i);

                    if (genreToCompareObject.getInt("id") == genreId) {
                        result[i] = genreToCompareObject.getString("name");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return TextUtils.join(", ", result);
    }*/

}

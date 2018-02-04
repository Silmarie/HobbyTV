package com.example.joanabeleza.hobbytv.Data.Movies;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Project HobbyTV refactored by joanabeleza on 20/05/2017.
 */

public class MoviesContract {

    static final String CONTENT_AUTHORITY = "com.example.joanabeleza.hobbytv.movies";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_MOVIES = "movies";

    /* Inner class that defines the table contents of the weather table */
    public static final class MoviesEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES)
                .build();

        static final String TABLE_NAME = "movies";

        /* Weather ID as returned by API, used to identify the icon to be used */
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_IMAGE_PATH = "image_path";

    }
}
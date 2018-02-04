package com.example.joanabeleza.hobbytv.Data.TvShows;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Project HobbyTV refactored by joanabeleza on 20/05/2017.
 */

public class TvShowsContract {

    static final String CONTENT_AUTHORITY = "com.example.joanabeleza.hobbytv.tvshows";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_TV_SHOWS = "tv_shows";

    /* Inner class that defines the table contents of the weather table */
    public static final class TvShowsEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the Weather table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TV_SHOWS)
                .build();

        static final String TABLE_NAME = "tv_shows";

        /* Weather ID as returned by API, used to identify the icon to be used */
        public static final String COLUMN_TV_SHOW_ID = "tv_show_id";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_GENRES = "genres";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_SEASON_NUMBER = "season_number";
        public static final String COLUMN_IMAGE_PATH = "image_path";

    }
}
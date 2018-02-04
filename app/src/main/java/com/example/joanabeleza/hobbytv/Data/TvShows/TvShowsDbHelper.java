package com.example.joanabeleza.hobbytv.Data.TvShows;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Project PopularMovies refactored by joanabeleza on 20/05/2017.
 */

public class TvShowsDbHelper extends SQLiteOpenHelper {

    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    private static final String DATABASE_NAME = "tv_shows.db";

    /*
     * If you change the database schema, you must increment the database version or the onUpgrade
     * method will not be called.
     *
     * The reason DATABASE_VERSION starts at 3 is because Sunshine has been used in conjunction
     * with the Android course for a while now. Believe it or not, older versions of Sunshine
     * still exist out in the wild. If we started this DATABASE_VERSION off at 1, upgrading older
     * versions of Sunshine could cause everything to break. Although that is certainly a rare
     * use-case, we wanted to watch out for it and warn you what could happen if you mistakenly
     * version your databases.
     */
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public TvShowsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    private void openDatabase() {
        String dbPath = mContext.getDatabasePath(DATABASE_NAME).getPath();
        if (mDatabase != null && mDatabase.isOpen()) {
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    void closeDatabase() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    public boolean isFavorite(String id) {

        openDatabase();
        String query = ("SELECT * FROM " + TvShowsContract.TvShowsEntry.TABLE_NAME + " WHERE " + TvShowsContract.TvShowsEntry.COLUMN_TV_SHOW_ID + " = ?");

        Cursor cursor = mDatabase.rawQuery(query, new String[]{id});

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            cursor.close();
            closeDatabase();
            return true;
        }

        cursor.close();
        closeDatabase();
        return false;
    }

    public int delete(String id) {
        openDatabase();

        int deletes = mDatabase.delete(TvShowsContract.TvShowsEntry.TABLE_NAME, TvShowsContract.TvShowsEntry.COLUMN_TV_SHOW_ID + "=" + id, null);

        closeDatabase();

        return deletes;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our weather data.
         */
        final String SQL_CREATE_TV_SHOWS_TABLE =

                "CREATE TABLE " + TvShowsContract.TvShowsEntry.TABLE_NAME + " (" +
                        TvShowsContract.TvShowsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        TvShowsContract.TvShowsEntry.COLUMN_TV_SHOW_ID + " TEXT NOT NULL, " +

                        TvShowsContract.TvShowsEntry.COLUMN_NAME + " TEXT NOT NULL," +

                        TvShowsContract.TvShowsEntry.COLUMN_YEAR + " TEXT NOT NULL, " +
                        TvShowsContract.TvShowsEntry.COLUMN_RATING + " REAL NOT NULL, " +
                        TvShowsContract.TvShowsEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                        TvShowsContract.TvShowsEntry.COLUMN_GENRES + " TEXT, " +
                        TvShowsContract.TvShowsEntry.COLUMN_STATUS + " TEXT, " +
                        TvShowsContract.TvShowsEntry.COLUMN_SEASON_NUMBER + " INTEGER, " +
                        TvShowsContract.TvShowsEntry.COLUMN_IMAGE_PATH + " TEXT NOT NULL, " +
                        " UNIQUE (" + TvShowsContract.TvShowsEntry.COLUMN_TV_SHOW_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_TV_SHOWS_TABLE);
    }

    /**
     * This database is only a cache for online data, so its upgrade policy is simply to discard
     * the data and call through to onCreate to recreate the table. Note that this only fires if
     * you change the version number for your database (in our case, DATABASE_VERSION). It does NOT
     * depend on the version number for your application found in your app/build.gradle file. If
     * you want to update the schema without wiping data, commenting out the current body of this
     * method should be your top priority before modifying this method.
     *
     * @param sqLiteDatabase Database that is being upgraded
     * @param oldVersion     The old database version
     * @param newVersion     The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TvShowsContract.TvShowsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
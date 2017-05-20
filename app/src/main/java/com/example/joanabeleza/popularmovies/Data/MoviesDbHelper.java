package com.example.joanabeleza.popularmovies.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by joanabeleza on 20/05/2017.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    public static final String DATABASE_NAME = "movies.db";

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
    private static final int DATABASE_VERSION = 3;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public void openDatabase() {
        String dbPath = mContext.getDatabasePath(DATABASE_NAME).getPath();
        if(mDatabase != null && mDatabase.isOpen()) {
            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void closeDatabase() {
        if(mDatabase!=null) {
            mDatabase.close();
        }
    }

    public boolean isFavorite(String id) {

        openDatabase();
        String query = ("SELECT * FROM movies WHERE movie_id = ?");

        Cursor cursor = mDatabase.rawQuery(query, new String[] {id});

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

        String query = ("SELECT * FROM movies");

        int deletes = mDatabase.delete(MoviesContract.MoviesEntry.TABLE_NAME, MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + "=" + id, null);

        closeDatabase();

        return deletes;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * This String will contain a simple SQL statement that will create a table that will
         * cache our weather data.
         */
        final String SQL_CREATE_MOVIES_TABLE =

                "CREATE TABLE " + MoviesContract.MoviesEntry.TABLE_NAME + " (" +

                /*
                 * WeatherEntry did not explicitly declare a column called "_ID". However,
                 * WeatherEntry implements the interface, "BaseColumns", which does have a field
                 * named "_ID". We use that here to designate our table's primary key.
                 */
                        MoviesContract.MoviesEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        MoviesContract.MoviesEntry.COLUMN_MOVIE_ID       + " TEXT NOT NULL, "                 +

                        MoviesContract.MoviesEntry.COLUMN_TITLE + " TEXT NOT NULL,"                  +

                        MoviesContract.MoviesEntry.COLUMN_OVERVIEW   + " TEXT NOT NULL, "                    +
                        MoviesContract.MoviesEntry.COLUMN_RATING  + " REAL NOT NULL, "                    +
                       // MoviesContract.MoviesEntry.COLUMN_RUNTIME  + " TEXT NOT NULL, "                    +
                        MoviesContract.MoviesEntry.COLUMN_YEAR  + " TEXT NOT NULL, "                    +
                        MoviesContract.MoviesEntry.COLUMN_IMAGE_PATH  + " TEXT NOT NULL, "                    +

                /*
                 * To ensure this table can only contain one weather entry per date, we declare
                 * the date column to be unique. We also specify "ON CONFLICT REPLACE". This tells
                 * SQLite that if we have a weather entry for a certain date and we attempt to
                 * insert another weather entry with that date, we replace the old weather entry.
                 */
                        " UNIQUE (" + MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

        /*
         * After we've spelled out our SQLite table creation statement above, we actually execute
         * that SQL with the execSQL method of our SQLite database object.
         */
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MoviesContract.MoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
package com.example.joanabeleza.popularmovies.Adapters;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.joanabeleza.popularmovies.Data.MoviesContract;
import com.example.joanabeleza.popularmovies.Models.Movie;
import com.example.joanabeleza.popularmovies.R;
import com.example.joanabeleza.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by joanabeleza on 24/02/2017.
 */

public class MoviesApiAdapter extends ArrayAdapter<Movie> {

    private ArrayList<Movie> moviesArray = new ArrayList<>();

    public MoviesApiAdapter(Activity context, ArrayList<Movie> movies) {
        super(context,0,movies);
        moviesArray = movies;
    }

    public int getCount() {
        return super.getCount();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(getContext());
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        Uri imageUri = NetworkUtils.buildImageUri(moviesArray.get(position).getImagePath());

        BitmapDrawable imageWithText =  writeOnDrawable(R.drawable.ic_launcher, moviesArray.get(position).getTitle());

        Picasso.with(getContext())
                .load(imageUri)
                .placeholder(R.drawable.ic_launcher)
                .error(imageWithText)
                .into(imageView);

        return imageView;
    }

    public void swapCursor(Cursor cursor) {
        moviesArray.clear();
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID);
            int imagePathIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_IMAGE_PATH);
            int movieTitleIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE);
            int movieOverviewIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_OVERVIEW);
            int movieRatingIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RATING);
            int movieYearIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_YEAR);
            int movieDurationIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RUNTIME);
            int movieGenresIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_GENRES);

            do {
                String id = cursor.getString(idIndex);
                String imagePath = cursor.getString(imagePathIndex);
                String movieTitle = cursor.getString(movieTitleIndex);
                String movieOverview = cursor.getString(movieOverviewIndex);
                Double movieRating = cursor.getDouble(movieRatingIndex);
                String movieYear = cursor.getString(movieYearIndex);
                int movieDuration = cursor.getInt(movieDurationIndex);
                String movieGenres = cursor.getString(movieGenresIndex);

                moviesArray.add(new Movie(id, movieTitle, imagePath, movieOverview, movieRating, movieYear, movieDuration, movieGenres, null, null));
            }
            while (cursor.moveToNext());
        }
        this.notifyDataSetChanged();
    }

    public BitmapDrawable writeOnDrawable(int drawableId, String text){

        Bitmap bm = BitmapFactory.decodeResource(this.getContext().getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        paint.setFakeBoldText(true);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int width = bounds.width();

        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, bm.getWidth()/2 - width/2, bm.getHeight()/5, paint);

        return new BitmapDrawable(bm);
    }
}

package com.example.joanabeleza.popularmovies.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private ArrayList<Movie> moviesList = new ArrayList<>();

    private Context mContext;

    public MoviesAdapter(Context context, ArrayList<Movie> movies) {
        mContext = context;
        moviesList = movies;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Inflate the task_layout to a view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Movie movie = moviesList.get(position);

        holder.itemView.setTag(movie.getId());

        holder.moviePosterImageView = new ImageView(mContext);
        holder.moviePosterImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Uri imageUri = NetworkUtils.buildImageUri(movie.getImagePath());

        BitmapDrawable imageWithText = writeOnDrawable(R.drawable.ic_launcher, movie.getTitle());

        Picasso.with(mContext)
                .load(imageUri)
                .placeholder(R.drawable.ic_launcher)
                .error(imageWithText)
                .into(holder.moviePosterImageView);

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void swapCursor(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {

            int idIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID);
            int imagePathIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_IMAGE_PATH);
            int movieTitleIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE);
            int movieOverviewIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_OVERVIEW);
            int movieRatingIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RATING);
            int movieYearIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_YEAR);
            //int movieRuntimeIndex = cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_RUNTIME);

            do {
                String id = cursor.getString(idIndex);
                String imagePath = cursor.getString(imagePathIndex);
                String movieTitle = cursor.getString(movieTitleIndex);
                String movieOverview = cursor.getString(movieOverviewIndex);
                Double movieRating = cursor.getDouble(movieRatingIndex);
                String movieYear = cursor.getString(movieYearIndex);
                //String movieRuntime = cursor.getString(movieYearIndex);
                moviesList.add(new Movie(id, movieTitle, imagePath, movieOverview, movieRating, movieYear /*, movieRuntime*/));
                this.notifyDataSetChanged();
            }
            while (cursor.moveToNext());
            cursor.close();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView moviePosterImageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            moviePosterImageView = (ImageView) itemView.findViewById(R.id.movie_poster_image);
        }
    }

    public BitmapDrawable writeOnDrawable(int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        paint.setFakeBoldText(true);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int width = bounds.width();

        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, bm.getWidth() / 2 - width / 2, bm.getHeight() / 5, paint);

        return new BitmapDrawable(bm);
    }
}

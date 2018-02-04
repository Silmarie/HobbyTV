package com.example.joanabeleza.hobbytv.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.joanabeleza.hobbytv.Data.Movies.MoviesContract;
import com.example.joanabeleza.hobbytv.Fragments.Main.MoviesFragment.OnListFragmentInteractionListener;
import com.example.joanabeleza.hobbytv.Models.Movie;
import com.example.joanabeleza.hobbytv.MovieDetailActivity;
import com.example.joanabeleza.hobbytv.R;
import com.example.joanabeleza.hobbytv.utilities.NetworkUtils;
import com.example.joanabeleza.hobbytv.utilities.UIUtilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.joanabeleza.hobbytv.utilities.NetworkUtils.MOVIE;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Movie} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MoviesRecyclerViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private ArrayList<Movie> moviesArray = new ArrayList<>();
    private Context mContext;

    public MoviesRecyclerViewAdapter(ArrayList<Movie> movies, OnListFragmentInteractionListener listener, Context context) {
        moviesArray = movies;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_movie_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = moviesArray.get(position);

        Uri imageUri = NetworkUtils.buildImageUri(moviesArray.get(position).getImagePath(), NetworkUtils.IMAGE_SIZE_W185);

        BitmapDrawable imageWithText = UIUtilities.writeOnDrawable(R.drawable.ic_launcher, moviesArray.get(position).getTitle(), mContext);

        int screenHeight = holder.mView.getResources().getConfiguration().screenHeightDp;
        int screenWidth = holder.mView.getResources().getConfiguration().screenWidthDp;
        int orientation = holder.mView.getResources().getConfiguration().orientation;

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int pxHeight = Math.round(screenHeight * (metrics.densityDpi / 160f));
        int pxWidth = Math.round(screenWidth * (metrics.densityDpi / 160f));

        Picasso.with(mContext)
                .load(imageUri)
                .placeholder(R.drawable.ic_launcher)
                .error(imageWithText)
                .resize(orientation == Configuration.ORIENTATION_PORTRAIT ? pxWidth/2+1 : pxWidth/3+1, orientation == Configuration.ORIENTATION_PORTRAIT ? pxHeight/2+1 : pxHeight+1)
                .centerCrop()
                .into(holder.mMoviePoster);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesArray.size();
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

            do {
                String id = cursor.getString(idIndex);
                String imagePath = cursor.getString(imagePathIndex);
                String movieTitle = cursor.getString(movieTitleIndex);
                String movieOverview = cursor.getString(movieOverviewIndex);
                Double movieRating = cursor.getDouble(movieRatingIndex);
                String movieYear = cursor.getString(movieYearIndex);

                moviesArray.add(new Movie(id, movieTitle, imagePath, movieOverview, movieRating, movieYear));
            }
            while (cursor.moveToNext());
        }
        this.notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mMoviePoster;
        Movie mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mMoviePoster = view.findViewById(R.id.movie_poster_image);

            mMoviePoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent showMovieDetailIntent = new Intent(mContext, MovieDetailActivity.class);

                    showMovieDetailIntent.putExtra(MOVIE, moviesArray.get(getAdapterPosition()));

                    mContext.startActivity(showMovieDetailIntent);

                    Toast.makeText(mContext, moviesArray.get(getAdapterPosition()).getTitle(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

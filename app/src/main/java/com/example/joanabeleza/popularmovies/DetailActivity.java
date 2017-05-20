package com.example.joanabeleza.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joanabeleza.popularmovies.Adapters.ReviewsAdapter;
import com.example.joanabeleza.popularmovies.Adapters.TrailersAdapter;
import com.example.joanabeleza.popularmovies.Data.MoviesContract;
import com.example.joanabeleza.popularmovies.Data.MoviesDbHelper;
import com.example.joanabeleza.popularmovies.Models.Movie;
import com.example.joanabeleza.popularmovies.Models.Review;
import com.example.joanabeleza.popularmovies.databinding.ActivityDetailBinding;
import com.example.joanabeleza.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding mBinding;

    private ImageButton favoriteButton;

    private RecyclerView rvTrailers;
    public static TrailersAdapter trailersAdapter;

    public static List<String> trailersList = new ArrayList<>();

    private RecyclerView rvReviews;
    public static ReviewsAdapter mReviewsAdapter;

    private ArrayList<Review> reviewsList = new ArrayList<>();

    private Movie movie;

    private MoviesDbHelper mOpenHelper;

    private boolean isFavorite;

    private TextView mMovieDurationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent intent = getIntent();

        if (intent.hasExtra("Movie")) {

            favoriteButton = (ImageButton) findViewById(R.id.favorite_button);

            mOpenHelper = new MoviesDbHelper(this);

            movie = (Movie) intent.getParcelableExtra("Movie");

            isFavorite = checkIfFavorite();

            favoriteButton.setImageResource(isFavorite ? R.drawable.star_movie_on : R.drawable.star_movie_off);

            mMovieDurationTextView = (TextView) findViewById(R.id.movie_duration);

            displayMovieInfo(movie);

            rvTrailers = (RecyclerView) findViewById(R.id.trailers_list);
            trailersAdapter = new TrailersAdapter(trailersList);
            trailersAdapter.notifyDataSetChanged();
            rvTrailers.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rvTrailers.setAdapter(trailersAdapter);

            rvReviews = (RecyclerView) findViewById(R.id.reviews_list);
            mReviewsAdapter = new ReviewsAdapter(reviewsList);
            mReviewsAdapter.notifyDataSetChanged();
            rvReviews.setLayoutManager(new LinearLayoutManager(this));
            rvReviews.setAdapter(mReviewsAdapter);

            loadMoviesDetails(movie.getId());

        }
    }

    private void loadMoviesDetails(String id) {
        new MovieReviewsTrailersQueryTask().execute(id);
    }

    private void displayMovieInfo(Movie movie) {
        mBinding.movieTitle.setText(movie.getTitle());
        mBinding.movieRating.setText(String.format(Locale.US, "%s/%d", movie.getVote_average(), 10));
        mBinding.movieReleaseDate.setText(movie.getRelease_date().substring(0, 4));
        mBinding.movieOverview.setText(movie.getOverview());
        mBinding.movieOverview.setMovementMethod(new ScrollingMovementMethod());
        Uri imageUri = NetworkUtils.buildImageUri(movie.getImagePath());
        Picasso.with(this).load(imageUri).into(mBinding.moviePoster);
    }

    public void toggleFavorite(View view) {
        favoriteButton.setImageResource(isFavorite ? R.drawable.star_movie_off : R.drawable.star_movie_on );

        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;

        if (isFavorite) {
            /*uri = MoviesContract.MoviesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(movie.getId()).build();
            getContentResolver().delete(uri, null, null);*/
            mOpenHelper.delete(movie.getId());

            isFavorite = false;
        } else {
            ContentValues contentValues = new ContentValues();
            // Put the task description and selected mPriority into the ContentValues
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie.getId());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_RATING, movie.getVote_average());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_YEAR, movie.getRelease_date());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_IMAGE_PATH, movie.getImagePath());
            // Insert the content values via a ContentResolver
            uri = getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);
            isFavorite = true;
        }
        // Display the URI that's returned with a Toast
        // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
        if (uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIfFavorite() {
        //String[] selectionArguments = {movie.getId()};
        /*Cursor cursor = mOpenHelper.getReadableDatabase().
                rawQuery("select * from movies", null);
           *//*             *//**//* Table we are going to query *//**//*
                MoviesContract.MoviesEntry.TABLE_NAME,
                null,
                MoviesContract.MoviesEntry.COLUMN_MOVIE_ID + " = ? ",
                selectionArguments,
                null,
                null,
                null);*//*

        if (cursor.getCount() <= 0) {
            return false;
        }*/

        return mOpenHelper.isFavorite(movie.getId());

        //return true;
    }

    public class MovieReviewsTrailersQueryTask extends AsyncTask<String, Void, String> {
        //final ProgressDialog progressDialog = ProgressDialog.show(getApplicationContext(), "Loading posts", "Please wait", false, false);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //mLoadingIndicator.setVisibility(View.VISIBLE);
            //mErrorMessage.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String details = params[0];
            String trailers = params[0] + "/videos";
            String reviews = params[0] + "/reviews";
            URL detailsRequestUrl = NetworkUtils.buildUrl(details);
            URL trailersRequestUrl = NetworkUtils.buildUrl(trailers);
            URL reviewsRequestUrl = NetworkUtils.buildUrl(reviews);

            try {
                String jsonDetailsResponse = NetworkUtils
                        .getResponseFromHttpUrl(detailsRequestUrl);
                String jsonTrailersResponse = NetworkUtils
                        .getResponseFromHttpUrl(trailersRequestUrl);
                String jsonReviewsResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewsRequestUrl);

//                trailersList.clear();
//                trailersList = NetworkUtils.getMovieTrailers(jsonTrailersResponse);


                return jsonDetailsResponse + "__" + jsonTrailersResponse + "__" + jsonReviewsResponse;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String moviesData) {
            try {
                String jsonResponse[] = moviesData.split("__");

                String movieDuration = NetworkUtils.getMovieDetails(jsonResponse[0]);
                mMovieDurationTextView.setText(String.format("%s %s", movieDuration, getString(R.string.minutes_label)));

                trailersList = NetworkUtils.getMovieTrailers(jsonResponse[1]);
                trailersAdapter.update(trailersList);

                reviewsList = NetworkUtils.getMovieReviews(jsonResponse[2]);
                mReviewsAdapter.update(reviewsList);
            } catch (Exception ex) {
                Log.e("EXCEPTIOOON", ex.getMessage());
            }
        }
    }
}

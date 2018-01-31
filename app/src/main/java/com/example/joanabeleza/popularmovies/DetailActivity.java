package com.example.joanabeleza.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding mBinding;

    private ImageButton favoriteButton;

    private RecyclerView rvTrailers;
    public static TrailersAdapter trailersAdapter;

    public static List<String> trailersList = new ArrayList<>();

    private RecyclerView rvReviews;
    public static ReviewsAdapter mReviewsAdapter;

    private ArrayList<Review> reviewsList = new ArrayList<>();

    public Movie movie;

    private MoviesDbHelper mOpenHelper;

    private boolean isFavorite;

    private TextView mMovieDurationTextView;
    private TextView mFavoriteMovieLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_detail);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);*/

        Intent intent = getIntent();

        if (intent.hasExtra("Movie")) {
            //setContentView(R.layout.detail_activity);

            //setViewPager();
            //mBinding = DataBindingUtil.setContentView(this, R.layout.fragment_movie_details);

            mOpenHelper = new MoviesDbHelper(this);

            movie = intent.getParcelableExtra("Movie");

            //
            // mMovieDurationTextView = (TextView) findViewById(R.id.movie_duration);

            loadMoviesDetails(movie.getId());

            //mBinding.movieTitle.setText(movie.getTitle());
            //mBinding.movieRating.setText(String.format(Locale.US, "%s/%d", movie.getVote_average(), 10));
            //mBinding.movieReleaseDate.setText(movie.getRelease_date().substring(0, 4));
            //Uri imageUri = NetworkUtils.buildImageUri(movie.getImagePath());
            //Picasso.with(this).load(imageUri).into(mBinding.moviePoster);

            /*favoriteButton = (ImageButton) findViewById(R.id.favorite_button);

            mOpenHelper = new MoviesDbHelper(this);

            movie = intent.getParcelableExtra("Movie");

            isFavorite = checkIfFavorite();

            favoriteButton.setImageResource(isFavorite ? R.drawable.star_movie_on : R.drawable.star_movie_off);

            mMovieDurationTextView = (TextView) findViewById(R.id.movie_duration);
            mFavoriteMovieLabel = (TextView) findViewById(R.id.toggle_favorite);

            mFavoriteMovieLabel.setText(isFavorite ? R.string.remove_from_favorites : R.string.mark_as_favorite);

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

            loadMoviesDetails(movie.getId());*/

        }
    }

    private void loadMoviesDetails(String id) {
        new MovieReviewsTrailersQueryTask().execute(id);
    }

    private void displayMovieInfo(Movie movie) {
        /*mBinding.movieTitle.setText(movie.getTitle());
        mBinding.movieRating.setText(String.format(Locale.US, "%s/%d", movie.getVote_average(), 10));
        mBinding.movieReleaseDate.setText(movie.getRelease_date().substring(0, 4));
        mBinding.movieOverview.setText(movie.getOverview());
        mBinding.movieOverview.setMovementMethod(new ScrollingMovementMethod());
        Uri imageUri = NetworkUtils.buildImageUri(movie.getImagePath());
        Picasso.with(this).load(imageUri).into(mBinding.moviePoster);*/
    }

    public void toggleFavorite(View view) {
        favoriteButton.setImageResource(isFavorite ? R.drawable.star_movie_off : R.drawable.star_movie_on );
        mFavoriteMovieLabel.setText(isFavorite ? R.string.mark_as_favorite : R.string.remove_from_favorites);

        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;

        if (isFavorite) {
            mOpenHelper.delete(movie.getId());

            isFavorite = false;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie.getId());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_RATING, movie.getVote_average());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_YEAR, movie.getRelease_date());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_IMAGE_PATH, movie.getImagePath());
            uri = getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);
            isFavorite = true;
        }
        if (uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIfFavorite() {

        return mOpenHelper.isFavorite(movie.getId());
    }

    public class MovieReviewsTrailersQueryTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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

                //mMovieDurationTextView.setText(String.format("%s %s", movieDuration, getString(R.string.minutes_label)));

               /* trailersList = NetworkUtils.getMovieTrailers(jsonResponse[1]);
                trailersAdapter.update(trailersList);

                reviewsList = NetworkUtils.getMovieReviews(jsonResponse[2]);
                mReviewsAdapter.update(reviewsList);*/
            } catch (Exception ex) {
                Log.e("EXCEPTION", ex.getMessage());
            }
        }
    }
}

package com.example.joanabeleza.popularmovies;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.joanabeleza.popularmovies.Adapters.TrailersAdapter;
import com.example.joanabeleza.popularmovies.Models.Movie;
import com.example.joanabeleza.popularmovies.databinding.ActivityDetailBinding;
import com.example.joanabeleza.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding mBinding;
    ArrayList<String> trailers;
    ArrayList<String> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        Intent intent = getIntent();

        if (intent.hasExtra("Movie")) {

            Movie movie = (Movie) intent.getParcelableExtra("Movie");

            displayMovieInfo(movie);

            loadMoviesDetails(movie.getId());

            RecyclerView rvTrailers = (RecyclerView) findViewById(R.id.trailers_list);

            trailers = new ArrayList<>();
            trailers.add("Trailer");
            trailers.add("Trailer");
            trailers.add("Trailer");
            trailers.add("Trailer");
            trailers.add("Trailer");

            TrailersAdapter trailersAdapter = new TrailersAdapter(this, trailers);
            rvTrailers.setAdapter(trailersAdapter);
            rvTrailers.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    private void loadMoviesDetails(int id) {
        new MovieReviewsTrailersQueryTask().execute(id + "/videos");
    }

    private void displayMovieInfo(Movie movie) {
        mBinding.movieTitle.setText(movie.getTitle());
        mBinding.movieRating.setText(String.format(Locale.US, "%s/%d", movie.getVote_average(), 10));
        mBinding.movieReleaseDate.setText(movie.getRelease_date().substring(0,4));
        mBinding.movieOverview.setText(movie.getOverview());
        Uri imageUri = NetworkUtils.buildImageUri(movie.getImagePath());
        Picasso.with(this).load(imageUri).into(mBinding.moviePoster);
    }

    public class MovieReviewsTrailersQueryTask extends AsyncTask<String, Void, String> {

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

            String preference = params[0];
            URL moviesRequestUrl = NetworkUtils.buildUrl(preference);

            try {
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                //String[] simpleJsonMovieData = NetworkUtils
                     //   .getSimpleMoviesInfoFromJson(jsonMoviesResponse);

                //return simpleJsonMovieData;
                return jsonMoviesResponse;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String moviesData) {
            //mLoadingIndicator.setVisibility(View.INVISIBLE);
           /* String[] movieInfo;
            if (moviesData != null) {
                if (Objects.equals(moviesData[0], "error")) {
                    //mErrorMessage.setText(moviesData[1]);
                    //mErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }

                for (String movieString : moviesData) {
                    movieInfo = movieString.split("__");
                    Log.v("movie string", movieString);
                    Movie movie = new Movie(Integer.parseInt(movieInfo[0]), movieInfo[1], movieInfo[2],
                            movieInfo[3], Double.parseDouble(movieInfo[4]), movieInfo[5]);
                    moviesList.add(movie);
                    moviesAdapter.notifyDataSetChanged();
                }
            } else {
                mErrorMessage.setText(R.string.network_error);
                mErrorMessage.setVisibility(View.VISIBLE);
            }*/
        }
    }
}

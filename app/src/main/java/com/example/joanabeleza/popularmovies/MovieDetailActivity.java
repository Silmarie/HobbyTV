package com.example.joanabeleza.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.joanabeleza.popularmovies.Fragments.MovieDetailsFragment;
import com.example.joanabeleza.popularmovies.Fragments.MovieReviewsFragment;
import com.example.joanabeleza.popularmovies.Fragments.MovieTrailersFragment;
import com.example.joanabeleza.popularmovies.Models.Movie;
import com.example.joanabeleza.popularmovies.utilities.NetworkUtils;

import java.net.URL;

public class MovieDetailActivity extends AppCompatActivity implements MovieDetailsFragment.OnFragmentInteractionListener, MovieReviewsFragment.OnFragmentInteractionListener, MovieTrailersFragment.OnFragmentInteractionListener {

    private TextView mTextMessage;
    public Movie movie;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_details:
                    MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.fragment_frame, movieDetailsFragment, "FragmentMovieDetails").commit();
                    return true;
                case R.id.navigation_reviews:
                    MovieReviewsFragment movieReviewsFragment = new MovieReviewsFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.fragment_frame, movieReviewsFragment, "FragmentMovieReviews").commit();
                    return true;
                case R.id.navigation_trailers:
                    MovieTrailersFragment movieTrailersFragment = new MovieTrailersFragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.fragment_frame, movieTrailersFragment, "FragmentMovieTrailers").commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Intent intent = getIntent();

        if (intent.hasExtra("Movie")) {
            movie = intent.getParcelableExtra("Movie");
            setTitle(movie.getTitle());

            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
            FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.fragment_frame, movieDetailsFragment, "FragmentName").commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

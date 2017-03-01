package com.example.joanabeleza.popularmovies;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.joanabeleza.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private TextView mMovieTitle;

    private TextView mMovieRating;

    private TextView mMovieReleaseDate;

    private TextView mMovieOverview;

    private ImageView mMoviePoster;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mMovieTitle = (TextView) findViewById(R.id.movie_title);

        mMovieRating = (TextView) findViewById(R.id.movie_rating);

        mMovieReleaseDate = (TextView) findViewById(R.id.movie_release_date);

        mMovieOverview = (TextView) findViewById(R.id.movie_overview);

        mMoviePoster = (ImageView) findViewById(R.id.movie_poster);

        Intent intent = getIntent();

        if (intent.hasExtra("Movie")) {

            Movie movie = (Movie) intent.getSerializableExtra("Movie");

            mMovieTitle.setText(movie.getTitle());

            mMovieRating.setText(movie.getVote_average().toString());

            mMovieReleaseDate.setText(movie.getRelease_date());
            
            mMovieOverview.setText(movie.getOverview());

            Uri imageUri = NetworkUtils.buildImageUri(movie.getImagePath());
            Picasso.with(this).load(imageUri).into(mMoviePoster);
        }
    }
}

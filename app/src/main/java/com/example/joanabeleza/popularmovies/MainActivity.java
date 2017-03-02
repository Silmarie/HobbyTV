package com.example.joanabeleza.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joanabeleza.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private GridView moviesGrid;

    public ArrayList<Movie> moviesList;

    private ProgressBar mLoadingIndicator;

    private TextView mErrorMessage;

    private MoviesAdapter moviesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        mErrorMessage = (TextView) findViewById(R.id.error_message);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            moviesList = new ArrayList<>();
            loadMoviesData("popular");
        }
        else {
            moviesList = savedInstanceState.getParcelableArrayList("movies");
        }

        moviesAdapter = new MoviesAdapter(this, moviesList);

        moviesGrid = (GridView) findViewById(R.id.grid_movies);
        moviesGrid.setAdapter(moviesAdapter);

        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Context context = MainActivity.this;
                Class destinationActivity = DetailActivity.class;

                Intent showMovieDetailIntent = new Intent(context, destinationActivity);

                showMovieDetailIntent.putExtra("Movie", moviesList.get(position));

                startActivity(showMovieDetailIntent);

                Toast.makeText(MainActivity.this, moviesList.get(position).getTitle(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", moviesList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItem = item.getItemId();
        if (clickedItem == R.id.popular) {
            moviesList.clear();
            loadMoviesData("popular");
            Toast.makeText(MainActivity.this, "Most Popular Movies",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        if (clickedItem == R.id.top_rated) {
            moviesList.clear();
            loadMoviesData("top_rated");
            Toast.makeText(MainActivity.this, "Top Rated Movies",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadMoviesData(String sortPreference) {
        new MovieQueryTask().execute(sortPreference);
    }

    public class MovieQueryTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
            mErrorMessage.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String preference = params[0];
            URL moviesRequestUrl = NetworkUtils.buildUrl(preference);

            try {
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                String[] simpleJsonMovieData = NetworkUtils
                        .getSimpleMoviesInfoFromJson(MainActivity.this, jsonMoviesResponse);

                return simpleJsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] moviesData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            String[] movieInfo;
            if (moviesData != null) {
                if (Objects.equals(moviesData[0], "error")) {
                    mErrorMessage.setText(moviesData[1]);
                    mErrorMessage.setVisibility(View.VISIBLE);
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
            }
        }
    }

}

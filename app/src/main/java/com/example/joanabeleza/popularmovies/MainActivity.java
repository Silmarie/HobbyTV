package com.example.joanabeleza.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joanabeleza.popularmovies.Adapters.MoviesAdapter;
import com.example.joanabeleza.popularmovies.Adapters.MoviesApiAdapter;
import com.example.joanabeleza.popularmovies.Data.MoviesContract;
import com.example.joanabeleza.popularmovies.Models.Movie;
import com.example.joanabeleza.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.grid_movies) GridView moviesGrid;

    @BindView(R.id.error_message) TextView mErrorMessage;

    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;

    public boolean isFavorite = false;

    private ArrayList<Movie> mMoviesList;

    private MoviesApiAdapter moviesApiAdapter;

    private MoviesAdapter moviesAdapter;
    RecyclerView mMoviesRecyclerView;

    private static final int MOVIES_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            mMoviesList = new ArrayList<>();
            loadMoviesData("popular");
        }
        else {
            mMoviesList = savedInstanceState.getParcelableArrayList("movies");
        }

        moviesApiAdapter = new MoviesApiAdapter(this, mMoviesList);

        moviesGrid.setAdapter(moviesApiAdapter);


        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Context context = MainActivity.this;
                Class destinationActivity = DetailActivity.class;

                Intent showMovieDetailIntent = new Intent(context, destinationActivity);

                showMovieDetailIntent.putExtra("Movie", mMoviesList.get(position));

                startActivity(showMovieDetailIntent);

                Toast.makeText(MainActivity.this, mMoviesList.get(position).getTitle(),
                        Toast.LENGTH_SHORT).show();
            }
        });

       /* mMoviesRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_movies);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mMoviesRecyclerView.setLayoutManager(layoutManager);
        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        moviesAdapter = new MoviesAdapter(getApplicationContext(), mMoviesList);
        mMoviesRecyclerView.setAdapter(moviesAdapter);*/
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", mMoviesList);
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
            isFavorite = false;
            mMoviesList.clear();
            loadMoviesData("popular");
            Toast.makeText(MainActivity.this, "Most Popular Movies",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        if (clickedItem == R.id.top_rated) {
            isFavorite = false;
            mMoviesList.clear();
            loadMoviesData("top_rated");
            Toast.makeText(MainActivity.this, "Top Rated Movies",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        if (clickedItem == R.id.favorites) {
            mMoviesList.clear();
            isFavorite = true;
            getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
            Toast.makeText(MainActivity.this, "Favorite Movies",
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
                        .getSimpleMoviesInfoFromJson(jsonMoviesResponse);

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
                    Movie movie = new Movie(movieInfo[0], movieInfo[1], movieInfo[2],
                            movieInfo[3], Double.parseDouble(movieInfo[4]), movieInfo[5]);
                    mMoviesList.add(movie);
                    moviesApiAdapter.notifyDataSetChanged();
                }
               // moviesAdapter.update(mMoviesList);
            } else {
                mErrorMessage.setText(R.string.network_error);
                mErrorMessage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isFavorite) {
            getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MoviesContract.MoviesEntry._ID);

                } catch (Exception e) {
                    Log.e("Message", "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        moviesApiAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesAdapter.swapCursor(null);
    }

}

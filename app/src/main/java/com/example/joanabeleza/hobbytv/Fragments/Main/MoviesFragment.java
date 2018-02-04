package com.example.joanabeleza.hobbytv.Fragments.Main;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joanabeleza.hobbytv.Adapters.MoviesRecyclerViewAdapter;
import com.example.joanabeleza.hobbytv.Data.Movies.MoviesContract;
import com.example.joanabeleza.hobbytv.Models.Movie;
import com.example.joanabeleza.hobbytv.R;
import com.example.joanabeleza.hobbytv.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.joanabeleza.hobbytv.utilities.NetworkUtils.MOVIE;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.movies_grid)
    RecyclerView moviesGrid;

    @BindView(R.id.error_message)
    TextView mErrorMessage;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    public boolean isFavorite = false;

    private ArrayList<Movie> mMoviesList;

    private MoviesRecyclerViewAdapter moviesRecyclerViewAdapter;

    private static final int MOVIES_LOADER_ID = 0;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MoviesFragment() {
    }

    public static MoviesFragment newInstance(int columnCount) {
        MoviesFragment fragment = new MoviesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        int orientation = getResources().getConfiguration().orientation;

        mColumnCount = orientation == Configuration.ORIENTATION_PORTRAIT ? 2 : 3;
        /*if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }*/
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);

        ButterKnife.bind(this, view);

        Context context = view.getContext();

        if (mColumnCount <= 1) {
            moviesGrid.setLayoutManager(new LinearLayoutManager(context));
        } else {
            moviesGrid.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            mMoviesList = new ArrayList<>();
            loadMoviesData("popular");
        } else {
            mMoviesList = savedInstanceState.getParcelableArrayList("movies");
        }

        moviesRecyclerViewAdapter = new MoviesRecyclerViewAdapter(mMoviesList, mListener, getContext());
        moviesGrid.setAdapter(moviesRecyclerViewAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItem = item.getItemId();

        if (clickedItem == R.id.popular) {
            isFavorite = false;
            mMoviesList.clear();
            loadMoviesData("popular");
            Toast.makeText(getContext(), "Most Popular Movies",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        if (clickedItem == R.id.top_rated) {
            isFavorite = false;
            mMoviesList.clear();
            loadMoviesData("top_rated");
            Toast.makeText(getContext(), "Top Rated Movies",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        if (clickedItem == R.id.favorites) {
            mMoviesList.clear();
            isFavorite = true;
            getActivity().getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
            Toast.makeText(getContext(), "Favorite Movies",
                    Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(getContext()) {

            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    return getContext().getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
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

            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        moviesRecyclerViewAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesRecyclerViewAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isFavorite) {
            getActivity().getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("movies", mMoviesList);
        super.onSaveInstanceState(outState);
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
            URL moviesRequestUrl = NetworkUtils.buildUrl(MOVIE,preference);

            try {
                String jsonMoviesResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                return NetworkUtils
                        .getSimpleMoviesInfoFromJson(jsonMoviesResponse);

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
                    moviesRecyclerViewAdapter.notifyDataSetChanged();
                }
            } else {
                mErrorMessage.setText(R.string.network_error);
                mErrorMessage.setVisibility(View.VISIBLE);
            }
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Movie item);
    }

}

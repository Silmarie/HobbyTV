package com.example.joanabeleza.hobbytv.Fragments.Main;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.joanabeleza.hobbytv.Adapters.MoviesRecyclerViewAdapter;
import com.example.joanabeleza.hobbytv.Adapters.SpinnerGenresAdapter;
import com.example.joanabeleza.hobbytv.Data.Movies.MoviesContract;
import com.example.joanabeleza.hobbytv.Models.Movie;
import com.example.joanabeleza.hobbytv.Models.SpinnerGenreItem;
import com.example.joanabeleza.hobbytv.R;
import com.example.joanabeleza.hobbytv.utilities.NetworkUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shawnlin.numberpicker.NumberPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
@SuppressWarnings("unchecked")
public class MoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {

    @BindView(R.id.movies_grid)
    RecyclerView moviesGrid;

    @BindView(R.id.error_message)
    TextView mErrorMessage;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.search_movies_fab)
    FloatingActionButton mSearchMoviesFab;

    NumberPicker yearPicker;

    public boolean isFavorite = false;
    public boolean isSearching = false;

    private ArrayList<Movie> mMoviesList;

    private MoviesRecyclerViewAdapter moviesRecyclerViewAdapter;

    private static final int MOVIES_LOADER_ID = 0;
    ArrayList<SpinnerGenreItem> genresSpinnerItemsList;
    SpinnerGenresAdapter genresAdapter;

    private static final String ARG_COLUMN_COUNT = "column-count";
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

        yearPicker = view.getRootView().findViewById(R.id.year_picker);

        Context context = view.getContext();

        if (mColumnCount <= 1) {
            moviesGrid.setLayoutManager(new LinearLayoutManager(context));
        } else {
            moviesGrid.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            mMoviesList = new ArrayList<>();
            HashMap<String, String> moviesDiscoverQuery = new HashMap<>();
            moviesDiscoverQuery.put(getString(R.string.filter_sort_by), getString(R.string.filter_sort_default));
            new MovieDiscoverTask(this).execute(moviesDiscoverQuery);
        } else {
            mMoviesList = savedInstanceState.getParcelableArrayList("movies");
        }

        if (savedInstanceState != null && savedInstanceState.containsKey("year")) {
            yearPicker.setValue(savedInstanceState.getInt("year"));
        }

        moviesRecyclerViewAdapter = new MoviesRecyclerViewAdapter(mMoviesList, mListener, getContext());
        moviesGrid.setAdapter(moviesRecyclerViewAdapter);

        mSearchMoviesFab.setOnClickListener(this);

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

        switch (clickedItem) {
            case R.id.menu_favorites:
                mMoviesList.clear();
                isFavorite = true;
                if (getActivity() != null)
                    getActivity().getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, this);
                Toast.makeText(getContext(), "Favorite Movies",
                        Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_settings:
                Toast.makeText(getContext(), "Not implemented yet",
                        Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (getContext() != null)
            return new GetCursorData(getContext());
        return null;
    }

    private static class GetCursorData extends AsyncTaskLoader<Cursor> {

        Cursor mTaskData = null;

        GetCursorData(@NonNull Context context) {
            super(context);
        }

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
            if (getActivity() != null)
                getActivity().getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("movies", mMoviesList);
        outState.putInt("year", yearPicker.getValue());
        super.onSaveInstanceState(outState);
    }

    private void loadGenresData() {

        final ObjectMapper mapper = new ObjectMapper();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL movieGenresRequestUrl = NetworkUtils.buildGenresUrl(MOVIE);

                        String jsonMovieGenresResponse = NetworkUtils
                                .getResponseFromHttpUrl(movieGenresRequestUrl);

                        JSONObject genresDBJson = new JSONObject(jsonMovieGenresResponse);

                        JSONArray genresArray = genresDBJson.getJSONArray("genres");

                    genresSpinnerItemsList = mapper.readValue(genresArray.toString(), new TypeReference<ArrayList<SpinnerGenreItem>>(){});
                    SpinnerGenreItem spinnerGenreItem = new SpinnerGenreItem();
                    spinnerGenreItem.setId(0);
                    spinnerGenreItem.setName("Select Genres");
                    spinnerGenreItem.setSelected(false);
                    genresSpinnerItemsList.add(0, spinnerGenreItem);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                Spinner spinner = getActivity().findViewById(R.id.sp_genres_select);

                                genresAdapter = new SpinnerGenresAdapter(getActivity(), 0,
                                        genresSpinnerItemsList);
                                spinner.setAdapter(genresAdapter);
                            }
                        }
                    });
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_movies_fab:
                BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from(view.getRootView().findViewById(R.id.search_bottom_sheet));
                mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                            isSearching = false;
                        } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                            isSearching = true;
                        }
                    }

                    @Override
                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                    }
                });

                if (!isSearching) {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    loadGenresData();
                } else {
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    HashMap<String, String> moviesDiscoverQuery = new HashMap<>();

                    List<String> genreIds = new ArrayList<>();
                    for (SpinnerGenreItem item:
                         genresAdapter.getItems()) {
                        if (item.isSelected())
                            genreIds.add(String.valueOf(item.getId()));
                    }

                    ToggleButton toggleSortOrder = view.getRootView().findViewById(R.id.toggle_sort_order);
                    String sortOrder = toggleSortOrder.isChecked() ? getString(R.string.filter_order_by_asc) : getString(R.string.filter_order_by_desc);
                    Spinner sortBySpinner = view.getRootView().findViewById(R.id.sp_select_sort_by);
                    String selectedVal = getResources().getStringArray(R.array.sort_by_spinner_array_values)[sortBySpinner.getSelectedItemPosition()];
                    moviesDiscoverQuery.put(getString(R.string.filter_sort_by), String.format("%s.%s", selectedVal, sortOrder));

                    Switch toggleYear = view.getRootView().findViewById(R.id.toggle_filter_year);
                    if (toggleYear.isChecked()) {
                        moviesDiscoverQuery.put(getString(R.string.filter_by_year), String.valueOf(yearPicker.getValue()));
                    }

                    moviesDiscoverQuery.put(getString(R.string.filter_by_genres), TextUtils.join(",", genreIds));

                    mMoviesList.clear();
                    moviesRecyclerViewAdapter.notifyDataSetChanged();
                    new MovieDiscoverTask(this).execute(moviesDiscoverQuery);
                }
                break;
        }
    }

    public static class MovieDiscoverTask extends AsyncTask<HashMap<String,String>, Void, String[]> {

        private WeakReference<MoviesFragment> fragment;

        MovieDiscoverTask(MoviesFragment fragment) {
            this.fragment = new WeakReference<>(fragment);
        }

        @Override
        protected String[] doInBackground(HashMap... params) {
            try {
                if (params.length == 0) {
                    return null;
                }
            HashMap queryValues = params[0];
            URL discoverMovies = NetworkUtils.buildDiscoverUrl(MOVIE, queryValues);
                String jsonMoviesResponse = NetworkUtils.getResponseFromHttpUrl(discoverMovies);
                return NetworkUtils.getSimpleMoviesInfoFromJson(jsonMoviesResponse);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fragment.get().mLoadingIndicator.setVisibility(View.VISIBLE);
            fragment.get().mErrorMessage.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(String[] moviesData) {
            fragment.get().mLoadingIndicator.setVisibility(View.INVISIBLE);
            String[] movieInfo;
            if (moviesData != null) {
                if (Objects.equals(moviesData[0], "error")) {
                    fragment.get().mErrorMessage.setText(moviesData[1]);
                    fragment.get().mErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }

                for (String movieString : moviesData) {
                    movieInfo = movieString.split("__");
                    Log.v("movie string", movieString);
                    Movie movie = new Movie(movieInfo[0], movieInfo[1], movieInfo[2],
                            movieInfo[3], Double.parseDouble(movieInfo[4]), movieInfo[5]);
                    fragment.get().mMoviesList.add(movie);
                    fragment.get().moviesRecyclerViewAdapter.notifyDataSetChanged();
                }
            } else {
                fragment.get().mErrorMessage.setText(R.string.network_error);
                fragment.get().mErrorMessage.setVisibility(View.VISIBLE);
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

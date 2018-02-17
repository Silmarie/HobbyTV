package com.example.joanabeleza.hobbytv.Fragments.Main;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.joanabeleza.hobbytv.Adapters.SpinnerGenresAdapter;
import com.example.joanabeleza.hobbytv.Adapters.TvShowsRecyclerViewAdapter;
import com.example.joanabeleza.hobbytv.Data.TvShows.TvShowsContract;
import com.example.joanabeleza.hobbytv.Models.SpinnerGenreItem;
import com.example.joanabeleza.hobbytv.Models.TvShow;
import com.example.joanabeleza.hobbytv.R;
import com.example.joanabeleza.hobbytv.utilities.NetworkUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import static com.example.joanabeleza.hobbytv.utilities.NetworkUtils.MOVIE_GENRES;
import static com.example.joanabeleza.hobbytv.utilities.NetworkUtils.TV_SHOW;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
@SuppressWarnings("unchecked")
public class TvShowsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {


    @BindView(R.id.tv_shows_grid)
    RecyclerView tvShowsGrid;

    @BindView(R.id.error_message)
    TextView mErrorMessage;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.search_tv_shows_fab)
    FloatingActionButton mSearchTvShowsFab;

    public boolean isFavorite = false;
    public boolean isSearching = false;

    private ArrayList<TvShow> mTvShowsList;

    private TvShowsRecyclerViewAdapter tvShowsRecyclerViewAdapter;

    private static final int TV_SHOWS_LOADER_ID = 1;

    ArrayList<SpinnerGenreItem> genresSpinnerItemsList;
    SpinnerGenresAdapter genresAdapter;

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TvShowsFragment() {
    }

    @SuppressWarnings("unused")
    public static TvShowsFragment newInstance(int columnCount) {
        TvShowsFragment fragment = new TvShowsFragment();
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
        View view = inflater.inflate(R.layout.fragment_tvshow_list, container, false);

        ButterKnife.bind(this, view);

        Context context = view.getContext();

        if (mColumnCount <= 1) {
            tvShowsGrid.setLayoutManager(new LinearLayoutManager(context));
        } else {
            tvShowsGrid.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            mTvShowsList = new ArrayList<>();
            HashMap<String, String> tvShowsDiscoverQuery = new HashMap<>();
            tvShowsDiscoverQuery.put(getString(R.string.filter_sort_by), getString(R.string.filter_sort_default));
            new TvShowDiscoverTask(this).execute(tvShowsDiscoverQuery);
        } else {
            mTvShowsList = savedInstanceState.getParcelableArrayList("movies");
        }

        tvShowsRecyclerViewAdapter = new TvShowsRecyclerViewAdapter(mTvShowsList, mListener, getContext());
        tvShowsGrid.setAdapter(tvShowsRecyclerViewAdapter);

        mSearchTvShowsFab.setOnClickListener(this);

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
                mTvShowsList.clear();
                isFavorite = true;
                if (getActivity() != null)
                    getActivity().getSupportLoaderManager().initLoader(TV_SHOWS_LOADER_ID, null, this);
                Toast.makeText(getContext(), "Favorite Tv Shows",
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

        @Nullable
        @Override
        public Cursor loadInBackground() {
            try {
                return getContext().getContentResolver().query(TvShowsContract.TvShowsEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        TvShowsContract.TvShowsEntry._ID);

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
        tvShowsRecyclerViewAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        tvShowsRecyclerViewAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isFavorite) {
            if (getActivity() != null)
                getActivity().getSupportLoaderManager().restartLoader(TV_SHOWS_LOADER_ID, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("tv_shows", mTvShowsList);
        super.onSaveInstanceState(outState);
    }

    private void loadGenresData() {

        final ObjectMapper mapper = new ObjectMapper();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL tvShowGenresRequestUrl = NetworkUtils.buildGenresUrl(TV_SHOW);

                    String jsonTvShowGenresResponse = NetworkUtils
                            .getResponseFromHttpUrl(tvShowGenresRequestUrl);

                    JSONObject genresDBJson = new JSONObject(jsonTvShowGenresResponse);

                    JSONArray genresArray = genresDBJson.getJSONArray(MOVIE_GENRES);

                    genresSpinnerItemsList = mapper.readValue(genresArray.toString(), new TypeReference<ArrayList<SpinnerGenreItem>>(){});
                    SpinnerGenreItem spinnerGenreItem = new SpinnerGenreItem();
                    spinnerGenreItem.setId(0);
                    spinnerGenreItem.setName(getString(R.string.select_genres_option));
                    spinnerGenreItem.setSelected(false);
                    genresSpinnerItemsList.add(0, spinnerGenreItem);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                Spinner spinner = getActivity().findViewById(R.id.search_tv_shows_bottom_sheet).findViewById(R.id.sp_genres_select);

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
            case R.id.search_tv_shows_fab:
                BottomSheetBehavior mBottomSheetBehavior = BottomSheetBehavior.from(view.getRootView().findViewById(R.id.search_tv_shows_bottom_sheet));
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
                    HashMap<String, String> movieGenresQuery = new HashMap<>();

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
                    movieGenresQuery.put(getString(R.string.filter_sort_by), String.format("%s.%s", selectedVal, sortOrder));

                    movieGenresQuery.put(getString(R.string.filter_by_genres), TextUtils.join(",", genreIds));

                    mTvShowsList.clear();
                    tvShowsRecyclerViewAdapter.notifyDataSetChanged();
                    new TvShowDiscoverTask(this).execute(movieGenresQuery);
                }
                break;
        }
    }

    public static class TvShowDiscoverTask extends AsyncTask<HashMap<String,String>, Void, String[]> {

        private WeakReference<TvShowsFragment> fragment;

        TvShowDiscoverTask(TvShowsFragment fragment) {
            this.fragment = new WeakReference<>(fragment);
        }

        @Override
        protected String[] doInBackground(HashMap... params) {
            try {
                if (params.length == 0) {
                    return null;
                }
                HashMap queryValues = params[0];
                URL discoverTvShows = NetworkUtils.buildDiscoverUrl(TV_SHOW, queryValues);
                String jsonTvShowsResponse = NetworkUtils.getResponseFromHttpUrl(discoverTvShows);
                return NetworkUtils.getSimpleTvShowsInfoFromJson(jsonTvShowsResponse);
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
        protected void onPostExecute(String[] tvShowsData) {
            fragment.get().mLoadingIndicator.setVisibility(View.INVISIBLE);
            String[] tvShowInfo;
            if (tvShowsData != null) {
                if (Objects.equals(tvShowsData[0], "error")) {
                    fragment.get().mErrorMessage.setText(tvShowsData[1]);
                    fragment.get().mErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }

                for (String tvShowString : tvShowsData) {
                    tvShowInfo = tvShowString.split("__");
                    Log.v("tv show string", tvShowString);
                    TvShow tvShow = new TvShow(tvShowInfo[0], tvShowInfo[1], tvShowInfo[2], Double.parseDouble(tvShowInfo[3]), tvShowInfo[4], "N/A", "N/A", -1, tvShowInfo[5]);
                    fragment.get().mTvShowsList.add(tvShow);
                    fragment.get().tvShowsRecyclerViewAdapter.notifyDataSetChanged();
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
        void onListFragmentInteraction(TvShow item);
    }
}

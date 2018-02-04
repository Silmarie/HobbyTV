package com.example.joanabeleza.hobbytv.Fragments.TvShows;

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

import com.example.joanabeleza.hobbytv.Adapters.TvShowsRecyclerViewAdapter;
import com.example.joanabeleza.hobbytv.Data.TvShows.TvShowsContract;
import com.example.joanabeleza.hobbytv.Fragments.Main.TvShowsFragment;
import com.example.joanabeleza.hobbytv.Models.TvShow;
import com.example.joanabeleza.hobbytv.R;
import com.example.joanabeleza.hobbytv.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.joanabeleza.hobbytv.utilities.NetworkUtils.TV_SHOW;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class TvShowOtherFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.tv_shows_grid)
    RecyclerView tvShowsGrid;

    @BindView(R.id.error_message)
    TextView mErrorMessage;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;
    public boolean isFavorite = false;

    private ArrayList<TvShow> mTvShowsList;

    private TvShowsRecyclerViewAdapter tvShowsRecyclerViewAdapter;

    private static final int TV_SHOWS_LOADER_ID = 0;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TvShowOtherFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TvShowOtherFragment newInstance(int columnCount) {
        TvShowOtherFragment fragment = new TvShowOtherFragment();
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
            loadTvShowsData("popular");
        } else {
            mTvShowsList = savedInstanceState.getParcelableArrayList("movies");
        }

        tvShowsRecyclerViewAdapter = new TvShowsRecyclerViewAdapter(mTvShowsList, mListener, getContext());
        tvShowsGrid.setAdapter(tvShowsRecyclerViewAdapter);

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
            mTvShowsList.clear();
            loadTvShowsData("popular");
            Toast.makeText(getContext(), "Most Popular Tv Shows",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        if (clickedItem == R.id.top_rated) {
            isFavorite = false;
            mTvShowsList.clear();
            loadTvShowsData("top_rated");
            Toast.makeText(getContext(), "Top Rated Tv Shows",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        if (clickedItem == R.id.favorites) {
            mTvShowsList.clear();
            isFavorite = true;
            getActivity().getSupportLoaderManager().initLoader(TV_SHOWS_LOADER_ID, null, this);
            Toast.makeText(getContext(), "Favorite Tv Shows",
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
        };
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
            getActivity().getSupportLoaderManager().restartLoader(TV_SHOWS_LOADER_ID, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList("tv_shows", mTvShowsList);
        super.onSaveInstanceState(outState);
    }

    private void loadTvShowsData(String sortPreference) {
        new TvShowsQueryTask().execute(sortPreference);
    }

    public interface OnFragmentInteractionListener {
    }

    public class TvShowsQueryTask extends AsyncTask<String, Void, String[]> {

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
            URL moviesRequestUrl = NetworkUtils.buildUrl(TV_SHOW, preference);

            try {
                String jsonTvShowsResponse = NetworkUtils
                        .getResponseFromHttpUrl(moviesRequestUrl);

                return NetworkUtils
                        .getSimpleTvShowsInfoFromJson(jsonTvShowsResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] tvShowData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            String[] tvShowInfo;
            if (tvShowData != null) {
                if (Objects.equals(tvShowData[0], "error")) {
                    mErrorMessage.setText(tvShowData[1]);
                    mErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }

                for (String tvShowString : tvShowData) {
                    tvShowInfo = tvShowString.split("__");
                    Log.v("tv show string", tvShowString);
                    TvShow tvShow = new TvShow(tvShowInfo[0], tvShowInfo[1], tvShowInfo[2], Double.parseDouble(tvShowInfo[3]), tvShowInfo[4], "N/A", "N/A", -1, tvShowInfo[5]);
                    mTvShowsList.add(tvShow);
                    tvShowsRecyclerViewAdapter.notifyDataSetChanged();
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
    public interface OnListFragmentInteractionListener extends TvShowsFragment.OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(TvShow item);
    }
}

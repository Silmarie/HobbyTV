package com.example.joanabeleza.hobbytv.Fragments.TvShows;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.joanabeleza.hobbytv.Data.Movies.MoviesContract;
import com.example.joanabeleza.hobbytv.Data.TvShows.TvShowsContract;
import com.example.joanabeleza.hobbytv.Data.TvShows.TvShowsDbHelper;
import com.example.joanabeleza.hobbytv.Models.TvShow;
import com.example.joanabeleza.hobbytv.R;
import com.example.joanabeleza.hobbytv.TvShowDetailActivity;
import com.example.joanabeleza.hobbytv.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.joanabeleza.hobbytv.utilities.NetworkUtils.IMAGE_SIZE_W500;
import static com.example.joanabeleza.hobbytv.utilities.NetworkUtils.TV_SHOW;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TvShowDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TvShowDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TvShowDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.tv_show_poster) ImageView mTvShowPoster;
    @BindView(R.id.tv_show_release_date) TextView mTvShowReleaseDate;
    @BindView(R.id.tv_show_rating) TextView mTvShowRating;
    @BindView(R.id.tv_show_overview) TextView mTvShowOverview;
    @BindView(R.id.tv_show_genres) TextView mTvShowGenres;
    @BindView(R.id.tv_show_season_number) TextView mTvShowSeasonNumber;
    @BindView(R.id.fab_toggle_favorite) FloatingActionButton mToggleFavoriteButton;
    private TvShow tvShow;

    private TvShowsDbHelper mOpenHelper;

    private boolean isFavorite;

    private OnFragmentInteractionListener mListener;

    public TvShowDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TvShowDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TvShowDetailsFragment newInstance(String param1, String param2) {
        TvShowDetailsFragment fragment = new TvShowDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tv_show_details, container, false);
        ButterKnife.bind(this, view);

        tvShow = (getActivity()) != null ? ((TvShowDetailActivity) getActivity()).tvShow : null;
        if (tvShow != null) {
            mTvShowReleaseDate.setText(tvShow.getRelease_date().substring(0, 4) );
            mTvShowRating.setText(String.format(Locale.US, "%s/%d", tvShow.getVote_average(), 10));
            mTvShowOverview.setText(tvShow.getOverview());
            Uri imageUri = NetworkUtils.buildImageUri(tvShow.getImagePath(), IMAGE_SIZE_W500);
            Picasso.with(getContext()).load(imageUri).into(mTvShowPoster);

            mOpenHelper = new TvShowsDbHelper(getContext());

            isFavorite = checkIfFavorite();

            mToggleFavoriteButton.setImageResource(isFavorite ? R.drawable.ic_star_full_24dp : R.drawable.ic_star_white_24dp);

            mToggleFavoriteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    toggleFavorite();
                }
            });
        }

        getTvShowDetails();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void toggleFavorite() {
        mToggleFavoriteButton.setImageResource(isFavorite ? R.drawable.ic_star_white_24dp : R.drawable.ic_star_full_24dp );

        Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;

        if (isFavorite) {
            mOpenHelper.delete(tvShow.getId());

            isFavorite = false;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TvShowsContract.TvShowsEntry.COLUMN_TV_SHOW_ID, tvShow.getId());
            contentValues.put(TvShowsContract.TvShowsEntry.COLUMN_NAME, tvShow.getName());
            contentValues.put(TvShowsContract.TvShowsEntry.COLUMN_OVERVIEW, tvShow.getOverview());
            contentValues.put(TvShowsContract.TvShowsEntry.COLUMN_RATING, tvShow.getVote_average());
            contentValues.put(TvShowsContract.TvShowsEntry.COLUMN_YEAR, tvShow.getRelease_date());
            contentValues.put(TvShowsContract.TvShowsEntry.COLUMN_IMAGE_PATH, tvShow.getImagePath());

            if (tvShow.getSeason_number() != -1) {
                contentValues.put(TvShowsContract.TvShowsEntry.COLUMN_SEASON_NUMBER, tvShow.getSeason_number());
            }

            if (!tvShow.getStatus().equals("N/A")) {
                contentValues.put(TvShowsContract.TvShowsEntry.COLUMN_STATUS, tvShow.getStatus());
            }

            if (!tvShow.getGenres().equals("N/A")) {
                contentValues.put(TvShowsContract.TvShowsEntry.COLUMN_GENRES, tvShow.getGenres());
            }

            uri = getActivity().getContentResolver().insert(TvShowsContract.TvShowsEntry.CONTENT_URI, contentValues);
            isFavorite = true;
        }
        if (uri != null) {
            Toast.makeText(getActivity().getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIfFavorite() {

        return mOpenHelper.isFavorite(tvShow.getId());
    }

    public void getTvShowDetails() {
        new TvShowDetailsQueryTask().execute(tvShow.getId());
    }

    public class TvShowDetailsQueryTask extends AsyncTask<String, Void, String> {
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
            URL detailsRequestUrl = NetworkUtils.buildUrl(TV_SHOW, details);

            try {

                return NetworkUtils
                        .getResponseFromHttpUrl(detailsRequestUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String tvShowData) {
            try {

                String[] tvShowDetails = NetworkUtils.getTvShowDetails(tvShowData).split("__");

                tvShow.setGenres(tvShowDetails[0]);
                mTvShowGenres.setText(tvShowDetails[0]);

                tvShow.setStatus(tvShowDetails[1]);
                getActivity().setTitle(String.format("%s (%s)", tvShow.getName(), tvShow.getStatus()));

                tvShow.setSeason_number(Integer.parseInt(tvShowDetails[2]));
                mTvShowSeasonNumber.setText(String.format("%s seasons", tvShowDetails[2]));

                if (tvShowDetails.length > 3) {
                    Uri imageUri = NetworkUtils.buildImageUri(tvShowDetails[3], IMAGE_SIZE_W500);
                    Picasso.with(getContext()).load(imageUri).into(mTvShowPoster);
                }

            } catch (Exception ex) {
                Log.e("EXCEPTION", ex.getMessage());
            }
        }
    }
}

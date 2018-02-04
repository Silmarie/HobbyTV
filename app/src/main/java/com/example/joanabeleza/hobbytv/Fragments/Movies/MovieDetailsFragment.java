package com.example.joanabeleza.hobbytv.Fragments.Movies;

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
import com.example.joanabeleza.hobbytv.Data.Movies.MoviesDbHelper;
import com.example.joanabeleza.hobbytv.Models.Movie;
import com.example.joanabeleza.hobbytv.MovieDetailActivity;
import com.example.joanabeleza.hobbytv.R;
import com.example.joanabeleza.hobbytv.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.joanabeleza.hobbytv.utilities.NetworkUtils.IMAGE_SIZE_W500;
import static com.example.joanabeleza.hobbytv.utilities.NetworkUtils.MOVIE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.movie_poster) ImageView mMoviePoster;
    @BindView(R.id.movie_release_date) TextView mMovieReleaseDate;
    @BindView(R.id.movie_duration) TextView mMovieDuration;
    @BindView(R.id.movie_rating) TextView mMovieRating;
    @BindView(R.id.movie_genres) TextView mMovieGenres;
    @BindView(R.id.movie_overview) TextView mMovieOverview;
    @BindView(R.id.fab_toggle_favorite) FloatingActionButton mToggleFavoriteButton;
    private Movie movie;

    private MoviesDbHelper mOpenHelper;

    private boolean isFavorite;

    private OnFragmentInteractionListener mListener;

    public MovieDetailsFragment() {
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
    public static MovieDetailsFragment newInstance(String param1, String param2) {
        MovieDetailsFragment fragment = new MovieDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        ButterKnife.bind(this, view);

        movie = (getActivity()) != null ? ((MovieDetailActivity) getActivity()).movie : null;
        if (movie != null) {
            mMovieRating.setText(String.format(Locale.US, "%s/%d", movie.getVote_average(), 10));
            mMovieReleaseDate.setText(movie.getRelease_date().substring(0, 4) );
            mMovieOverview.setText(movie.getOverview());
            Uri imageUri = NetworkUtils.buildImageUri(movie.getImagePath(), IMAGE_SIZE_W500);
            Picasso.with(getContext()).load(imageUri).into(mMoviePoster);

            mOpenHelper = new MoviesDbHelper(getContext());

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

        getMovieDetails();
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
            mOpenHelper.delete(movie.getId());

            isFavorite = false;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIE_ID, movie.getId());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_OVERVIEW, movie.getOverview());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_RATING, movie.getVote_average());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_YEAR, movie.getRelease_date());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_IMAGE_PATH, movie.getImagePath());
            uri = getActivity().getContentResolver().insert(MoviesContract.MoviesEntry.CONTENT_URI, contentValues);
            isFavorite = true;
        }
        if (uri != null) {
            Toast.makeText(getActivity().getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkIfFavorite() {

        return mOpenHelper.isFavorite(movie.getId());
    }

    public void getMovieDetails() {
        new MovieDetailsQueryTask().execute(movie.getId());
    }

    public class MovieDetailsQueryTask extends AsyncTask<String, Void, String> {
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
            URL detailsRequestUrl = NetworkUtils.buildUrl(MOVIE, details);

            try {

                return NetworkUtils
                        .getResponseFromHttpUrl(detailsRequestUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String moviesData) {
            try {

                String[] movieDetails = NetworkUtils.getMovieDetails(moviesData).split("__");

                mMovieDuration.setText(String.format("%s %s", movieDetails[0], getString(R.string.minutes_label)));

                mMovieGenres.setText(movieDetails[1]);

                if (movieDetails.length > 2) {
                    Uri imageUri = NetworkUtils.buildImageUri(movieDetails[2], IMAGE_SIZE_W500);
                    Picasso.with(getContext()).load(imageUri).into(mMoviePoster);
                }

            } catch (Exception ex) {
                Log.e("EXCEPTION", ex.getMessage());
            }
        }
    }
}

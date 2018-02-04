package com.example.joanabeleza.hobbytv.Fragments.Movies;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.joanabeleza.hobbytv.Adapters.TrailersAdapter;
import com.example.joanabeleza.hobbytv.Models.Movie;
import com.example.joanabeleza.hobbytv.MovieDetailActivity;
import com.example.joanabeleza.hobbytv.R;
import com.example.joanabeleza.hobbytv.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MovieTrailersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MovieTrailersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MovieTrailersFragment extends Fragment {

    private RecyclerView rvTrailers;
    public TrailersAdapter trailersAdapter;

    public static List<String> trailersList = new ArrayList<>();

    private Movie movie;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MovieTrailersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TvShowVideosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MovieTrailersFragment newInstance(String param1, String param2) {
        MovieTrailersFragment fragment = new MovieTrailersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_movie_trailers, container, false);

        movie = (getActivity()) != null ? ((MovieDetailActivity) getActivity()).movie : null;

        rvTrailers = view.findViewById(R.id.trailers_list);
        trailersAdapter = new TrailersAdapter(trailersList, getContext());
        trailersAdapter.notifyDataSetChanged();

        Context context = view.getContext();

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            rvTrailers.setLayoutManager(new LinearLayoutManager(context));
        } else {
            rvTrailers.setLayoutManager(new GridLayoutManager(context, 2));
        }

        rvTrailers.setAdapter(trailersAdapter);


        getMovieTrailers();

        // Inflate the layout for this fragment
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

    public void getMovieTrailers() {
        //((MovieDetailActivity)getActivity()).loadMoviesDetails(movie.getId());
        new MovieTrailersQueryTask().execute(movie.getId());
    }

    public class MovieTrailersQueryTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }
            String trailers = params[0] + "/videos";
            URL trailersRequestUrl = NetworkUtils.buildUrl(NetworkUtils.MOVIE, trailers);

            try {
                return NetworkUtils
                        .getResponseFromHttpUrl(trailersRequestUrl);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String moviesData) {
            try {
                trailersList = NetworkUtils.getMovieTrailers(moviesData);
                trailersAdapter.update(trailersList);
            } catch (Exception ex) {
                Log.e("EXCEPTION", ex.getMessage());
            }
        }
    }
}

package com.example.joanabeleza.hobbytv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.joanabeleza.hobbytv.Fragments.TvShows.TvShowDetailsFragment;
import com.example.joanabeleza.hobbytv.Fragments.TvShows.TvShowOtherFragment;
import com.example.joanabeleza.hobbytv.Fragments.TvShows.TvShowVideosFragment;
import com.example.joanabeleza.hobbytv.Models.TvShow;

import static com.example.joanabeleza.hobbytv.utilities.NetworkUtils.TV_SHOW;

public class TvShowDetailActivity extends AppCompatActivity implements TvShowDetailsFragment.OnFragmentInteractionListener, TvShowOtherFragment.OnListFragmentInteractionListener, TvShowVideosFragment.OnFragmentInteractionListener {

    private TextView mTextMessage;
    public TvShow tvShow;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_details:
                    TvShowDetailsFragment tvShowDetailsFragment = new TvShowDetailsFragment();
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.fragment_frame, tvShowDetailsFragment, "FragmentTvShowDetails").commit();
                    return true;
                case R.id.navigation_other_tv_shows:
                    TvShowOtherFragment tvShowOtherFragment = new TvShowOtherFragment();
                    FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction2.replace(R.id.fragment_frame, tvShowOtherFragment, "FragmentTvShowOther").commit();
                    return true;
                case R.id.navigation_videos:
                    TvShowVideosFragment tvShowVideosFragment = new TvShowVideosFragment();
                    FragmentTransaction fragmentTransaction3 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction3.replace(R.id.fragment_frame, tvShowVideosFragment, "FragmentTvShowVideos").commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_show_detail);

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Intent intent = getIntent();

        if (intent.hasExtra(TV_SHOW)) {
            tvShow = intent.getParcelableExtra(TV_SHOW);
            setTitle(tvShow.getName());

            TvShowDetailsFragment tvShowDetailsFragment = new TvShowDetailsFragment();
            FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.fragment_frame, tvShowDetailsFragment, "FragmentName").commit();
        }
    }

    @Override
    public void onListFragmentInteraction(TvShow item) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

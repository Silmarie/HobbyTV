package com.example.joanabeleza.hobbytv.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.joanabeleza.hobbytv.Fragments.Main.MoviesFragment;
import com.example.joanabeleza.hobbytv.Fragments.Main.TvShowsFragment;

/**
 * Project PopularMovies refactored by joanabeleza on 01/02/2018.
 */

public class MainFragmentAdapter extends FragmentPagerAdapter {

    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new MoviesFragment();
            case 1:
                return new TvShowsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            //
            //Your tab titles
            //
            case 0:return "Movies";
            case 1:return "TV Shows";
            default:return null;
        }
    }
}

package com.example.joanabeleza.hobbytv.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.joanabeleza.hobbytv.Data.TvShows.TvShowsContract;
import com.example.joanabeleza.hobbytv.Fragments.Main.TvShowsFragment.OnListFragmentInteractionListener;
import com.example.joanabeleza.hobbytv.Models.TvShow;
import com.example.joanabeleza.hobbytv.R;
import com.example.joanabeleza.hobbytv.TvShowDetailActivity;
import com.example.joanabeleza.hobbytv.utilities.NetworkUtils;
import com.example.joanabeleza.hobbytv.utilities.UIUtilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.joanabeleza.hobbytv.utilities.NetworkUtils.TV_SHOW;

/**
 * {@link RecyclerView.Adapter} that can display a {@link TvShow} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class TvShowsRecyclerViewAdapter extends RecyclerView.Adapter<TvShowsRecyclerViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private ArrayList<TvShow> tvShowsArray = new ArrayList<>();
    private Context mContext;

    public TvShowsRecyclerViewAdapter(ArrayList<TvShow> tvShows, OnListFragmentInteractionListener listener, Context context) {
        tvShowsArray = tvShows;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tvshow_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = tvShowsArray.get(position);

        Uri imageUri = NetworkUtils.buildImageUri(holder.mItem.getImagePath(), NetworkUtils.IMAGE_SIZE_W185);

        BitmapDrawable imageWithText = UIUtilities.writeOnDrawable(R.drawable.ic_launcher, holder.mItem.getName(), mContext);

        int screenHeight = holder.mView.getResources().getConfiguration().screenHeightDp;
        int screenWidth = holder.mView.getResources().getConfiguration().screenWidthDp;
        int orientation = holder.mView.getResources().getConfiguration().orientation;

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int pxHeight = Math.round(screenHeight * (metrics.densityDpi / 160f));
        int pxWidth = Math.round(screenWidth * (metrics.densityDpi / 160f));

        Picasso.with(mContext)
                .load(imageUri)
                .placeholder(R.drawable.ic_launcher)
                .error(imageWithText)
                .resize(orientation == Configuration.ORIENTATION_PORTRAIT ? pxWidth/2+1 : pxWidth/3+1, orientation == Configuration.ORIENTATION_PORTRAIT ? pxHeight/2+1 : pxHeight+1)
                .centerCrop()
                .into(holder.mTvShowPoster);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tvShowsArray.size();
    }

    public void swapCursor(Cursor cursor) {
        tvShowsArray.clear();
        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(TvShowsContract.TvShowsEntry.COLUMN_TV_SHOW_ID);
            int tvShowNameIndex = cursor.getColumnIndex(TvShowsContract.TvShowsEntry.COLUMN_NAME);
            int tvShowYearIndex = cursor.getColumnIndex(TvShowsContract.TvShowsEntry.COLUMN_YEAR);
            int tvShowRatingIndex = cursor.getColumnIndex(TvShowsContract.TvShowsEntry.COLUMN_RATING);
            int tvShowOverviewIndex = cursor.getColumnIndex(TvShowsContract.TvShowsEntry.COLUMN_OVERVIEW);
            int imagePathIndex = cursor.getColumnIndex(TvShowsContract.TvShowsEntry.COLUMN_IMAGE_PATH);

            do {
                String id = cursor.getString(idIndex);
                String tvShowName = cursor.getString(tvShowNameIndex);
                String tvShowYear = cursor.getString(tvShowYearIndex);
                Double tvShowRating = cursor.getDouble(tvShowRatingIndex);
                String tvShowOverview = cursor.getString(tvShowOverviewIndex);
                String imagePath = cursor.getString(imagePathIndex);

                tvShowsArray.add(new TvShow(id, tvShowName, tvShowYear, tvShowRating, tvShowOverview, "N/A", "N/A", -1, imagePath));
            }
            while (cursor.moveToNext());
        }
        this.notifyDataSetChanged();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mTvShowPoster;
        TvShow mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mTvShowPoster = view.findViewById(R.id.tv_show_poster_image);

            mTvShowPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent showTvShowDetailIntent = new Intent(mContext, TvShowDetailActivity.class);

                    showTvShowDetailIntent.putExtra(TV_SHOW, tvShowsArray.get(getAdapterPosition()));

                    mContext.startActivity(showTvShowDetailIntent);

                    Toast.makeText(mContext, tvShowsArray.get(getAdapterPosition()).getName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

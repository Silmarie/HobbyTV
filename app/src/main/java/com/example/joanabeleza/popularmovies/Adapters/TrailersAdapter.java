package com.example.joanabeleza.popularmovies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.joanabeleza.popularmovies.R;

import java.util.List;

/**
 * Created by joanabeleza on 18/05/2017.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.MyViewHolder> {

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView trailerLabel;
        public ImageButton playButton;

        public MyViewHolder(View view) {
            super(view);

            trailerLabel = (TextView) view.findViewById(R.id.trailer_label);
            playButton = (ImageButton) view.findViewById(R.id.play_trailer_button);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    private List<String> mTrailersUrl;

    public TrailersAdapter(List<String> trailersUrl) {
        this.mTrailersUrl = trailersUrl;
    }

    public void update(List<String> data) {
        this.mTrailersUrl.clear();
        this.mTrailersUrl.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public TrailersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View trailerView = inflater.inflate(R.layout.item_trailer, parent, false);

        return new MyViewHolder(trailerView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final String trailer = mTrailersUrl.get(position);

        TextView trailerLabelView = holder.trailerLabel;
        trailerLabelView.setText("Trailer " + (position + 1));
        ImageButton playImageButton = holder.playButton;
        playImageButton.setColorFilter(R.color.colorPrimary);

        playImageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Context context = holder.itemView.getContext();

                Intent yt_play = new Intent(Intent.ACTION_VIEW, Uri.parse(trailer));
                Intent chooser = Intent.createChooser(yt_play , "Open With");

                if (yt_play .resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(chooser);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return mTrailersUrl.size();
    }
}

package com.example.joanabeleza.popularmovies.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.joanabeleza.popularmovies.Models.Movie;
import com.example.joanabeleza.popularmovies.R;
import com.example.joanabeleza.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by joanabeleza on 24/02/2017.
 */

public class MoviesAdapter extends ArrayAdapter<Movie> {

    private static final String LOG_TAG = MoviesAdapter.class.getSimpleName();

    private ArrayList<Movie> moviesArray = new ArrayList<>();

    public MoviesAdapter(Activity context, ArrayList<Movie> movies) {
        super(context,0,movies);
        moviesArray = movies;
    }

    public int getCount() {
        return super.getCount();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(getContext());
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        Uri imageUri = NetworkUtils.buildImageUri(moviesArray.get(position).getImagePath());

        BitmapDrawable imageWithText =  writeOnDrawable(R.drawable.launcher_icon, moviesArray.get(position).getTitle());

        Picasso.with(getContext())
                .load(imageUri)
                .placeholder(R.drawable.launcher_icon)
                .error(imageWithText)
                .into(imageView);

        return imageView;
    }

    public BitmapDrawable writeOnDrawable(int drawableId, String text){

        Bitmap bm = BitmapFactory.decodeResource(this.getContext().getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        paint.setFakeBoldText(true);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int width = bounds.width();

        Canvas canvas = new Canvas(bm);
        canvas.drawText(text, bm.getWidth()/2 - width/2, bm.getHeight()/5, paint);

        return new BitmapDrawable(bm);
    }
}

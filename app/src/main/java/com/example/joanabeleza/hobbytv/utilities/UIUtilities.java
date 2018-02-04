package com.example.joanabeleza.hobbytv.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

/**
 * Project PopularMovies refactored by joanabeleza on 03/02/2018.
 */

public class UIUtilities {

    public static BitmapDrawable writeOnDrawable(int drawableId, String text, Context context){

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), drawableId).copy(Bitmap.Config.ARGB_8888, true);

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

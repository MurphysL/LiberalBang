package com.example.lenovo.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * MyImageView
 *
 * @author: lenovo
 * @time: 2016/9/18 15:45
 */

public class MyImageView extends ImageView {

    private Bitmap image;
    public MyImageView(Context context) {
        this(context, null);
    }
    public MyImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public MyImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        image = BitmapFactory.decodeResource(getResources(), R.drawable.doge);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(image.getWidth(), image.getHeight());
    }

    public void setColor(int color) {
        setImageBitmap(createColor(color));
    }

    private Bitmap createColor(int color) {
        int heartWidth= image.getWidth();
        int heartHeight= image.getHeight();
        Bitmap newBitmap=Bitmap.createBitmap(heartWidth, heartHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(newBitmap);
        Paint paint=new Paint();
        paint.setAntiAlias(true);
        canvas.drawBitmap(image, 0, 0, paint);
        canvas.drawColor(color, PorterDuff.Mode.SRC_ATOP);
        canvas.setBitmap(null);
        return newBitmap;
    }
}

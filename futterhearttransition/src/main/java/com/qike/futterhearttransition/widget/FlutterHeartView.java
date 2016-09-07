package com.qike.futterhearttransition.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.qike.futterhearttransition.R;


/**
 * 播放飘心动画的界面
 * 作者：漆可 on 2016/9/6 09:46
 */
public class FlutterHeartView extends ImageView
{
    private Paint mPaint;
    private Canvas mCanvas = new Canvas();

    private int mHeartResId = R.drawable.heart;
    private int mHeartBorderResId = R.drawable.heart1;

    private Bitmap mHeart;
    private Bitmap mHeartBorder;

    public FlutterHeartView(Context context)
    {
        this(context, null);
    }

    public FlutterHeartView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public FlutterHeartView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init()
    {
        //边缘抗锯齿，位图过滤波处理
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    }

    public void setDrawable(int resourceId)
    {
        Bitmap heart = BitmapFactory.decodeResource(getResources(), resourceId);
        setImageDrawable(new BitmapDrawable(getResources(), heart));
    }

    public void setColor(int color)
    {
        Bitmap heart = createHeart(color);
        setImageDrawable(new BitmapDrawable(getResources(), heart));
    }

    public void setColorAndDrawable(int color, int heartResId, int heartBorderResId)
    {
        if (heartResId != mHeartResId)
        {
            mHeart = null;
            mHeartResId = heartResId;
        }

        if (heartBorderResId != mHeartBorderResId)
        {
            mHeartBorder = null;
            mHeartBorderResId = heartBorderResId;
        }

        setColor(color);
    }

    private Bitmap createHeart(int color)
    {
        if (mHeart == null)
        {
            mHeart = BitmapFactory.decodeResource(getResources(), mHeartResId);
        }

        if (mHeartBorder == null)
        {
            mHeartBorder = BitmapFactory.decodeResource(getResources(), mHeartBorderResId);
        }

        Bitmap heart = mHeart;
        Bitmap heartBorder = mHeartBorder;
        Bitmap bitmap = createBitmapSafely(heartBorder.getWidth(), heartBorder.getHeight());

        if (bitmap == null)
        {
            return null;
        }

        Canvas canvas = mCanvas;
        canvas.setBitmap(bitmap);
        Paint paint = mPaint;
        //设置滤镜：取交集
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(heartBorder, 0, 0, paint);

        //保证居中绘制heart
        float dx = (heartBorder.getWidth() - heart.getWidth()) / 2.0f;
        float dy = (heartBorder.getHeight() - heart.getHeight()) / 2.0f;
        canvas.drawBitmap(heart, dx, dy, paint);

        paint.setColorFilter(null);
        canvas.setBitmap(null);

        return bitmap;
    }

    private Bitmap createBitmapSafely(int width, int height)
    {
        try
        {
            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
}

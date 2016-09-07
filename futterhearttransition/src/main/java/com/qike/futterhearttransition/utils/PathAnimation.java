package com.qike.futterhearttransition.utils;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 动画路径控制器
 * 作者：漆可 on 2016/9/5 16:53
 */
public class PathAnimation extends AbstractPathAnimation
{

    private final AtomicInteger mCounter = new AtomicInteger(0);
    private Handler mHandler;

    public PathAnimation(Config mConfig)
    {
        super(mConfig);

        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void start(final View child, final ViewGroup parent)
    {
        mConfig.adjustInitX();

        parent.addView(child, new ViewGroup.LayoutParams(mConfig.heartWidth, mConfig.heartHeight));
        FloatAnimation animation = new FloatAnimation(createPath(mCounter, parent, 2), randomRotation(), parent, child);
        animation.setDuration(mConfig.animDuration);
        animation.setInterpolator(new LinearInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation)
            {
                mCounter.incrementAndGet();
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                mHandler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        parent.removeView(child);
                    }
                });
                mCounter.decrementAndGet();
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {

            }
        });

        child.startAnimation(animation);
    }

    static class FloatAnimation extends Animation
    {
        private PathMeasure mPathMeasure;
        private View mView;
        private float mDistance; // 路径长度
        private float mRotation;

        public FloatAnimation(Path path, float rotation, View parent, View child)
        {
            this.mPathMeasure = new PathMeasure(path, false);
            this.mRotation = rotation;
            mView = child;
            mDistance = mPathMeasure.getLength();
            mRotation = rotation;
            parent.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        @Override
        protected void applyTransformation(float factor, Transformation t)
        {
            super.applyTransformation(factor, t);

            Matrix matrix = t.getMatrix();
            mPathMeasure.getMatrix(mDistance * factor, matrix, PathMeasure.POSITION_MATRIX_FLAG);

            mView.setRotation(mRotation * factor);

            float scale = 1.0f - factor * factor*(0.05f + 0.55f * factor);
            mView.setScaleX(scale);
            mView.setScaleY(scale);

            t.setAlpha(1.0f - factor);
        }
    }

}

package com.qike.futterhearttransition.utils;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Path;
import android.view.View;
import android.view.ViewGroup;

import com.qike.futterhearttransition.R;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 动画控制器
 * 作者：漆可 on 2016/9/5 16:13
 */
public abstract class AbstractPathAnimation
{
    private final Random mRandom;
    final Config mConfig;

    AbstractPathAnimation(Config mConfig)
    {
        this.mConfig = mConfig;
        mRandom = new Random();
    }

    public abstract void start(View child, ViewGroup parent);

    /**
     * 创建随机角度
     * -14.3到14.3之间
     *
     * @return 角度
     */
    float randomRotation()
    {
        return mRandom.nextFloat() * 28.6F - 14.3F;
    }


    /**
     * 绘制一条path
     */
    Path createPath(AtomicInteger counter, View view, int factor)
    {
        Random r = mRandom;
        int x = r.nextInt(mConfig.xRand);
        int x2 = r.nextInt(mConfig.xRand);
        int y = view.getHeight() - mConfig.initY;
        int y2 = counter.intValue() * 15 + mConfig.animLength * factor + r.nextInt(mConfig.animLengthRand);
        factor = y2 / 6;
        x += mConfig.xPointFactor;
        x2 += mConfig.xPointFactor;
        int y3 = y - y2;
        y2 = y - y2 / 2;

        Path path = new Path();
        path.moveTo(mConfig.initX, y);
        path.cubicTo(mConfig.initX, y - factor, x, y2 + factor, x, y2); //绘制二阶贝塞尔曲线，实现摇摆效果
        path.moveTo(x, y2);
        path.cubicTo(x, y2 - factor, x2, y3 + factor, x2, y3);
        return path;
    }

    public static class Config
    {
        int initX; //x方向最大偏移量
        int initY; //y坐标起始位置
        int xRand; //贝塞尔控制点x方向最大基准计算值
        int animLengthRand;
        int xPointFactor; //heart当前x坐标
        int animLength;  //动画路径长度
        int heartWidth;  //heart当前x坐标宽度
        int heartHeight; //heart当前x坐标高度
        int animDuration = 3000; //动画播放时长

        public static Config fromTypeArray(TypedArray typedArray, float initX, float initY, int pointX, int heartWidth, int heartHeight)
        {
            Config config = new Config();
            Resources res = typedArray.getResources();
            config.initX = (int) initX;
            config.initY = (int) initY;
            config.xRand = (int) typedArray.getDimension(R.styleable.HeartLayout_xRand,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_bezier_x_rand));
            config.animLength = (int) typedArray.getDimension(R.styleable.HeartLayout_animLength,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_length));//动画长度
            config.animLengthRand = (int) typedArray.getDimension(R.styleable.HeartLayout_animLengthRand,
                    res.getDimensionPixelOffset(R.dimen.heart_anim_length_rand));

            config.xPointFactor = pointX;
            config.heartWidth = heartWidth;
            config.heartHeight = heartHeight;

            return config;
        }

        /**
         * 调整贝塞尔曲线控制点偏移
         */
        void adjustInitX()
        {
            if (xPointFactor <= initX && xPointFactor >= 0)
            {
                xPointFactor -= 10; //左移
            } else if (xPointFactor >= -initX && xPointFactor <= 0)
            {
                xPointFactor += 10;//右移
            } else
            {
                //超出范围，拉回初始位置
                xPointFactor = initX;
            }
        }
    }
}

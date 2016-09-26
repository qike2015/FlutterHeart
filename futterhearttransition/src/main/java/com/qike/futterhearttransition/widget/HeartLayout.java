package com.qike.futterhearttransition.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.qike.futterhearttransition.R;
import com.qike.futterhearttransition.utils.AbstractPathAnimation;
import com.qike.futterhearttransition.utils.PathAnimation;

import java.lang.ref.WeakReference;
import java.util.Random;

import static com.qike.futterhearttransition.widget.HeartLayout.Mode.SingleMode;

/**
 * 动画的播放场景
 * 作者：漆可 on 2016/9/6 10:40
 */
public class HeartLayout extends RelativeLayout
{
    private AbstractPathAnimation mAnimation;
    private AttributeSet attributeSet = null;
    private int defStyleAttr = 0;
    private HeartThread mHeartThread;
    private HeartHandler mHeartHandler;


    private int mPointX;       //随机上浮X坐标
    private int mInitX;      //x坐标最大值
    private int mTextHeight;  //文字高度
    private int mHeartHeight; //heart高度
    private int mHeartWidth;    //hear宽度

    private static int[] drawableIds = new int[]{R.drawable.heart0, R.drawable.heart1, R.drawable.heart2, R.drawable.heart3, R.drawable.heart4, R.drawable.heart5, R.drawable.heart6, R.drawable.heart7, R.drawable.heart8,};
    private Random random = new Random();

    private Mode mMode = SingleMode;
    private int mHeartCount = 50; //多个播放模式下一次播放heart的数目
    private int mTimeLength = 2000; //多个播放模式heart释放时长

    //播放模式
    public enum Mode
    {
        /**
         * 单个模式
         */
        SingleMode,

        /**
         * 多个模式
         */
        MultiMode;
    }

    public HeartLayout(Context context)
    {
        this(context, null);
    }

    public HeartLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public HeartLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        this.attributeSet = attrs;
        this.defStyleAttr = defStyleAttr;

        init();
    }

    private void init()
    {
        //加载布局文件
        View.inflate(getContext(), R.layout.ly_periscope, this);

        //点赞图标
        Bitmap mLikeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_like_normal);
        mHeartHeight = mLikeBitmap.getHeight() / 2;
        mHeartWidth = mLikeBitmap.getWidth() / 2;
        mTextHeight = sp2px(20) + mHeartHeight / 2;

        mPointX = mHeartWidth;

        mLikeBitmap.recycle();

        findViewById(R.id.img).setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                switch (mMode)
                {
                    case SingleMode://单个播放模式

                        addFavor();
                        break;

                    case MultiMode: //多个播放模式

                        addFavor(40);
                        break;
                }
            }
        });
    }

    //sp转化为px
    private int sp2px(int sp)
    {
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scaledDensity + 0.5);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //初始化上浮过程中x方向偏移的最大位置，防止heart超出播放view的边界
        int width = getMeasuredWidth();
        mInitX = (width - mHeartWidth) / 2;

        initAnimation(attributeSet, defStyleAttr);
    }

    //初始化动画
    private void initAnimation(AttributeSet attributeSet, int defStyleAttr)
    {
        TypedArray ta = getContext().obtainStyledAttributes(attributeSet, R.styleable.HeartLayout, defStyleAttr, 0);
        mAnimation = new PathAnimation(AbstractPathAnimation.Config.fromTypeArray(ta, mInitX, mTextHeight, mPointX, mHeartWidth, mHeartHeight));
        ta.recycle();
    }

    //清除动画，所有heart的动画全部清除，并移除heartView
    public void cleanAnimation()
    {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++)
        {
            getChildAt(i).clearAnimation();
        }

        removeAllViews();
    }

    /**
     * 设置播放模式
     * @param mode 播放模式，分为俩种单个和多个模式
     */
    public void setPalyerMode(Mode mode)
    {
        this.mMode = mode;
    }

    /**
     * 漂浮一个星星，星星随机颜色
     */
    public void addFavor()
    {
        FlutterHeartView heartView = new FlutterHeartView(getContext());
        heartView.setDrawable(drawableIds[random.nextInt(8)]);
//        initAnimation(attributeSet, defStyleAttr);
        mAnimation.start(heartView, this);
    }

    /**
     * 连续漂浮多个星星,星星随机颜色
     *
     * @param heartCount 显示的星星数目
     */
    public void addFavor(int heartCount)
    {
        if (heartCount == 0) return;

        int timeCell = mTimeLength / heartCount;

        //防止星星过于密集
        if (timeCell > 40)
        {
            timeCell = 40;
            heartCount = mTimeLength / timeCell;
        }

        if (mHeartThread == null)
        {
            mHeartThread = new HeartThread();
        }

        if (mHeartHandler == null)
        {
            mHeartHandler = new HeartHandler(this);
            post(mHeartThread);
        }

        mHeartThread.addTask(timeCell, heartCount);
    }

    public void addHeart(int color)
    {
        FlutterHeartView heartView = new FlutterHeartView(getContext());
        heartView.setColor(color);
        initAnimation(attributeSet, defStyleAttr);
        mAnimation.start(heartView, this);
    }

    public void addHeart(int color, int heartResId, int heartBorderResId)
    {
        FlutterHeartView heartView = new FlutterHeartView(getContext());
        heartView.setColorAndDrawable(color, heartResId, heartBorderResId);
        initAnimation(attributeSet, defStyleAttr);
        mAnimation.start(heartView, this);
    }

    /**
     * 释放资源
     */
    public void release()
    {
        if (mHeartHandler != null)
        {
            mHeartHandler.removeCallbacks(mHeartThread);
            mHeartThread = null;
            mHeartHandler = null;
        }

        if (mHeartThread != null)
        {
            mHeartThread.clean();
            mHeartThread = null;
        }

        new Thread().interrupt();
    }


    private class HeartHandler extends Handler
    {
        public final static int MSG_SHOW = 1;
        WeakReference<HeartLayout> _wHeartLayout;

        public HeartHandler(HeartLayout heartLayout)
        {
            this._wHeartLayout = new WeakReference<>(heartLayout);
        }

        @Override
        public void handleMessage(Message msg)
        {

            HeartLayout heartLayout = _wHeartLayout.get();
            if (heartLayout == null)
            {
                return;
            }

            switch (msg.what)
            {
                case MSG_SHOW:
                    addFavor();
                    break;
            }
        }
    }

    private class HeartThread implements Runnable
    {
        private long _delayedTime = 0;
        private int _heartCount = 0;

        //开启任务
        public void addTask(long time, int heartCount)
        {
            this._heartCount += heartCount;
            this._delayedTime = time;
        }

        //清除任务
        public void clean()
        {
            _heartCount = 0;
        }

        //定时更新大小
        @Override
        public void run()
        {
            if (mHeartHandler == null)
            {
                return;
            }

            if (_heartCount > 0)
            {
                mHeartHandler.sendEmptyMessage(HeartHandler.MSG_SHOW);
                _heartCount--;
            }

            postDelayed(this, _delayedTime);
        }
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        release();
    }
}

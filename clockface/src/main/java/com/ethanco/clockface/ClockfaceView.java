package com.ethanco.clockface;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.ethanco.clockface.utils.Lunar;
import com.ethanco.clockface.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * @Description 时间表盘 View
 * Created by EthanCo on 2016/10/13.
 */

public class ClockfaceView extends View {
    //默认宽度
    private static final float DEFAULT_WIDTH = 300;
    //默认高度
    private static final float DEFAULT_HEIGHT = 300;
    //间隔时长
    public static final int DELAY_MILLIS = 1000;

    //外环 渐变开始颜色
    private int mRingStartColor;
    //外环 渐变结束颜色
    private int mRingEndColor;
    //外环 宽度
    private float mRingWidth;
    //小时:分钟 渐变开始颜色
    private int mHourStartColor;
    //小时:分钟 渐变结束颜色
    private int mHourEndColor;
    //小时:分钟 字体大小
    private float mHourTextSize;
    //表盘颜色
    private int mPlateColor;
    //顶部 字体颜色
    private int mTopTextColor;
    //顶部 字体大小
    private float mTopTextSize;
    //底部 字体颜色
    private int mBottomTextColor;
    //底部 字体大小
    private float mBottomTextSize;
    //底部(次标题) 字体颜色
    private int mBottomSecondTextColor;
    //底部(次标题) 字体大小
    private float mBottomSecondTextSize;

    //View宽
    private int mWidth;
    //View高
    private int mHeight;

    //半径
    private int mRadius;
    //直径
    private int mDiameter;

    //分钟光环
    private Paint mRingPaint;
    //小时文字
    private Paint mHourPaint;
    //黑色表盘
    private Paint mPlatePaint;
    //顶部字体Paint
    private Paint mTopTextPaint;
    //底部字体Paint
    private Paint mBottomTextPaint;
    //底部(次标题) 字体Paint
    private Paint mBottomSecondTextPaint;

    ////小时分钟
    private String txtHour = "--:--";
    //时段 AM、PM
    private String txtTimeFrame = "--";
    //星期
    private String txtWeek = "--月--日 星期-/-";
    //农历
    private String txtLunarCalendar = "-/-年-/-月-/-";
    //秒
    private int second = 0;

    //外环增加 百分比
    private float ringIncreasePercent;

    //更新UI Handler
    private static class UpdateHandler extends Handler {
        protected WeakReference<ClockfaceView> clockfaceRef;

        public UpdateHandler(ClockfaceView clockfaceView) {
            this.clockfaceRef = new WeakReference(clockfaceView);
        }
    }

    private UpdateHandler mH = new UpdateHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            ClockfaceView clockfaceview = this.clockfaceRef.get();
            if (clockfaceview == null) return;

            updateDateInfo();
            startRingIncreaseAnim();
            //invalidate();

            mH.sendEmptyMessageDelayed(0, DELAY_MILLIS);
        }
    };

    public ClockfaceView(Context context) {
        this(context, null);
    }

    public ClockfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化自定义属性
        initAttrs(context, attrs, defStyleAttr);
        //初始化Paint
        initPaint(context);
        //定时间隔更新UI
        updateUi();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getViewWidth(widthMeasureSpec);
        mHeight = getViewHeight(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mDiameter = Math.min(mWidth, mHeight);
        mRadius = (int) (mDiameter / 2.0F);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.i(TAG, "onDraw second: " + second);

        RectF plateRectF = new RectF(0, 0, mDiameter, mDiameter);
        //绘制外环
        drawRing(canvas, plateRectF);
        //绘制表盘
        drawPlate(canvas);
        //绘制文字
        drawTexts(canvas, plateRectF);
    }

    //============================= Z-具体方法 ==============================/

    private void drawRing(Canvas canvas, RectF plateRectF) {
        float angle = second / 60F * 360;
        angle = angle + 6 * ringIncreasePercent;
        canvas.drawArc(plateRectF, -90, angle, true, mRingPaint);
        canvas.save();
    }

    private void drawPlate(Canvas canvas) {
        int centerX = mRadius, centerY = mRadius;
        int platemRadius = (int) (mRadius - mRingWidth);
        canvas.drawCircle(centerX, centerY, platemRadius, mPlatePaint);
    }

    private void drawTexts(Canvas canvas, RectF plateRectF) {
        //字体高度 http://sd4886656.iteye.com/blog/1200890
        //Paint.FontMetrics fm = mHourPaint.getFontMetrics();
        //float hourHalfHeight = (fm.bottom - fm.top) / 2.0F;

        //文字居中
        Paint.FontMetricsInt fontMetrics = mHourPaint.getFontMetricsInt();
        int baseline = (int) ((plateRectF.bottom + plateRectF.top - fontMetrics.bottom - fontMetrics.top) / 2F);

        canvas.drawText(txtHour, plateRectF.centerX(), baseline, mHourPaint);
        canvas.drawText(txtTimeFrame, plateRectF.centerX(), baseline / 2F, mTopTextPaint);
        canvas.drawText(txtWeek, plateRectF.centerX(), baseline + mRadius * 0.3F, mBottomTextPaint);
        Paint.FontMetrics bottomSecondFm = mHourPaint.getFontMetrics();
        canvas.drawText(txtLunarCalendar, plateRectF.centerX(), baseline + mRadius * 0.3F
                + bottomSecondFm.bottom + mRadius * 0.13F, mBottomSecondTextPaint);
    }

    private int getViewHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = View.MeasureSpec.getMode(heightMeasureSpec);
        int size = View.MeasureSpec.getSize(heightMeasureSpec);
        if (mode == View.MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = Utils.dp2px(getContext(), DEFAULT_HEIGHT);
            if (mode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClockFaceView, defStyleAttr, R.style.DefaultClockfaceStyle);

        mRingStartColor = ta.getColor(R.styleable.ClockFaceView_ringStartColor, 0);
        mRingEndColor = ta.getColor(R.styleable.ClockFaceView_ringEndColor, 0);
        mRingWidth = ta.getDimension(R.styleable.ClockFaceView_ringWidth, 0);
        mHourStartColor = ta.getColor(R.styleable.ClockFaceView_hourStartColor, 0);
        mHourEndColor = ta.getColor(R.styleable.ClockFaceView_hourEndColor, 0);
        mPlateColor = ta.getColor(R.styleable.ClockFaceView_plateColor, 0);
        mTopTextColor = ta.getColor(R.styleable.ClockFaceView_mTopTextColor, 0);
        mTopTextSize = ta.getDimension(R.styleable.ClockFaceView_mTopTextSize, 0);
        mBottomTextColor = ta.getColor(R.styleable.ClockFaceView_mBottomTextColor, 0);
        mBottomTextSize = ta.getDimension(R.styleable.ClockFaceView_mBottomTextSize, 0);
        mBottomSecondTextColor = ta.getColor(R.styleable.ClockFaceView_mBottomSecondTextColor, 0);
        mBottomSecondTextSize = ta.getDimension(R.styleable.ClockFaceView_mBottomSecondTextSize, 0);
        mHourTextSize = ta.getDimension(R.styleable.ClockFaceView_hourTextSize, 0);

        ta.recycle();
    }

    private void initPaint(Context context) {
        mHourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHourPaint.setTextSize(mHourTextSize);
        mHourPaint.setTypeface(Typeface.DEFAULT_BOLD);//设置字体类型
        mHourPaint.setTextAlign(Paint.Align.CENTER); //设置居中

        mRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPlatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPlatePaint.setColor(mPlateColor);

        mTopTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTopTextPaint.setTextAlign(Paint.Align.CENTER);
        mTopTextPaint.setTextSize(mTopTextSize);
        mTopTextPaint.setColor(mTopTextColor);

        mBottomTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBottomTextPaint.setTextAlign(Paint.Align.CENTER);
        mBottomTextPaint.setTextSize(mBottomTextSize);
        mBottomTextPaint.setColor(mBottomTextColor);

        mBottomSecondTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBottomSecondTextPaint.setTextAlign(Paint.Align.CENTER);
        mBottomSecondTextPaint.setTextSize(mBottomSecondTextSize);
        mBottomSecondTextPaint.setColor(mBottomSecondTextColor);

        int tempRadius = 150;
        int sqrt = (int) Math.sqrt(Math.pow(tempRadius, 2) / 2.0F); //以半径为半径的等边三角形的等边
        Point gradientStartPoint = new Point((tempRadius + sqrt), (tempRadius - sqrt));
        Point gradientEndPoint = new Point((tempRadius - sqrt), (tempRadius + sqrt));

        LinearGradient gradientRing = new LinearGradient(gradientStartPoint.x, gradientStartPoint.y,
                gradientEndPoint.x, gradientEndPoint.y, mRingStartColor, mRingEndColor, Shader.TileMode.CLAMP);
        mRingPaint.setShader(gradientRing);

        LinearGradient gradientHour = new LinearGradient(gradientStartPoint.x, gradientStartPoint.y,
                gradientEndPoint.x, gradientEndPoint.y, mHourStartColor, mHourEndColor, Shader.TileMode.CLAMP);
        mHourPaint.setShader(gradientHour);
    }

    private void updateUi() {
        mH.sendEmptyMessageDelayed(0, DELAY_MILLIS);
    }

    private int getViewWidth(int widthMeasureSpec) {
        int result = 0;
        int mode = View.MeasureSpec.getMode(widthMeasureSpec);
        int size = View.MeasureSpec.getSize(widthMeasureSpec);
        if (mode == View.MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = Utils.dp2px(getContext(), DEFAULT_WIDTH);
            if (mode == View.MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    private void startRingIncreaseAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0F);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ringIncreasePercent = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.start();
    }

    private void updateDateInfo() {
        Date d = new Date();
        txtHour = Utils.date2Str(d, Utils.FORMAT_HM);
        txtTimeFrame = Utils.getAmPm(d);
        txtWeek = String.format(getResources().getString(R.string.date_info),
                Utils.getMonth(d), Utils.getDay(d), Utils.getWeek(d));
        Lunar lunar = Utils.getLunar(d);
        txtLunarCalendar = String.format(getResources().getString(
                R.string.date_lunar), lunar.cyclical(), lunar.toString());
        second = Utils.getSecond(d);
    }
}

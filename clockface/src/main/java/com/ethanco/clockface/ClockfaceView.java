package com.ethanco.clockface;

import android.animation.ValueAnimator;
import android.content.ContentValues;
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
import android.util.Log;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * @Description 时间表盘 View
 * Created by EthanCo on 2016/10/13.
 */

public class ClockfaceView extends View {
    private static final float DEFAULT_WIDTH = 300;
    private static final float DEFAULT_HEIGHT = 300;
    public static final String TAG = "Z-Clockface";

    private final int mRingStartColor;
    private final int mRingEndColor;
    private final int mHourStartColor;
    private final int mHourEndColor;
    private final int mPlateColor;
    private final float hourTextSize;
    private final float mRingWidth;
    private final int mTopTextColor;
    private final float mTopTextSize;
    private final int mBottomTextColor;
    private final float mBottomTextSize;
    private final int mBottomSecondTextColor;
    private final float mBottomSecondTextSize;

    private int mWidth; //View宽
    private int mHeight; //View高
    private int mRadius; //半径
    private int mDiameter; //直径

    private Paint mRingPaint; //分钟光环
    private Paint mHourPaint;  //小时文字
    private Paint mPlatePaint; //黑色表盘
    private Paint mTopTextPaint;
    private Paint mBottomTextPaint;
    private Paint mBottomSecondTextPaint;

    private String txtHour = "--:--"; //小时分钟
    private String txtTimeFrame = "--"; //时段 AM、PM
    private String txtWeek = "--月--日 星期-/-"; //星期
    private String txtLunarCalendar = "-/-年-/-月-/-"; //农历
    private int second = 0; //秒
    private float ringIncreasePercent;
    private float ringWidthPercent;
    private ValueAnimator valueAnimator1;

    private static class UpdateHandler extends Handler {
        protected WeakReference<ClockfaceView> clockfaceRef;

        public UpdateHandler(ClockfaceView clockfaceView) {
            this.clockfaceRef = new WeakReference(clockfaceView);
        }
    }

    public static final int DELAY_MILLIS = 1000;
    private UpdateHandler mH = new UpdateHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            ClockfaceView clockfaceview = this.clockfaceRef.get();
            if (clockfaceview == null) return;

            updateDateInfo();
            startAnim();
            //invalidate();

            mH.sendEmptyMessageDelayed(0, DELAY_MILLIS);
        }
    };

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

    public ClockfaceView(Context context) {
        this(context, null);
    }

    public ClockfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

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

        hourTextSize = ta.getDimension(R.styleable.ClockFaceView_hourTextSize, 0);

        ta.recycle();

        init(context);

        mH.sendEmptyMessageDelayed(0, DELAY_MILLIS);
    }

    private void init(Context context) {
        mHourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHourPaint.setTextSize(hourTextSize);
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

        int mRadius = 150;

        //int leftStart = (int) (mRadius - Math.sqrt(Math.pow(mRadius, 2) / 2.0F));
        //int rightEnd = (int) (mRadius + Math.sqrt(Math.pow(mRadius, 2) / 2.0F)) + 1;

        int sqrt = (int) Math.sqrt(Math.pow(mRadius, 2) / 2.0F); //以半径为半径的等边三角形的等边
        Point gradientStartPoint = new Point((mRadius + sqrt), (mRadius - sqrt));
        Point gradientEndPoint = new Point((mRadius - sqrt), (mRadius + sqrt));
        //int leftStart = (int) (mRadius - sqrt);
        //int rightEnd = (int) (mRadius + sqrt) + 1;

        Log.i(ContentValues.TAG, "init gradientStartPoint.x: " + gradientStartPoint.x + " gradientStartPoint.y:"
                + gradientStartPoint.y + " gradientEndPoint.x:" + gradientEndPoint.x + " gradientEndPoint.y:" + gradientEndPoint.y);

        LinearGradient gradient = new LinearGradient(gradientStartPoint.x, gradientStartPoint.y,
                gradientEndPoint.x, gradientEndPoint.y, mRingStartColor, mRingEndColor, Shader.TileMode.CLAMP);
        mRingPaint.setShader(gradient);

        //LinearGradient gradient1 = new LinearGradient(gradientStartPoint.x, gradientStartPoint.y,
        //       gradientEndPoint.x, gradientEndPoint.y, Color.RED, Color.BLUE, Shader.TileMode.CLAMP);

        LinearGradient gradient1 = new LinearGradient(gradientStartPoint.x, gradientStartPoint.y,
                gradientEndPoint.x, gradientEndPoint.y, mHourStartColor, mHourEndColor, Shader.TileMode.CLAMP);

        mHourPaint.setShader(gradient1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = getViewWidth(widthMeasureSpec);
        mHeight = getViewHeight(heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
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

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

//        int width = getWidth();
//        int height = getHeight();
//        int min = Math.min(width, height);
//        mWidth = min;
//        mHeight = min;

        mDiameter = Math.min(mWidth, mHeight);
        mRadius = (int) (mDiameter / 2.0F);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF plateRectF = new RectF(0, 0 , mDiameter, mDiameter );
        Log.i(TAG, "onDraw second: " + second);
        float angle = second / 60F * 360;
        angle = angle + 6 * ringIncreasePercent;
//        if (angle > 355 || angle < 5) {
//            angle = 360;
//        }
        canvas.drawArc(plateRectF, -90, angle, true, mRingPaint);
        canvas.save();

        int centerX = mRadius, centerY = mRadius;
        int platemRadius = (int) (mRadius - mRingWidth);
        canvas.drawCircle(centerX, centerY, platemRadius, mPlatePaint);

        //Paint.FontMetrics fm = mHourPaint.getFontMetrics();
        //字体高度 http://sd4886656.iteye.com/blog/1200890
        //float hourHalfHeight = (fm.bottom - fm.top) / 2.0F;
        //float hourHalfHeight = Math.abs(fm.ascent) / 2.0F;
        //Log.i(TAG, "onDraw hourHalfHeight: " + hourHalfHeight);
        //文字居中
        Paint.FontMetricsInt fontMetrics = mHourPaint.getFontMetricsInt();
        int baseline = (int) ((plateRectF.bottom + plateRectF.top - fontMetrics.bottom - fontMetrics.top) / 2F);

        canvas.drawText(txtHour, plateRectF.centerX(), baseline, mHourPaint);
        //canvas.drawText("15:30", 44, mRadius + hourHalfHeight, mHourPaint);

        canvas.drawText(txtTimeFrame, plateRectF.centerX(), baseline / 2F, mTopTextPaint);

        canvas.drawText(txtWeek, plateRectF.centerX(), baseline + mRadius * 0.3F, mBottomTextPaint);

        Paint.FontMetrics bottomSecondFm = mHourPaint.getFontMetrics();
        canvas.drawText(txtLunarCalendar, plateRectF.centerX(), baseline + mRadius * 0.3F + bottomSecondFm.bottom + mRadius * 0.13F, mBottomSecondTextPaint);
    }

    private void startAnim() {
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
}

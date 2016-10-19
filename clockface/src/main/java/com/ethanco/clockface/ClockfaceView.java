package com.ethanco.clockface;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * @Description 时间表盘 View
 * Created by EthanCo on 2016/10/13.
 */

public class ClockfaceView extends View {
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

    private int mWidth;
    private int mHeight;
    private int mRadius;

    private Paint mRingPaint; //分钟光环
    private Paint mHourPaint;  //小时文字
    private Paint mPlatePaint; //黑色表盘
    private Paint mTopTextPaint;
    private Paint mBottomTextPaint;
    private Paint mBottomSecondTextPaint;

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

        Log.i(TAG, "init gradientStartPoint.x: " + gradientStartPoint.x + " gradientStartPoint.y:"
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
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mWidth = getWidth();
        mHeight = getHeight();
        mRadius = (int) (Math.min(mWidth, mHeight) / 2.0F);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.i(TAG, "onDraw getWidth: " + getWidth() + " getHeight:" + getHeight());
        Log.i(TAG, "onDraw getPaddingLeft(): " + getPaddingLeft());
        RectF plateRectF = new RectF(0, 0, mWidth, mHeight); //TODO dp to px
        canvas.drawArc(plateRectF, -90, 270, true, mRingPaint);
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

        canvas.drawText("10:27", plateRectF.centerX(), baseline, mHourPaint);
        //canvas.drawText("15:30", 44, mRadius + hourHalfHeight, mHourPaint);

        canvas.drawText("PM", plateRectF.centerX(), baseline / 2F, mTopTextPaint);

        canvas.drawText("10月13日 星期四", plateRectF.centerX(), baseline + mRadius * 0.3F, mBottomTextPaint);

        Paint.FontMetrics bottomSecondFm = mHourPaint.getFontMetrics();
        canvas.drawText("农历九月十三", plateRectF.centerX(), baseline + mRadius * 0.3F + bottomSecondFm.bottom + mRadius * 0.13F, mBottomSecondTextPaint);
    }
}

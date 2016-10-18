package com.ethanco.clockface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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

    private Paint mHalopaint; //分钟光环
    private Paint mHourPaint;  //小时文字
    private Paint mPlatePaint; //黑色表盘

    public ClockfaceView(Context context) {
        super(context);

        init(context);
    }

    public ClockfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    public ClockfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        mHourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHourPaint.setTextSize(80);
        mHourPaint.setTypeface(Typeface.DEFAULT_BOLD);//设置字体类型

        mHalopaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //mPlatePaint.setColor(Color.GREEN);

        mPlatePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPlatePaint.setColor(Color.rgb(0x3b, 0x3a, 0x3f));

        int radius = 150;

        //int leftStart = (int) (radius - Math.sqrt(Math.pow(radius, 2) / 2.0F));
        //int rightEnd = (int) (radius + Math.sqrt(Math.pow(radius, 2) / 2.0F)) + 1;

        int sqrt = (int) Math.sqrt(Math.pow(radius, 2) / 2.0F); //以半径为半径的等边三角形的等边
        Point gradientStartPoint = new Point((radius + sqrt), (radius - sqrt));
        Point gradientEndPoint = new Point((radius - sqrt), (radius + sqrt));
        //int leftStart = (int) (radius - sqrt);
        //int rightEnd = (int) (radius + sqrt) + 1;

        Log.i(TAG, "init gradientStartPoint.x: " + gradientStartPoint.x + " gradientStartPoint.y:"
                + gradientStartPoint.y + " gradientEndPoint.x:" + gradientEndPoint.x + " gradientEndPoint.y:" + gradientEndPoint.y);

        LinearGradient gradient = new LinearGradient(gradientStartPoint.x, gradientStartPoint.y,
                gradientEndPoint.x, gradientEndPoint.y, Color.rgb(0x01, 0xce, 0x9b), Color.rgb(0xae, 0xe8, 0x47), Shader.TileMode.CLAMP);
        mHalopaint.setShader(gradient);

        //LinearGradient gradient1 = new LinearGradient(gradientStartPoint.x, gradientStartPoint.y,
        //       gradientEndPoint.x, gradientEndPoint.y, Color.RED, Color.BLUE, Shader.TileMode.CLAMP);

        LinearGradient gradient1 = new LinearGradient(gradientStartPoint.x, gradientStartPoint.y,
                gradientEndPoint.x, gradientEndPoint.y, Color.rgb(0xae, 0xe8, 0x47), Color.rgb(0x01, 0xce, 0x9b), Shader.TileMode.CLAMP);

        mHourPaint.setShader(gradient1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int radius = 150;

        Log.i(TAG, "onDraw getWidth: " + getWidth() + " getHeight:" + getHeight());
        Log.i(TAG, "onDraw getPaddingLeft(): " + getPaddingLeft());
        RectF plateRectF = new RectF(0, 0, 300, 300); //TODO dp to px
        canvas.drawArc(plateRectF, -90, 270, true, mHalopaint);
        canvas.save();

        int centerX = 150, centerY = 150;
        int plateRadius = radius - 30;
        canvas.drawCircle(centerX, centerY, plateRadius, mPlatePaint);

        Paint.FontMetrics fm = mHourPaint.getFontMetrics();
        //字体高度 http://sd4886656.iteye.com/blog/1200890
        //float hourHalfHeight = (fm.bottom - fm.top) / 2.0F;
        float hourHalfHeight = Math.abs(fm.ascent) / 2.0F;

        Log.i(TAG, "onDraw hourHalfHeight: " + hourHalfHeight);

        canvas.drawText("15:30", 44, radius + hourHalfHeight, mHourPaint);
    }
}

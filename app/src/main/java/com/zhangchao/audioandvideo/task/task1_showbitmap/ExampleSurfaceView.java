package com.zhangchao.audioandvideo.task.task1_showbitmap;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.RotateDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.zhangchao.audioandvideo.R;

/**
 * Created by zhangchao on 18-3-16.
 */

public class ExampleSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    public static final int FRAME_TIME = 120;
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Paint mPaint;

    private Path mPath;


    private boolean mIsEnableDrawing;

    public ExampleSurfaceView(Context context) {
        super(context);
        initView();
    }

    public ExampleSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ExampleSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public ExampleSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        //setFocusable(true);
        //setFocusableInTouchMode(true);
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);
        mPaint.setColor(Color.RED);
        setBackgroundResource(R.mipmap.test2);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsEnableDrawing = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsEnableDrawing = false;
    }

    @Override
    public void run() {
        long start;
        long end;
        drawBitmap();
        while (mIsEnableDrawing) {
            start = System.currentTimeMillis();
            draw();
            end = System.currentTimeMillis();
            if (end - start < FRAME_TIME) {
                try {
                    Thread.sleep(FRAME_TIME - (end - start));
                } catch (Exception e) {

                }
            }
        }
    }

    private void drawBitmap(){
        try {
            mCanvas = mHolder.lockCanvas();
            mCanvas.drawBitmap(BitmapFactory.decodeResource(getContext().getResources(),R.mipmap.test2),0,0,mPaint);
        } catch (Exception e) {

        } finally {
            //判断画布是否为空，从而避免黑屏情况
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            mCanvas.drawPath(mPath,mPaint);
        } catch (Exception e) {

        } finally {
            //判断画布是否为空，从而避免黑屏情况
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("s", "onTouchEvent: down");
                mPath.moveTo(x,y);
                break;
            case MotionEvent.ACTION_UP:
                Log.d("s", "onTouchEvent: up");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("s", "onTouchEvent: move");
                mPath.lineTo(x,y);
                break;
            default:
                break;
        }

        return true;
        //return super.onTouchEvent(event);
    }
}

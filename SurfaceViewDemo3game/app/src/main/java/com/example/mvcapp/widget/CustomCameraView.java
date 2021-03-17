package com.example.mvcapp.widget;


import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class CustomCameraView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    public CustomCameraView(Context context) {
        super(context);
        init();
        Log.i(TAG, "CustomCameraView: 1");

    }

    public CustomCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        Log.i(TAG, "CustomCameraView: 2");

    }

    public CustomCameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        Log.i(TAG, "CustomCameraView: 3");

    }

    private void init() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "666", Toast.LENGTH_SHORT).show();
            }
        });
        mHolder = getHolder();
        mHolder.addCallback(this);
//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        setKeepScreenOn(true);
        setZOrderOnTop(true);
        setZOrderMediaOverlay(true);//正常视图的顶层
        mHolder.setFormat(PixelFormat.TRANSPARENT);

        mPath = new Path();
        //初始化画笔
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
    }

    private Camera mCamera;
    private Canvas mCanvas;
    private Paint mPaint;
    private SurfaceHolder mHolder;
    private boolean mIsDrawing;
    //路径
    private Path mPath;

    //在创建时激发，一般在这里调用画图的线程。
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsDrawing = true;
        new Thread(this).start();

        Log.i(TAG, "surfaceCreated: ");

    }

    //在surface的大小发生改变时激发
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged: ");

    }

    //销毁时激发，一般在这里将画图的线程停止、释放。
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
        Log.i(TAG, "surfaceDestroyed: ");
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        while (mIsDrawing) {
            draw();
//            drawLine();
//            long end = System.currentTimeMillis();
//            if (end - start < 100) {
//                try {
//                    Thread.sleep(100 - end + start);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    private Path mPathLine;
    private int lineY = 0;

    private void draw() {
        try {
            if (mPathLine == null) {
                mPathLine = new Path();
                lineY = getHeight() / 9;
            }
            mPathLine.reset();
            if (lineY >= getHeight() / 9 * 8) {
                lineY = getHeight() / 9;
//                mPathLine.reset();
            }
            lineY = lineY + 5;
            mPathLine.moveTo(getWidth() / 2 - 400, lineY);
            mPathLine.lineTo(getWidth() / 2 + 400, lineY);


            //锁定画布并返回画布对象
            mCanvas = mHolder.lockCanvas();
            //接下去就是在画布上进行一下draw
            mCanvas.drawColor(Color.YELLOW);

            //接下去就是在画布上进行一下draw
//            mCanvas.drawColor(Color.YELLOW);
            Rect rect = new Rect();
            rect.left = getWidth() / 2 - 400;
            rect.right = getWidth() / 2 + 400;
            rect.top = getHeight() / 9;
            rect.bottom = getHeight() / 9 * 8;
            mPaint.setColor(Color.RED);
            mCanvas.drawRect(rect, mPaint);
            mCanvas.drawPath(mPathLine, mPaint);


            mPaint.setColor(Color.RED);
            mCanvas.drawPath(mPath, mPaint);

        } catch (Exception e) {
        } finally {
            //当画布内容不为空时，才post，避免出现黑屏的情况。
            if (mCanvas != null)
                mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private static final String TAG = "----------CustomCameraView:" + Thread.currentThread().getName();

    /**
     * 绘制触摸滑动路径
     *
     * @param event MotionEvent
     * @return true
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent: down");
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d(TAG, "onTouchEvent: move");
                mPath.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                Log.d(TAG, "onTouchEvent: up");
                break;
        }
        return true;
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        int x = (int) event.getX();
//        int y = (int) event.getY();
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                Log.d(TAG, "dispatchTouchEvent: down");
//                mPath.moveTo(x, y);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.d(TAG, "dispatchTouchEvent: move");
//                mPath.lineTo(x, y);
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.d(TAG, "dispatchTouchEvent: up");
//                break;
//        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 清屏
     *
     * @return true
     */
    public boolean reDraw() {
        mPath.reset();
        return true;
    }

}

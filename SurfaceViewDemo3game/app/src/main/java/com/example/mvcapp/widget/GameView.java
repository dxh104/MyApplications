package com.example.mvcapp.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Paint.Style.FILL;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        setZOrderOnTop(true);
        setZOrderMediaOverlay(true);//正常视图的顶层
        mHolder.setFormat(PixelFormat.TRANSPARENT);
        //初始化画笔
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(6);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);

        setBackgroundColor(Color.argb(0x00, 0, 0, 0));
    }

    private Canvas mCanvas;
    private Paint mPaint;
    public SurfaceHolder mHolder;
    private boolean mIsDrawing;

    //在创建时激发，一般在这里调用画图的线程。
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mAttackTime = 400;
        mBulletTime = 300;
        mark = 0;
        mIsDrawing = true;
        mPlayerPathCenterX = getWidth() / 2;
        mPlayerPathCenterY = getHeight() - mPlayerPathBottom;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsDrawing) {
                    drawGame();//绘制game
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsDrawing) {
                    createBullet();//创建子弹
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mIsDrawing)
                    createAttack();//创建攻击道具
            }
        }).start();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (mIsDrawing)
//                    calculateCollide();//计算碰撞
//            }
//        }).start();
    }

    private int mark = 0;

    private void calculateCollide() {
        for (int j = 0; j < mAttackPoints.size(); j++) {
            int mAttackPointLeft = mAttackPoints.get(j).X - mAttackSize / 2;
            int mAttackPointTop = mAttackPoints.get(j).Y - mAttackSize / 2;
            boolean flagPlayer = checkRectCollsion(mPlayerPathCenterX - mPlayerPathCenterDistance / 2, mPlayerPathCenterY, mPlayerPathCenterDistance, mPlayerPathCenterDistance, mAttackPointLeft, mAttackPointTop, mAttackSize * 2, mAttackSize * 2);
            if (flagPlayer) {
                mIsDrawing=false;
                if (onUpdateListener != null)
                    onUpdateListener.onFailed();
            }
            for (int i = 0; i < mBulletPoints.size(); i++) {
                int mBulletPointLeft = mBulletPoints.get(i).X - mBulletSize;
                int mBulletPointTop = mBulletPoints.get(i).Y - mBulletSize;

                boolean flagBullet = checkRectCollsion(mBulletPointLeft, mBulletPointTop, mBulletSize * 2, mBulletSize * 2, mAttackPointLeft, mAttackPointTop, mAttackSize * 2, mAttackSize * 2);
                if (flagBullet) {//碰撞

                    mBulletPoints.remove(mBulletPoints.get(i));
                    mBulletPaths.remove(mBulletPaths.get(i));

                    mAttackPoints.remove(mAttackPoints.get(j));
                    mAttackPaths.remove(mAttackPaths.get(j));
                    mark++;

                    if (mBulletTime > 30)//加速
                        mBulletTime = mBulletTime - mark / 10;
                    if (mAttackTime > 50)//加速
                        mAttackTime = mAttackTime - mark / 10;
                    if (onUpdateListener != null) {
                        onUpdateListener.onUpdate(mark, mAttackTime, mBulletTime);
                    }
                }
            }
        }

    }

    public void setOnUpdateListener(OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    OnUpdateListener onUpdateListener;

    public interface OnUpdateListener {
        void onUpdate(int mark, int 当前掉落速度, int 当前射速);//更新分数

        void onFailed();//失败重置
    }

    /**
     * 碰撞检测
     *
     * @param x1 矩形1的X坐标
     * @param y1 矩形1的Y坐标
     * @param w1 矩形1的宽
     * @param h1 矩形1的高
     * @param x2 矩形2的X坐标
     * @param y2 矩形2的Y坐标
     * @param w2 矩形2的宽
     * @param h2 矩形2的高
     * @return
     */
    private boolean checkRectCollsion(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        if (x1 >= x2 && x1 >= x2 + w2) {
            return false;
        } else if (x1 <= x2 && x1 + w1 <= x2) {
            return false;
        } else if (y1 >= y2 && y1 >= y2 + h2) {
            return false;
        } else if (y1 <= y2 && y1 + h1 <= y2) {
            return false;
        }
        return true;
    }

    private int mAttackSize = 25;//攻击物品大小
    private List<Point> mAttackPoints = new ArrayList<>();//攻击物品位置集合
    private List<Path> mAttackPaths = new ArrayList<>();//攻击物品路径集合
    private int mAttackTime = 400;//攻击物品生成时间

    private void createAttack() {
        try {
            Thread.sleep(mAttackTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Point mAttackPoint = new Point();//攻击物品坐标
        mAttackPoint.X = (int) (getWidth() * Math.random());
        mAttackPoint.Y = -mAttackSize;
        mAttackPoints.add(mAttackPoint);
        mAttackPaths.add(new Path());
    }

    private int mBulletTime = 300;//子弹生成时间
    private int mBulletSize = 200;//子弹大小
    private List<Point> mBulletPoints = new ArrayList<>();//子弹位置集合
    private List<Path> mBulletPaths = new ArrayList<>();//子弹路径集合

    private void createBullet() {
        try {
            Thread.sleep(mBulletTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Point mBulletPoint = new Point();//子弹坐标
        mBulletPoint.X = mPlayerPathCenterX;
        mBulletPoint.Y = mPlayerPathCenterY - mPlayerPathCenterDistance;
        mBulletPoints.add(mBulletPoint);//3s增加一发子弹
        mBulletPaths.add(new Path());
    }

    private int gameViewWidth;
    private int gameViewHeight;

    //在surface的大小发生改变时激发
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        gameViewWidth = getWidth();
        gameViewHeight = getHeight();
    }

    //销毁时激发，一般在这里将画图的线程停止、释放。
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsDrawing = false;
    }

    private Path mPlayerPath = new Path();//玩家路径
    private int mPlayerPathCenterX, mPlayerPathCenterY;//玩家中心坐标
    private int mPlayerPathCenterDistance = 200;//中心点距离顶部底边距离，底边中心点距离两边距离
    private int mPlayerPathBottom = mPlayerPathCenterDistance * 2;//玩家默认中心点距离底部距离


    private Rect rect = new Rect();

    private void drawGame() {
        try {
            //锁定画布并返回画布对象
            mCanvas = mHolder.lockCanvas();
            rect.left = 3;
            rect.right = getWidth() - 3;
            rect.top = 3;
            rect.bottom = getHeight() - 3;
            mPaint.setColor(Color.RED);//设置红色画笔
            mPaint.setStyle(Paint.Style.STROKE);//空心
            mCanvas.drawRect(rect, mPaint);//画边框
            //接下去就是在画布上进行一下draw
            mCanvas.drawColor(Color.YELLOW);//画背景

            mPlayerPath.reset();
            mPlayerPath.moveTo(mPlayerPathCenterX - mPlayerPathCenterDistance, mPlayerPathCenterY + mPlayerPathCenterDistance);//a
            mPlayerPath.lineTo(mPlayerPathCenterX + mPlayerPathCenterDistance, mPlayerPathCenterY + mPlayerPathCenterDistance);//b
            mPlayerPath.lineTo(mPlayerPathCenterX, mPlayerPathCenterY - mPlayerPathCenterDistance);//c
            mPlayerPath.lineTo(mPlayerPathCenterX - mPlayerPathCenterDistance, mPlayerPathCenterY + mPlayerPathCenterDistance);//a

            mPaint.setColor(Color.RED);//设置红色画笔
            mPaint.setStyle(Paint.Style.FILL);//实心
            mCanvas.drawPath(mPlayerPath, mPaint);//画玩家


            for (int i = 0; i < mBulletPoints.size(); i++) {//处理所有子弹
                Point mBulletPoint = mBulletPoints.get(i);
                Path mBulletPath = mBulletPaths.get(i);
                mBulletPath.reset();
                mBulletPath.addCircle(mBulletPoint.X, mBulletPoint.Y, mBulletSize, Path.Direction.CCW);
                mBulletPoint.Y = mBulletPoint.Y - mAttackSize;
                mPaint.setColor(Color.RED);//设置红色画笔
                mPaint.setStyle(Paint.Style.FILL);//实心
                mCanvas.drawPath(mBulletPath, mPaint);//画子弹
                if (mBulletPoint.Y < -mBulletSize * 20) {
                    mBulletPoints.remove(mBulletPoint);
                    mBulletPaths.remove(mBulletPath);
                }
            }

            for (int i = 0; i < mAttackPoints.size(); i++) {//处理所有攻击物品
                Point mAttackPoint = mAttackPoints.get(i);
                Path mAttackPath = mAttackPaths.get(i);
                mAttackPath.reset();
                mAttackPath.addCircle(mAttackPoint.X, mAttackPoint.Y, mAttackSize, Path.Direction.CCW);
                mAttackPoint.Y = mAttackPoint.Y + mAttackSize;
                mPaint.setColor(Color.BLACK);//设置黑色画笔
                mPaint.setStyle(Paint.Style.FILL);//实心
                mCanvas.drawPath(mAttackPath, mPaint);//画攻击物品
                if (mAttackPoint.Y > getHeight() + mBulletSize * 20) {
                    mAttackPoints.remove(mAttackPoint);
                    mAttackPaths.remove(mAttackPath);
                }
            }

            calculateCollide();//计算碰撞


        } catch (Exception e) {
        } finally {
            //当画布内容不为空时，才post，避免出现黑屏的情况。
            if (mCanvas != null)
                mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

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
                mPlayerPathCenterX = x;
                mPlayerPathCenterY = y - mPlayerPathCenterDistance / 3;
                break;
            case MotionEvent.ACTION_MOVE:
                mPlayerPathCenterX = x;
                mPlayerPathCenterY = y - mPlayerPathCenterDistance / 3;
                break;
            case MotionEvent.ACTION_UP:
                mPlayerPathCenterX = x;
                mPlayerPathCenterY = y - mPlayerPathCenterDistance / 3;
                break;
        }
        return true;
    }

    static class Point {
        public int X = 0;
        public int Y = 0;
    }

}

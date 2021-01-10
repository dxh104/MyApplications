package com.example.myapplicationscroller.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

public class MyViewGroup extends ViewGroup {

    MyScroller myScroller;
    TextView textView;


    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        myScroller = new MyScroller(context,new LinearInterpolator());
        //10s内 myScroller.getCurrX()从0变化到1000 y同理
//        myScroller.startScroll(0,0,1000,1000,10*1000);
        //10000px/s
        myScroller.fling(0,0,100000,100000,-Integer.MIN_VALUE,Integer.MAX_VALUE,-Integer.MIN_VALUE,Integer.MAX_VALUE);
        textView = new TextView(context);
        textView.setText("text");
        textView.setBackgroundColor(Color.parseColor("#ff0000"));
        addView(textView);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(1100,1100);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i <getChildCount() ; i++) {
            View view = getChildAt(i);
            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();
            view.layout(0,0,measuredWidth,measuredHeight);
        }
    }

    @Override
    public void computeScroll() {
        if (myScroller.computeScrollOffset()) {//得到一个新的位置，偏移完成返回false
            Log.i(myScroller.getDuration()+"-----", "computeScroll:x= "+myScroller.getCurrX()+"  y="+myScroller.getCurrY());
            scrollTo(-myScroller.getCurrX()/100,-myScroller.getCurrY()/100);
            postInvalidate();//刷新computeScroll
        }
        super.computeScroll();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}

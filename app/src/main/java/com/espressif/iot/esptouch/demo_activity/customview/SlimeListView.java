package com.espressif.iot.esptouch.demo_activity.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;

/**
 * Created by Administrator on 2017/8/23.
 */

public class SlimeListView extends ListView {
    private float mTouchX;
    private float mTouchY;
    private float mMoveX;
    private float mMoveY;
    private int mTouchPosition;
    private float mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    public SlimeListView(Context context) {
        super(context);
    }

    public SlimeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlimeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ViewConfiguration.get(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        SlideItemView.width = width;
        for (int i = 0; i < getChildCount(); i++) {
            SlideItemView item = (SlideItemView) getChildAt(i);
            item.resetWidth();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    /**
     * 屏幕当前
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float dx = 0;
        float dy = 0;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchX = ev.getX();
                mTouchY = ev.getY();
                mMoveX = ev.getX();
                mMoveY = ev.getY();
                //根据当前位置获取当前设备的具体位置;
                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

                break;
            case MotionEvent.ACTION_MOVE:
                dx = ev.getX() - mMoveX;
                dy = ev.getY() - mMoveY;
                if (Math.abs(dx) > Math.abs(dy)) {//左右滑动
                    //根据坐标得到获取listView的数据值
                    int position = pointToPosition((int) ev.getX(), (int) ev.getY());
                    //ListView.INVALID_POSITION  代表了一个无效的位置。所有有效的位置都在0到1的范围内，小于当前适配器的条目数。
                    if (mTouchPosition != ListView.INVALID_POSITION && position == mTouchPosition) {
                        //得到内存中真实的Item
                        //getFirstVisiblePosition() 获取当前屏幕显示的第一项位置索引值
                        //得到当前子类的位置
                        SlideItemView itemView = (SlideItemView) getChildAt(position - getFirstVisiblePosition());
                        itemView.scroll((int) dx);//滚动
                    }
                }
                mMoveX = ev.getX();
                mMoveY = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                dx = ev.getX() - mTouchX;
                dy = ev.getY() - mTouchY;
                if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) >= mTouchSlop) {
                    int position = pointToPosition((int) ev.getX(), (int) ev.getY());
                    if (mTouchPosition != ListView.INVALID_POSITION && position == mTouchPosition) {
                        //得到真正的在内存中得item;
                        SlideItemView itemView = (SlideItemView) getChildAt(position - getFirstVisiblePosition());
                        //根据当前scrollX以及Dx判断是否显示正文内容;
                        if (itemView.shouldShowContent((int) dx)) {
                            itemView.showContent();
                        } else {
                            itemView.showMenu();
                        }

                    } else if (position != mTouchPosition) {
                        SlideItemView itemView = (SlideItemView) getChildAt(mTouchPosition - getFirstVisiblePosition());
                        //根据当前scrollX以及Dx判断是否显示正文内容;
                        if (itemView.shouldShowContent((int) dx)) {
                            itemView.showContent();
                        } else {
                            itemView.showMenu();
                        }
                    }
                } else {
                    SlideItemView itemView = (SlideItemView) getChildAt(mTouchPosition - getFirstVisiblePosition());
                    //根据当前scrollX以及Dx判断是否显示正文内容;
                    if(itemView==null){
                        return false;
                    }
                    if (itemView.shouldShowContent((int) dx)) {
                        itemView.showContent();
                    } else {
                        itemView.showMenu();
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (mTouchPosition != ListView.INVALID_POSITION) {
                    SlideItemView itemView = (SlideItemView) getChildAt(mTouchPosition - getFirstVisiblePosition());
                    itemView.showContent();
                }
                break;
        }
        return super.onTouchEvent(ev);
    }
}

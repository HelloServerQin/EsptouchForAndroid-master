package com.espressif.iot.esptouch.demo_activity.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.Scroller;

import com.espressif.iot_esptouch_demo.R;

/**
 * Created by Administrator on 2017/8/4.
 * 滑动菜单按钮
 * 1.有bug
 * 1.用户左右滑动出现重复操作;
 */

public class SlideMenu extends ViewGroup {
    private Scroller mScroller;
    private int mTouchSlop;
    private View menuLeft, mainMenu;
    private int aPaoint;
    private ImageButton ibutton;

    //设置用户打开当前数据状态判定当前是否打开;

    public SlideMenu(Context context) {
        super(context);
        init(context);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    /**
     * 获取当前布局数据
     * 1.将布局文件压制进来
     */
    private void init(Context context) {
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();//滑动菜单隐藏的一定距离


    }

    //测量当前容器中得子类插件得宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        menuLeft = getChildAt(0);
        mainMenu = getChildAt(1);

        menuLeft.measure(menuLeft.getLayoutParams().width, heightMeasureSpec);
        mainMenu.measure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置子类空间的位置所在
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        menuLeft.layout(-menuLeft.getLayoutParams().width, 0, 0, b);
        mainMenu.layout(l, t, r, b);
    }

    private int totalCount;
    private int MENU_VIEW = 1;
    private int MAIN_VIEW =0;
    private int currentView;
    //滑动布局
    private  static boolean falg = true;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://用户按下
                aPaoint = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE://用户移动,
                int currentX = (int) event.getX();
                int delta = aPaoint - currentX;
                int scrollX = getScrollX() + delta;
                Log.i("serverQin", "--->" + scrollX);
                    if (scrollX < -menuLeft.getWidth())//scrollx 小于这个宽时 ,不断小于不停的
                    {
                        scrollTo(-menuLeft.getWidth(), 0);
                    } else if (scrollX > 0) {
                        scrollTo(0, 0);
                    } else{
                            scrollBy(delta, 0);
                    }
                aPaoint = currentX;
                break;
            case MotionEvent.ACTION_UP://用户松开过程梳理;
                int menuCenter = menuLeft.getWidth() / 2;
                int _x = getScrollX();//1.正值, 0-menuCenter |  menuCenter-无穷大;2.负值,负无穷大到-menucCenter   |  menuCenter-0;
                //从左向右为负数,
                if (_x <= -menuCenter) {
                    currentView = MENU_VIEW;//滚动量小于菜单的一半
                    Log.i("serverQIn","hahahahahahah");
                } else if (_x > -menuCenter) {
                    Log.i("serverQIn","hahahahahahah");
                    currentView = MAIN_VIEW;
                }
                switchView();
                break;
        }
        return true;
    }

    public boolean  isHide(){
        return currentView==MENU_VIEW?true:false;
    }

    public void   openView(){
        currentView=MENU_VIEW;
        switchView();

    }

    public void   closeView(){
        currentView=MAIN_VIEW;
        switchView();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), 0);
            invalidate();
        }
    }

    private void switchView() {
        int startX = getScrollX();
        int dx = 0;
        if (currentView == MENU_VIEW)
            dx = -menuLeft.getWidth() - startX;
        else if (currentView == MAIN_VIEW)
            dx = 0 - startX;

        mScroller.startScroll(startX, 0, dx, 0, Math.abs(dx));//带时间的响应数据
        invalidate();
    }

    private float mXDown;

    /**
     * 事件分发机制
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                float mXMove = ev.getRawX();//用户滑动数据
                float mXLastLocal = mXMove - mXDown;
                if (Math.abs(mXLastLocal) > mTouchSlop) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

}



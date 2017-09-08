package com.espressif.iot.esptouch.demo_activity.customview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

/**
 * ListView 中得ItemView项目;
 */

public class SlideItemView extends LinearLayout {
    //正文的宽度
    public static int width;
    private View content;
    private View menu;
    private Scroller mScroller;
    private ListView listView;
    private float scale;


    public SlideItemView(Context context) {
        super(context);
        initView();
    }

    //初始化当前布局和滚动类
    private void initView() {
        setOrientation(LinearLayout.HORIZONTAL);
        mScroller = new Scroller(getContext());
    }

    public SlideItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SlideItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    //使用Scroller类中得startScroller方法无法完成滚动,必须重写somputeScroll方法,完成实际的滚动操作
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());//滚动;
            postInvalidate();//绘制;
        }
        super.computeScroll();
    }

    /**
     * 初始化当前布局文件
     *
     * @param listView  listView类
     * @param contentId 主菜单布局ID
     * @param menuId    右菜单布局ID
     * @param menuScale 右菜单滑动测量值
     */
    public void setView(ListView listView, int contentId, int menuId, float menuScale) {
        this.listView = listView;
        this.content = View.inflate(getContext(), contentId, null);
        this.menu = View.inflate(getContext(), menuId, null);
        this.scale = menuScale;
        //加载view到当前布局文件中;
        LayoutParams param1 = new LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(content, param1);
        LayoutParams param2 = new LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(menu, param2);
    }

    public View getContent() {
        return content;
    }

    public View getMenu() {
        return menu;
    }

    /**
     * 显示主内容页面的位置;
     * mScroller.getFinalX() 返回滚动结束位置。最终位置X方向距离原点的绝对距离
     * getStartX () 起始点在X方向距离原点的绝对距离
     */
    public void showContent() {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), -mScroller.getFinalX(), 0);
        invalidate();
    }

    /**
     * 显示右菜单按钮;
     */
    public void showMenu() {
        mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), menu.getWidth() - mScroller.getFinalX(), 0);
        invalidate();
    }

    /**
     * 应该现实的内容
     *
     * @param dx 结束位置
     * @return
     */
    public boolean shouldShowContent(int dx) {
        //初始化
        if (menu.getWidth() == 0) {
            resetWidth();//测量控件宽高
        }
        if (dx > 0) {
            //右滑动,当过1/4的时候开始变化
            if (mScroller.getFinalX() < menu.getWidth() * 3 / 4) {
                return true;
            } else
                return false;
        } else {
            //左滑动,当滑过1/4的时候开始变化
            if (mScroller.getFinalX() < menu.getWidth() / 4)
                return true;
            else
                return false;
        }

    }

    /**
     * 滚动
     *
     * @param dx 滚动距离;水平方向滑动的距离
     */
    public void scroll(int dx) {
        if (dx > 0) {
            //右滑动
            if (mScroller.getFinalX() > 0) {
                if (dx > mScroller.getFinalX()) {
                    mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), -mScroller.getFinalX(), 0);
                } else
                    mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), -dx, 0);
            } else
                mScroller.setFinalX(0);
            invalidate();
        } else {
            //左滑动
            if (mScroller.getFinalX() < menu.getWidth()) {
                if (mScroller.getFinalX() - dx > menu.getWidth())
                    mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), menu.getWidth() - mScroller.getFinalX(), 0);
                else
                    mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), -dx, 0);
            } else
                mScroller.setFinalX(menu.getWidth());
            invalidate();
        }
    }

    /**
     * 重置宽度;
     * 保证屏幕的宽度;
     */
    public void resetWidth() {
        ViewGroup.LayoutParams param1 = content.getLayoutParams();
        if (param1 == null) {
            param1 = new ViewGroup.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        } else
            param1.width = width;
        content.setLayoutParams(param1);
        ViewGroup.LayoutParams param2 = menu.getLayoutParams();
        if (param2 == null) {
            param2 = new ViewGroup.LayoutParams((int) (width * scale), ViewGroup.LayoutParams.MATCH_PARENT);
        } else
            param2.width = (int) (width * scale);
        menu.setLayoutParams(param2);
    }

}

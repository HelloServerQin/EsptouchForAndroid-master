package com.espressif.iot.esptouch.demo_activity.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.espressif.iot_esptouch_demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/12.
 * 1.绘制九点:
 * 点的长度,
 * 点的颜色,
 * 点的位置,
 * 点代表的value;
 * 2.滑动时,线条的绘制
 * 3.线与点之间的三个关系(交,切,离),运算
 * 4.通过通过线条的颜色来表示错误;
 */
public class LockView extends View {


    private Paint mPaint, mPressPaint, mErrorPaint;
    private Bitmap mNormalBitmap, mPressBitmap, mErrorBitmap;
    private int mPorintRadius;
    private Point[][] mPoints = new Point[3][3];
    private float mX, mY;
    private boolean isDraw=false;
    //用户选用的点;
    private List<Point> mSelectedPoints=new ArrayList<Point>();
    //绘制正确的点位置;
    private List<Integer> mPassPositions=new ArrayList<Integer>();

    private  OnDrawFinishedListener  mListener;
    public LockView(Context context) {
        this(context, null);
    }

    public LockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPressPaint = new Paint();
        mErrorPaint = new Paint();
        mPressPaint.setColor(Color.parseColor("#00b7ee"));
        mPressPaint.setStrokeWidth(7);
        mErrorPaint.setColor(Color.parseColor("#fb0c13"));
        mErrorPaint.setStrokeWidth(7);
        //加载三种状态的图片
        mNormalBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.lock_point_normal);
        mPressBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.lock_point_press);
        mErrorBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.lock_point_error);
    }

    /**
     * 初始化画笔,图片,和九个点的数据;
     * 1.进行调整的地方;
     * @param context
     *
     */
    private void init(Context context) {
        //当前视图的大小;
        int width = getWidth();//获取的宽高
        int height = getHeight();
        int offSet = Math.abs(width - height) / 2;
        int offsetX = 0, offSetY = 0;
        int pointItemWidth = 0;//每个点所占方格的宽度;
        if (width > height) {
            offsetX = offSet;
            offSetY = 0;
            pointItemWidth = height / 4;
        }
        if (width < height) {
            offSetY = offSet;
            offsetX = 0;
            pointItemWidth = width / 4;
        }

        //初始化九个点;
        mPoints[0][0] = new Point(offsetX + pointItemWidth, offSetY + pointItemWidth);
        mPoints[0][1] = new Point(offsetX + pointItemWidth * 2, offSetY + pointItemWidth);
        mPoints[0][2] = new Point(offsetX + pointItemWidth * 3, offSetY + pointItemWidth);

        mPoints[1][0] = new Point(offsetX + pointItemWidth, offSetY + pointItemWidth * 2);
        mPoints[1][1] = new Point(offsetX + pointItemWidth * 2, offSetY + pointItemWidth * 2);
        mPoints[1][2] = new Point(offsetX + pointItemWidth * 3, offSetY + pointItemWidth * 2);

        mPoints[2][0] = new Point(offsetX + pointItemWidth, offSetY + pointItemWidth * 3);
        mPoints[2][1] = new Point(offsetX + pointItemWidth * 2, offSetY + pointItemWidth * 3);
        mPoints[2][2] = new Point(offsetX + pointItemWidth * 3, offSetY + pointItemWidth * 3);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制点
        drawPoint(canvas);
        //绘制线
        drawLines(canvas);
    }

    /**
     * 绘制用户拖动的线条;
     *
     * @param canvas
     */
    private void drawLines(Canvas canvas) {
        if (mSelectedPoints.size() > 0){
            // 从第一个被选中的点开始绘制
            Point a = mSelectedPoints.get(0);
            for (int i = 1; i < mSelectedPoints.size(); i++){
                Point b = mSelectedPoints.get(i);
                drawLine(canvas, a, b); // 连接两个点
                a = b; // 把下一个点作为下一次绘制的第一个点
            }
            if (isDraw){// 如果还在绘制状态，那就继续绘制连接线
                drawLine(canvas, a, new Point(mX, mY));
            }
        }
    }

    /**
     * 绘制两点之间的线
     * @param canvas
     * @param a
     * @param b
     */
    private void drawLine(Canvas canvas, Point a, Point b){
        if (a.state == Point.STATE_PRESS){
            canvas.drawLine(a.x, a.y, b.x, b.y, mPressPaint);
        }
        if (a.state == Point.STATE_ERROR){
            canvas.drawLine(a.x, a.y, b.x, b.y, mErrorPaint);
        }
    }
    /**
     * 绘制9个点;
     */
    private void drawPoint(Canvas canvas) {
        for (int i = 0; i < mPoints.length; i++) {
            for (int j = 0; j < mPoints[i].length; j++) {
                Point point = mPoints[i][j];
                switch (point.state) {
                    case Point.STATE_NORMAL:
                        canvas.drawBitmap(mNormalBitmap, point.x - mPorintRadius, point.y - mPorintRadius, mPaint);
                        break;
                    case Point.STATE_PRESS:
                        canvas.drawBitmap(mPressBitmap, point.x - mPorintRadius, point.y - mPorintRadius, mPressPaint);
                        break;
                    case Point.STATE_ERROR:
                        canvas.drawBitmap(mErrorBitmap, point.x - mPorintRadius, point.y - mPorintRadius, mErrorPaint);
                        break;
                }
            }
        }
    }

    /**
     * 获取选择的点的位置
     * 有点消耗内存
     * @return
     */
    private int[] getSelectedPointPosition() {
        Point point = new Point(mX, mY);
        for (int i = 0; i < mPoints.length; i++) {
            for (int j = 0; j < mPoints[i].length; j++) {
                //判断触摸的点和遍历的当前点的距离是否小于这个点的半径
                if (mPoints[i][j].getInstance(point) < mPorintRadius) {
                    int[] result = new int[2];
                    result[0] = i;
                    result[1] = j;
                    return result;
                }
            }
        }
        return null;
    }

    /*1.用户触屏滑动
        1.设置一个判断域,判断当前用户接触部位是否为包含在给域中
            -->1.在,判断该接触点位于该域的那个部分(划分为9个小域)
            ---->然后在通过该域的数值与用户接触域进行对比:
            ---->a:切,b:包含;
            ---->用户所选择的位置数据提取;
            -->2.不在,不做任何响应;
    */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        mX = event.getX();
        mY = event.getY();
        int[] position;
        int i, j;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                resetPoints();//重置所有点;
                position = getSelectedPointPosition();
                if(position!=null){
                    isDraw=true;
                    i=position[0];
                    j=position[1];
                    mPoints[i][j].state=Point.STATE_PRESS;
                    mSelectedPoints.add(mPoints[i][j]);
                    mPassPositions.add(i*3+j);//把选中的路径转换成一位数组存储起来;
                }
                break;
            case MotionEvent.ACTION_MOVE://用户移动手指时;
                if(isDraw){
                    position=getSelectedPointPosition();
                    if(position!=null){
                        i=position[0];
                        j=position[1];
                        if (!mSelectedPoints.contains(mPoints[i][j]))
                        {
                            mPoints[i][j].state=Point.STATE_PRESS;
                            mSelectedPoints.add(mPoints[i][j]);
                            mPassPositions.add(i*3+j);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                boolean valid=false;
                if(mListener!=null&&isDraw){
                    valid=mListener.onDrawFinished(mPassPositions);
                }
                if(!valid){
                    for(Point  p:mSelectedPoints){
                        p.state=Point.STATE_ERROR;
                    }
                }
                isDraw=false;
                break;
            case MotionEvent.ACTION_CANCEL://取消是什么意思;
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 重置所有的点;
     */
    public void resetPoints(){
        mPassPositions.clear();
        mSelectedPoints.clear();
        for (int i = 0; i < mPoints.length; i++) {
            for (int j = 0; j < mPoints[i].length; j++) {
                mPoints[i][j].state = Point.STATE_NORMAL;
            }
        }
        invalidate();
    }

    /**
     *  控件改变
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init(getContext());
    }


    /**
     *  设置绘制完成监听借口;
     */
    public  interface OnDrawFinishedListener{
        boolean onDrawFinished(List<Integer> passPositions);
    }

    /**
     * @param listener
     */
    public void   setOnDrawFinishedListener(OnDrawFinishedListener  listener){
        this.mListener=listener;
    }
}

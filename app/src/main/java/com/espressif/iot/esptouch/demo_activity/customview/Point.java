package com.espressif.iot.esptouch.demo_activity.customview;

/**
 * Created by Administrator on 2017/8/14.
 * 1.点的位置
 * 2.点的状态
 */

public class Point {
    public static final int STATE_NORMAL = 1;
    public static final int STATE_PRESS = 2;
    public static final int STATE_ERROR = 3;

    float x, y;
    int state = STATE_NORMAL;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * 1.计算两点间的距离;
     * @param a 两点间的距离
     * @return
     */
    public float getInstance(Point a) {
        return (float) Math.sqrt((x - a.x) * (x - a.x) + (y - a.y) * (y - a.y));
    }
}

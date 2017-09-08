package com.espressif.iot.esptouch.util.storage_utils;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot_esptouch_demo.R;

/**
 * Created by Administrator on 2017/8/12.
 * log类
 * 1.使用其打印不同的log日志;
 * 2.可插拔,即关闭和打开需要随心所欲;
 *
 * 如果使用classLoad进行类的加载或者提出
 *  1.另放入一个任务站中,进行数据管理;
 *  2.
 *
 */

public class Logger {

    public static int LOGGER_STATES = 0;
    public static int LOGGER_OPEAN_STATES = 1;
    public static int LOGGER_CLOSE_STATES = 2;
    public static final String tagName="serverQin";

    public static void I(String msg) {
        Log.i(tagName,msg+"");
    }

    /**
     * 自定义Toast;
     * @param context
     */
    public  static   void    customToast(Context  context){
        Toast   mToast=new Toast(context);
        View view=LayoutInflater.from(context).inflate(R.layout.toast_layout,null);
        TextView content= (TextView) view.findViewById(R.id.toast);
        content.setText("没网,请欣赏我的马赛克!");
        mToast.setView(view);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
    }


}

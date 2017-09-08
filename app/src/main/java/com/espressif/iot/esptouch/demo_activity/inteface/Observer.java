package com.espressif.iot.esptouch.demo_activity.inteface;

import com.espressif.iot.esptouch.entry.MqttMsg;

/**
 * Created by Administrator on 2017/8/26.
 * 观察者
 */

public interface Observer {
    //观察数据修改;
    public  void  upData2State(MqttMsg msg);
    public  void  upSlimeData(String mac,int data);
}

package com.espressif.iot.esptouch.demo_activity.customview;

import com.espressif.iot.esptouch.demo_activity.inteface.Observer;
import com.espressif.iot.esptouch.entry.MqttMsg;
import com.espressif.iot.esptouch.serveice.BrocastServiceListener;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Administrator on 2017/8/26.
 * 观察者
 */

public class Observable {
    private List<Observer> list=new ArrayList<>();

    //状态改变通知数据;
    public  void  setChange(MqttMsg msg){
        notifyObserver(msg);
    }

    /**
     * 通知所有的类当前设备已经修改;
     * 根据类型来发送给设备;
     */
    public  void  notifyObserver(MqttMsg  msg){
        for(Observer ob:list){
            ob.upData2State(msg);
        }
    }

    /**
     * 添加数据;
     * @param ob
     */
    public  void   addObserver(Observer ob){
        list.add(ob);
    }

}

package com.espressif.iot.esptouch.demo_activity.customview;

/**
 *  设备产品的类型型号
 *  设备产品特性
 *
 */
public interface  FacilityState {
    /**控制器*/
    public  int CONTROLLER_MOFANG = 1;
    /**气体报警器*/
    public int GAS_ALARM = 2;
    /**湿度传感器*/
    public int HUMIDITY_SENSOR = 3;
    /**手持式设备*/
    public int HAND_HELD_TERMINAL=4;

    public  enum   FacilityWorkState{
        //正常工作
        NORMAL,//正常工作
        STOP,//停止工作
        REPAIRS//报修损坏
    }
}

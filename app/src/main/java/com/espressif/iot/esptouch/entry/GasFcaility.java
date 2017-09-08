package com.espressif.iot.esptouch.entry;

/**
 * Created by Administrator on 2017/8/29.
 */

public class GasFcaility extends  FacilityInfo {
    /**设备损坏*/
    public  static  final  int     MALFUNCTION_STATE=-1;
    /**黄色警戒值*/
    public  static  final  double     YELLOW_WARNING_STATE=400;
    /**红色警戒值*/
    public  static  final  double     RED_WARNING_STATE=500;
    /**超级警戒值*/
    public  static  final   double     SUPER_WARNING_STATE=1000;

    private  double  overFlowValue;//气体实时溢出值
    private  int  isWorkState;//当前设备的从设备工作状态
    //从设备的
    private   int   type;

    public int getIsWorkState() {
        return isWorkState;
    }

    public void setIsWorkState(int isWorkState) {
        this.isWorkState = isWorkState;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getOverFlowValue() {
        return overFlowValue;
    }

    public void setOverFlowValue(double overFlowValue) {
        this.overFlowValue = overFlowValue;
    }
}

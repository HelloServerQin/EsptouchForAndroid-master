package com.espressif.iot.esptouch.entry;

/**
 * 控制器设备
*/
public  class ControlFcaility extends  FacilityInfo {

    //定时启动功能
    private  int  workHours;
    private  String  alertColck;
    //是否添加到组播种
    private  int isMulti;

    public int getIsMulti() {
        return isMulti;
    }

    public void setIsMulti(int isMulti) {
        this.isMulti = isMulti;
    }

    public int getFacilitystate() {
        return facilitystate;
    }

    public void setFacilitystate(int facilitystate) {
        this.facilitystate = facilitystate;
    }

    //运行状态
    private  int  facilitystate;

    public int getWorkHours() {
        return workHours;
    }

    public void setWorkHours(int workHours) {
        this.workHours = workHours;
    }

    public String getAlertColck() {
        return alertColck;
    }

    public void setAlertColck(String alertColck) {
        this.alertColck = alertColck;
    }
}

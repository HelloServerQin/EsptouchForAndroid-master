package com.espressif.iot.esptouch.demo_activity.customview;

/**
 * 控制器
 * 1.类型
 * 2.是否开启
 * 3.是否在线;
 * 4.是否有值;
 */
public class FacilityControl {
    private String  mFacilityName;//设备名称
    private String mFacilityMac;//mac地址
    private int mTypeState;//设备类型
    private boolean isOpen=true;//是否打开
    private boolean isOnLine=true;//是否在线
    private float values=0;//值;

    public String getmFacilityName() {
        return mFacilityName;
    }

    public void setmFacilityName(String mFacilityName) {
        this.mFacilityName = mFacilityName;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isOnLine() {
        return isOnLine;
    }

    public void setOnLine(boolean onLine) {
        isOnLine = onLine;
    }

    public int getmTypeState() {
        return mTypeState;
    }

    public void setmTypeState(int mTypeState) {
        this.mTypeState = mTypeState;
    }

    public float getValues() {
        return values;
    }

    public void setValues(float values) {
        this.values = values;
    }
}

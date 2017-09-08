package com.espressif.iot.esptouch.entry;

/**
 * 数据信息类
 *  所有设备信息类
 */

public abstract class FacilityInfo {

    private String name;//设备名称
    private String mac;//mac地址
    private int  Type;
    private int  savleFacitily;

    public int getSavleFacitily() {
        return savleFacitily;
    }

    public void setSavleFacitily(int savleFacitily) {
        this.savleFacitily = savleFacitily;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}

package com.espressif.iot.esptouch.entry;

import com.espressif.iot.esptouch.serveice.MqttClitentUtils;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by Administrator on 2017/9/3.
 */

public class MqttMsg {
    private String alias;//别名;
    private String mac;//mac地址;
    private String msg; //信息类;
    private double gasData;//气体数据;
    private int mType;
    private int offLine;
    private int facilityState;
    private int salveState;
    private MqttClitentUtils  mMqttUtil;
    private String confrim;

    public String getConfrim() {
        return confrim;
    }

    public void setConfrim(String confrim) {
        this.confrim = confrim;
    }

    public MqttMsg(MqttClitentUtils mMqttUtil) {
        this.mMqttUtil = mMqttUtil;
    }

    public int getFacilityState() {
        return facilityState;
    }

    public void setFacilityState(int facilityState) {
        this.facilityState = facilityState;
    }

    public int getSalveState() {
        return salveState;
    }

    public void setSalveState(int salveState) {
        this.salveState = salveState;
    }

    public int getOffLine() {
        return offLine;
    }

    public void setOffLine(int offLine) {
        this.offLine = offLine;
    }

    public int getmType() {
        return mType;
    }

    public void setmType(int mType) {
        this.mType = mType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public double getGasData() {
        return gasData;
    }

    public void setGasData(double gasData) {
        this.gasData = gasData;
    }

    /**
     * 更新订阅
     */
    public void updateSub() {
//            updateSubscribe();
    }

    /**
     * 更新订阅
     */
    public void updateSub(String topic) {
        if (mMqttUtil.isConnectMqtt()) {
            try {
                mMqttUtil.subscribe(topic);
                mMqttUtil.publish("state", topic);
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新订阅
     */
    public void updateSub(List<String> topics) {
        if (mMqttUtil.isConnectMqtt()) {
            try {
                mMqttUtil.subscribe(topics);
                mMqttUtil.publish("state", topics);
            } catch (MqttException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

}

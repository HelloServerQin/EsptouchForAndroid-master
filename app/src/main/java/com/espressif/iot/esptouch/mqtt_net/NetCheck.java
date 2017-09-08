package com.espressif.iot.esptouch.mqtt_net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2017/8/21.
 * 1.监测当前网络是否可用;
 * 2.启用一个广播实时监听当前手机网络的变动;
 * 3.低耦合高内聚;
 * 4.一个网络状态:wifi/GPRS/无网络,三种状态;
 * 5.通过监测到的网络返回当前移动手机的网络状态;
 *
 */

public class NetCheck {
    //当前网络的状态;
    public   static  final int  CURR_WIFI =1;
    public   static  final  int  CURR_GPRS_WAP=2;
    public   static  final  int  CURR_GPRS_NET=3;
    public    static  final  int  NOT_NET=-1;

    //监测网络当前的状态;
    public  static  int   isNetState(Context context){
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo==null)
        {
            return netType;
        }
        int nType = networkInfo.getType();
        if(nType==ConnectivityManager.TYPE_MOBILE)
        {
            if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet"))
            {
                netType = 3;
            }
            else
            {
                netType = 2;
            }
        }
        else if(nType==ConnectivityManager.TYPE_WIFI)
        {
            netType = 1;
        }
        return netType;
    }

    //判断当前网络是否可用
    public static  boolean  isMobileConnected(Context context){
        if(context!=null){
            ConnectivityManager   cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo   netInfo=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (netInfo!=null)
            return netInfo.isAvailable();
        }
        return  false;
    }

    //WIFI链接是否有效;
    public static boolean isWifiConnected(Context  context){
        if(context!=null){
            ConnectivityManager  cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo   nwif=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(nwif!=null){
                return nwif.isAvailable();
            }
        }
        return false;
    }

    //判断当前是否有网络接入;
    public   static  boolean   isNetWokeConnect(Context  context){
        ConnectivityManager   cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo  nf=cm.getActiveNetworkInfo();
        if(nf!=null){
            return nf.isAvailable();//判断当前是否有网络连接;
        }
        return  false;
    }

}

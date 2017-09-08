package com.espressif.iot.esptouch.brocastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.espressif.iot.esptouch.demo_activity.KeepLiveActivity;
import com.espressif.iot.esptouch.mqtt_net.NetCheck;
import com.espressif.iot.esptouch.serveice.BrocastServiceListener;
import com.espressif.iot.esptouch.serveice.MqttThread;
import com.espressif.iot.esptouch.util.storage_utils.Logger;

/**
 * 显示/隐式广播
 * 1.开机启动服务
 * 2.重启启动服务
 * 3.保活机制
 * 4.网络的断开,连接等来启动服务
 */

public class DMReceiver extends BroadcastReceiver {
    private Context  mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext=context;
        String  action=intent.getAction();
        switch(action){
            case Intent.ACTION_BOOT_COMPLETED:
                Logger.I("重启成功");
                startService();//启动服务;
                break;
            case Intent.ACTION_SCREEN_OFF://屏幕关闭时
                Logger.I("屏幕关闭");
                //屏幕关闭启动一个activity保活当前service

                break;
            case Intent.ACTION_SCREEN_ON://屏幕开启时,
                Logger.I("屏幕开启");
            case Intent.ACTION_USER_PRESENT://用户解开锁屏时
                Logger.I("解锁成功");
                //关闭当前activity

                startService();
                break;
            case Intent.ACTION_POWER_CONNECTED://用户插入电源时
                Logger.I("插入电源广播");
                startService();
                break;
            default://监听当前网络
                 if(NetCheck.isNetWokeConnect(context)){//当前网开启启动服务;
                    startService();
                    context.sendBroadcast(new Intent("com.shheqing.network"));//当网络可用时;
                 }else{
                     MqttThread.close();
                     context.stopService(new Intent(context,BrocastServiceListener.class));
                 }
        }
    }

    private void  startService(){
        mContext.startService(new Intent(mContext, BrocastServiceListener.class));//启动服务;
    }
}

package com.espressif.iot.esptouch.demo_activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.esptouch.mqtt_net.NetCheck;
import com.espressif.iot.esptouch.serveice.BrocastServiceListener;
import com.espressif.iot.esptouch.util.storage_utils.FileUtils;
import com.espressif.iot_esptouch_demo.R;

import net.youmi.android.AdManager;

public class SplashActivity extends Activity {

    private Button login;
    private TextView config, register;
    private EditText user_emial, passwd;
    private SharedPreferences sp;
    private boolean open = true;

    //当前网络出现问题时通知用户当前网络的状态;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                startActivity(new Intent(SplashActivity.this, MenuTestActivity.class));
                finish();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdManager.getInstance(this).init("ec220d3c3b7aaf9e", "f53e07fcee89f879", true);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(NetCheck.isNetWokeConnect(this)){
        startService(new Intent(this, BrocastServiceListener.class));//启动服务;
        }

        if (FileUtils.setStorage(this).getBoolean(FileUtils.FRIST_LOGIN, false)) {//判断当前是否为首次登录
            //用户引导页;
            initView();


            FileUtils.storageData(this).putBoolean(FileUtils.FRIST_LOGIN,true).commit();
            handler.sendEmptyMessage(0);//进入主界面

        } else {
            //闪屏页面
            setContentView(R.layout.splash_screen_activity);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (open)
                        handler.sendEmptyMessage(0);
                }
            }, 1500);
        }
        //启动一个广播监听并检测当前网络是可用,并提醒用户操作;
        IntentFilter intent = new IntentFilter();
        intent.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    /**
     * 初始化引导页面
     * 图片过大使用Bitmap类来操作当前界面;
     * 注意内存溢出问题;
     * 使用GiF和静态图
     */
    private  void   initView(){

    }

    public void onDump(View v) {
        open = false;
        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

package com.espressif.iot.esptouch.demo_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.espressif.iot.esptouch.serveice.BrocastServiceListener;

/**
 *  保活当前设备的服务端;
 */

public class KeepLiveActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        //设置当前屏幕的位置和布局参数;
        Window window=getWindow();
        window.setGravity(Gravity.LEFT|Gravity.TOP);
        WindowManager.LayoutParams  params=window.getAttributes();
        params.x=0;
        params.y=0;
        params.width=1;
        params.height=1;
        window.setAttributes(params);
        startService(new Intent(this, BrocastServiceListener.class));
        super.onCreate(savedInstanceState, persistentState);
        Log.i("serverQin_KeepLive","启动一个activity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("serverQin_KeepLive","我死了");
    }
}

package com.espressif.iot.esptouch.demo_activity;

import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.esptouch.db.SQLiteUtils;
import com.espressif.iot.esptouch.demo_activity.inteface.Observer;
import com.espressif.iot.esptouch.entry.FacilityInfo;
import com.espressif.iot.esptouch.entry.MqttMsg;
import com.espressif.iot.esptouch.serveice.BrocastServiceListener;
import com.espressif.iot.esptouch.serveice.MqttClitentUtils;
import com.espressif.iot.esptouch.serveice.MqttThread;
import com.espressif.iot.esptouch.util.storage_utils.FileUtils;
import com.espressif.iot.esptouch.util.storage_utils.Logger;
import com.espressif.iot_esptouch_demo.R;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 控制器
 * 1.获取当前设备的工作状态和mac地址;
 * 2.发布用户操作状态;
 */
public class ControlFacility extends Activity implements Observer {
    private MyServiceConn conn;
    private BrocastServiceListener serviceListener;
    private TextView title;
    private List<Long> list;
    private CheckBox contorl;
    private List<FacilityInfo> fList;
    private String mac;
    private String name;
    private EditText workHourse;
    private ImageView selectPop;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent mIntent = new Intent("com.shheqing.notify");
            String  []  str=new  String[]{mac,msg.what+""};
            mIntent.putExtra("brocast",str);
            sendBroadcast(mIntent);
            if (msg.what == 0) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setAttributes(new WindowManager.LayoutParams(WindowManager.LayoutParams.FLAG_FULLSCREEN));
        setContentView(R.layout.activity_control_facility);
        //获取当前设备的mac地址和设备状态
        mac = getIntent().getStringExtra(FileUtils.EQUIPMENT_MAC);
        checkString(mac);
        initView();
    }

    private void initView() {
        contorl = (CheckBox) findViewById(R.id.rb_control);
        title = (TextView) findViewById(R.id.title);
        workHourse = (EditText) findViewById(R.id.workhouse);//工作时长
        selectPop = (ImageView) findViewById(R.id.nextLiner);
        conn = new MyServiceConn();
        Intent intent = new Intent(this, BrocastServiceListener.class);
        intent.setAction("activity");
        bindService(intent, conn, BIND_AUTO_CREATE);
        contorl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (name == name && name.isEmpty()) name = mac;
                //防止用户恶意点击()
                if (!isChecked) {
                    contorl.setChecked(false);
                    title.setText(name + "设备已经关闭");
                    workHourse.setEnabled(true);
                    workHourse.setClickable(true);
                    workHourse.setFocusable(true);
                    workHourse.setFocusableInTouchMode(true);
                    workHourse.requestFocus();
                    selectPop.setImageResource(R.drawable.icon_03);
                    selectPop.setEnabled(true);

                    try {
                        MqttClitentUtils.getMqttConn().publish("close", mac + "-A");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                } else {
                    contorl.setChecked(true);
                    title.setText(name + "设备已经打开");
                    workHourse.setEnabled(false);
                    workHourse.setClickable(false);
                    workHourse.setFocusable(false);
                    selectPop.setEnabled(false);
                    selectPop.setImageResource(R.drawable.passwd);
                    try {
                        MqttClitentUtils.getMqttConn().publish("open", mac + "-A");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 添加自己到
     */
    class MyServiceConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceListener = ((BrocastServiceListener.MyIBinder) service).getBService();
            serviceListener.addObserver(ControlFacility.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceListener = null;
        }
    }


    int i = 0;

    /**
     * 当用户发送当前设备
     *
     * @param msg
     */
    @Override
    public void upData2State(MqttMsg msg) {
        Logger.I("Hello contorl");
        if (msg == null) {
            return;
        }

        if (msg.getSalveState() > 0 && msg.getFacilityState() > 0) {//开启当前设备;
            contorl.setClickable(true);
            contorl.setEnabled(true);
            contorl.setChecked(true);
            i = 1;
        } else if (msg.getSalveState() <= 0 && msg.getFacilityState() > 0) {
            contorl.setClickable(true);
            contorl.setEnabled(true);
            contorl.setChecked(false);
            i = -1;
        } else if (msg.getFacilityState() <= 0) {
            Toast.makeText(ControlFacility.this, "当前设备已经离线", Toast.LENGTH_SHORT).show();
            contorl.setClickable(false);
            contorl.setEnabled(false);
            contorl.setChecked(false);
            i = 0;
        }
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                Message mMessage = handler.obtainMessage();
                mMessage.what = i;
                handler.sendMessage(mMessage);
            }
        }, 1500);
    }

    @Override
    public void upSlimeData(String mac, int data) {
    }

    public void checkString(final String mac) {
        new Thread() {
            @Override
            public void run() {
                fList = SQLiteUtils.getSQLiteUtils(ControlFacility.this).getAllFacility(
                        SQLiteUtils.getSQLiteUtils(ControlFacility.this).queryAllData(SQLiteUtils.CONTROLLER_TABLE));
                if (fList != null) {
                    for (FacilityInfo info : fList) {
                        if (info.getMac().equals(mac)) {
                            name = info.getName();
                        }
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null)
            unbindService(conn);
        list = null;
    }
}

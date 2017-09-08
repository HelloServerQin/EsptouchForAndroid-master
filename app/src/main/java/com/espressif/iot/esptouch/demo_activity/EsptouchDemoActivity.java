package com.espressif.iot.esptouch.demo_activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.esptouch.EsptouchTask;
import com.espressif.iot.esptouch.IEsptouchListener;
import com.espressif.iot.esptouch.IEsptouchResult;
import com.espressif.iot.esptouch.IEsptouchTask;
import com.espressif.iot.esptouch.db.SQLiteUtils;
import com.espressif.iot.esptouch.mqtt_net.NetCheck;
import com.espressif.iot.esptouch.task.__IEsptouchTask;
import com.espressif.iot.esptouch.util.storage_utils.FileUtils;
import com.espressif.iot.esptouch.util.storage_utils.Logger;
import com.espressif.iot_esptouch_demo.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class EsptouchDemoActivity extends Activity implements OnClickListener {

    private static final String TAG = "EsptouchDemoActivity";

    private TextView mTvApSsid;

    private EditText mEdtApPassword;

    private Button mBtnConfirm;

    private EspWifiAdminSimple mWifiAdmin;

    private Spinner mSpinnerTaskCount;
    private HashSet<String> set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
        super.onCreate(savedInstanceState);
        //测试模拟添加数据
        initSet();
        testData();
        //测试模拟添加数据
        setContentView(R.layout.esptouch_demo_activity);
        checkNetUser();
        mWifiAdmin = new EspWifiAdminSimple(this);//wifi管理类
        mTvApSsid = (TextView) findViewById(R.id.tvApSssidConnected);
        mEdtApPassword = (EditText) findViewById(R.id.edtApPassword);
        mBtnConfirm = (Button) findViewById(R.id.btnConfirm);
        mBtnConfirm.setOnClickListener(this);
        initSpinner();//初始化spinner
    }

    /**
     * 提醒用户当前设备网络状况;
     */
    private void checkNetUser() {
        String mContent = "";
        switch (NetCheck.isNetState(this)) {
            case NetCheck.NOT_NET:
                mContent = "当前网络不可用,请开启wifi网络";
                break;
            case NetCheck.CURR_GPRS_NET:
            case NetCheck.CURR_GPRS_WAP:
                mContent = "移动网络不可用于,添加设备网络";
                break;
            case NetCheck.CURR_WIFI:
                mContent = "";
                break;
        }
        if (!mContent.isEmpty()) {
            Toast.makeText(EsptouchDemoActivity.this, mContent, Toast.LENGTH_LONG).show();
        }
    }

    private void initSpinner() {
        mSpinnerTaskCount = (Spinner) findViewById(R.id.spinnerTaskResultCount);
        int[] spinnerItemsInt = getResources().getIntArray(R.array.taskResultCount);//获取资源数据;12345
        int length = spinnerItemsInt.length;
        Integer[] spinnerItemsInteger = new Integer[length];
        for (int i = 0; i < length; i++) {
            spinnerItemsInteger[i] = spinnerItemsInt[i];
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
                android.R.layout.simple_list_item_1, spinnerItemsInteger);
        mSpinnerTaskCount.setAdapter(adapter);
        mSpinnerTaskCount.setSelection(1);//默认选着1;
    }

    @Override
    protected void onResume() {//生命周期
        super.onResume();
        // display the connected ap's ssid
        String apSsid = mWifiAdmin.getWifiConnectedSsid();//获取ssid
        if (apSsid != null) {
            mTvApSsid.setText(apSsid);
        } else {
            mTvApSsid.setText("");
        }
        // check whether the wifi is connected
        boolean isApSsidEmpty = TextUtils.isEmpty(apSsid);
        mBtnConfirm.setEnabled(!isApSsidEmpty);
    }

    @Override
    public void onClick(View v) {

        if (v == mBtnConfirm) {
            String apSsid = mTvApSsid.getText().toString();//获取ssid;
            String apPassword = mEdtApPassword.getText().toString();//密码
            String apBssid = mWifiAdmin.getWifiConnectedBssid();//bssid;
            String taskResultCountStr = Integer.toString(mSpinnerTaskCount
                    .getSelectedItemPosition());//获取当前用户启动的任务数量;
            if (__IEsptouchTask.DEBUG) {//logo日志;
                Log.d(TAG, "mBtnConfirm is clicked, mEdtApSsid = " + apSsid
                        + ", " + " mEdtApPassword = " + apPassword);
            }
            //启动异步数据;
            new EsptouchAsyncTask3().execute(apSsid, apBssid, apPassword, taskResultCountStr);

        }
    }

    private class EsptouchAsyncTask2 extends AsyncTask<String, Void, IEsptouchResult> {

        private ProgressDialog mProgressDialog;

        private IEsptouchTask mEsptouchTask;
        // without the lock, if the user tap confirm and cancel quickly enough,
        // the bug will arise. the reason is follows:
        // 0. task is starting created, but not finished
        // 1. the task is cancel for the task hasn't been created, it do nothing
        // 2. task is created
        // 3. Oops, the task should be cancelled, but it is running
        private final Object mLock = new Object();

        @Override
        protected void onPreExecute() {
            mProgressDialog = new ProgressDialog(EsptouchDemoActivity.this);
            mProgressDialog
                    .setMessage("Esptouch is configuring, please wait for a moment...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    synchronized (mLock) {
                        if (__IEsptouchTask.DEBUG) {
                            Log.i(TAG, "progress dialog is canceled");
                        }
                        if (mEsptouchTask != null) {
                            mEsptouchTask.interrupt();
                        }
                    }
                }
            });
            mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    "Waiting...", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            mProgressDialog.show();
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(false);
        }

        @Override
        protected IEsptouchResult doInBackground(String... params) {
            synchronized (mLock) {
                String apSsid = params[0];
                String apBssid = params[1];
                String apPassword = params[2];
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, EsptouchDemoActivity.this);
            }
            IEsptouchResult result = mEsptouchTask.executeForResult();
            return result;
        }

        @Override
        protected void onPostExecute(IEsptouchResult result) {
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(true);
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                    "Confirm");
            // it is unnecessary at the moment, add here just to show how to use isCancelled()
            if (!result.isCancelled()) {
                if (result.isSuc()) {
                    mProgressDialog.setMessage("Esptouch success, bssid = "
                            + result.getBssid() + ",InetAddress = "
                            + result.getInetAddress().getHostAddress());
                } else {
                    mProgressDialog.setMessage("Esptouch fail");
                }
            }
        }
    }

    private void onEsptoucResultAddedPerform(final IEsptouchResult result) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String text = result.getBssid() + " is connected to the wifi";
                Toast.makeText(EsptouchDemoActivity.this, text,
                        Toast.LENGTH_LONG).show();
            }

        });
    }

    //监听类
    private IEsptouchListener myListener = new IEsptouchListener() {

        @Override
        public void onEsptouchResultAdded(final IEsptouchResult result) {
            onEsptoucResultAddedPerform(result);
        }
    };

    private class EsptouchAsyncTask3 extends AsyncTask<String, Void, List<IEsptouchResult>> {

        private ProgressDialog mProgressDialog;

        private IEsptouchTask mEsptouchTask;
        private final Object mLock = new Object();// 锁

        @Override
        protected void onPreExecute() {//ui线程中的第一个执行方法;

            mProgressDialog = new ProgressDialog(EsptouchDemoActivity.this);
            mProgressDialog
                    .setMessage("网络正在配置中, 请稍等...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {//用户点击取消按钮时
                    synchronized (mLock) {
                        if (__IEsptouchTask.DEBUG) {
                            Log.i(TAG, "progress dialog is canceled");
                        }
                        if (mEsptouchTask != null) {//任务站不为null时
                            mEsptouchTask.interrupt();//中断任务
                        }
                    }
                }
            });
            mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    "请稍等...", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
            mProgressDialog.show();
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(false);//设置当前按钮不可单击
        }

        @Override
        protected List<IEsptouchResult> doInBackground(String... params) {//异步任务;
            int taskResultCount = -1;//启动的任务站结果数
            synchronized (mLock) {
                // !!!NOTICE
                String apSsid = mWifiAdmin.getWifiConnectedSsidAscii(params[0]);
                String apBssid = params[1];
                String apPassword = params[2];
                String taskResultCountStr = params[3];//启动的任务数
                taskResultCount = Integer.parseInt(taskResultCountStr);
                mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, EsptouchDemoActivity.this);
                mEsptouchTask.setEsptouchListener(myListener);
            }
            List<IEsptouchResult> resultList = mEsptouchTask.executeForResults(taskResultCount);
            return resultList;
        }

        @Override
        protected void onPostExecute(List<IEsptouchResult> result) {//在此获取当前链接配置的设备BSSID;
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(true);
            mProgressDialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(
                    "确定");
            IEsptouchResult firstResult = result.get(0);
            if (!firstResult.isCancelled()) {
                int count = 0;
                final int maxDisplayCount = 5;
                if (firstResult.isSuc()) {
                    initSet();//初始化数据
                    for (IEsptouchResult resultInList : result) {
                        set.add(resultInList.getBssid());//添加数据
                        count++;
                        if (count >= maxDisplayCount) {
                            break;
                        }
                    }
                    FileUtils.storageData(EsptouchDemoActivity.this).putStringSet(FileUtils.EQUIPMENT_MAC, set).commit();//提交
                    mProgressDialog.setMessage("设备添加已完成...");
                    sendBroadcast(new Intent("com.shheqing.monitorData"));//发送一个广播

                    //做一个时间控制器当
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            EsptouchDemoActivity.this.finish();
                        }
                    }, 2000);

                } else {
                    mProgressDialog.setMessage("设备配置失败,请稍后再试...");
                }
            }
        }
    }

    /**
     * 初始化数据set集合用于存储所有数据;
     */
    private void initSet() {
//        set = (HashSet<String>) FileUtils.setStorage(EsptouchDemoActivity.this).getStringSet(FileUtils.EQUIPMENT_MAC, null);
//        if (FileUtils.setStorage(EsptouchDemoActivity.this).getStringSet(FileUtils.EQUIPMENT_MAC, null) == null) {//判断sp中得是为空数据;
        set = new HashSet<String>();//为空创建当前数据;
        Logger.I(set.size() + "");
//        } else {
//            set = (HashSet<String>) FileUtils.setStorage(EsptouchDemoActivity.this).getStringSet(FileUtils.EQUIPMENT_MAC, null);//不为空获取当前的set集合;
//        }
    }

    private void testData() {
        set.add("asdfhjjakldadfga");
        FileUtils.storageData(this).putStringSet(FileUtils.EQUIPMENT_MAC, set).commit();//提交
        Logger.I(set.size() + "");
        sendBroadcast(new Intent("com.shheqing.monitorData"));//发送一个广播
        //做一个时间控制器当
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EsptouchDemoActivity.this.finish();
            }
        }, 2000);
    }
}

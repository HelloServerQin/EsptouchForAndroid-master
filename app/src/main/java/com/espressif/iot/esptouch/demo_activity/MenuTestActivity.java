package com.espressif.iot.esptouch.demo_activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.espressif.iot.esptouch.db.SQLiteUtils;
import com.espressif.iot.esptouch.demo_activity.inteface.Observer;
import com.espressif.iot.esptouch.entry.ControlFcaility;
import com.espressif.iot.esptouch.entry.FacilityInfo;
import com.espressif.iot.esptouch.entry.MqttMsg;
import com.espressif.iot.esptouch.mqtt_net.NetCheck;
import com.espressif.iot.esptouch.serveice.BrocastServiceListener;
import com.espressif.iot.esptouch.serveice.BrocastServiceListener.MyIBinder;
import com.espressif.iot.esptouch.serveice.MqttClitentUtils;
import com.espressif.iot.esptouch.util.storage_utils.FileUtils;
import com.espressif.iot.esptouch.util.storage_utils.Logger;
import com.espressif.iot_esptouch_demo.R;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 测试类使用通信技术;
 */
public class MenuTestActivity extends Activity implements Observer {
    private List<FacilityInfo> list;
    private LinearLayout _container;
    private ListView listView;
    private MyAdapter adpter;
    private BrocastReceiverMonitorData brmd;
    private ImageButton setData;
    private MyServiceConn conn;
    private BrocastServiceListener bservice;
    private String bindState;
    private Set<String> mSet;
    private PopupWindow window;
    //判断当前设备
    private static final int ALL_STATE = 1;
    private static final int SIMPLE_STATE = 2;
    int[] onlin = new int[]{R.drawable.main_menu_03, R.drawable.main_menu_06, R.drawable.main_menu_08};
    int[] offlin = new int[]{R.drawable.menu_main_03, R.drawable.menu_main_06, R.drawable.menu_main_08};
    private SQLiteUtils mSqlite;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0X123:
                    AlertDialog.Builder _builder = new AlertDialog.Builder(MenuTestActivity.this);
                    _builder.setTitle("尚未发现可用配置")
                            .setMessage("您当前尚未对任何设备进行网络数据配置,是否开启配置数据?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(MenuTestActivity.this, EsptouchDemoActivity.class));
                                }
                            })
                            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                }
                            }).show();
                    break;
                case 0x321:
                    notifyListView();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setAttributes(new WindowManager.LayoutParams(WindowManager.LayoutParams.FLAG_FULLSCREEN));
        setContentView(R.layout.main_menu);

        initView();
        monitorData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        bindState = "activity";
        sendBind();
        int net = NetCheck.isNetState(this);
        if (net == NetCheck.NOT_NET) {
            Logger.customToast(this);
        }
    }

    private void initView() {
        if (mSqlite == null) {
            mSqlite = SQLiteUtils.getSQLiteUtils(this);
        }
        listView = (ListView) findViewById(R.id.slimelistview);
        setData = (ImageButton) findViewById(R.id.main_back);
        brmd = new BrocastReceiverMonitorData();
//        mSqlite = SQLiteUtils.getSQLiteUtils(this);
        IntentFilter _IFilter = new IntentFilter();
        _IFilter.setPriority(Integer.MAX_VALUE);
        _IFilter.addAction("com.shheqing.monitorData");
        _IFilter.addAction("com.shheqing.network");
        registerReceiver(brmd, _IFilter);
        //弹窗数据
        setData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMyPop(popMenu(), new ColorDrawable(Color.argb(125, 0, 0, 0)), 2);
                window.showAsDropDown(setData, 10, 0);
            }
        });
        //单击进入当前设备;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!NetCheck.isNetWokeConnect(MenuTestActivity.this)) {
                    Toast.makeText(MenuTestActivity.this, "当前网络不可用!", Toast.LENGTH_SHORT).show();
                    return;
                }else  if(!getFacility(list.get(position).getMac())){
                    Toast.makeText(MenuTestActivity.this, "当前设备不在线", Toast.LENGTH_SHORT).show();
                    return ;
                }
                Intent intent = new Intent(MenuTestActivity.this, ControlFacility.class);
                //用户单击进入当前设备;
                intent.putExtra(FileUtils.EQUIPMENT_MAC, list.get(position).getMac());
                startActivity(intent);
            }
        });
        //长安弹出按钮,添加数据;
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                setMyPop(popListView(), new ColorDrawable(Color.WHITE), 1);
                window.showAsDropDown(view, 10, 0);
                return false;
            }
        });
    }

    /**
     * 弹出菜单
     * new ColorDrawable(Color.argb(125, 0, 0, 0))
     *
     * @param ContorView 菜单视图
     * @param scalex     宽度布局的比例:如屏幕的一半为请填写2;
     */
    private void setMyPop(View ContorView, ColorDrawable background, int scalex) {
        window = new PopupWindow();
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density = dm.density;
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        window.setWidth(width / scalex);
        window.setBackgroundDrawable(background);
        window.setHeight(ActionBar.LayoutParams.WRAP_CONTENT);
        window.setOutsideTouchable(true);
        window.setAnimationStyle(R.style.popup_window_anim);
        window.setFocusable(true);
        window.setTouchable(true);
        window.update();
        window.setContentView(ContorView);
    }

    /**
     * @return 返回一个菜单按钮;
     */
    private View popMenu() {
        //弹出一个菜单;
        View contorView = MenuTestActivity.this.getLayoutInflater().inflate(R.layout.pop_list_item, null);
        LinearLayout config = (LinearLayout) contorView.findViewById(R.id.config);
        LinearLayout task = (LinearLayout) contorView.findViewById(R.id.task);
        LinearLayout timer = (LinearLayout) contorView.findViewById(R.id.timer);
        config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//添加设备;
                startActivity(new Intent(MenuTestActivity.this, EsptouchDemoActivity.class));
                if (window.isShowing())
                    window.dismiss();
            }
        });
        task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        return contorView;
    }

    private View popListView() {
        View view = View.inflate(this, R.layout.pop_listview, null);
        ;
        view.findViewById(R.id.edit_facility_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuTestActivity.this, "1", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.delete_facility).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuTestActivity.this, "2", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.clock_facility).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MenuTestActivity.this, "3", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    /**
     * 监听数据的添加使用观察者模式或内部广播机制;更新ListView
     */
    private void monitorData() {
        //启动一个线程查询当前数据
        new Thread() {
            @Override
            public void run() {
                Logger.I("查询当前数据....");
                //在此请求数据状态吗;
                Cursor mCursor = mSqlite.queryAllData(SQLiteUtils.CONTROLLER_TABLE);//查询的表名称;
                list = mSqlite.getAllFacility(mCursor);
                if (list != null) {//list
                    handler.sendEmptyMessage(0x321);
                } else {
                    handler.sendEmptyMessage(0x123);
                    notifyListView();
                }
            }
        }.start();
    }

    private void notifyListView() {
        adpter = new MyAdapter(this, listView, list);
        listView.setAdapter(adpter);
        adpter.notifyDataSetChanged();
        adpter.notifyDataSetInvalidated();
       /* DataSetObserver data=new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
            }
            @Override
            public void onInvalidated() {
                super.onInvalidated();
            }
        };
        adpter.registerDataSetObserver(data);*/
    }

    //需要优化
    class MyAdapter extends BaseAdapter {
        private Context context;
        private ListView listview;
        private List<FacilityInfo> list;

        public MyAdapter(Context context, ListView listView, List<FacilityInfo> list) {
            this.context = context;
            this.listview = listView;
            if (list == null) {
                list = new ArrayList<>();
            }
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();//出现空指针异常list为空;
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ViewHodler hodler = null;
            if (view == null) {
                view = View.inflate(MenuTestActivity.this, R.layout.list_item, null);
                hodler = new ViewHodler(view);
                view.setTag(hodler);
            } else {
                hodler = (ViewHodler) view.getTag();
            }
            //添加工作状态
            Iterator<FacilityInfo> iterator = list.iterator();
            while (iterator.hasNext()) {
                ControlFcaility info = (ControlFcaility) iterator.next();
                hodler.title.setText(info.getName());
                String str = info.getSavleFacitily() > 0 ? "设备已开启" : "设备已关闭";
                hodler.state.setText(str);
                int i = info.getType() == 1 ? 0 : info.getType() == 2 ? 1 : 2;
                if (info.getFacilitystate() > 0) {
                    hodler.image.setImageResource(onlin[i]);
                } else {
                    hodler.state.setText("与亲人失去联络...");
                    hodler.image.setImageResource(offlin[i]);
                }
                info = null;
            }
            return view;
        }

    }

    class ViewHodler {
        public ImageView image;
        public TextView title;
        public TextView state;
        public View mContent;

        public ViewHodler(View view) {
            mContent = view;
            image = (ImageView) mContent.findViewById(R.id.state_icon);
            title = (TextView) mContent.findViewById(R.id.title);
            state = (TextView) mContent.findViewById(R.id.state);
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
    }//用户单击停止时;

    /**
     * 广播销毁;
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        unregisterReceiver(brmd);
    }

    @Override
    public void upData2State(MqttMsg msg) {//当用户发送数据后调试当前数据;
        if (msg.getConfrim() != null && !msg.getConfrim().isEmpty()) {//
            open(msg);
        } else if (msg.getFacilityState() < 0) {//用户关闭当前设备;
            saveleState(msg, "offline");
            notifyListView();
        } else {
            saveleState(msg, "");
            notifyListView();
        }
    }

    @Override
    public void upSlimeData(String mac, int data) {
    }

    private void open(MqttMsg msg) {
        Logger.I("ok-----------------------------------------------------------------" + msg.getConfrim());
        if (list == null) {
            list = mSqlite.getAllFacility(mSqlite.queryAllData(SQLiteUtils.CONTROLLER_TABLE));
        }
        if (msg.getConfrim().equals("NewData")) {//当前数据为新插入的数据;
            monitorData();
        } else {
            Logger.I("ok-----------------------------------------------------------------");
            for (int i = 0; i < list.size(); i++) {
                ControlFcaility cf = (ControlFcaility) list.get(i);
                if (cf.getMac().equals(msg.getMac())) {
                    cf.setSavleFacitily(msg.getConfrim().equals("ok") ? 1 : -1);
                }
                notifyListView();
            }
        }
    }

    private void saveleState(MqttMsg msg, String lab) {
        if (list == null) {
            list = mSqlite.getAllFacility(mSqlite.queryAllData(SQLiteUtils.CONTROLLER_TABLE));
        }
        for (int i = 0; i < list.size(); i++) {
            ControlFcaility cf = (ControlFcaility) list.get(i);
            if (cf.getMac().equals(msg.getMac())) {
                if (lab.equals("offline")) {
                    cf.setFacilitystate(msg.getFacilityState());
                } else {
                    cf.setSavleFacitily(msg.getSalveState());
                }
            }
        }
    }

    /**
     * 连接服务端
     */
    public class MyServiceConn implements ServiceConnection {
        //链接成功后
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bservice = ((MyIBinder) service).getBService();//获取当前的server
            bservice.addObserver(MenuTestActivity.this);//添加自己;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bservice = null;
        }
    }

    /**
     * 发送数据监听;绑定当前设备;
     */
    public void sendBind() {
        //绑定当前设备服务查询当前设备的状态;
        Intent intent = new Intent(this, BrocastServiceListener.class);
        //获取当前设备的状态请求数据
        intent.setAction(bindState);//检测所有的数据
        conn = new MyServiceConn();
        bindService(intent, conn, BIND_AUTO_CREATE);//服务泄漏;
    }

    /**
     * 广播机制
     */
    public class BrocastReceiverMonitorData extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            String action = intent.getAction();

            if (action.equals("com.shheqing.monitorData")) {//用户添加数据完成;
                mSet = FileUtils.setStorage(context).getStringSet(FileUtils.EQUIPMENT_MAC, null);
                if (mSet == null || !(mSet.size() > 0)) {//表示当前set中无数据;
                    Logger.I("未存储");
                    handler.sendEmptyMessage(0x123);
                } else {
                    new Thread() {
                        @Override
                        public void run() {
                            MqttClitentUtils mClient = MqttClitentUtils.getMqttConn();
                            List<String> topics = new ArrayList<String>();
                            for (int i = 0; i < mSet.size(); i++) {
                                topics.add(i, mSet.iterator().next());
                            }
                            Logger.I(topics.size() + "");
                            mSet.clear();
                            mSet = null;
                            FileUtils.storageData(context).putStringSet(FileUtils.EQUIPMENT_MAC, mSet).commit();
                            if (topics.isEmpty()) {//提醒用户当前数据为空;
                                return;
                            } else {
                                try {
                                    mClient.publish("state", mClient.Str2Topics(topics));
                                    mClient.subscribe(mClient.Str3Topics(topics));
                                    Logger.I("完成订阅;");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
                }
            } else if (action.equals("com.shheqing.network")) {//如网络已经关闭或开启通知用户和监听
//                Logger.I("网络已经连接上---->activity");
                if (NetCheck.isNetWokeConnect(MenuTestActivity.this)) {//检测当前网络是否可用;
                    sendBind();
                    monitorData();
                }
            }else if(action.equals("com.shheqing.notify")){
                String[]  state=intent.getStringArrayExtra("brocast");
                getFacility(state[0],Integer.valueOf(state[1]));
                notifyListView();
            }
        }
    }

    public boolean getFacility(String mac) {
        if (list != null) {
            for (FacilityInfo info : list) {
                ControlFcaility conInfo = (ControlFcaility) info;
                if (conInfo.getMac().equals(mac) && conInfo.getFacilitystate() > 0) {
                    return true;
                }
            }
        }
        return false;
    }
    public void getFacility(String mac,int j) {
        if (list != null) {
            for (int i=0;i<list.size();i++) {
                ControlFcaility conInfo = (ControlFcaility)list.get(i);
                if (conInfo.getMac().equals(mac) && conInfo.getFacilitystate() > 0) {
                    if(j==0){
                        ((ControlFcaility) list.get(i)).setFacilitystate(-1);
                    }else{
                        list.get(i).setSavleFacitily(j);
                    }
                }
            }
        }
    }

}

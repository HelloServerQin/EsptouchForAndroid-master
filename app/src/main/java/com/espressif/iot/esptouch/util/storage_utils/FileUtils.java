package com.espressif.iot.esptouch.util.storage_utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.JsonReader;
import android.util.Log;

import com.espressif.iot.esptouch.demo_activity.customview.UserPeople;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2017/8/11.
 * <p>
 * 数据存储在手机中
 * 以文件名为.json格式存储
 * 1.获取应用的目录存储路径
 * 2.书写文件名.json格式的文件
 * 3.存储到手机文件中;
 * 4.检索文件数据
 * <p>
 * 1.写一个Sp
 */

public class FileUtils {

    //存储flag
    public final static String fileName = "heQing";
    public static final String CACHE_NAME = "ShHeqing";
    public static final String USER_NAME = "Email";
    public static final String USER_PASSWD = "passwd";
    public static final String USER_EMAIL = "email";
    public static final String USER_PHONE = "phone";
    public static final String EQUIPMENT_MAC = "mac";
    public static final String EQUIPMENT_IP = "ipAddre";
    public static final String IS_FRIST = "login";
    public static final String OPEN_LOCK = "lock";
    public static final String LOCK_PASSWD = "lockpasswd";
    public static final String LOCK_CONFIG="config_lock";
    public static final String SERVICE_NOTHING="service";//服务是否被杀死;
    public static final String FRIST_LOGIN="frist";//服务是否被杀死;
    public static final String CONTROLLER_FRIST="controller";//控制器
    public static final String GASALERT_FRISET="gas";

    //SEND 发送标志
    public static final  String ALL_STATE="CHECK_UPDATA_ALL";//查询所有的设备状态
    public static final  String ADD_STATE="ADD_STATE";//查询所有的设备状态
    public static final  String SEND_STATE="SEND_STATE";//查询所有的设备状态

    /**
     * 数据存储到手机中;
     * @param context
     * @param userPeople 用户存储类
     */
    public static boolean storageFilee(Context context, UserPeople userPeople) {
        try {
            FileOutputStream file = context.openFileOutput(fileName, context.MODE_PRIVATE);
            if (userPeople != null) {
                String userInfo = "{username:" + userPeople.getName() + ",email:" + userPeople.getEmail() +
                        ",phone:" + userPeople.getPhone() + ",passwd:" + userPeople.getPasswd() + "}";
                file.write(userInfo.getBytes());
                file.close();
                return true;
            }
            file.close();
            return false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
//        return false;
    }

    /**
     * 消息读取类
     *
     * @param context
     * @param fileName
     * @return
     */
    public static UserPeople getStorageData(Context context, String fileName) {
        try {
            UserPeople user = new UserPeople();
            FileInputStream file = context.openFileInput(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(file, "utf-8"));
            JsonReader jr = new JsonReader(br);
            Log.i("serverQin", "" + jr.toString());
            if (jr == null) return null;
            while (jr.hasNext()) {
                String name = jr.nextName();
                if (name.equals("username")) {
                    user.setName(jr.nextString());
                } else if (name.equals("email")) {
                    user.setEmail(jr.nextString());
                } else if (name.equals("phone")) {
                    user.setPhone(jr.nextString());
                } else if (name.equals("passwd")) {
                    user.setPasswd(jr.nextString());
                }
            }
            jr.close();
            br.close();
            return user;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SharedPreferences setStorage(Context context) {
        return context.getSharedPreferences(CACHE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor storageData(Context context) {
        return setStorage(context).edit();
    }

}

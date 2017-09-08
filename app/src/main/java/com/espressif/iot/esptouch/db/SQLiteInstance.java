package com.espressif.iot.esptouch.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 1.数据创建
 */

public class SQLiteInstance extends SQLiteOpenHelper {
    private  Context  mContext;
    //控制器表:别名,设备状态,定时开关,同时操控设备
    private  String   controller="create table controller(_id integer primary key autoincrement," +
            "facilityType integer,alias text,mac text,workHours integer,facilityState integer," +
            "alertClock text,muliteBrocast integer,slaveState integer)";

    public SQLiteInstance(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//创建数据库
        db.execSQL(controller);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {//修改数据库
        if(oldVersion!=newVersion){
            db.delete(SQLiteUtils.CONTROLLER_TABLE,null,null);
            onCreate(db);
        }
    }

}

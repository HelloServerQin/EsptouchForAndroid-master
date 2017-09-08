package com.espressif.iot.esptouch.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.espressif.iot.esptouch.entry.ControlFcaility;
import com.espressif.iot.esptouch.entry.FacilityInfo;
import com.espressif.iot.esptouch.util.storage_utils.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据的增删改查
 * 数据库的表
 * 数据库名称:shheqing
 * 1.提供一个
 */
public class SQLiteUtils {

    private String mSQLname = "shheqing";
    private Context mContext;
    private SQLiteDatabase mSQLiteDatabase;

    public static final String MIAN_TABLE = "facilityInfo";
    public static final String CONTROLLER_TABLE = "controller";
    public static final String TYPE_GAS_TABLE = "typeTable";

    public String alias = "alias";
    public String mac = "mac";

    public String workhours = "workHours";
    public String facilityState = "facilityState";
    public String alertClock = "alertClock";
    public String mulitebrocast = "muliteBrocast";

    public String slavestate = "slaveState";
    public String type = "facilityType";

    private ContentValues mCValues;
    private SQLiteInstance mSql;

    private SQLiteUtils(Context mContext) {
        this.mContext = mContext;
        init();
    }

    /**
     * 初始化当前类;
     */
    private void init() {
        mSql = new SQLiteInstance(mContext, mSQLname, null, 1);
    }

    /**
     * .
     *
     * @param mContext
     * @return
     */
    public static SQLiteUtils getSQLiteUtils(Context mContext) {
        return new SQLiteUtils(mContext);
    }

    /**
     * 添加设备
     */
    public long addData(String tabel, FacilityInfo facility) {
        ContentValues cv = getmCValues(facility);
        if (cv == null) {
            return -1;
        }
        mSQLiteDatabase = mSql.getReadableDatabase();
        long i = mSQLiteDatabase.insert(tabel, null, cv);//插入数据成功
        mSQLiteDatabase.close();
        return i;
    }

    /**
     * 修改数据库数据
     *
     * @param table       表名
     * @param mCValues    键值对表
     * @param whereClause 条件语句
     * @param selectArgs  条件语句值
     */
    public int updateData(String table, ContentValues mCValues, String whereClause, String[] selectArgs) {
        if (mCValues == null) {
            return -1;
        }
        mSQLiteDatabase = mSql.getReadableDatabase();
        int i = mSQLiteDatabase.update(table, mCValues, whereClause, selectArgs);
        mSQLiteDatabase.close();
        return i;
    }

    /**
     * 删除一个数据
     *
     * @param table
     * @param whereClause
     * @param WhereArgs
     * @return
     */
    public int deleteData(String table, String whereClause, String[] WhereArgs) {
        mSQLiteDatabase = mSql.getReadableDatabase();
        int i = mSQLiteDatabase.delete(table, whereClause, WhereArgs);
        mSQLiteDatabase.close();
        return i;
    }

    //查询

    /**
     * 查询所有
     *
     * @param table
     * @return
     */
    public Cursor queryAllData(String table) {
        mSQLiteDatabase = mSql.getReadableDatabase();
        Cursor cursor = mSQLiteDatabase.query(table, null, null, null, null, null, null);
//        mSQLiteDatabase.close();
        return cursor;
    }

    /**
     * 获取所有的数据;
     *
     * @param cursor
     * @return
     */
    public List<FacilityInfo> getAllFacility(Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) return null;
        Logger.I(cursor.getCount() + "---->" + cursor.getColumnCount());
        List<FacilityInfo> mlist = new ArrayList<>();
        while (cursor.moveToNext()) {
            ControlFcaility mFcaility = new ControlFcaility();
            mFcaility.setName(cursor.getString(cursor.getColumnIndex(alias)));//别名
            mFcaility.setMac(cursor.getString(cursor.getColumnIndex(mac)));
            mFcaility.setType(cursor.getInt(cursor.getColumnIndex(type)));
            mFcaility.setWorkHours(cursor.getInt(cursor.getColumnIndex(workhours)));
            mFcaility.setAlertColck(cursor.getString(cursor.getColumnIndex(alertClock)));
            mFcaility.setFacilitystate(cursor.getInt(cursor.getColumnIndex(facilityState)));
            mFcaility.setIsMulti(cursor.getInt(cursor.getColumnIndex(mulitebrocast)));
            mlist.add(mFcaility);
        }
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
        }
        return mlist;
    }

    /**
     * 添加数据
     *
     * @param facility
     * @return
     */
    private ContentValues getmCValues(FacilityInfo facility) {
        if (facility == null) {
            return null;
        }
        mCValues = new ContentValues();
        mCValues.clear();
        ControlFcaility mCFcaility = (ControlFcaility) facility;
        mCValues.put(alias, mCFcaility.getName());
        mCValues.put(mac, mCFcaility.getMac());
        mCValues.put(workhours, mCFcaility.getWorkHours());
        mCValues.put(alertClock, mCFcaility.getAlertColck());
        mCValues.put(facilityState, mCFcaility.getFacilitystate());
        mCValues.put(mulitebrocast, mCFcaility.getIsMulti());
        mCValues.put(type, mCFcaility.getType());//类型
        return mCValues;
    }

    /**
     * 语法
     *
     * @param sqlSyntax
     */
    public void sqlSyntax(String sqlSyntax, Object[] whereArgs) {
        mSQLiteDatabase = mSql.getReadableDatabase();
        if (whereArgs == null || whereArgs.length < 1) {
            mSQLiteDatabase.execSQL(sqlSyntax);
        } else
            mSQLiteDatabase.execSQL(sqlSyntax, whereArgs);
        mSQLiteDatabase.close();
    }

    /*
     * 关闭数据库;
     */
    public void closeSQL() {
        if (mSQLiteDatabase != null) {
            mSQLiteDatabase.close();
        }
    }

}

package com.koudaiv.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import com.koudaiv.db.DbHelper;

import java.util.logging.Handler;

/**
 * Created by Administrator on 14-1-21.
 */
public class BaseDao {

    protected Context mContext;
    protected DbHelper mDbHelper;

    public BaseDao(Context context) {
        this.mContext = context;
        mDbHelper = new DbHelper(mContext,"download.db",null,1);
    }

    protected SQLiteDatabase getWriteableConnection() {
        SQLiteDatabase conn = mDbHelper.getWritableDatabase();
//        if(!conn.isOpen()) {
//            conn = openWriteable();
//        }
        return conn;
    }

    protected SQLiteDatabase getReadableConnection() {
        SQLiteDatabase conn = mDbHelper.getReadableDatabase();
//        if(!conn.isOpen()) {
//            conn = openReadable();
//        }
        return conn;
    }

    protected SQLiteDatabase openWriteable() {
        SQLiteDatabase conn = SQLiteDatabase.openDatabase("/data/data/com.koudaiv/databases/download.db",null,SQLiteDatabase.OPEN_READWRITE);
        return conn;
    }

    protected SQLiteDatabase openReadable() {
        SQLiteDatabase conn = SQLiteDatabase.openDatabase("/data/data/com.koudaiv/databases/download.db",null,SQLiteDatabase.OPEN_READONLY);
        return conn;
    }

}

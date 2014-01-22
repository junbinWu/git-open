package com.koudaiv.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.koudaiv.base.BaseDao;
import com.koudaiv.domain.DownloadInfo;
import com.koudaiv.domain.ModelDownloadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-1-21.
 */
public class DownloadInfoDao extends BaseDao {

    private static DownloadInfoDao instance;

    public DownloadInfoDao(Context context) {
        super(context);
    }

    public static DownloadInfoDao getInstance(Context context) {
        if(instance == null) {
            instance = new DownloadInfoDao(context);
        }
        return instance;
    }

    public synchronized void saveInfos(DownloadInfo info) {
        SQLiteDatabase conn = null;
        try {
            conn = getWriteableConnection();
            Object[] objects = new Object[] {info.getFileSize(),info.getCompletedPos(),
                    info.getUrl(),info.getLocalPath()};
            conn.execSQL("insert into download_info(file_size,completed_pos,url,local_path) values(?,?,?,?)",objects);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }

    public synchronized void deleteInfo(String url) {
        SQLiteDatabase conn = null;
        try {
            conn = mDbHelper.getWritableDatabase();
            conn.execSQL("delete from download_info where url = ?",new String[]{url});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }

    public synchronized void updateInfos(DownloadInfo info) {
        SQLiteDatabase conn = null;
        try {
            conn = getWriteableConnection();
            conn.execSQL("update download_info set completed_pos = ? where file_size = ? and url = ?" ,
                    new Object[]{info.getCompletedPos(),info.getFileSize(),info.getUrl()} );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized DownloadInfo getInfos(String url) {
        SQLiteDatabase conn = null;
        Cursor cursor = null;
        DownloadInfo info = null;
        try {
            conn = getReadableConnection();
            cursor = conn.rawQuery("select * from download_info where url = ?", new String[]{url});
            while(cursor.moveToNext()) {
                long completedPos = cursor.getLong(cursor.getColumnIndex("completed_pos"));
                long fileSize = cursor.getLong(cursor.getColumnIndex("file_size"));
                String localPath = cursor.getString(cursor.getColumnIndex("local_path"));
                info = new DownloadInfo(url,fileSize,completedPos,localPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return info;
    }
}

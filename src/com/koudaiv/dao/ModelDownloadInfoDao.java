package com.koudaiv.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.koudaiv.base.BaseDao;
import com.koudaiv.db.DbHelper;
import com.koudaiv.domain.ModelDownloadInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 14-1-21.
 */
public class ModelDownloadInfoDao extends BaseDao {
    private static ModelDownloadInfoDao instance;

    public ModelDownloadInfoDao(Context context) {
        super(context);
    }

    public static ModelDownloadInfoDao getInstance(Context context) {
        if(instance == null) {
            instance = new ModelDownloadInfoDao(context);
        }
        return instance;
    }

    public synchronized void saveInfos(List<ModelDownloadInfo> infos) {
        SQLiteDatabase conn = null;
        try {
            conn = getWriteableConnection();
            for(ModelDownloadInfo info : infos) {
                Object[] objects = new Object[] {info.getThreadId(),info.getStartPos(),
                        info.getEndPos(),info.getCompletedPos(),info.getUrl()};
                conn.execSQL("insert into model_download_info(thread_id,start_pos, end_pos,completed_pos,url) values(?,?,?,?,?)",objects);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }

    public synchronized void deleteInfos(String url) {
        SQLiteDatabase conn = null;
        try {
            conn = mDbHelper.getWritableDatabase();
            conn.execSQL("delete from model_download_info where url = ?",new String[]{url});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }

    public synchronized void updateInfos(ModelDownloadInfo info) {
        SQLiteDatabase conn = null;
        try {
            conn = getWriteableConnection();
            conn.execSQL("update model_download_info set completed_pos = ? where thread_id = ? and url = ?" ,
                        new Object[]{info.getCompletedPos(),info.getThreadId(),info.getUrl()} );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }

    public synchronized List<ModelDownloadInfo> getInfos(String url) {
        SQLiteDatabase conn = null;
        Cursor cursor = null;
        List<ModelDownloadInfo> list = null;
        try {
            conn = getReadableConnection();
            cursor = conn.rawQuery("select * from model_download_info where url = ?", new String[]{url});
            list = new ArrayList<ModelDownloadInfo>();
            while(cursor.moveToNext()) {
                int threadId = cursor.getInt(cursor.getColumnIndex("thread_id"));
                long startPos = cursor.getLong(cursor.getColumnIndex("start_pos"));
                long endPos = cursor.getLong(cursor.getColumnIndex("end_pos"));
                long completedPos = cursor.getLong(cursor.getColumnIndex("completed_pos"));
                ModelDownloadInfo info = new ModelDownloadInfo(threadId,startPos,endPos,completedPos,url);
                list.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
        return list;
    }
}

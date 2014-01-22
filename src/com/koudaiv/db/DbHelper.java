package com.koudaiv.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 14-1-21.
 */
public class DbHelper extends SQLiteOpenHelper {

    public static final String CREATE_TABLE_DOWNLOAD_INFO =
                                    "create table download_info ( " +
                                    "id integer primary key autoincrement ,file_size integer, "+
                                    "url varchar,completed_pos integer, local_path varchar " +
                                    ")";

    public static final String CREATE_TABLE_MODEL_DOWNLOAD_INFO =
                                    "create table model_download_info ( " +
                                    "id integer primary key autoincrement ,thread_id integer, "+
                                    "start_pos integer,end_pos integer, " +
                                    "url varchar,completed_pos integer " +
                                    ")";

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DOWNLOAD_INFO);
        db.execSQL(CREATE_TABLE_MODEL_DOWNLOAD_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

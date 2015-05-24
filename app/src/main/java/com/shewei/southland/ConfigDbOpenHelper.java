package com.shewei.southland;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by qliang on 2015/5/10.
 */
public class ConfigDbOpenHelper extends SQLiteOpenHelper {
    private static final int CONFIG_DB_VERSION = 1;

    ConfigDbOpenHelper(Context context) {
        super(context, "Config.db", null, CONFIG_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String sql_projects = "CREATE TABLE [projects] ( " +
                "[_id] INTEGER PRIMARY KEY, " +
                "[project_name] TEXT, " +
                "[owners_db] TEXT, " +
                "[parcel_map] TEXT, " +
                "[creation_time] TEXT );";

//        final String sql_recent_projects = "CREATE TABLE [recent_projects] ( " +
//                "[_id] INTEGER PRIMARY KEY, " +
//                "[project_id] INTEGER, " +
//                "[access_time] TEXT );";
        db.execSQL(sql_projects);
//        db.execSQL(sql_recent_projects);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

package com.aquarius.emoticonmemory.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.aquarius.emoticonmemory.core.DatabaseContext;

/**
 * Created by aquarius on 2017/6/23.
 */
public class GameDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "GameDbHelper";


    private static final int DATABASE_VERSION = 2;


    private static final String CREATE_USER_TABLE_SQL = "CREATE TABLE " + DBContract.TABLE_PLAYER_NAME +
            "( "+
            DBContract.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.NAME + " TEXT NOT NULL DEFAULT 'No Name', " +
            DBContract.SCORE + " INTEGER NOT NULL DEFAULT 0 ," +
            DBContract.ELAPSEDTIME + " TEXT NOT NULL DEFAULT ''" +
            " )";


    private static final String CREATE_HISTORY_LOG_TABLE_SQL = "CREATE TABLE " + DBContract.TABLE_HISTORY_NAME+
            "(" +
            DBContract.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            DBContract.PLAYER_NAME + " TEXT NOT NULL, " +
            DBContract.TIMESTAMP + " TEXT NOT NULL " +     // 记录完成游戏的系统时间
            ")";

    // insert -- > new
    // delete/update --> old
    private static final String CREATE_HISTORY_LOG_TRIGGER = "CREATE TRIGGER " + DBContract.TRIGGER_NAME +
            " AFTER INSERT ON "
            + DBContract.TABLE_PLAYER_NAME  + " " +
            "BEGIN " +
            "INSERT INTO " + DBContract.TABLE_HISTORY_NAME + " (" +DBContract.PLAYER_NAME +"," +
            DBContract.TIMESTAMP +") VALUES (" + " new."+ DBContract.NAME + ", " +
            "datetime('now', 'localtime')); " +
            "END;";

    /**
     *
     * @param saveDbInternal if true , save the db file in internal storage, if not , save the db file in
     *                       application cache dir on sdcard. See DatabaseContext
     *                       best way is saving the db file in internal storage
     */
    public GameDbHelper(Context context, boolean saveDbInternal) {
        this(saveDbInternal ? context : new DatabaseContext(context), DBContract.DATABASE_NAME, null, DATABASE_VERSION);
    }


    public GameDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE_SQL);
        upgradeToVersion2(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade from " + oldVersion + " to " + newVersion);
        if (oldVersion == 1) {
            upgradeToVersion2(db);
            oldVersion = 2;
        }
    }

    private void upgradeToVersion2(SQLiteDatabase db) {
        db.execSQL(CREATE_HISTORY_LOG_TABLE_SQL);
        db.execSQL(CREATE_HISTORY_LOG_TRIGGER);
    }
}

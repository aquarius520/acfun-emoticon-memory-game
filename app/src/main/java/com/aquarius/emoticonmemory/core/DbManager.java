package com.aquarius.emoticonmemory.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.view.TextureView;

import com.aquarius.emoticonmemory.database.DBContract;
import com.aquarius.emoticonmemory.database.GameDbHelper;
import com.aquarius.emoticonmemory.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aquarius on 2017/6/23.
 */
public class DbManager {

    private DbManager() {}

    public static SQLiteDatabase getDataBaseInstance(Context context) {
        GameDbHelper dbHelper = new GameDbHelper(context, true);
        return dbHelper.getReadableDatabase();
    }

    public static List<Player> queryPlayerHistoryInfos(Context context) {
        SQLiteDatabase db = getDataBaseInstance(context);
        Cursor cursor = db.query(DBContract.TABLE_PLAYER_NAME, null, null, null, null, null, null);
        try {
            if (cursor != null) {
                List<Player> list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(DBContract.PLAYER_NAME));
                    int score = cursor.getInt(cursor.getColumnIndex(DBContract.SCORE));
                    String timestamp = cursor.getString(cursor.getColumnIndex(DBContract.TIMESTAMP));
                    list.add(new Player(name, score, timestamp));
                }
                return list;
            }
        } catch (Exception e) {
            return  null;
        } finally {
          if(cursor != null) cursor.close();
          if(db != null) db.close();
        }
        return null;
    }

    public static void insertResult(Context context, String name, int score, String elapsedTime) {
        ContentValues cv = new ContentValues();
        if(!TextUtils.isEmpty(name)) {
            cv.put(DBContract.NAME, name);
        }
        cv.put(DBContract.SCORE, score);
        cv.put(DBContract.ELAPSEDTIME, elapsedTime);

        insertPlayerInfo(context, cv);
    }

    public static boolean insertPlayerInfo(Context context, ContentValues contentValues) {
        SQLiteDatabase db = getDataBaseInstance(context);
        long count = 0;
        if (db != null) {
            count = db.insert(DBContract.TABLE_PLAYER_NAME, null, contentValues);
            db.close();
        }
        return count > 0;
    }

    public static boolean insertPlayerInfo(Context context, ContentValues... cvList) {
        SQLiteDatabase db = getDataBaseInstance(context);
        long count = 0;
        if (db != null) {
            db.beginTransaction();
            for(ContentValues value : cvList) {
                count = db.insert(DBContract.TABLE_PLAYER_NAME, null, value);
            }
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }
        return count > 0;
    }

    public static boolean deletePlayerInfo(Context context, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getDataBaseInstance(context);
        int count = 0;
        if (db != null) {
            count = db.delete(DBContract.TABLE_PLAYER_NAME, whereClause, whereArgs);
            db.close();
        }
        return count > 0;
    }
}

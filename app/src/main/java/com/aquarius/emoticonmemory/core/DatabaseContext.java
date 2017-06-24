package com.aquarius.emoticonmemory.core;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by aquarius on 2017/6/20.
 *
 * 为方便调试，将数据库文件保存在 /sdcard/Android/data/packageName/files/databases/ 下
 */
public class DatabaseContext extends ContextWrapper {

    private static final String TAG = "DatabaseContext";
    private Context mContext;

    public DatabaseContext(Context context){
        super(context);
        mContext = context;

    }


    @Override
    public File getDatabasePath(String name) {

        //判断是否存在sd卡
        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
        if(!sdExist){
            return null;
        }else{

            // String rootDir= android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
            File appCacheFile = mContext.getExternalFilesDir(null);
            if (!appCacheFile.exists()) {
                appCacheFile.mkdirs();
            }

            String rootDir = appCacheFile.getPath() + File.separator + "databases";//数据库所在目录
            String dbPath = rootDir + File.separator +name;//数据库路径


            //判断目录是否存在，不存在则创建该目录
            File dirFile = new File(rootDir);
            if(!dirFile.exists()){
                dirFile.mkdirs();
            }

            //数据库文件是否创建成功
            boolean isFileCreateSuccess = false;
            //判断文件是否存在，不存在则创建该文件
            File dbFile = new File(dbPath);
            if(!dbFile.exists()){
                try {
                    isFileCreateSuccess = dbFile.createNewFile();//创建文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                isFileCreateSuccess = true;
            }

            //返回数据库文件对象
            if(isFileCreateSuccess){
                return dbFile;
            }else{
                return null;
            }
        }
    }

    /**
     * 重载这个方法，是用来打开SD卡上的数据库的，android 2.3及以下会调用这个方法。
     *
     * @param    name
     * @param    mode
     * @param    factory
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        SQLiteDatabase result = null;
        try{
            result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getPath(), factory);
        } catch (Exception e) {
            Log.e(TAG, "create the db failed," + e.getMessage());
        }
        return result;
    }

    /**
     * Android 4.0会调用此方法获取数据库。
     *
     * @see ContextWrapper#openOrCreateDatabase(String, int,
     *              SQLiteDatabase.CursorFactory,
     *              DatabaseErrorHandler)
     * @param    name
     * @param    mode
     * @param    factory
     * @param     errorHandler
     */
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase result = null;
        try {
            result = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name).getPath(), factory, errorHandler);
        } catch (Exception e) {
            Log.e(TAG, "create the db failed," + e.getMessage());
        }
        return result;
    }
}
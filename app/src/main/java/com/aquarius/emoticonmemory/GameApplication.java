package com.aquarius.emoticonmemory;

import android.app.Application;
import android.content.Context;

import com.aquarius.emoticonmemory.helper.ImageResourceHelper;

/**
 * Created by aquarius on 2017/6/23.
 */
public class GameApplication extends Application {

    private static GameApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static GameApplication getInstance() {
        return mInstance;
    }


    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}

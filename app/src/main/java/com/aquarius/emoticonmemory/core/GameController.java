package com.aquarius.emoticonmemory.core;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import com.aquarius.emoticonmemory.R;
import com.aquarius.emoticonmemory.helper.AnimationHelper;
import com.aquarius.emoticonmemory.helper.ImageResourceHelper;
import com.aquarius.emoticonmemory.view.GameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aquarius on 2017/6/23.
 */
public class GameController {

    public static AnimationHelper mAnimHelper;

    public static void initAnimationHelper(GameLayout target) {
        mAnimHelper = new AnimationHelper(target);
    }

    // 从选定的要显示的分组中选出八个
    // 为了显示的统一性和逻辑的简单性，暂时我们只允许一个为true
    public static List<Integer> getShowResIdSeeds(boolean hadColour, boolean hadMagic, boolean hadNormal, boolean hadOverWatch) {
        ArrayList<Integer> resultList = new ArrayList<>();
        ArrayList<Integer> seedList = null;
        if (hadColour && ImageResourceHelper.mEmoticonColourList.size() !=0 ) {
            Collections.shuffle(ImageResourceHelper.mEmoticonColourList);
            seedList = new ArrayList<>(ImageResourceHelper.mEmoticonColourList.subList(0, 8));
        }

        if (hadMagic && ImageResourceHelper.mEmoticonMagicList.size() !=0 ) {
            Collections.shuffle(ImageResourceHelper.mEmoticonMagicList);
            seedList = new ArrayList<>(ImageResourceHelper.mEmoticonMagicList.subList(0, 8));
        }

        if (hadNormal && ImageResourceHelper.mEmoticonNormalList.size() !=0 ) {
            Collections.shuffle(ImageResourceHelper.mEmoticonNormalList);
            seedList = new ArrayList<>(ImageResourceHelper.mEmoticonNormalList.subList(0, 8));
        }

        if (hadOverWatch && ImageResourceHelper.mEmoticonOverWatchList.size() !=0) {
            Collections.shuffle(ImageResourceHelper.mEmoticonOverWatchList);
            seedList = new ArrayList<>(ImageResourceHelper.mEmoticonOverWatchList.subList(0, 8));

        }

        // 选出用于显示的8个图片，翻倍组成16个
        if (seedList != null && seedList.size() != 0) {
            resultList.addAll(seedList);
            resultList.addAll(seedList);
            Collections.shuffle(resultList);
        }
        return resultList;
    }

    public static boolean isSuccess(List<Integer> resultList) {
        if(resultList != null) {
            for (Integer value : resultList) {
                if (value != -1) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private void sortLists() {
        Collections.sort(ImageResourceHelper.mEmoticonColourList);
        Collections.sort(ImageResourceHelper.mEmoticonMagicList);
        Collections.sort(ImageResourceHelper.mEmoticonNormalList);
        Collections.sort(ImageResourceHelper.mEmoticonOverWatchList);
    }

    public static void playScoreVoice(Context context) {
        MediaPlayer player = MediaPlayer.create(context, R.raw.acquire_score);
        player.start();
        //player.release();
    }
}

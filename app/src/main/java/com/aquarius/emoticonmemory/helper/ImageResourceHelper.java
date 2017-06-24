package com.aquarius.emoticonmemory.helper;

import com.aquarius.emoticonmemory.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by aquarius on 2017/6/23.
 */
public class ImageResourceHelper {

    public static List<Integer> mEmoticonColourList = new ArrayList<>();

    public static List<Integer> mEmoticonNormalList = new ArrayList<>();

    public static List<Integer> mEmoticonMagicList = new ArrayList<>();

    public static List<Integer> mEmoticonOverWatchList = new ArrayList<>();


    public static void init() {
        fillImageResIdToList();
    }

    public static void clear() {
        mEmoticonNormalList.clear();
        mEmoticonColourList.clear();
        mEmoticonMagicList.clear();
        mEmoticonOverWatchList.clear();
    }

    private static void fillImageResIdToList() {
        try {
            Field[] resIds = R.mipmap.class.getDeclaredFields();
            for (Field field : resIds) {
                if (field.getName().startsWith("cai")) {
                    mEmoticonColourList.add(field.getInt(R.mipmap.class));
                    continue;
                }

                if (field.getName().startsWith("normal")) {
                    mEmoticonNormalList.add(field.getInt(R.mipmap.class));
                    continue;
                }

                if (field.getName().startsWith("magic")) {
                    mEmoticonMagicList.add(field.getInt(R.mipmap.class));
                    continue;
                }

                if (field.getName().startsWith("overwatch")) {
                    mEmoticonOverWatchList.add(field.getInt(R.mipmap.class));
                    continue;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package com.aquarius.emoticonmemory.helper;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.view.View;

import com.aquarius.emoticonmemory.GameApplication;
import com.aquarius.emoticonmemory.R;
import com.aquarius.emoticonmemory.view.GameLayout;

/**
 * Created by aquarius on 2017/6/24.
 *
 * see https://github.com/SpikeKing/wcl-flip-anim-demo
 */
public class AnimationHelper {

    public static AnimatorSet mRightOutSet; // 右出动画
    public static AnimatorSet mLeftInSet; // 左入动画

    public static AnimatorSet mRightOutSetTemp; // 右出动画
    public static AnimatorSet mLeftInSetTemp; // 左入动画

    private static boolean isAnimationEnded = true;     // 点击翻转动画
    private static boolean isrecoveryAnimationEnded = true;     // 不匹配恢复动画
    private GameLayout mTarget;

    public AnimationHelper(GameLayout target) {
        this.mTarget = target;
    }

    static {
        mRightOutSet = (AnimatorSet) AnimatorInflater.loadAnimator(GameApplication.getInstance(), R.animator.anim_out);
        mLeftInSet = (AnimatorSet) AnimatorInflater.loadAnimator(GameApplication.getInstance(), R.animator.anim_in);

        mRightOutSetTemp = (AnimatorSet) AnimatorInflater.loadAnimator(GameApplication.getInstance(), R.animator.anim_out);
        mLeftInSetTemp = (AnimatorSet) AnimatorInflater.loadAnimator(GameApplication.getInstance(), R.animator.anim_in);
    }

    public void addAnimatorListener(Context context, final View front, final View back) {

        // 设置点击事件
        mRightOutSet.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                // 设置动画中点击事件不可用
                front.setClickable(false);
                back.setClickable(false);
                isAnimationEnded = false;
            }
        });
        mLeftInSet.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                front.setClickable(true);
                back.setClickable(true);
                isAnimationEnded = true;
                mTarget.checkCard();
            }
        });
    }

    public void setAnimatorListener(Context context, final View front, final View back) {

        // 设置点击事件
        mRightOutSetTemp.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                // 设置动画中点击事件不可用
                front.setClickable(false);
                back.setClickable(false);
                isrecoveryAnimationEnded = false;
            }
        });
        mLeftInSetTemp.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                front.setClickable(true);
                back.setClickable(true);
                isrecoveryAnimationEnded = true;
            }
        });
    }

    // 翻转卡片
    public static void flipCard(View front, View back/*, boolean isShowBack*/) {
//        // 正面朝上
//        if (isShowBack) {
        mRightOutSet.setTarget(front);
        mLeftInSet.setTarget(back);
        mRightOutSet.start();
        mLeftInSet.start();
//        } else { // 背面朝上
//            mRightOutSet.setTarget(front);
//            mLeftInSet.setTarget(back);
//            mRightOutSet.start();
//            mLeftInSet.start();
//        }
    }

    public static void flipCardToNormal(View front, View back) {
        mRightOutSetTemp.setTarget(front);
        mLeftInSetTemp.setTarget(back);
        mRightOutSetTemp.start();
        mLeftInSetTemp.start();
    }


    public static boolean isAnimationEnded() {
        return isAnimationEnded && isrecoveryAnimationEnded;
    }
}

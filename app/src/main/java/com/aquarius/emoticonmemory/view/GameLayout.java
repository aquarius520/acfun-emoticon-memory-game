package com.aquarius.emoticonmemory.view;

/**
 * Created by aquarius on 2017/6/23.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aquarius.emoticonmemory.R;
import com.aquarius.emoticonmemory.core.GameController;
import com.aquarius.emoticonmemory.core.ResultActionListener;
import com.aquarius.emoticonmemory.helper.AnimationHelper;
import com.aquarius.emoticonmemory.helper.ScreenUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by aquarius on 2017/6/5.
 */
public class GameLayout extends RelativeLayout implements View.OnClickListener{

    private int mItemWidth;     // 每个小块的宽度 宽高一致
    private int mScreenWidth;
    private int mScreenHeight;
    private int mGamePanelWidth;

    private int mTouchSlop;     // 最小认为滑动距离

    private int mColumn = 4;
    private int mInnerMargin;   // 图片块之间的间距 dp值


    private List<RoundImageView> mGameFrontItems;
    private List<RoundImageView> mGameBackItems;
    private List<Integer> mSeedResIds;
    private List<Integer> mFlipInfoList;  // 维护卡片的翻转信息 0 正面， 1 反面
    private Context mContext;
    private ResultActionListener mListener;

    public GameLayout(Context context) {
        this(context, null, 0);
    }

    public GameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mInnerMargin = ScreenUtil.dp2px(6, context);
        mScreenWidth = ScreenUtil.getScreenWidth(context);
        mScreenHeight = ScreenUtil.getScreenHeight(context);
        mGamePanelWidth = Math.min(mScreenWidth, mScreenHeight);
        mItemWidth = (mScreenWidth - getPaddingLeft() - getPaddingRight() - mInnerMargin * (mColumn - 1)) / mColumn;
        initFlipList();
    }

    private void initFlipList() {
        mFlipInfoList = new ArrayList<>(mColumn * mColumn);
        for (int i = 0; i < mColumn * mColumn; i++) {
            mFlipInfoList.add(0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mGamePanelWidth, mGamePanelWidth);
    }

    public void fillGameBitmaps(Context context, boolean[] selected) {
        if (mSeedResIds == null) {
            mSeedResIds = GameController.getShowResIdSeeds(selected[0],selected[1], selected[2], selected[3]);
        }
    }

    private void initGameBackItemViews(Context context) {
        mGameBackItems = new ArrayList<RoundImageView>(mColumn * mColumn);
        for (int i = 0; i < mSeedResIds.size(); i++) {
            RoundImageView item = new RoundImageView(context);
            item.setOnClickListener(this);
            item.setImageResource(mSeedResIds.get(i));
            item.setId(i + mColumn * mColumn + 1);    // 设置view的id
            item.setTag(i);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);

            // 不是第一列
            if (i % mColumn != 0) {
                params.leftMargin = mInnerMargin;
                params.addRule(RelativeLayout.RIGHT_OF, mGameBackItems.get(i-1).getId());
            }

            // 不是第一行
            if(i / mColumn > 0) {
                params.topMargin = mInnerMargin;
                params.addRule(RelativeLayout.BELOW, mGameBackItems.get(i - mColumn).getId());
            }
            mGameBackItems.add(item);

            addView(item, params);
        }
    }

    private void initGameFrontItemViews(Context context) {
        mGameFrontItems = new ArrayList<RoundImageView>(mColumn * mColumn);
        for (int i = 0; i < mSeedResIds.size(); i++) {
            RoundImageView item = new RoundImageView(context);
            item.setOnClickListener(this);
            item.setImageResource(R.mipmap.mark);
            item.setId(i+1);    // 设置view的id
            item.setTag(i);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);

            // 不是第一列
            if (i % mColumn != 0) {
                params.leftMargin = mInnerMargin;
                params.addRule(RelativeLayout.RIGHT_OF, mGameFrontItems.get(i-1).getId());
            }

            // 不是第一行
            if(i / mColumn > 0) {
                params.topMargin = mInnerMargin;
                params.addRule(RelativeLayout.BELOW, mGameFrontItems.get(i - mColumn).getId());
            }
            mGameFrontItems.add(item);

            addView(item, params);
        }
    }

    private int mFirstClickIndex =  -1;
    private int mSecondClickIndex = -1;
    private int mClickCount = 0;

    @Override
    public void onClick(View v) {

        if (!AnimationHelper.isAnimationEnded()) {
            return;
        }

        int index = (int) v.getTag();

        if (mFlipInfoList.get(index) == -1) {
            // -1 表示有相同卡片出现，此卡片已经消失
            return;
        }

        if (mClickCount % 2 == 0) {
            mFirstClickIndex = index;
        }

        if (mClickCount % 2 == 1) {
            mSecondClickIndex = index;
        }

        mClickCount++;

        View front = mGameFrontItems.get(index);
        View back = mGameBackItems.get(index);

        boolean showBack = mFlipInfoList.get(index) == 0;
        GameController.mAnimHelper.addAnimatorListener(mContext, front, back);
        AnimationHelper.flipCard(showBack ? front : back, showBack ? back : front);
        reCalculateFlipList(index);
    }

    public void checkCard() {

        if (mClickCount % 2 != 0) {
            return;
        }

//        postDelayed(new Runnable() {
//            @Override
//            public void run() {
                if (mFirstClickIndex != -1 && mSecondClickIndex != -1 && mFirstClickIndex != mSecondClickIndex) {
                    // 翻到了两张卡片，进行校验
                    if (mSeedResIds.get(mFirstClickIndex) == mSeedResIds.get(mSecondClickIndex)) {
                        GameController.playScoreVoice(mContext);
                        makeCardDisappear(mFirstClickIndex, mSecondClickIndex);
                        if (GameController.isSuccess(mFlipInfoList)) {
                            mListener.whenGameSucceed();
                        }
                    } else {
                        View firstFront = mGameFrontItems.get(mFirstClickIndex);
                        View firstBack = mGameBackItems.get(mFirstClickIndex);
                        AnimationHelper.flipCard(firstBack, firstFront);

                        View secondFront = mGameFrontItems.get(mSecondClickIndex);
                        View secondBack = mGameBackItems.get(mSecondClickIndex);
                        GameController.mAnimHelper.setAnimatorListener(mContext, secondFront, secondBack);
                        AnimationHelper.flipCardToNormal( secondBack, secondFront);
                        reCalculateFlipList(mFirstClickIndex);
                        reCalculateFlipList(mSecondClickIndex);

                    }
                    mFirstClickIndex = -1;
                    mSecondClickIndex = -1;
                }
//            }
//        }, 150);

    }

    private void reCalculateFlipList(int index) {
        if (mFlipInfoList != null) {
            if (mFlipInfoList.get(index) == 0) {
                mFlipInfoList.set(index, 1);
            } else if (mFlipInfoList.get(index) == 1) {
                mFlipInfoList.set(index, 0);
            }
        }
    }


    private void makeCardDisappear(int firstIndex, int secondIndex) {
        mGameFrontItems.get(firstIndex).setVisibility(View.INVISIBLE);
        mGameBackItems.get(firstIndex).setVisibility(View.INVISIBLE);
        mGameFrontItems.get(secondIndex).setVisibility(View.INVISIBLE);
        mGameBackItems.get(secondIndex).setVisibility(View.INVISIBLE);
        mFlipInfoList.set(firstIndex, -1);
        mFlipInfoList.set(secondIndex, -1);
    }

    public void resetGameAllInfo() {
        removeGameItemViews();
        initFlipList();
        initGameBackItemViews(mContext);
        initGameFrontItemViews(mContext);
    }

    // 移除之前背景图添加到父控件上的view
    private void removeGameItemViews() {
        if (mGameBackItems != null) {
            for (ImageView view : mGameBackItems) {
                removeView(view);
            }
            mGameBackItems.clear();
        }
        if (mGameFrontItems != null) {
            for (ImageView view : mGameFrontItems) {
                removeView(view);
            }
            mGameFrontItems.clear();
        }
    }


    public void setResultActionListener(ResultActionListener listener) {
        mListener = listener;
    }
}

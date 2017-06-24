package com.aquarius.emoticonmemory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.FrameStats;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aquarius.emoticonmemory.core.DbManager;
import com.aquarius.emoticonmemory.core.GameController;
import com.aquarius.emoticonmemory.core.ResultActionListener;
import com.aquarius.emoticonmemory.helper.ImageResourceHelper;
import com.aquarius.emoticonmemory.helper.ScreenUtil;
import com.aquarius.emoticonmemory.view.GameLayout;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements ResultActionListener{

    private static final int MSG_NO_CHOICE_CATEGORY = 100;
    private static final int GAME_SPEND_TIME_MSG = 200;

    private boolean mSelectNormalRes = false;
    private boolean mSelectMagicRes = false;
    private boolean mSelectColourRes = false;
    private boolean mSelectOverWatchRes = false;

    private boolean isAlreadyStarted;   // 游戏是否开始 为了计时
    private boolean isGameSucceed;

    private TextView mElapsedTimeTv;
    private GameLayout mGameLayout;

    private ScheduledExecutorService executorService;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == MSG_NO_CHOICE_CATEGORY) {
                showSelectCategoryDialog(MainActivity.this, false);
            }

            if (msg.what == GAME_SPEND_TIME_MSG) {
                mElapsedTimeTv.setText("elapsed time: " + msg.obj);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mElapsedTimeTv = (TextView) findViewById(R.id.elapsed_time_tv);
        mGameLayout = (GameLayout) findViewById(R.id.game_layout);


        // DbManager.getDataBaseInstance(this);

        GameController.initAnimationHelper(mGameLayout);
        ImageResourceHelper.init();
        showSelectCategoryDialog(this, false);
        mGameLayout.setResultActionListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(isAlreadyStarted) {
            calculateGameTime();
        }
    }


    private void calculateGameTime() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(new CalculateTimeTask(), 0, 1000, TimeUnit.MILLISECONDS);
        }
    }

    private String mSpendTime;
    private int mSecond;
    private int mMinute;

    private class CalculateTimeTask implements Runnable{

        @Override
        public void run() {
            if (isGameSucceed) {
                handleTimeAfterGameEnd();
                return;
            }
            mSecond++;
            if (mSecond == 60) {
                mMinute++;
                mSecond = 0;
            }

            mSpendTime = addZeroIfNeed(mMinute) + ":" + addZeroIfNeed(mSecond);
            mHandler.obtainMessage(GAME_SPEND_TIME_MSG, mSpendTime).sendToTarget();
        }
    }

    private String addZeroIfNeed(int value) {
        if((value+"").length() == 1){
            return "0" + String.valueOf(value);
        }
        return String.valueOf(value);
    }

    private void handleTimeAfterGameEnd() {
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageResourceHelper.clear();
    }


    // 弹框显示图片类别供选择
    private void showSelectCategoryDialog(Context context, boolean reStart) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.select_game_pic_category_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(reStart ? "再来一局?" : "选择游戏图片类别");
        builder.setCancelable(false);
        builder.setView(contentView);
        builder.setPositiveButton("开始游戏", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!mSelectNormalRes && !mSelectMagicRes && !mSelectOverWatchRes && !mSelectColourRes) {
                    Toast toast = Toast.makeText(MainActivity.this, "你还没有选择类别！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP, 0, ScreenUtil.getScreenHeight(MainActivity.this) / 8);
                    toast.show();
                    mHandler.obtainMessage(MSG_NO_CHOICE_CATEGORY).sendToTarget();
                } else {
                    mGameLayout.fillGameBitmaps(MainActivity.this, new boolean[]{mSelectColourRes,
                            mSelectMagicRes, mSelectNormalRes, mSelectOverWatchRes});
                    mGameLayout.resetGameAllInfo();
                    calculateGameTime();
                    isAlreadyStarted = true;
                }
            }
        });
        builder.create();

        final ImageView normalIv = (ImageView) contentView.findViewById(R.id.normal_type);
        final ImageView magicIv = (ImageView) contentView.findViewById(R.id.magic_type);
        final ImageView colourIv = (ImageView) contentView.findViewById(R.id.colour_type);
        final ImageView ovGameIv = (ImageView) contentView.findViewById(R.id.overwatch_type);

        normalIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichColorFiltered(normalIv, magicIv, colourIv, ovGameIv);
                mSelectNormalRes = true;
                whichSelected(true, mSelectMagicRes, mSelectColourRes, mSelectOverWatchRes);
            }
        });

        magicIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichColorFiltered(magicIv, normalIv, colourIv, ovGameIv);
                mSelectMagicRes = true;
                // whichSelected(true, mSelectNormalRes, mSelectColourRes, mSelectOverWatchRes);
                mSelectNormalRes = mSelectColourRes = mSelectOverWatchRes = false;
            }
        });

        colourIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichColorFiltered(colourIv, magicIv, normalIv, ovGameIv);
                mSelectColourRes = true;
                // whichSelected(true, mSelectNormalRes, mSelectMagicRes, mSelectOverWatchRes);
                mSelectNormalRes = mSelectMagicRes = mSelectOverWatchRes = false;
            }
        });

        ovGameIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichColorFiltered(ovGameIv, colourIv, magicIv, normalIv);
                mSelectOverWatchRes = true;
                // whichSelected(true, mSelectNormalRes, mSelectMagicRes, mSelectColourRes);
                mSelectNormalRes = mSelectMagicRes = mSelectColourRes = false;
            }
        });

        builder.show();
    }

    // bad practice
    private void whichSelected(boolean first, boolean second, boolean third, boolean forth) {
        if(first) {
            second = false;
            third = false;
            forth = false;
            return;
        }
    }

    private void whichColorFiltered(ImageView first, ImageView second, ImageView third, ImageView forth) {
        first.setColorFilter(Color.parseColor("#990000FF"));
        second.setColorFilter(null);
        third.setColorFilter(null);
        forth.setColorFilter(null);
    }

    @Override
    public void whenGameSucceed() {
        isGameSucceed = true;
        showGameSucceedDialog();

    }

    @Override
    public void whenGameFailed() {
    }

    private void showGameSucceedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View contentView = LayoutInflater.from(this).inflate(R.layout.player_name_layout, null);
        builder.setTitle("Success");
        builder.setMessage("Please input your name: ");
        builder.setView(contentView);
        builder.setCancelable(false);

        final EditText playNameTv = (EditText) contentView.findViewById(R.id.player_name_tv);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DbManager.insertResult(MainActivity.this, playNameTv.getText().toString(), 8 , mSpendTime);
                reStartGame(MainActivity.this);
            }
        });
        builder.create().show();
    }

    private void reStartGame(Context context) {
        mSpendTime = "00:00";
        mSecond = 0;
        mMinute = 0;
        isGameSucceed = false;
        isAlreadyStarted = false;
        showSelectCategoryDialog(context, true);
    }

}

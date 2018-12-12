package cn.yuntk.radio.ibook.executor;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;


import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.util.NetworkUtils;

/**
 * Created by hzwangchenyan on 2017/1/20.
 */
public abstract class PlayMusic implements IExecutor<String> {
    private Context mActivity;
    private int mTotalStep;
    protected int mCounter = 0;

    public PlayMusic(Context activity, int totalStep) {
        mActivity = activity;
        mTotalStep = totalStep;
    }

    @Override
    public void execute() {
//        checkNetwork();
        getPlayInfoWrapper();
    }

    private void checkNetwork() {
        boolean mobileNetworkPlay = NetworkUtils.isAvailable(mActivity);
        if (NetworkUtils.isActiveNetworkMobile(mActivity) && !mobileNetworkPlay) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(R.string.tips);
            builder.setMessage(R.string.play_tips);
            builder.setPositiveButton(R.string.play_tips_sure, (dialog, which) -> {
//                    Preferences.saveMobileNetworkPlay(true);
                getPlayInfoWrapper();
            });
            builder.setNegativeButton(R.string.cancel, null);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            getPlayInfoWrapper();
        }
    }

    private void getPlayInfoWrapper() {
        onPrepare();
        getPlayInfo();
    }

    protected abstract void getPlayInfo();
/*如果存在下载歌词 或者封面 可以使用以下计步器*/
//    protected void checkCounter() {
//        mCounter++;
//        if (mCounter == mTotalStep) {
//            onExecuteSuccess(music);
//        }
//    }

}
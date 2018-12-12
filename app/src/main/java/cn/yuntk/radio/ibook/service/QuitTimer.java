package cn.yuntk.radio.ibook.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hzwangchenyan on 2017/8/8.
 */
public class QuitTimer {

    private Context context;
    private Handler handler;
    private long timerRemain;
    private List<OnTimerListener> listeners = new ArrayList<OnTimerListener>();

    public interface OnTimerListener {
        /**
         * 更新定时停止播放时间
         */
        void onTimer(long remain);
    }

    public static QuitTimer get() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static final QuitTimer sInstance = new QuitTimer();
    }

    private QuitTimer() {
    }

    public void init(Context context) {
        this.context = context.getApplicationContext();
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void addOnTimerListener(OnTimerListener listener) {
        if (listener!=null&&!listeners.contains(listener)){
            listeners.add(listener);
        }
    }

    public void removeOnTimerListener(OnTimerListener listener) {
        if (listeners.size()!=0&&listeners.contains(listener)){
            listeners.remove(listener);
        }
    }

    public void start(long milli) {
        stop();
        if (milli > 0) {
            timerRemain = milli + DateUtils.SECOND_IN_MILLIS;
            handler.post(mQuitRunnable);
        } else {
            timerRemain = 0;
            if (listeners != null&&listeners.size() !=0) {
                for (OnTimerListener listener:listeners){
                    listener.onTimer(timerRemain);
                }
            }
        }
    }

    public void stop() {
        handler.removeCallbacks(mQuitRunnable);
    }

    private Runnable mQuitRunnable = new Runnable() {
        @Override
        public void run() {
            timerRemain -= DateUtils.SECOND_IN_MILLIS;
            if (timerRemain > 0) {
                if (listeners != null&&listeners.size() !=0) {
                    for (OnTimerListener listener:listeners){
                        listener.onTimer(timerRemain);
                    }
                }
                handler.postDelayed(this, DateUtils.SECOND_IN_MILLIS);
            } else {
                AppCache.get().clearStack();
                TingPlayService.startCommand(context, Actions.ACTION_STOP);
            }
        }
    };

}

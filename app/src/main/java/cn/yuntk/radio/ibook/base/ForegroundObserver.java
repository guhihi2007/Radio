package cn.yuntk.radio.ibook.base;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import cn.yuntk.radio.ibook.XApplication;
import cn.yuntk.radio.ibook.activity.ScreenOffAcivity;
import cn.yuntk.radio.ibook.activity.SplashADActivity;
import cn.yuntk.radio.ui.activity.SplashActivity;
import cn.yuntk.radio.ibook.ads.ADConstants;
import cn.yuntk.radio.ibook.ads.AdController;
import cn.yuntk.radio.ibook.service.Actions;
import cn.yuntk.radio.ibook.service.AudioPlayer;
import cn.yuntk.radio.ibook.service.FloatViewService;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.NetworkUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cn.yuntk.radio.ibook.ads.ADConstants.AD_APP_BACKGROUND_TIME;

/**
 * Created by hzwangchenyan on 2017/9/20.
 */
public class ForegroundObserver implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "ForegroundObserver";
    private static final long CHECK_TASK_DELAY = 500;

    private List<Observer> observerList;
    private Handler handler;
    public static boolean isForeground;
    private int resumeActivityCount;
    private AdController builder;

    public interface Observer {
        /**
         * 进入前台
         *
         * @param activity 当前处于栈顶的Activity
         */
        void onForeground(Activity activity);

        /**
         * 进入后台
         *
         * @param activity 当前处于栈顶的Activity
         */
        void onBackground(Activity activity);
    }

    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(getInstance());
    }

    public static ForegroundObserver getInstance() {
        return SingletonHolder.sInstance;
    }

    private static class SingletonHolder {
        private static ForegroundObserver sInstance = new ForegroundObserver();
    }

    private ForegroundObserver() {
        observerList = Collections.synchronizedList(new ArrayList<Observer>());
        handler = new Handler(Looper.getMainLooper());
    }

    public static void addObserver(Observer observer) {
        if (observer == null) {
            return;
        }

        if (getInstance().observerList.contains(observer)) {
            return;
        }

        getInstance().observerList.add(observer);
    }

    public static void removeObserver(Observer observer) {
        if (observer == null) {
            return;
        }

        getInstance().observerList.remove(observer);
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ActivityManager.getInstance().setCurrentActivity(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
//        AppCache.get().finishActivity();//从后台进入前台 如果锁屏页面开着 就关闭
        ActivityManager.getInstance().setCurrentActivity(activity);
        if (!(activity instanceof ScreenOffAcivity)) {
            //锁屏界面不计数
            resumeActivityCount++;
        }
        if (!isForeground && resumeActivityCount > 0) {
            isForeground = true;
            // 从后台进入前台
            Log.i(TAG, "app in foreground");
            notify(activity, true);
            if (AudioPlayer.get().isPlaying())
                FloatViewService.startCommand(activity, Actions.SERVICE_VISABLE_WINDOW);
            if (!XApplication.getsInstance().isBackGroud) return;
            if (!(activity instanceof SplashActivity)) {
                gotoSplashADActivity(activity);
                XApplication.getsInstance().isBackGroud=false;
            }
        }
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        if (!(activity instanceof ScreenOffAcivity)) {
            //锁屏界面不计数
            resumeActivityCount--;
        }
        handler.postDelayed(() -> {
            if (isForeground && resumeActivityCount == 0) {
                isForeground = false;
                // 从前台进入后台
                Log.i(TAG, "app in background");

                ForegroundObserver.this.notify(activity, false);
                if (AudioPlayer.get().isPlaying())
                    FloatViewService.startCommand(activity, Actions.SERVICE_STOP);
                XApplication.getsInstance().isBackGroud = true;
                SharedPreferencesUtil.getInstance().putLong(AD_APP_BACKGROUND_TIME, System.currentTimeMillis());//记录退到后台时间
                LogUtils.showLog("ForegroundObserver：退到后台记录时间：" + System.currentTimeMillis());
            }
        }, CHECK_TASK_DELAY);
    }

    private void notify(Activity activity, boolean foreground) {
        for (Observer observer : observerList) {
            if (foreground) {
                observer.onForeground(activity);
            } else {
                observer.onBackground(activity);
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    //从后台进入前台 判断是否需要显示开屏广告页面
    private void gotoSplashADActivity(Activity activity) {
        LogUtils.showLog("ForegroundObserver：进入前台getLocalClassName：" + activity.getLocalClassName());
        if (SharedPreferencesUtil.getInstance().getBoolean(ADConstants.AD_SPLASH_STATUS) && needSplashAD()) {
            Intent intent = new Intent(activity, SplashADActivity.class);
            intent.putExtra("fromGameCenterActivity", true);
            activity.startActivity(intent);
        }
    }

    /**
     * 进入后台，再次进入app是否展示开屏广告
     * 时间超过间隔，并且有网络才展示
     *
     * @return
     */
    public boolean needSplashAD() {
        long current = System.currentTimeMillis();
        long background = SharedPreferencesUtil.getInstance().getLong(AD_APP_BACKGROUND_TIME, 0);
        long gapTime = (current - background) / 1000;
        long serverTime = SharedPreferencesUtil.getInstance().getLong(ADConstants.AD_SPREAD_PERIOD, 5);
        LogUtils.showLog("ForegroundObserver gapTime==" + gapTime + ",serverTime===" + serverTime);
        return gapTime >= serverTime && serverTime != 0 && NetworkUtils.isConnected(XApplication.getsInstance());
    }

}

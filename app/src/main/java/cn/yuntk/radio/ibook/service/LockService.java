package cn.yuntk.radio.ibook.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;


import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.util.LogUtils;

public class LockService extends Service {

    public static String status = "default";
    BroadcastReceiver receiver;

    public LockService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate() {

//        final IntentFilter filter = new IntentFilter();
//        // 屏幕灭屏广播
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        // 屏幕亮屏广播
//        filter.addAction(Intent.ACTION_SCREEN_ON);
//        // 屏幕解锁广播
//        filter.addAction(Intent.ACTION_USER_PRESENT);
//        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
//        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
//        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
//        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        status = "default";
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtils.showLog("LockService:status:"+status);
                if (status.equals("stop")){
                    stopSelf();
                    LogUtils.showLog("LockService:stopSelf");
                }else {
                    if (intent == null|| TextUtils.isEmpty(intent.getAction())){
                        LogUtils.showLog("LockService  intent == null|| TextUtils.isEmpty(intent.getAction())");
                        return;
                    }
                    if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                        if (AudioPlayer.get().isPlaying()|| AudioPlayer.get().isPreparing()){
                            if (XApplication.isScreenOff){
                                LogUtils.showLog("当前正在播放。。锁屏页面已存在");
                            }else {
//                                LogUtils.showLog("当前正在播放。。去往锁屏页面");
//                                Intent lockscreen = new Intent(LockService.this, ScreenOffAcivity.class);
//                                lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(lockscreen);
                            }
                        }else {
                            LogUtils.showLog("当前没有正在播放。。");
                        }
                        LogUtils.showLog("屏幕锁屏广播 :ACTION_SCREEN_OFF");
                    }else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                        LogUtils.showLog("屏幕亮屏广播 :ACTION_SCREEN_ON");
                    }else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
                        LogUtils.showLog("屏幕解锁广播 :ACTION_USER_PRESENT");
                    }else if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)){
                        LogUtils.showLog("ACTION_CLOSE_SYSTEM_DIALOGS");
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);//home
        registerReceiver(receiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.showLog("MyService"+"Service Start");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消锁屏的广播监听
        unregisterReceiver(receiver);
    }

}
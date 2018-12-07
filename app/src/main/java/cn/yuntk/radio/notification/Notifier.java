package cn.yuntk.radio.notification;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.yuntk.radio.Constants;
import cn.yuntk.radio.R;
import cn.yuntk.radio.bean.FMBean;
import cn.yuntk.radio.receiver.StatusBarReceiver;
import cn.yuntk.radio.service.PlayService;
import cn.yuntk.radio.ui.activity.MainActivity;

import static android.app.Notification.VISIBILITY_SECRET;


/**
 * Created by wcy on 2017/4/18.
 */
public class Notifier {
    private static final int NOTIFICATION_ID = 0x111;
    private static final String CHANNEL_ID = "radio_id";
    private static final String NOTIFICATION_NAME = "fairy_notification";
    private PlayService playService;
    private NotificationManager notificationManager;

    public static Notifier get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static Notifier instance = new Notifier();
    }

    private Notifier() {
    }

    public void init(PlayService playService) {
        this.playService = playService;
        notificationManager = (NotificationManager) playService.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void showPlay(FMBean fmBean) {
        if (fmBean == null) {
            return;
        }
        playService.startForeground(NOTIFICATION_ID, buildNotification(playService, fmBean, true));
    }

    public void showPause(FMBean fmBean) {
        if (fmBean == null) {
            return;
        }
        playService.stopForeground(false);
        notificationManager.notify(NOTIFICATION_ID, buildNotification(playService, fmBean, false));
    }

    public void cancelAll() {
        notificationManager.cancelAll();
    }

    private Notification buildNotification(Context context, FMBean fmBean, boolean isPlaying) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.canBypassDnd();//是否绕过请勿打扰模式
            channel.enableLights(true);//闪光灯
            channel.setLockscreenVisibility(VISIBILITY_SECRET);//锁屏显示通知
            channel.setLightColor(Color.RED);// 闪关灯的灯光颜色
            channel.canShowBadge();// 桌面launcher的消息角标
            channel.setSound(null, null);
            channel.enableVibration(false);// 是否允许震动
            channel.getGroup();// 获取通知取到组
            channel.setBypassDnd(true);//设置可绕过 请勿打扰模式
            channel.shouldShowLights();//是否会有灯光
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context, CHANNEL_ID);
            notification.setAutoCancel(false);
        }
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.EXTRA_NOTIFICATION, true);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
                .setCustomBigContentView(getBigRemoteViews(context, fmBean, isPlaying))
                .setCustomContentView(getRemoteViews(context, fmBean, isPlaying));
        return builder.build();

    }

    @SuppressLint("StaticFieldLeak")
    private RemoteViews getBigRemoteViews(Context context, FMBean fmBean, boolean isPlaying) {
        String title = fmBean.getName();
        String subtitle = "";
        if (playService.getCurrentFMBean() != null) {
            subtitle = playService.getCurrentFMBean().getName();
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification);

        Bitmap cover = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        if (cover != null) {
            remoteViews.setImageViewBitmap(R.id.iv_icon, cover);
        } else {
            remoteViews.setImageViewResource(R.id.iv_icon, R.mipmap.ic_launcher);
        }
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_subtitle, fmBean.getRadioFm());

        boolean isLightNotificationTheme = isLightNotificationTheme(playService);

        Intent playIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_PLAY_PAUSE);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.play_start, getPlayIconRes(isLightNotificationTheme, isPlaying));
        remoteViews.setOnClickPendingIntent(R.id.play_start, playPendingIntent);

//        Intent nextIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
//        nextIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_NEXT);
//        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setImageViewResource(R.id.play_next, getNextIconRes(isLightNotificationTheme));
//        remoteViews.setOnClickPendingIntent(R.id.play_next, nextPendingIntent);

//        Intent preIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
//        preIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_PRE);
//        PendingIntent prePendingIntent = PendingIntent.getBroadcast(context, 2, preIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        remoteViews.setImageViewResource(R.id.play_prev, getPreIconRes(isLightNotificationTheme));
//        remoteViews.setOnClickPendingIntent(R.id.play_prev, prePendingIntent);

        Intent stopIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        stopIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 3, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_close, stopPendingIntent);

        return remoteViews;
    }

    @SuppressLint("StaticFieldLeak")
    private RemoteViews getRemoteViews(Context context, FMBean fmBean, boolean isPlaying) {
        String title = fmBean.getName();
        String subtitle = "";
        if (playService.getCurrentFMBean() != null) {
            subtitle = playService.getCurrentFMBean().getName();
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_notification);

        Bitmap cover = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        if (cover != null) {
            remoteViews.setImageViewBitmap(R.id.iv_icon, cover);
        } else {
            remoteViews.setImageViewResource(R.id.iv_icon, R.mipmap.ic_launcher);
        }
        remoteViews.setTextViewText(R.id.tv_title, title);
        remoteViews.setTextViewText(R.id.tv_subtitle, subtitle);

        boolean isLightNotificationTheme = isLightNotificationTheme(playService);


        Intent playIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_PLAY_PAUSE);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setImageViewResource(R.id.play_start, getPlayIconRes(isLightNotificationTheme, isPlaying));
        remoteViews.setOnClickPendingIntent(R.id.play_start, playPendingIntent);

        Intent stopIntent = new Intent(StatusBarReceiver.ACTION_STATUS_BAR);
        stopIntent.putExtra(StatusBarReceiver.EXTRA, StatusBarReceiver.EXTRA_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(context, 3, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_close, stopPendingIntent);


        return remoteViews;
    }

    private int getPlayIconRes(boolean isLightNotificationTheme, boolean isPlaying) {
        if (isPlaying) {
            return isLightNotificationTheme
                    ? R.drawable.status_btn_pause_selector
                    : R.drawable.status_btn_pause_selector;
        } else {
            return isLightNotificationTheme
                    ? R.drawable.status_btn_play_selector
                    : R.drawable.status_btn_play_selector;
        }
    }

//    private int getNextIconRes(boolean isLightNotificationTheme) {
//        return isLightNotificationTheme
//                ? R.drawable.status_btn_next_selector
//                : R.drawable.status_btn_next_selector;
//    }
//
//    private int getPreIconRes(boolean isLightNotificationTheme) {
//        return isLightNotificationTheme ?
//                R.mipmap.ic_status_btn_prev : R.mipmap.ic_play_btn_prev;
//
//    }

    private boolean isLightNotificationTheme(Context context) {
        int notificationTextColor = getNotificationTextColor(context);
        return isSimilarColor(Color.BLACK, notificationTextColor);
    }

    private int getNotificationTextColor(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.build();
        RemoteViews remoteViews = notification.contentView;
        if (remoteViews == null) {
            return Color.BLACK;
        }
        int layoutId = remoteViews.getLayoutId();
        ViewGroup notificationLayout = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
        TextView title = notificationLayout.findViewById(android.R.id.title);
        if (title != null) {
            return title.getCurrentTextColor();
        } else {
            return findTextColor(notificationLayout);
        }
    }

    /**
     * 如果通过 android.R.id.title 无法获得 title ，
     * 则通过遍历 notification 布局找到 textSize 最大的 TextView ，应该就是 title 了。
     */
    private int findTextColor(ViewGroup notificationLayout) {
        List<TextView> textViewList = new ArrayList<>();
        findTextView(notificationLayout, textViewList);

        float maxTextSize = -1;
        TextView maxTextView = null;
        for (TextView textView : textViewList) {
            if (textView.getTextSize() > maxTextSize) {
                maxTextView = textView;
            }
        }

        if (maxTextView != null) {
            return maxTextView.getCurrentTextColor();
        }

        return Color.BLACK;
    }

    private void findTextView(View view, List<TextView> textViewList) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                findTextView(viewGroup.getChildAt(i), textViewList);
            }
        } else if (view instanceof TextView) {
            textViewList.add((TextView) view);
        }
    }

    private boolean isSimilarColor(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        return value < 180.0;
    }
}

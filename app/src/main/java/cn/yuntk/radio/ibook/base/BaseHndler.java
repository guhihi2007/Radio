package cn.yuntk.radio.ibook.base;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/*自定义Handler*/
public class BaseHndler extends Handler {
    private WeakReference<Activity> activityWeakReference;

    public BaseHndler(Activity activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void handleMessage(Message msg) {
        Activity activity = activityWeakReference.get();
        if (activity != null) {

        }
    }
}

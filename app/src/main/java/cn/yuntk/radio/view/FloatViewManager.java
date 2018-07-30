package cn.yuntk.radio.view;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import cn.yuntk.radio.base.Presenter;
import cn.yuntk.radio.bean.FMBean;

/**
 * Author : Gupingping
 * Date : 2018/7/26
 * Mail : gu12pp@163.com
 */
public class FloatViewManager {

    private FloatView floatView;
    private static volatile FloatViewManager mInstance;
    private FrameLayout mContainer;

    private FloatViewManager() {
    }

    public static FloatViewManager getInstance() {
        if (mInstance == null) {
            synchronized (FloatViewManager.class) {
                if (mInstance == null) {
                    mInstance = new FloatViewManager();
                }
            }
        }
        return mInstance;
    }

    public void remove() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (floatView == null) {
                    return;
                }
                if (ViewCompat.isAttachedToWindow(floatView) && mContainer != null) {
                    mContainer.removeView(floatView);
                }
                floatView = null;
            }
        });
    }

    private void ensureMiniPlayer(Context context) {
        synchronized (this) {
            if (floatView != null) {
                return;
            }
            floatView = new FloatView(context.getApplicationContext());
//            floatView.setLayoutParams(getParams());
            addViewToWindow(floatView);
        }
    }

    public void add(Context context, Presenter presenter,FMBean fmBean) {
        ensureMiniPlayer(context);
        floatView.setPresenter(presenter);
        floatView.setFMBean(fmBean);
    }

    public void attach(Activity activity) {
        attach(getActivityRoot(activity));
    }

    public void attach(FrameLayout container) {
        if (container == null || floatView == null) {
            mContainer = container;
            return;
        }
        if (floatView.getParent() == container) {
            return;
        }
        if (mContainer != null && floatView.getParent() == mContainer) {
            mContainer.removeView(floatView);
        }
        mContainer = container;
        container.addView(floatView);
    }

    public void detach(Activity activity) {
        detach(getActivityRoot(activity));
    }

    public void detach(FrameLayout container) {
        if (floatView != null && container != null && ViewCompat.isAttachedToWindow(floatView)) {
            container.removeView(floatView);
        }
        if (mContainer == container) {
            mContainer = null;
        }
    }

    public FloatView getFloatingView() {
        return floatView;
    }

    private void addViewToWindow(final FloatView view) {
        if (mContainer == null) {
            return;
        }
        mContainer.addView(view);
    }

    private FrameLayout.LayoutParams getParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM | Gravity.START;
        params.setMargins(13, params.topMargin, params.rightMargin, 56);
        return params;
    }

    private FrameLayout getActivityRoot(Activity activity) {
        if (activity == null) {
            return null;
        }
        try {
            return (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
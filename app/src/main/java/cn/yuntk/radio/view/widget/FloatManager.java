//package cn.yuntk.radio.view;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Handler;
//import android.os.Looper;
//import android.support.v4.view.ViewCompat;
//import android.view.Gravity;
//import android.widget.FrameLayout;
//import android.widget.RelativeLayout;
//
//import cn.yuntk.radio.base.Presenter;
//import cn.yuntk.radio.bean.FMBean;
//import cn.yuntk.radio.utils.Lg;
//
///**
// * Author : Gupingping
// * Date : 2018/7/26
// * Mail : gu12pp@163.com
// */
//public class FloatManager {
//    private FloatView floatBall;
//    private FrameLayout mContainer;
//    private static volatile FloatManager Instance;
//
//    private FloatManager() {
//    }
//
//    public static FloatManager getInstance() {
//        if (Instance == null) {
//            synchronized (FloatManager.class) {
//                if (Instance == null) {
//                    Instance = new FloatManager();
//                }
//            }
//        }
//        return Instance;
//    }
//
//    //移除悬浮框
//    public void remove() {
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                if (floatBall == null) {
//                    return;
//                }
//                if ((ViewCompat.isAttachedToWindow(floatBall) && mContainer != null)) {
//                    mContainer.removeView(floatBall);
//                }
//                floatBall = null;
//            }
//        });
//    }
//
//    //外层activity调用，在基类Activity onStart初始化
//    public void attachActivity(Activity activity) {
//        attach(getActivityRoot(activity));
//        Lg.e("FloatView", "attachActivity  mContainer==" + mContainer);
//
//    }
//
//
//    public void setClickListener(Presenter presenter) {
//        if (floatBall != null) {
//            floatBall.setListener(presenter);
//        }
//    }
//
//    public void setData(FMBean fmBean, Presenter presenter) {
////        if (floatBall != null) {
////            floatBall.setData(fmBean, presenter);
////        }
//    }
//
//    //外层activity调用，在基类Activity onStop销毁
//    public void detachActivity(Activity activity) {
//        detach(getActivityRoot(activity));
//    }
//
//    //在需要添加floatView的页面初始化调用
//    public void addFloatView(Context context) {
//        Lg.e("FloatView", "addFloatView  mContainer==" + mContainer);
//        synchronized (this) {
//            if (floatBall != null) {
//                return;
//            }
//            floatBall = new FloatView(context.getApplicationContext());
//            //设置初始化位置
//            floatBall.setLayoutParams(getParams());
//            //外部容器为空，不添加
//            if (mContainer == null) {
//                return;
//            }
//            mContainer.addView(floatBall);
//        }
//
//    }
//
//    private void attach(FrameLayout frameLayout) {
//        Lg.e("FloatView", "frameLayout==" + frameLayout);
//        if (frameLayout == null || floatBall == null) {
//            mContainer = frameLayout;
//            return;
//        }
//        if (floatBall.getParent() == frameLayout) {
//            return;
//        }
//        if (mContainer != null && floatBall.getParent() == mContainer) {
//            mContainer.removeView(floatBall);
//        }
//        mContainer = frameLayout;
//        Lg.e("FloatView", "mContainer==" + mContainer);
//        frameLayout.addView(floatBall);
//    }
//
//    private void detach(FrameLayout frameLayout) {
//        if (floatBall != null && mContainer != null && ViewCompat.isAttachedToWindow(floatBall)) {
//            frameLayout.removeView(floatBall);
//        }
//        if (mContainer == frameLayout) {
//            mContainer = null;
//        }
//    }
//
//    private FrameLayout getActivityRoot(Activity activity) {
//        if (activity == null) {
//            return null;
//        }
//        try {
//            return (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//    //设置悬浮框初始化位置
//    private FrameLayout.LayoutParams getParams() {
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                RelativeLayout.LayoutParams.WRAP_CONTENT,
//                RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params.gravity = Gravity.BOTTOM | Gravity.START;
//        params.leftMargin = 200;
//        params.bottomMargin = 200;
////        params.setMargins(20, params.topMargin, params.rightMargin, 56);
//        return params;
//    }
//}

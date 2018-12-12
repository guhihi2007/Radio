package cn.yuntk.radio.ad;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.yuntk.radio.Constants;
import cn.yuntk.radio.utils.LogUtils;
import cn.yuntk.radio.utils.SPUtil;


/**
 * Created by Gpp on 2018/1/22.
 */

abstract class AbsADParent {
    public static final String TAG = "controller";

    protected TextView mSkipVew;
    protected ImageView mLogo;
    protected ImageView splashHolder;
    protected Activity mActivity;
    protected FrameLayout mContainer;
    protected boolean isNight;
    protected String mPage;
    protected boolean isLoading;//后台返回，是否广告拉取中
    protected AdReportListener listener;
    protected FrameLayout nativeLayout;//阅读页原生广告容器

    protected abstract void showAdView(AD.AdType type);

    protected boolean isShowNative;
    protected View view;

    public void setSkipVew(TextView mSkipVew) {
        this.mSkipVew = mSkipVew;
    }


    public void setLogo(ImageView mLogo) {
        this.mLogo = mLogo;
    }


    public void setSplashHolder(ImageView splashHolder) {
        this.splashHolder=splashHolder;
    }

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setContainer(FrameLayout mContainer) {
        this.mContainer = mContainer;
    }

    public void setNight(boolean night) {
        isNight = night;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public abstract void destroy(AD.AdType type);

    public void setPage(String page) {
        mPage = page;
    }

    public void setListener(AdReportListener listener) {
        this.listener = listener;
    }


    public void setNativeAdLayout(FrameLayout frameLayout) {
        this.nativeLayout = frameLayout;
    }

    protected void gone(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.GONE);
                }
            }
        }
    }

    protected void visible(final View... views) {
        if (views != null && views.length > 0) {
            for (View view : views) {
                if (view != null) {
                    view.setVisibility(View.VISIBLE);
                }
            }
        }

    }
    protected void saveInsertShowTime() {

        //记录此次显示时间
        SPUtil.getInstance().putLong(mPage + Constants.AD_INSERT_LAST_SHOW, System.currentTimeMillis());

        LogUtils.i(TAG, "页面:" + mPage + ",广告被点击,保存这次显示广告时间:" + System.currentTimeMillis());
    }

//    protected Drawable getNightDrawable() {
//        Drawable night = ContextCompat.getDrawable(mActivity, R.drawable.background_rectangle);
//        night.setBounds(0, 0, mContainer.getWidth(), mContainer.getHeight());
//        return night;
//    }
//
//    protected Drawable getDayDrawable() {
//        Drawable night = ContextCompat.getDrawable(mActivity, R.drawable.background_transparent);
//        night.setBounds(0, 0, mContainer.getWidth(), mContainer.getHeight());
//        return night;
//    }

}

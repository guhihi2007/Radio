package cn.yuntk.radio.ibook.ads;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;


/**
 * Created by Gpp on 2018/1/22.
 */
abstract class AbsADParent {
    public static final String TAG = "controller";

    protected TextView mSkipVew;
    protected ImageView mLogo;
    protected Activity mActivity;
    protected FrameLayout mContainer;
    protected boolean isNight;
    protected String mPage;
    protected boolean isLoading;
    protected AdReportListener listener;

    protected abstract void showAdView(AD.AdType type);

    protected View view;

    public void setSkipVew(TextView mSkipVew) {
        this.mSkipVew = mSkipVew;
    }


    public void setLogo(ImageView mLogo) {
        this.mLogo = mLogo;
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

    protected void saveInsertShowTime() {

        //记录此次显示时间
        SharedPreferencesUtil.getInstance().putLong(mPage + ADConstants.AD_INSERT_LAST_SHOW, System.currentTimeMillis());

        LogUtils.showLog("页面:" + mPage + ",广告被点击,保存这次显示广告时间:" + System.currentTimeMillis());
    }

    protected Drawable getNightDrawable() {
        Drawable night = ContextCompat.getDrawable(mActivity, R.drawable.listener_background_rectangle);
        night.setBounds(0, 0, mContainer.getWidth(), mContainer.getHeight());
        return night;
    }

    protected Drawable getDayDrawable() {
        Drawable night = ContextCompat.getDrawable(mActivity, R.drawable.background_transparent);
        night.setBounds(0, 0, mContainer.getWidth(), mContainer.getHeight());
        return night;
    }



}

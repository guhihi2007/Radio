package cn.yuntk.radio.ad;


import android.view.View;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.yuntk.radio.BuildConfig;
import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ui.activity.SplashActivity;
import cn.yuntk.radio.utils.LogUtils;


/**
 * Created by Gpp on 2018/1/20.
 */

public class GDT_AD extends AbsADParent {
    private BannerView banner_gdt;
    private InterstitialAD insert_gdt;
    private boolean status;
    private NativeExpressADView nativeExpressADView;

    @Override
    protected void showAdView(final AD.AdType type) {
        switch (type) {

            case BANNER:

                showBannerView();
                break;
            case INSET:

                showInsertView();

                break;
            case SPLASH:
                showSplashView();
                break;
            case NATIVE:
                showNativeView();
                break;
        }

    }

    private void showNativeView() {
        LogUtils.e(TAG, "GDT showNativeView mActivity==" + mActivity + ",nativeLayout==" + nativeLayout);
        if (mActivity != null && nativeLayout != null) {
            LogUtils.e("tencent", "GDT showNativeView");
            if (nativeLayout.getChildCount() > 0) {
                nativeLayout.removeAllViews();
            }
            NativeExpressAD nativeExpressAD = new NativeExpressAD(mActivity, getMyADSize(), BuildConfig.GDT_APP_KEY,
                    BuildConfig.GDT_NATIVE_AD, new NativeExpressAD.NativeExpressADListener() {
                @Override
                public void onNoAD(AdError adError) {
                    LogUtils.e("tencent", "GDT native onNoAD----->" + adError);
                }

                @Override
                public void onADLoaded(List<NativeExpressADView> list) {
                    if (nativeExpressADView != null) {
                        nativeExpressADView.destroy();
                    }
                    if (list != null && list.size() > 0) {
                        nativeExpressADView = list.get(0);
                        LogUtils.e("tencent", "GDT native onADLoaded----->" + nativeExpressADView);

                        nativeLayout.addView(nativeExpressADView);

                        nativeExpressADView.render();
                    }
                }

                @Override
                public void onRenderFail(NativeExpressADView nativeExpressADView) {
                    LogUtils.e("tencent", "GDT native onRenderFail----->" + nativeExpressADView);

                }

                @Override
                public void onRenderSuccess(NativeExpressADView nativeExpressADView) {
                    LogUtils.e("tencent", "GDT native onRenderSuccess----->" + nativeExpressADView);
//                    if (BuildConfig.READ_PAGE.equals(mPage)) {
//                        nativeLayout.setForeground(isNight ? getNightDrawable() : getDayDrawable());
//                        nativeExpressADView.setForeground(isNight ? getNightDrawable() : getDayDrawable());
//
//                    }
//                    if (AdManager.needShowNative) {
                    nativeLayout.setVisibility(View.VISIBLE);
                    LogUtils.e("tencent", "GDT native  展示周期到，显示原生广告");
//                    } else {
//                        LogUtils.e("tencent", "GDT native 跳过了要显示的页面");
//                        nativeLayout.setVisibility(View.GONE);
//                        if (nativeExpressADView != null) {
//                            nativeExpressADView.destroy();
//                        }
//                    }
                }

                @Override
                public void onADExposure(NativeExpressADView nativeExpressADView) {
                    LogUtils.e("tencent", "GDT native onADExposure----->" + nativeExpressADView);

                }

                @Override
                public void onADClicked(NativeExpressADView nativeExpressADView) {
                    LogUtils.e("tencent", "GDT native onADClicked----->" + nativeExpressADView);

                }

                @Override
                public void onADClosed(NativeExpressADView nativeExpressADView) {
                    LogUtils.e("tencent", "GDT native onADClosed----->" + nativeExpressADView);

                    if (nativeLayout.getChildCount() > 0) {
                        nativeLayout.removeAllViews();
                    }
                }

                @Override
                public void onADLeftApplication(NativeExpressADView nativeExpressADView) {
                    LogUtils.e("tencent", "GDT native onADLeftApplication----->" + nativeExpressADView);

                }

                @Override
                public void onADOpenOverlay(NativeExpressADView nativeExpressADView) {
                    LogUtils.e("tencent", "GDT native onADOpenOverlay----->" + nativeExpressADView);

                }

                @Override
                public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {
                    LogUtils.e("tencent", "GDT native onADCloseOverlay----->" + nativeExpressADView);

                }
            });
            nativeExpressAD.loadAD(1);
        }
    }


    private com.qq.e.ads.nativ.ADSize getMyADSize() {
        int w = com.qq.e.ads.nativ.ADSize.FULL_WIDTH;
        int h = com.qq.e.ads.nativ.ADSize.AUTO_HEIGHT;
        return new com.qq.e.ads.nativ.ADSize(w, h);
    }

    @Override
    public void destroy(AD.AdType type) {
        switch (type) {

            case SPLASH:

                break;
            case INSET:
                if (insert_gdt != null) {
                    insert_gdt.destroy();
                }

                break;
            case BANNER:

                if (banner_gdt != null) {
                    banner_gdt.destroy();
                }

                break;
            case NATIVE:
                if (nativeLayout != null && (nativeLayout.isShown() || nativeLayout.getChildCount() > 0)) {
                    nativeLayout.removeAllViews();
                    nativeLayout.setVisibility(View.GONE);
                }
                if (nativeExpressADView != null) {
                    nativeExpressADView.destroy();
                }
                break;
        }
    }


    private void showSplashView() {
        XApplication.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                if (mActivity != null) {
                    try {
                        new SplashAD(mActivity, mContainer, mSkipVew, BuildConfig.GDT_APP_KEY, BuildConfig.GDT_SPLASH_ID, new SplashADListener() {
                            @Override
                            public void onADDismissed() {
                                LogUtils.i(TAG, "onADDismissed");
                                if (null != mActivity) {
                                    if (isLoading) {//不是启动页就finish
                                        LogUtils.i(TAG, "onADDismissed isLoading finish");
                                        mActivity.finish();
                                        return;
                                    }
                                    ((SplashActivity) mActivity).checkIn();
                                } else {
                                    EventBus.getDefault().post(new LoadEvent(true));
                                }
                            }


                            @Override
                            public void onNoAD(final AdError adError) {
                                if (null != mActivity) {
                                    XApplication.getMainThreadHandler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            LogUtils.i(TAG, "onNoAD==" + adError.getErrorCode() + ",Message==" + adError.getErrorMsg());
                                            listener.onNoAD("splash", "GDT_AD", "开屏");
                                            if (isLoading) {
                                                mActivity.finish();
                                                return;
                                            }
                                            ((SplashActivity) mActivity).checkIn();
                                        }
                                    });

                                } else {
                                    EventBus.getDefault().post(new LoadEvent(true));
                                }

                            }

                            @Override
                            public void onADPresent() {
                                if (null != mActivity) {
                                    if (mSkipVew != null) {
                                        gone(splashHolder);
                                        visible(mSkipVew, mLogo);
                                        mSkipVew.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                LogUtils.e("mSkipVew setOnClickListener");
                                                if (isLoading) {//不是启动页就finish
                                                    LogUtils.i(TAG, "onADDismissed isLoading finish");
                                                    mActivity.finish();
                                                    return;
                                                }
                                                ((SplashActivity) mActivity).checkIn();
                                            }
                                        });

                                    }
                                    listener.onShowAD("splash", "GDT_AD", "开屏");

                                    LogUtils.i(TAG, "onADPresent");
                                } else {
                                    EventBus.getDefault().post(new LoadEvent(true));
                                }
                            }

                            @Override
                            public void onADClicked() {

                                if (null != mActivity) {
                                    listener.onClickAD("splash", "GDT_AD", "开屏");
                                    LogUtils.i(TAG, "onADClicked");
                                } else {
                                    EventBus.getDefault().post(new LoadEvent(true));
                                }
                            }

                            @Override
                            public void onADTick(long l) {
                                LogUtils.i(TAG, "SplashADTick: " + l + "ms");
                                mSkipVew.setText(String.format("点击跳过 %d", Math.round(l / 1000f)));
                                if (l < 1000) {
                                    if (null != mActivity) {
                                        if (isLoading) {//不是启动页就finish
                                            LogUtils.i(TAG, "onADDismissed isLoading finish");
                                            mActivity.finish();
                                            return;
                                        }
                                        ((SplashActivity) mActivity).checkIn();
                                    }
                                }
                            }

                            @Override
                            public void onADExposure() {

                            }
                        }, 0);
                    } catch (Throwable e) {
                        if (null != mActivity) {
                            if (isLoading) {
                                mActivity.finish();
                                return;
                            }
                            ((SplashActivity) mActivity).checkIn();
                        } else {
                            EventBus.getDefault().post(new LoadEvent(true));
                        }
                    }
                }
            }
        });
    }

    /**
     * Banner广告
     */
    private void showBannerView() {
        XApplication.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                if (mActivity != null && !mActivity.isFinishing()) {
                    try {

                        if (banner_gdt == null) {
                            banner_gdt = new BannerView(mActivity, ADSize.BANNER, BuildConfig.GDT_APP_KEY, BuildConfig.GDT_BANNER_ID);
                            LogUtils.i(TAG, "初始化广点通Banner广告");

                        }
                        banner_gdt.setRefresh(30);
                        banner_gdt.setADListener(new AbstractBannerADListener() {

                            @Override
                            public void onNoAD(AdError adError) {
                                if (null != mActivity) {
                                    LogUtils.i(TAG, "banner_gdt_NoAD:" + adError);
                                    listener.onNoAD("Banner", "GDT_AD", "banner");
                                    mContainer.setVisibility(View.GONE);
                                    listener.onFailedAD(adError.getErrorCode(), AD.AdOrigin.gdt);
                                }

                            }

                            @Override
                            public void onADReceiv() {
                                mContainer.setVisibility(View.VISIBLE);
//                                if (BuildConfig.READ_PAGE.equals(mPage)) {
//                                    mContainer.setForeground(isNight ? getNightDrawable() : getDayDrawable());
//                                    banner_gdt.setForeground(isNight ? getNightDrawable() : getDayDrawable());
//                                }
                                if (null != mActivity) {
                                    LogUtils.i(TAG, "banner_gdt_onADReceiv");
                                    listener.onShowAD("Banner", "GDT_AD", "banner");
                                }
                            }

                            @Override
                            public void onADClicked() {
                                super.onADClicked();
                                if (null != mActivity) {
                                    LogUtils.i(TAG, "banner_gdt_onADClicked");
                                    listener.onClickAD("Banner", "GDT_AD", "banner");
                                }
                            }

                            @Override
                            public void onADClosed() {
                                super.onADClosed();
                                mContainer.setVisibility(View.GONE);
                                LogUtils.i(TAG, "banner_gdt_onADClosed");
                            }

                            @Override
                            public void onADLeftApplication() {
                                super.onADLeftApplication();
                                LogUtils.i(TAG, "banner_gdt_onADLeftApplication");

                            }
                        });

                        if (mContainer != null) {
                            mContainer.removeAllViews();
                            mContainer.addView(banner_gdt);
                            banner_gdt.loadAD();
                        }
                    } catch (Throwable e) {
                        LogUtils.i(TAG, "初始化广点通Banner广告Exception==" + e.getMessage());

                    }
                }
            }
        });
    }


    /**
     * 插屏广告
     */
    private void showInsertView() {
        if (mActivity != null) {
            if (insert_gdt == null) {

                insert_gdt = new InterstitialAD(mActivity, BuildConfig.GDT_APP_KEY, BuildConfig.GDT_INSERT_ID);

                LogUtils.i(TAG, "初始化广点通Insert广告");

            }
            insert_gdt.setADListener(new AbstractInterstitialADListener() {
                @Override
                public void onNoAD(AdError adError) {
                    LogUtils.i(TAG, "initGDT_Insert_onNoAD:" + adError.getErrorMsg());
                    if (null != mActivity) {
                        listener.onNoAD("interstitial", "GDT_AD", "插屏");
                        status = true;
                    }
                }

                @Override
                public void onADOpened() {
                    super.onADOpened();
                    status = true;

                    //记录此次显示时间
                    saveInsertShowTime();

                    LogUtils.i(TAG, "initGDT_Insert_onADOpened");
                }

                @Override
                public void onADReceive() {
                    LogUtils.i(TAG, "initGDT_Insert_onADReceive");
                    if (null != mActivity) {
                        status = true;

                        insert_gdt.show();

                        listener.onShowAD("interstitial", "GDT_AD", "插屏");

                    }
                }

                @Override
                public void onADClicked() {
                    super.onADClicked();
                    if (null != mActivity) {
                        status = true;

                        LogUtils.i(TAG, "initGDT_Insert_onADClicked");
                        listener.onClickAD("interstitial", "GDT_AD", "插屏");

                        //记录此次显示时间
                        saveInsertShowTime();
                    }
                }

                @Override
                public void onADClosed() {
                    super.onADClosed();
                    status = true;

                    listener.onClosedAD();

                    //记录此次显示时间
                    saveInsertShowTime();

                    LogUtils.i(TAG, "initGDT_Insert_onADClosed");
                }
            });

            insert_gdt.loadAD();

            //记录此次显示时间
            saveInsertShowTime();

            //如果load失败，再load一次
            XApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!status) {

                        LogUtils.i(TAG, "initGDT_Insert_loadAD");
                        insert_gdt.loadAD();
                    }
                }
            }, 1500);
        }
    }

}

package cn.yuntk.radio.ad;

import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import cn.yuntk.radio.BuildConfig;
import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.utils.LogUtils;


public class Google_AD extends AbsADParent {
    private AdView banner;
    private InterstitialAd insert;
    private AdLoader adLoader;
    private UnifiedNativeAdView adView;

    public Google_AD() {
    }

    @Override
    protected void showAdView(AD.AdType type) {
        switch (type) {
            case BANNER:
                showBannerView();

                break;
            case INSET:
                XApplication.getMainThreadHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        showInsertView();
                    }
                });

                break;
            case SPLASH:
                break;
            case NATIVE:
                showNative();
                break;
        }
    }

    private void showNative() {
        if (adLoader == null) {
            LogUtils.e("google", "google showNative adLoader=null ");
            getNativeAD();
        } else {
            adLoader.loadAd(new AdRequest.Builder().build());
        }

    }

    public void getNativeAD() {
        if (mActivity != null && nativeLayout != null) {
            AdLoader.Builder build = new AdLoader.Builder(mActivity, BuildConfig.AD_GOOGLE_NATIVE);
//            build.forUnifiedNativeAd(unifiedNativeAd -> {
//                adView = (UnifiedNativeAdView) mActivity.
//                        getLayoutInflater().inflate(R.layout.ad_unified, null);
//
//                populateUnifiedNativeAdView(unifiedNativeAd, adView);
//                nativeLayout.addView(adView);
//            });

            adLoader = build.withAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    LogUtils.e("google", "google showNative onAdLoaded VISIBLE ,ChildCount=" + nativeLayout.getChildCount());
                    //如果是在当前要显示广告的页面才显示
                    if (AdManager.needShowNative) {
                        nativeLayout.setVisibility(View.VISIBLE);
                        LogUtils.e("google", "google native  展示周期到，显示原生广告");
                    } else {
                        LogUtils.e("google", "google native 跳过了要显示的页面");
                        nativeLayout.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    LogUtils.e("google", "google------------showNative------------ onAdFailedToLoad==" + i);
//                    ToastUtils.showToast("google 广告请求失败");
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    LogUtils.e("google", "google------------showNative------------ onAdImpression ");
                }
            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());

            LogUtils.e("google", "google showNative adLoader loadAd ");
        }
    }


    public void showInsertView() {
        if (insert == null) {
            insert = new InterstitialAd(mActivity);
        }
        insert.setAdUnitId(BuildConfig.AD_GOOGLE_INSERT);
        insert.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                LogUtils.e("google", "google InterstitialAd onAdClosed ");
                //记录此次显示时间
                saveInsertShowTime();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                LogUtils.e("google", "google InterstitialAd onAdFailedToLoad =" + i);

            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                LogUtils.e("google", "google InterstitialAd onAdLoaded ");
                //记录此次显示时间
                saveInsertShowTime();
                insert.show();

            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                LogUtils.e("google", "google InterstitialAd onAdClicked ");
                //记录此次显示时间
                saveInsertShowTime();

            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                LogUtils.e("google", "google InterstitialAd onAdImpression ");

            }
        });
        insert.loadAd(new AdRequest.Builder().build());
    }

    private void showBannerView() {
        if (mActivity != null) {
            if (banner == null) {
                banner = new AdView(mActivity);
            }
            banner.setAdSize(AdSize.BANNER);
            banner.setAdUnitId(BuildConfig.AD_GOOGLE_BANNER);
            banner.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    LogUtils.e("google", "google Banner onAdClosed ");
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    LogUtils.e("google", "google Banner onAdFailedToLoad =" + i);

                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    LogUtils.e("google", "google Banner onAdClicked ");

                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    LogUtils.e("google", "google Banner onAdImpression ");

                }
            });
            AdRequest request = new AdRequest.Builder().build();
            mContainer.addView(banner);
            mContainer.setVisibility(View.VISIBLE);
            banner.loadAd(request);
        }
    }

    @Override
    public void destroy(AD.AdType type) {
        switch (type) {
            case SPLASH:

                break;
            case INSET:

                break;
            case BANNER:
                if (banner != null) {
                    banner.destroy();
                    banner = null;
                }
            case NATIVE:
                if (adView != null) {
                    adView.destroy();
                }
                break;
        }
    }
}

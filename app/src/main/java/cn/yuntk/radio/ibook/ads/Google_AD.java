package cn.yuntk.radio.ibook.ads;

import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import cn.yuntk.radio.ibook.util.LogUtils;

public class Google_AD extends AbsADParent {

    private AdView banner;
    private InterstitialAd insert;

    public Google_AD() {}

    @Override
    protected void showAdView(AD.AdType type) {
        switch (type) {
            case BANNER:
                showBannerView();
                break;
            case INSET:
                showInsertView();
                break;
            case SPLASH:
                break;
            case NATIVE:
//                showNative();
                break;
        }
    }

    public void showInsertView() {
        if (insert == null) {
            insert = new InterstitialAd(mActivity);
        }
        insert.setAdUnitId(ADConstants.AD_GOOGLE_INSERT);
        insert.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                LogUtils.e("Google_AD", "google InterstitialAd onAdClosed ");
                //记录此次显示时间
                saveInsertShowTime();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                LogUtils.e("Google_AD", "google InterstitialAd onAdFailedToLoad =" + i);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                LogUtils.e("Google_AD", "google InterstitialAd onAdLoaded ");
                //记录此次显示时间
                saveInsertShowTime();
                insert.show();

            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                LogUtils.e("Google_AD", "google InterstitialAd onAdClicked ");
                //记录此次显示时间
                saveInsertShowTime();

            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                LogUtils.e("Google_AD", "google InterstitialAd onAdImpression ");
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
            banner.setAdUnitId(ADConstants.AD_GOOGLE_BANNER);
            banner.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    LogUtils.e("Google_AD", "google Banner onAdClosed ");
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    LogUtils.e("Google_AD", "google Banner onAdFailedToLoad =" + i);

                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    LogUtils.e("Google_AD", "google Banner onAdClicked ");

                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    LogUtils.e("Google_AD", "google Banner onAdImpression ");

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
                break;
        }
    }

}

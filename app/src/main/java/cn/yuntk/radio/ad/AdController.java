package cn.yuntk.radio.ad;


import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.yuntk.radio.utils.LogUtils;


/**
 * Created by Gpp on 2018/1/20.
 */

public class AdController {
    public static final String TAG = "controller";

    private Activity activity;
    private AdManager manager;
    private String page;
    private TextView skipView;
    private FrameLayout Container;
    private ImageView logo;
    private ImageView splashHolder;
    private boolean isLoading;
    private FrameLayout nativeAdLayout;

    AdController(Activity activity, String page) {
        this.activity = activity;
        this.page = page;
    }

    public void show() {
        LogUtils.i(TAG, "AdController show ====" + page);

        manager = new AdManager(activity, page, Container, logo, skipView, isLoading, nativeAdLayout, splashHolder);
        manager.show();
    }

    //用于切换了日夜间模式
//    public void reLoadBanner() {
//        if (manager != null && page.equals(TingConstants.LISTENING_PAGE)) {
//            manager.destroy();
//            manager = new AdManager(activity, page, Container, logo, skipView, isLoading, nativeAdLayout, splashHolder);
//            manager.show();
//        }
//    }

    public void destroy() {
        if (null != manager) {
            manager.destroy();
            manager = null;
            System.gc();
        }
    }

    public void destroy(AD.AdType type) {
        if (null != manager) {
            manager.destroy(type);
        }
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public TextView getSkipView() {
        return skipView;
    }

    public void setSkipView(TextView skipView) {
        this.skipView = skipView;
    }

    public ViewGroup getContainer() {
        return Container;
    }

    public void setContainer(FrameLayout container) {
        Container = container;
    }

    public ImageView getLogo() {
        return logo;
    }

    public void setLogo(ImageView logo) {
        this.logo = logo;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public void setNativeAdLayout(FrameLayout nativeAdLayout) {
        this.nativeAdLayout = nativeAdLayout;
    }

    private void setSplashHolder(ImageView splashHolder) {
        this.splashHolder = splashHolder;
    }

    public void activityDestroy() {
        if (manager != null) {
            manager.activityDestroy();
        }
    }

    public static class Builder {
        private TextView skipView;
        private FrameLayout container;
        private ImageView logo;
        private ImageView splashHolder;
        private String page;
        private Activity activity;
        private boolean isLoading;
        private FrameLayout nativeAdLayout;

        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setSkipView(TextView skipView) {
            this.skipView = skipView;
            return this;
        }

        public Builder setContainer(FrameLayout container) {
            this.container = container;
            return this;

        }

        public Builder setLogo(ImageView logo) {
            this.logo = logo;
            return this;
        }

        public Builder setSplashHolder(ImageView splashHolder) {
            this.splashHolder = splashHolder;
            return this;
        }

        public Builder setPage(String currentPageName) {
            this.page = currentPageName;
            return this;
        }

        public Builder setLoading(boolean isLoading) {
            this.isLoading = isLoading;
            return this;
        }

        public Builder setNativeAdLayout(FrameLayout nativeAdLayout) {
            this.nativeAdLayout = nativeAdLayout;
            return this;
        }

        public AdController create() {

            final AdController controller = new AdController(activity, page);

            controller.setLoading(isLoading);

            if (container != null) {
                controller.setContainer(container);
            }
            if (logo != null) {
                controller.setLogo(logo);
            }
            if (splashHolder != null) {
                controller.setSplashHolder(splashHolder);
            }

            if (skipView != null) {
                controller.setSkipView(skipView);
            }

            if (nativeAdLayout != null) {
                controller.setNativeAdLayout(nativeAdLayout);
            }
            if (TextUtils.isEmpty(page)) {
                throw new NullPointerException("page can not null");
            }
            if (activity == null) {
                throw new NullPointerException("activity can not null");
            }

//            if (mDataList==null){
//                throw new NullPointerException("mDataList can not null");
//            }
//            if (mAdapter==null){
//                throw new NullPointerException("mAdapter can not null");
//            }

            return controller;
        }


    }

}

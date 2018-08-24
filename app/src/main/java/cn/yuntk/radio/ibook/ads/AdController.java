package cn.yuntk.radio.ibook.ads;


import android.app.Activity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


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
    private boolean isLoading;
    private String tag_ad = "";

    public AdController(Activity activity, String page) {
        this.activity = activity;
        this.page = page;
    }

    public void show() {
        manager = new AdManager(activity, page, Container, logo, skipView, isLoading,tag_ad);
        manager.show();
    }

    public void destroy() {
        if (null != manager) {
            manager.destroy();
            manager = null;
            System.gc();
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

    public String getTag_ad() {
        return tag_ad;
    }

    public void setTag_ad(String tag_ad) {
        this.tag_ad = tag_ad;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public static class Builder {
        private TextView skipView;
        private FrameLayout container;
        private ImageView logo;
        private String page;
        private Activity activity;
        private boolean isLoading;
        private String tag_ad = "";

        public String getTag_ad() {
            return tag_ad;
        }

        public Builder setTag_ad(String tag_ad) {
            this.tag_ad = tag_ad;
            return this;
        }

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

        public Builder setPage(String currentPageName) {
            this.page = currentPageName;
            return this;
        }

        public Builder setLoading(boolean isLoading) {
            this.isLoading = isLoading;
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

            if (skipView != null) {
                controller.setSkipView(skipView);
            }

            controller.setTag_ad(tag_ad);

            if (TextUtils.isEmpty(page)) {
                throw new NullPointerException("page can not null");
            }
            if (activity == null) {
                throw new NullPointerException("activity can not null");
            }
            return controller;
        }
    }
}

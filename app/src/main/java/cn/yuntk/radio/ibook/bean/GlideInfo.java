package cn.yuntk.radio.ibook.bean;

import android.widget.ImageView;

/**
 * <p>类描述：</p>
 * <p>创建人：yb</p>
 * <p>创建时间：2017/12/29</p>
 * <p>修改人：       </p>
 * <p>修改时间：   </p>
 * <p>修改备注：   </p>
 */

public class GlideInfo {
    private String url;
    private int resourceId;
    private boolean centerCrop;
    private int roundSize;
    private boolean isCircle;
    private ImageView target;
    private boolean isLocalImage = false;
    private LoadListener listener;

    public LoadListener getListener() {
        return listener;
    }

    public GlideInfo listener(LoadListener listener) {
        this.listener = listener;
        return this;
    }

    public ImageView getTarget() {
        return target;
    }

    public GlideInfo setTarget(ImageView target) {
        this.target = target;
        return this;
    }

    public int getRoundSize() {
        return roundSize;
    }

    public GlideInfo setRoundSize(int roundSize) {
        this.roundSize = roundSize;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public GlideInfo setUrl(String url) {
        this.url = url;
        return this;
    }

    public GlideInfo circle() {
        isCircle = true;
        return this;
    }

    public boolean isCenterCrop() {
        return centerCrop;
    }

    public GlideInfo setCenterCrop(boolean centerCrop) {
        this.centerCrop = centerCrop;
        return this;
    }

    public boolean isRound() {
        return roundSize > 0;
    }

    public boolean isCircle() {
        return isCircle;
    }

    public static GlideInfo create() {
        return new GlideInfo();
    }

    public boolean isLocalImage() {
        return isLocalImage;
    }

    public GlideInfo setLocalImage(boolean localImage) {
        isLocalImage = localImage;
        return this;
    }

    public int getResourceId() {
        return resourceId;
    }

    public GlideInfo setResourceId(int resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public static interface LoadListener {
        void fail(String msg);
        void ready(float scale);
    }
}

package cn.yuntk.radio.ibook.base.presenter;


import java.lang.ref.WeakReference;

import cn.yuntk.radio.ibook.base.model.IBaseModel;
import cn.yuntk.radio.ibook.base.view.IBaseView;

/**
 * 创建时间:2018/4/2
 * 创建人: yb
 * 描述:
 */

public class BasePresenter<V extends IBaseView,M extends IBaseModel> {

    protected M model;

    public BasePresenter() {}

    public BasePresenter(M model) {
        this.model = model;
    }

    private WeakReference<V> vWeakReference;
    public final void attachView(V view) {
        vWeakReference = new WeakReference<>(view);
    }

    public final void detachView() {
        if (isViewNotEmpty()) {
            vWeakReference.clear();
            vWeakReference = null;
        }
    }

    private boolean isViewNotEmpty() {
        return vWeakReference != null && vWeakReference.get() != null;
    }

    protected V obtainView() {
        if (isViewNotEmpty())
            return vWeakReference.get();
        return null;
    }
}
package cn.yuntk.radio.ibook.common;

import android.app.Dialog;
import android.content.Context;


import java.lang.ref.WeakReference;

import cn.yuntk.radio.ibook.base.view.IBaseView;
import cn.yuntk.radio.ibook.widget.SimpleLoadingDialog;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 创建时间:2018/4/3
 * 创建人: yb
 * 描述:回调接口
 */

public abstract class DataCallBack<T> implements Observer<T> {
    private WeakReference<ILoadingAction> mAction;
    private Dialog dialog;

    public abstract void onSuccess(T data);

    public abstract void onError(int code, String msg);

    public void onCancel() {
    }

    public DataCallBack() {
    }

    public DataCallBack(IBaseView view) {
        if (view instanceof ILoadingAction)
            mAction = new WeakReference<>((ILoadingAction) view);
    }

    public boolean isActionNotNull() {
        return mAction != null && mAction.get() != null;
    }

    public Context getContext() {
        if (isActionNotNull()) {
            return mAction.get().getLoadingContext();
        }
        return null;
    }

    public void showDialog() {
        if (isActionNotNull() && mAction.get().showLoading()) {
            hideDialog();
            dialog = SimpleLoadingDialog.build(getContext());
        }
    }

    public void hideDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onComplete() {

    }
}

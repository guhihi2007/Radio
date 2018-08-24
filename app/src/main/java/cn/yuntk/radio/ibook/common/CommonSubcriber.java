package cn.yuntk.radio.ibook.common;

import cn.yuntk.radio.ibook.util.ExceptionHandle;
import cn.yuntk.radio.ibook.util.loger.Loger;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 创建时间:2018/4/3
 * 创建人: yb
 * 描述:
 */

public class CommonSubcriber<T> implements Observer<T> {

    private static final String TAG = "CommonSubcriber";

    private DataCallBack<T> mCallBack;

    public CommonSubcriber() {
    }

    public CommonSubcriber(DataCallBack<T> callBack) {
        mCallBack = callBack;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (mCallBack != null) {
            mCallBack.showDialog();
            mCallBack.onSubscribe(d);
        }
    }

    @Override
    public void onNext(T t) {
        if (mCallBack != null) {
            mCallBack.hideDialog();
            mCallBack.onSuccess(t);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (mCallBack != null) {
            mCallBack.hideDialog();

            Loger.w(TAG,e.getMessage());

            if (e instanceof Exception) {
//            mListener.onError(ExceptionHandle.handleException(throwable));
                ExceptionHandle.handleException(e);
            } else {
//            mListener.onError(throwable);
            }
        }
    }

    @Override
    public void onComplete() {
        mCallBack.onComplete();
    }
}

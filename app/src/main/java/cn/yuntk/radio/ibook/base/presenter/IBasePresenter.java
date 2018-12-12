package cn.yuntk.radio.ibook.base.presenter;


import cn.yuntk.radio.ibook.base.view.IBaseView;

/**
 * Created by yb on 2018/4/2.
 */

public interface IBasePresenter {
    void attachView(IBaseView view);
    void detachView();
}

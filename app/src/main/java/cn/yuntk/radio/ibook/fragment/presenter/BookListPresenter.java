package cn.yuntk.radio.ibook.fragment.presenter;

import android.content.Context;

import cn.yuntk.radio.ibook.api.BookApi;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.BookListBean;
import cn.yuntk.radio.ibook.fragment.model.IBookListModel;
import cn.yuntk.radio.ibook.fragment.view.IBookListView;
import cn.yuntk.radio.ibook.util.LogUtils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BookListPresenter extends BasePresenter<IBookListView,IBookListModel> {

    private Context mContext;
    private BookApi bookApi;

    @Inject
    public BookListPresenter(Context mContext, BookApi bookApi) {
        this.mContext = mContext;
        this.bookApi = bookApi;
    }

    public void getBookList(String flag){
        Observable<BookListBean> o = bookApi.getRecommend(flag);
        o.subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
//         .subscribe(new CommonSubcriber<BookListBean>(callBack));
         .subscribe(new Observer<BookListBean>() {
             @Override
             public void onSubscribe(Disposable d) {
                 LogUtils.showLog("onSubscribe:"+d.toString());
             }

             @Override
             public void onNext(BookListBean bookListBean) {
                 LogUtils.showLog("onNext:");
                 if (obtainView()!=null){
                     obtainView().refreshSuccess(bookListBean);
                 }
             }

             @Override
             public void onError(Throwable e) {
                 LogUtils.showLog("onError:getBookList:"+e.toString());
                 if (obtainView()!=null){
                     obtainView().refreshFail(-1,e.getMessage());
                 }
             }

             @Override
             public void onComplete() {
                 LogUtils.showLog("onComplete:");
             }
         });
    }

}
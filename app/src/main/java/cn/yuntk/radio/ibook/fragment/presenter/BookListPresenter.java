package cn.yuntk.radio.ibook.fragment.presenter;

import android.content.Context;


import java.util.HashMap;

import javax.inject.Inject;

import cn.yuntk.radio.ibook.api.BookApi;
import cn.yuntk.radio.ibook.base.RootListBean;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.TCBean3;
import cn.yuntk.radio.ibook.fragment.model.IBookListModel;
import cn.yuntk.radio.ibook.fragment.model.IBookListModelImp;
import cn.yuntk.radio.ibook.fragment.view.IBookListView;
import cn.yuntk.radio.ibook.util.LogUtils;
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
        super(new IBookListModelImp());
        this.mContext = mContext;
        this.bookApi = bookApi;
    }

    public void getAlbumInRank(HashMap<String,String> map){
        Observable<RootListBean<TCBean3>> o = bookApi.getAlbumInRank(map);
        o.subscribeOn(Schedulers.io())
         .observeOn(AndroidSchedulers.mainThread())
//         .subscribe(new CommonSubcriber<BookListBean>(callBack));
         .subscribe(new Observer<RootListBean<TCBean3>>() {
             @Override
             public void onSubscribe(Disposable d) {
                 LogUtils.showLog("onSubscribe:"+d.toString());
             }

             @Override
             public void onNext(RootListBean<TCBean3> tc3) {
                 LogUtils.showLog("onNext:");
                 if (obtainView()!=null){
                     obtainView().refreshSuccess(tc3);
                 }
             }

             @Override
             public void onError(Throwable e) {
                 LogUtils.showLog("onError:getBookList:"+e.toString());
                 if (obtainView()!=null){
                     if (e instanceof com.google.gson.JsonSyntaxException){
                         obtainView().refreshFail(-2,e.getMessage());
                     }else {
                         obtainView().refreshFail(-1,e.getMessage());
                     }
                 }
             }

             @Override
             public void onComplete() {
                 LogUtils.showLog("onComplete:");
             }
         });
    }

    public void getAlbumByBookTypeid(HashMap<String,String> map){
        Observable<RootListBean<TCBean3>> o = bookApi.getAlbumByBookTypeid(map);
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//         .subscribe(new CommonSubcriber<BookListBean>(callBack));
                .subscribe(new Observer<RootListBean<TCBean3>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.showLog("onSubscribe:"+d.toString());
                    }

                    @Override
                    public void onNext(RootListBean<TCBean3> tc3) {
                        LogUtils.showLog("onNext:");
                        if (obtainView()!=null){
                            obtainView().refreshSuccess(tc3);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.showLog("onError:getBookList:"+e.toString());
                        if (obtainView()!=null){
                            if (e instanceof com.google.gson.JsonSyntaxException){
                                obtainView().refreshFail(-2,e.getMessage());
                            }else {
                                obtainView().refreshFail(-1,e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.showLog("onComplete:");
                    }
                });
    }

}
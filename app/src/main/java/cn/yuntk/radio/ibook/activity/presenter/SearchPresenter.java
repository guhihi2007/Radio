package cn.yuntk.radio.ibook.activity.presenter;

import android.content.Context;


import java.util.HashMap;

import javax.inject.Inject;

import cn.yuntk.radio.ibook.activity.model.ISearchModel;
import cn.yuntk.radio.ibook.activity.model.ISearchModelImp;
import cn.yuntk.radio.ibook.activity.view.ISearchView;
import cn.yuntk.radio.ibook.api.BookApi;
import cn.yuntk.radio.ibook.base.RootListBean;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.TCBean3;
import cn.yuntk.radio.ibook.util.LogUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchPresenter extends BasePresenter<ISearchView,ISearchModel> {

    private Context mContext;
    private BookApi bookApi;

    @Inject
    public SearchPresenter(Context mContext, BookApi bookApi) {
        super(new ISearchModelImp());
        this.mContext = mContext;
        this.bookApi = bookApi;
    }

    public void serchAlbumForKeyword(String key){
        //model.serchAlbumForKeyword();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("keyword", key);
        map.put("oauth_token", "");
        Observable<RootListBean<TCBean3>> o = bookApi.serchAlbumForKeyword(map);
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RootListBean<TCBean3>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.showLog("onSubscribe:"+d.toString());
                    }

                    @Override
                    public void onNext(RootListBean<TCBean3> bean) {
                        LogUtils.showLog("onNext:");
                        try {
                            if (obtainView() !=null){
                                obtainView().refreshSuccess(bean);
                            }
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.showLog("onError:getMp3Url:"+e.toString());
                        try{
                            if (obtainView()!=null){
                                obtainView().refreshFail(-1,e.getMessage());
                            }
                        }catch (Exception ex){

                        }
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.showLog("onComplete:");
                    }
                });

    }
}
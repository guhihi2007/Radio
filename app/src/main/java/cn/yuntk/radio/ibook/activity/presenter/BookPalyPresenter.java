package cn.yuntk.radio.ibook.activity.presenter;

import android.content.Context;


import java.util.HashMap;

import javax.inject.Inject;

import cn.yuntk.radio.ibook.activity.model.IBookPlayModel;
import cn.yuntk.radio.ibook.activity.model.IBookPlayModelImp;
import cn.yuntk.radio.ibook.activity.view.IBookPlayView;
import cn.yuntk.radio.ibook.api.BookApi;
import cn.yuntk.radio.ibook.base.RootBean;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.TCBean6;
import cn.yuntk.radio.ibook.util.LogUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/** 获取音频地址 */
public class BookPalyPresenter extends BasePresenter<IBookPlayView,IBookPlayModel> {

    private Context mContext;
    private BookApi bookApi;

    @Inject
    public BookPalyPresenter(Context mContext, BookApi bookApi) {
        super(new IBookPlayModelImp());
        this.mContext = mContext;
        this.bookApi = bookApi;
    }

    public void getPlayCdn(HashMap<String,String> map){
        Observable<RootBean<TCBean6>> o = bookApi.getPlayCdn(map);
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RootBean<TCBean6>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.showLog("onSubscribe:"+d.toString());
                    }

                    @Override
                    public void onNext(RootBean<TCBean6> bean) {
                        LogUtils.showLog("onNext:");
                        try {
                            if (obtainView() !=null){
                                obtainView().getSuccess(bean);
                            }
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.showLog("onError:getMp3Url:"+e.toString());
                        try{
                            if (obtainView()!=null){
                                obtainView().getFail(-1,e.getMessage());
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
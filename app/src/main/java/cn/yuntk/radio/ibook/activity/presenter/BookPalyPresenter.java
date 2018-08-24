package cn.yuntk.radio.ibook.activity.presenter;

import android.content.Context;


import java.util.Map;

import javax.inject.Inject;

import cn.yuntk.radio.ibook.activity.view.IBookPlayView;
import cn.yuntk.radio.ibook.api.BookApi;
import cn.yuntk.radio.ibook.base.model.IBaseModel;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.Mp3urlBean;
import cn.yuntk.radio.ibook.util.LogUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*获取音频地址*/
public class BookPalyPresenter extends BasePresenter<IBookPlayView,IBaseModel> {

    private Context mContext;
    private BookApi bookApi;

    @Inject
    public BookPalyPresenter(Context mContext, BookApi bookApi) {
        this.mContext = mContext;
        this.bookApi = bookApi;
    }

    public void getMp3Url(Map<String,String> map){
        Observable<Mp3urlBean> o = bookApi.getMp3Url(map);
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Mp3urlBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.showLog("onSubscribe:"+d.toString());
                    }

                    @Override
                    public void onNext(Mp3urlBean bean) {
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

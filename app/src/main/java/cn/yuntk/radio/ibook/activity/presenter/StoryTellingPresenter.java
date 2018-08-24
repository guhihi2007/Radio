package cn.yuntk.radio.ibook.activity.presenter;

import android.content.Context;

import cn.yuntk.radio.ibook.activity.model.IStoryTellingModel;
import cn.yuntk.radio.ibook.activity.view.IStoryTellingListView;
import cn.yuntk.radio.ibook.api.BookApi;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.PsListBean;
import cn.yuntk.radio.ibook.util.LogUtils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*评书列表*/
public class StoryTellingPresenter extends BasePresenter<IStoryTellingListView,IStoryTellingModel> {

    private Context mContext;
    private BookApi bookApi;

    @Inject
    public StoryTellingPresenter(Context mContext, BookApi bookApi) {
        this.mContext = mContext;
        this.bookApi = bookApi;
    }


    public void getPsList(String id){
        Observable<PsListBean> o = bookApi.getPsList(id);
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<PsListBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.showLog("onSubscribe:"+d.toString());
                    }

                    @Override
                    public void onNext(PsListBean bean) {
                        LogUtils.showLog("onNext:");
                        try {
                            if (obtainView() !=null){
                                obtainView().refreshSuccess(bean);
                            }
                        }catch (Exception e){
                            LogUtils.showLog("onNext:"+e.toString());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.showLog("onError:getPsList:"+e.toString());
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

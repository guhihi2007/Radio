package cn.yuntk.radio.ibook.activity.presenter;

import android.content.Context;

import java.util.HashMap;

import javax.inject.Inject;

import cn.yuntk.radio.ibook.activity.model.IBookDetailModel;
import cn.yuntk.radio.ibook.activity.model.IBookDetailModelImp;
import cn.yuntk.radio.ibook.activity.view.IBookDetailView;
import cn.yuntk.radio.ibook.api.BookApi;
import cn.yuntk.radio.ibook.base.RootBean;
import cn.yuntk.radio.ibook.base.RootListBean;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.TCBean4;
import cn.yuntk.radio.ibook.bean.TCBean5;
import cn.yuntk.radio.ibook.util.LogUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**小说详情*/
public class BookDetailPresenter extends BasePresenter<IBookDetailView,IBookDetailModel> {

    private Context mContext;
    private BookApi bookApi;

    @Inject
    public BookDetailPresenter(Context mContext, BookApi bookApi) {
        super(new IBookDetailModelImp());
        this.mContext = mContext;
        this.bookApi = bookApi;
    }

    //    获取专辑详情
    public void getAlbumDetail(HashMap<String,String> map){
        Observable<RootBean<TCBean4>> o = bookApi.getAlbumDetail(map);
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RootBean<TCBean4>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.showLog("onSubscribe:"+d.toString());
                    }

                    @Override
                    public void onNext(RootBean<TCBean4> bean) {
                        LogUtils.showLog("onNext:");
                        try{
                            if (obtainView()!=null){
                                obtainView().refreshSuccess(bean);
                            }
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.showLog("onError:getBookInfo:"+e.toString());
                        try {
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

    //获取声音列表
    public void getAlbumTracks(HashMap<String,String> map){
        Observable<RootListBean<TCBean5>> o = bookApi.getAlbumTracks(map);
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<RootListBean<TCBean5>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.showLog("onSubscribe:"+d.toString());
                    }

                    @Override
                    public void onNext(RootListBean<TCBean5> bean) {
                        LogUtils.showLog("onNext:");
                        try{
                            if (obtainView()!=null){
                                obtainView().refreshSuccess(bean);
                            }
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.showLog("onError:getBookInfo:"+e.toString());
                        try {
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
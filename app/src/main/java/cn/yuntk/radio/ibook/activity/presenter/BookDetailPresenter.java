package cn.yuntk.radio.ibook.activity.presenter;

import android.content.Context;

import javax.inject.Inject;

import cn.yuntk.radio.ibook.activity.model.IBookDetailModel;
import cn.yuntk.radio.ibook.activity.view.IBookDetailView;
import cn.yuntk.radio.ibook.api.BookApi;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.BookDetailBean;
import cn.yuntk.radio.ibook.util.LogUtils;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*小说详情*/
public class BookDetailPresenter extends BasePresenter<IBookDetailView,IBookDetailModel> {

    private Context mContext;
    private BookApi bookApi;

    @Inject
    public BookDetailPresenter(Context mContext, BookApi bookApi) {
        this.mContext = mContext;
        this.bookApi = bookApi;
    }

    public void getBookInfo(String id){
        Observable<BookDetailBean> o = bookApi.getBookInfo(id);
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BookDetailBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.showLog("onSubscribe:"+d.toString());
                    }

                    @Override
                    public void onNext(BookDetailBean bean) {
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
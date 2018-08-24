package cn.yuntk.radio.ibook.fragment.presenter;

import android.content.Context;

import cn.yuntk.radio.ibook.api.BookApi;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.ClassifyListBean;
import cn.yuntk.radio.ibook.fragment.model.IClassifyListModel;
import cn.yuntk.radio.ibook.fragment.view.IClassifyListView;
import cn.yuntk.radio.ibook.util.LogUtils;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*分类请求 */
public class ClassifyListPresenter extends BasePresenter<IClassifyListView,IClassifyListModel> {

    private Context mContext;
    private BookApi bookApi;

    @Inject
    public ClassifyListPresenter(Context mContext, BookApi bookApi) {
        this.mContext = mContext;
        this.bookApi = bookApi;
    }

    public void getClassifyList(){
        Observable<ClassifyListBean> o = bookApi.getClassifyList();
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//         .subscribe(new CommonSubcriber<BookListBean>(callBack));
                .subscribe(new Observer<ClassifyListBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.showLog("onSubscribe:"+d.toString());
                    }

                    @Override
                    public void onNext(ClassifyListBean bean) {
                        LogUtils.showLog("onNext:");
                        if (obtainView()!=null){
                            obtainView().refreshSuccess(bean);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.showLog("onError:getClassifyList:"+e.toString());
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

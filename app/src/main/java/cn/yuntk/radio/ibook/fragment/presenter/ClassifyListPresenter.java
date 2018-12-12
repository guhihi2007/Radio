package cn.yuntk.radio.ibook.fragment.presenter;

import android.content.Context;


import java.util.HashMap;

import javax.inject.Inject;

import cn.yuntk.radio.ibook.api.BookApi;
import cn.yuntk.radio.ibook.base.RootBean;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.TCBean1;
import cn.yuntk.radio.ibook.fragment.model.IClassifyListModel;
import cn.yuntk.radio.ibook.fragment.model.IClassifyListModelImp;
import cn.yuntk.radio.ibook.fragment.view.IClassifyListView;
import cn.yuntk.radio.ibook.util.LogUtils;
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
        super(new IClassifyListModelImp());
        this.mContext = mContext;
        this.bookApi = bookApi;
    }

    public void getBookTypeList(HashMap<String,String> map){
        Observable<RootBean<TCBean1>> o = bookApi.getBookTypeList(map);
        o.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//         .subscribe(new CommonSubcriber<BookListBean>(callBack));
                .subscribe(new Observer<RootBean<TCBean1>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        LogUtils.showLog("onSubscribe:"+d.toString());
                    }

                    @Override
                    public void onNext(RootBean<TCBean1> bean) {
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

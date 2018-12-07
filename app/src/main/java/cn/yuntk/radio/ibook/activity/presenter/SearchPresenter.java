package cn.yuntk.radio.ibook.activity.presenter;

import android.content.Context;

import cn.yuntk.radio.ibook.activity.model.ISearchModel;
import cn.yuntk.radio.ibook.activity.view.ISearchView;
import cn.yuntk.radio.ibook.api.BaseOkhttp;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.SearchBean;
import cn.yuntk.radio.ibook.common.Api;
import cn.yuntk.radio.ibook.util.GsonUtils;
import cn.yuntk.radio.ibook.util.LogUtils;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class SearchPresenter extends BasePresenter<ISearchView,ISearchModel> {

    private Context mContext;

    @Inject
    public SearchPresenter(Context mContext) {
        this.mContext = mContext;
    }

    public void serchBookForKeyword(String key){
        Map<String, String> map = new HashMap<String, String>();
        map.put("keyword", key);
        BaseOkhttp.getInstance().getSearch(
                Api.APP_BASE_URL_SEACHER + Api.BOOK_SEARCH,
                map, new BaseOkhttp.RequestCallback() {
                    @Override
                    public void onSuccess(String response) {
                        try{
                            if (obtainView()!=null){
                                SearchBean searchBean = GsonUtils.parseObject(response,SearchBean.class);
                                obtainView().searchSuccess(searchBean);
                            }
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onFailure(String msg, Exception e) {
                        try{
                            LogUtils.showLog("onFailure:getClassifyList:"+e.toString());
                            obtainView().searchFailear(e.toString());
                        }catch (Exception ex){

                        }
                    }
                });
    }
}
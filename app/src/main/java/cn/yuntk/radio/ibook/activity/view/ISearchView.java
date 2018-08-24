package cn.yuntk.radio.ibook.activity.view;

import cn.yuntk.radio.ibook.base.view.IBaseView;
import cn.yuntk.radio.ibook.bean.SearchBean;

public interface ISearchView extends IBaseView {
    void searchSuccess(SearchBean searchBean);
    void searchFailear(String fail);
}

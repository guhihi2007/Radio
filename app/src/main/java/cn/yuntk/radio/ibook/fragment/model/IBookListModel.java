package cn.yuntk.radio.ibook.fragment.model;

import cn.yuntk.radio.ibook.base.model.IBaseModel;
import cn.yuntk.radio.ibook.bean.BookListBean;
import cn.yuntk.radio.ibook.common.DataCallBack;

public interface IBookListModel extends IBaseModel{
    void getBookList(int pageNo, int pageSize, String param, DataCallBack<BookListBean> dataCallBack);
}

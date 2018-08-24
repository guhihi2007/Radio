package cn.yuntk.radio.ibook.fragment.model;

import cn.yuntk.radio.ibook.base.model.IBaseModel;
import cn.yuntk.radio.ibook.bean.ClassifyListBean;
import cn.yuntk.radio.ibook.common.DataCallBack;

public interface IClassifyListModel extends IBaseModel{
    void getClassifyList(int pageNo, int pageSize, String param, DataCallBack<ClassifyListBean> dataCallBack);
}

package cn.yuntk.radio.ibook.activity.view;

import cn.yuntk.radio.ibook.base.view.IBaseView;
import cn.yuntk.radio.ibook.bean.Mp3urlBean;

public interface IBookPlayView extends IBaseView {
    void getSuccess(Mp3urlBean data);
    void getFail(int code, String msg);
}

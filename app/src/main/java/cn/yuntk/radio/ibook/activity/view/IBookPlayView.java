package cn.yuntk.radio.ibook.activity.view;



import cn.yuntk.radio.ibook.base.RootJson;
import cn.yuntk.radio.ibook.base.view.IBaseView;

public interface IBookPlayView<T extends RootJson> extends IBaseView {

    void getSuccess(T t);

    void getFail(int code, String msg);

}

package cn.yuntk.radio.ibook.base.refresh;


import cn.yuntk.radio.ibook.base.view.IBaseView;
import cn.yuntk.radio.ibook.bean.PageInfo;

/**
 * <p>类描述：</p>
 * <p>创建人：yb</p>
 * <p>创建时间：2018/4/3</p>
 */
public interface IBaseRefreshView<T extends PageInfo> extends IBaseView {
    /**
     * 加载数据
     * @param pageNo
     * @param pageSize
     * @param isLoadMore true 加载更多  false 下拉刷新
     */
    void refreshData(int pageNo, int pageSize, boolean isLoadMore);
    //加载更多
//    void loadMoreData(int pageNo,int pageSize);

    void refreshSuccess(T data);
    void refreshFail(int code, String msg);

    void emptyView(boolean isEmpty);
}

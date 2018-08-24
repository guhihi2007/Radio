package cn.yuntk.radio.ibook.base.refresh;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import cn.yuntk.radio.ibook.base.BaseTitleActivity;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.PageInfo;
import cn.yuntk.radio.ibook.common.refresh.BaseRefreshHelper;
import cn.yuntk.radio.ibook.common.refresh.RefreshConfig;

/**
 * 创建时间:2018/4/3
 * 创建人: yb
 * 描述:
 */

public abstract class BaseRefreshActivity<T extends BasePresenter,P extends PageInfo> extends BaseTitleActivity<T>
        implements IBaseRefreshView<P> {

    private BaseRefreshHelper<P> mRefreshHelper;
    private SmartRefreshLayout refreshLayout;

    protected void initRefreshView(SmartRefreshLayout refreshLayout, RefreshConfig config) {
        this.refreshLayout = refreshLayout;
        if (mRefreshHelper == null)
            mRefreshHelper = new BaseRefreshHelper<>(this,config == null ? defaultConfig() : config);
        mRefreshHelper.initRefresh(refreshLayout);
    }

    private RefreshConfig defaultConfig() {
        return RefreshConfig.create();
    }

//    //下拉刷新
//    public void refreshData(int pageNo,int pageSize) {
//
//    }
//    //加载更多
//    public void loadMoreData(int pageNo,int pageSize) {
//
//    }

    public final void refreshSuccess(P data) {
        if (mRefreshHelper!=null){
            mRefreshHelper.updateLoadMore(data);
            mRefreshHelper.finishRefresh(refreshLayout);
            mRefreshHelper.finishLoadMore(refreshLayout);
        }
//        refreshUISuccess(data,mRefreshHelper.isLoadMore());
        refreshUISuccess(data,false);
    }

    public final void refreshFail(int code,String msg) {
        if (mRefreshHelper!=null){
            mRefreshHelper.finishRefresh(refreshLayout);
            mRefreshHelper.finishLoadMore(refreshLayout);
            mRefreshHelper.reduction();
        }
        refreshUIFail(code,msg);
    }

    /**
     * autoRefresh
     */
    protected void executeRefresh() {
        mRefreshHelper.executeRefresh(refreshLayout);
    }

    /**
     * 非autoRefresh
     */
    protected void manualRefresh() {
        mRefreshHelper.manualRefresh();
    }

    protected abstract void refreshUISuccess(P data,boolean isLoadMore);

    protected abstract void refreshUIFail(int code,String msg);


    @Override
    public void emptyView(boolean isEmpty) {
    }

    @Override
    public boolean showLoading() {
        return mRefreshHelper.isManualRefresh();
    }
}

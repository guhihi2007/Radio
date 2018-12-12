package cn.yuntk.radio.ibook.base.refresh;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.PageInfo;
import cn.yuntk.radio.ibook.common.ILoadingAction;
import cn.yuntk.radio.ibook.common.refresh.BaseRefreshHelper;
import cn.yuntk.radio.ibook.common.refresh.RefreshConfig;

/**
 * 创建时间:2018/11/13
 * 创建人: xue
 * 描述:基础刷新碎片
 */
public abstract class BaseRefreshFragment<T extends BasePresenter,P extends PageInfo> extends BaseFragment<T>
        implements IBaseRefreshView<P>,ILoadingAction {

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


    public final void refreshSuccess(P data) {
        if (mRefreshHelper!=null){
            mRefreshHelper.updateLoadMore(data);
            mRefreshHelper.finishRefresh(refreshLayout);
            mRefreshHelper.finishLoadMore(refreshLayout);
        }
        assert mRefreshHelper != null;
        refreshUISuccess(data,mRefreshHelper.isLoadMore());
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
    public boolean showLoading() {
        return mRefreshHelper.isManualRefresh();
    }

}

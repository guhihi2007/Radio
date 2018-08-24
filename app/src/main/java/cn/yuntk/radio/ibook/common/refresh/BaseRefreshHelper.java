package cn.yuntk.radio.ibook.common.refresh;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import cn.yuntk.radio.ibook.base.refresh.IBaseRefreshView;
import cn.yuntk.radio.ibook.bean.PageInfo;

import java.lang.ref.WeakReference;


/**
 * <p>类描述：</p>
 * <p>创建人：yb</p>
 * <p>创建时间：2018/4/3</p>
 */
public class BaseRefreshHelper<T extends PageInfo> {
    public static final int LOAD_STATUS_REFRESH = 0;//下拉刷新
    public static final int LOAD_STATUS_MORE = 1;//加载更多
    private int status = LOAD_STATUS_REFRESH;
    private boolean hasMore = false;//是否有更多数据
    private WeakReference<IBaseRefreshView> mRefreshView;
    private RefreshConfig mConfig;
    private int pageNo = 1;
    private int oldPageNo;

    private boolean isManualRefresh = true;


    public BaseRefreshHelper(IBaseRefreshView refreshView, RefreshConfig refreshConfig) {
        mRefreshView = new WeakReference<IBaseRefreshView>(refreshView);
        mConfig = refreshConfig;
    }

    public IBaseRefreshView obtainRefresh() {
        if (mRefreshView != null)
            return mRefreshView.get();
        return null;
    }

    public void initRefresh(SmartRefreshLayout refreshLayout) {
        refreshLayout.setEnableRefresh(mConfig.isEnableRefresh());
        if (mConfig.isEnableRefresh()) {
            refreshLayout.setOnRefreshListener(refreshLayout1 -> {
                isManualRefresh = false;
                refresh();
            });
        }
        if (mConfig.isEnableLoadMore()) {
            refreshLayout.setOnLoadmoreListener(refreshLayout12 -> {
                isManualRefresh = false;
                if (obtainRefresh() != null) {
                    status = LOAD_STATUS_MORE;
                    oldPageNo = pageNo;
                    pageNo++;
                    obtainRefresh().refreshData(pageNo, mConfig.getPageSize(), true);
                }
            });
        }
        if (mConfig != null && mConfig.isAutoRefresh())
            refreshLayout.autoRefresh();
    }

    public void manualRefresh() {
        isManualRefresh = true;
        refresh();
    }

    public void refresh() {
        if (obtainRefresh() != null) {
            status = LOAD_STATUS_REFRESH;
            oldPageNo = pageNo;
            pageNo = 1;
            obtainRefresh().refreshData(pageNo, mConfig.getPageSize(), false);
        }
    }

    public void executeRefresh(SmartRefreshLayout refreshLayout) {
        if (obtainRefresh() != null)
            refreshLayout.autoRefresh();
    }

    /**
     * 判断是否有更多数据
     *
     * @param pageInfo
     */
    public void updateLoadMore(T pageInfo) {
        if (pageInfo == null) {
            empty(true);
            return;
        }
        empty(pageInfo.getSize() == 0);
        int pages = pageInfo.getPages();
        hasMore = pages > pageNo;
    }

    private void empty(boolean empty) {
        if (obtainRefresh() != null && !isLoadMore())
            obtainRefresh().emptyView(empty);
    }

    /**
     * 结束刷新UI
     *
     * @param refreshLayout
     */
    public void finishRefresh(SmartRefreshLayout refreshLayout) {
        if (status == LOAD_STATUS_REFRESH) {
            refreshLayout.finishRefresh();
            if (hasMore) {
                refreshLayout.resetNoMoreData();
            } else {
                refreshLayout.setLoadmoreFinished(true);
            }
        }
    }

    /**
     * 结束加载更多
     *
     * @param refreshLayout
     */
    public void finishLoadMore(SmartRefreshLayout refreshLayout) {
        if (status == LOAD_STATUS_MORE) {
            if (hasMore) {
                refreshLayout.finishLoadmore();
            } else {
                refreshLayout.finishLoadmoreWithNoMoreData();
            }
        }
    }

    /**
     * 刷新失败  还原pageNo
     */
    public void reduction() {
        pageNo = Math.max(1, oldPageNo);
    }

    public int getStatus() {
        return status;
    }

    //是否是加载更多操作
    public boolean isLoadMore() {
        return status == LOAD_STATUS_MORE;
    }


    public boolean isAutoRefresh() {
        return mConfig.isAutoRefresh();
    }

    public boolean isManualRefresh() {
        return isManualRefresh;
    }
}

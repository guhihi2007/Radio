package cn.yuntk.radio.ibook.common.refresh;



import cn.yuntk.radio.ibook.common.TingConstants;

/**
 * <p>类描述：</p>
 * <p>创建人：yb</p>
 * <p>创建时间：2018/1/10</p>
 * <p>修改人：       </p>
 * <p>修改时间：   </p>
 * <p>修改备注：   </p>
 */
public class RefreshConfig {
    private int pageSize = TingConstants.PAGE_SIZE;
    private boolean autoRefresh = false;
    private boolean enableLoadMore = true;//是否启用加载更多
    private boolean enableRefresh = true;//是否启用下拉刷新

    public static RefreshConfig create() {
        return new RefreshConfig();
    }

    public int getPageSize() {
        return pageSize;
    }

    public RefreshConfig pageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    public RefreshConfig autoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
        return this;
    }

    public boolean isEnableLoadMore() {
        return enableLoadMore;
    }

    public RefreshConfig enableLoadMore(boolean enableLoadMore) {
        this.enableLoadMore = enableLoadMore;
        return this;
    }

    public boolean isEnableRefresh() {
        return enableRefresh;
    }

    public RefreshConfig enableRefresh(boolean enableRefresh) {
        this.enableRefresh = enableRefresh;
        return this;
    }
}

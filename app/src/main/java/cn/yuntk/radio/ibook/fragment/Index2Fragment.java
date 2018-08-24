package cn.yuntk.radio.ibook.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.activity.BookSearchActivtity;
import cn.yuntk.radio.ibook.activity.StoryTellingListActivity;
import cn.yuntk.radio.ibook.adapter.ClassifiesAdapter;
import cn.yuntk.radio.ibook.adapter.RvItemClickInterface;
import cn.yuntk.radio.ibook.ads.ADConstants;
import cn.yuntk.radio.ibook.ads.AdController;
import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.bean.ClassifyListBean;
import cn.yuntk.radio.ibook.cache.ACache;
import cn.yuntk.radio.ibook.common.Api;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.component.DaggerBookComponent;
import cn.yuntk.radio.ibook.fragment.presenter.ClassifyListPresenter;
import cn.yuntk.radio.ibook.fragment.view.IClassifyListView;
import cn.yuntk.radio.ibook.util.GsonUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.NetworkUtils;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;
import cn.yuntk.radio.ibook.widget.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class Index2Fragment extends BaseFragment<ClassifyListPresenter> implements RvItemClickInterface, IClassifyListView {

    @BindView(R.id.search_tv)
    TextView searchTv;
    @BindView(R.id.listener_title_tv)
    TextView titleTv;
    @BindView(R.id.game_tv)
    TextView gameTv;
    @BindView(R.id.rv_subject)
    EmptyRecyclerView rvSubject;
    @BindView(R.id.no_data_ll)
    LinearLayout noDataLl;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.ad_container_fl)
    FrameLayout ad_container_fl;
    @BindView(R.id.comm_back_ll)
    LinearLayout comm_back_ll;

    ClassifiesAdapter adapter;
    List<ClassifyListBean.ClassifyType> types = new ArrayList<ClassifyListBean.ClassifyType>();

    ACache aCache;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_listener_index2;
    }

    @Override
    protected void initViews() {
        titleTv.setText(getString(R.string.classif));
        searchTv.setVisibility(View.GONE);
        comm_back_ll.setVisibility(View.VISIBLE);

        gameTv.setText("搜索");
        aCache = ACache.get(mContext);
    }

    @Override
    protected void bindEvent() {
        adapter = new ClassifiesAdapter(getActivity(),types,R.layout.listener_item_classify);
        adapter.setRvItemClickInterface(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        rvSubject.setEmptyView(noDataLl);
        rvSubject.setHasFixedSize(true);
        rvSubject.setNestedScrollingEnabled(false);
        rvSubject.setLayoutManager(linearLayoutManager);
        rvSubject.setAdapter(adapter);

        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableAutoLoadmore(false);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshData(1,100,false);
            }
        });
    }

    @Override
    protected void loadData() {
        refreshLayout.autoRefresh();
    }

    @Override
    protected ClassifyListPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @OnClick({R.id.search_tv, R.id.game_tv, R.id.comm_back_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_tv:
                break;
            case R.id.game_tv:
                BookSearchActivtity.jumpToSearch(mContext);
                break;
            case R.id.comm_back_ll:
                if (getActivity() != null)
                    getActivity().finish();
                break;
        }
    }

    @Override
    public void onItemClick(Object o) {
        ClassifyListBean.ClassifyItem book = (ClassifyListBean.ClassifyItem) o;
//        ToastUtil.showLongToast(book.getTitle());
        Intent intent = new Intent(mContext, StoryTellingListActivity.class);
        intent.putExtra("ps_id",book.getId()+"");
        intent.putExtra("title",book.getTitle()+"");
        startActivity(intent);
    }

    @Override
    public void refreshData(int pageNo, int pageSize, boolean isLoadMore) {
        LogUtils.showLog("refreshData:");
        if (!NetworkUtils.isConnected(mContext)){
            refreshFail(-1,"");
        }else {
            requestUrl();
        }
    }

    @Override
    public void refreshSuccess(ClassifyListBean data) {
        LogUtils.showLog("refreshSuccess:");
        if (data!=null&&data.getBook()!=null&&data.getBook().size()!=0){
            aCache.put(Api.BOOK_CLASSIFY, GsonUtils.parseToJsonString(data));
            types.clear();;
            types.addAll(data.getBook());
            adapter.notifyDataSetChanged();
        }else {
            emptyView(true);
        }
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadmore();
    }

    @Override
    public void refreshFail(int code, String msg) {
        LogUtils.showLog("refreshFail:");
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadmore();
        String json = aCache.getAsString(Api.BOOK_CLASSIFY);
        if (!StringUtils.isEmpty(json)){
            ClassifyListBean data = GsonUtils.parseObject(json,ClassifyListBean.class);
            refreshSuccess(data);
        }else {
        ToastUtil.showToast(getString(R.string.load_again));}
    }

    @Override
    public void emptyView(boolean isEmpty) {
        LogUtils.showLog("emptyView:");
    }

    //    loadurl
    private void requestUrl() {
        mPresenter.getClassifyList();
    }

    AdController builder;
    @Override
    public void onResume() {
        super.onResume();
        if (isVisible){
            builder = new AdController
                    .Builder(getActivity())
                    .setContainer(ad_container_fl)
                    .setPage(ADConstants.CATEGORY_PAGE)
                    .create();
            builder.show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (builder != null) {
            builder.destroy();
        }
    }
}

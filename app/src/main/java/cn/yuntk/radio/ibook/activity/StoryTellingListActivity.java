package cn.yuntk.radio.ibook.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.activity.presenter.StoryTellingPresenter;
import cn.yuntk.radio.ibook.activity.view.IStoryTellingListView;
import cn.yuntk.radio.ibook.adapter.ClassifyPsListAdapter;
import cn.yuntk.radio.ibook.adapter.RvItemClickInterface;
import cn.yuntk.radio.ibook.base.refresh.BaseRefreshActivity;
import cn.yuntk.radio.ibook.bean.PsListBean;
import cn.yuntk.radio.ibook.bean.PsUrlItemBean;
import cn.yuntk.radio.ibook.common.refresh.RefreshConfig;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.component.DaggerBookComponent;
import cn.yuntk.radio.ibook.util.ToastUtil;
import cn.yuntk.radio.ibook.widget.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class StoryTellingListActivity extends BaseRefreshActivity<StoryTellingPresenter, PsListBean>
        implements IStoryTellingListView, RvItemClickInterface {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.back_tv)
    TextView backTv;
    @BindView(R.id.back_ll)
    LinearLayout backLl;
    @BindView(R.id.next_tv)
    TextView nextTv;
    @BindView(R.id.next_iv)
    ImageView nextIv;
    @BindView(R.id.next_ll)
    LinearLayout nextLl;
    @BindView(R.id.listener_title_tv)
    TextView titleTv;
    @BindView(R.id.rv_subject)
    EmptyRecyclerView rv_subject;
    @BindView(R.id.no_data_ll)
    LinearLayout no_data_ll;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    ClassifyPsListAdapter adapter;
    List<PsUrlItemBean> timeListBeans = new ArrayList<>();

    String ps_id = "";
    String title = "";
    PsListBean bean;

    @Override
    protected void refreshUISuccess(PsListBean data, boolean isLoadMore) {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadmore();
        if (data != null && data.getUrl_list() != null && data.getUrl_list().size() != 0) {
            timeListBeans.clear();
            timeListBeans.addAll(data.getUrl_list());
            adapter.notifyDataSetChanged();
        }
        this.bean = data;
    }

    @Override
    protected void refreshUIFail(int code, String msg) {
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadmore();
        ToastUtil.showToast(getString(R.string.load_again));
    }

    @Override
    protected void initViews() {
        ps_id = getIntent().getStringExtra("ps_id");
        title = getIntent().getStringExtra("title");
        titleTv.setText(title);
        nextLl.setVisibility(View.GONE);
        adapter = new ClassifyPsListAdapter(this, timeListBeans, R.layout.listener_item_booklist);
        adapter.setRvItemClickInterface(this);
        rv_subject.setEmptyView(no_data_ll);
        rv_subject.setLayoutManager(new LinearLayoutManager(this));
        rv_subject.setAdapter(adapter);

        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableAutoLoadmore(false);

        initRefreshView(refreshLayout, RefreshConfig.create().pageSize(20).autoRefresh(true));

    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void loadData() {
        showLoading();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_listener_story_telling;
    }

    @Override
    protected StoryTellingPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void refreshData(int pageNo, int pageSize, boolean isLoadMore) {
        requestUrl();
    }

    //item点击
    @Override
    public void onItemClick(Object o) {
        PsUrlItemBean bean = (PsUrlItemBean) o;
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        intent.putExtra("bookid", bean.getU());
        intent.putExtra("booktitle", bean.getTitle());
        intent.putExtra("booktype", title);
        startActivity(intent);
    }

    //    loadurl
    private void requestUrl() {
        mPresenter.getPsList(ps_id);
    }


    @OnClick({R.id.back_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_ll:
                finish();
                break;
        }
    }

}
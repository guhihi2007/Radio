package cn.yuntk.radio.ibook.activity;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.activity.presenter.SearchPresenter;
import cn.yuntk.radio.ibook.activity.view.ISearchView;
import cn.yuntk.radio.ibook.adapter.RvItemClickInterface;
import cn.yuntk.radio.ibook.adapter.TCBooksAdapter;
import cn.yuntk.radio.ibook.base.RootListBean;
import cn.yuntk.radio.ibook.base.refresh.BaseRefreshActivity;
import cn.yuntk.radio.ibook.bean.TCBean3;
import cn.yuntk.radio.ibook.common.refresh.RefreshConfig;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.component.DaggerBookComponent;
import cn.yuntk.radio.ibook.service.Actions;
import cn.yuntk.radio.ibook.service.FloatViewService;
import cn.yuntk.radio.ibook.util.ToastUtil;

/**搜索结果列表页面*/
public class SearchResultActivtity extends BaseRefreshActivity<SearchPresenter,RootListBean<TCBean3>>
        implements
        RvItemClickInterface<TCBean3>,
        ISearchView
{

    @BindView(R.id.title_left_text)
    TextView title_left_text;
    @BindView(R.id.title_right_text)
    TextView title_right_text;
    @BindView(R.id.title_content_text)
    TextView title_content_text;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.result_rv)
    RecyclerView result_rv;

    private String key;

    TCBooksAdapter itemAdapter;
    public List<TCBean3> items = new ArrayList<TCBean3>();//存放搜索结果

    @Override
    protected void initViews() {

        key = getIntent().getStringExtra("Key");//关键词
        title_left_text.setText(getString(R.string.string_search));
        title_content_text.setText(key);
        title_right_text.setVisibility(View.GONE);

        itemAdapter = new TCBooksAdapter(this,items, R.layout.ting_item_booklist_new,"5");
        itemAdapter.setRvItemClickInterface(this);
        result_rv.setLayoutManager(new CustomLinearManager(this));
        result_rv.setAdapter(itemAdapter);

        initRefreshView(refreshLayout, RefreshConfig.create().enableRefresh(false).enableLoadMore(false));
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void loadData() {
       showProgressBarDialog("搜索中...");
        mPresenter.serchAlbumForKeyword(key);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ting_activity_search_result;
    }

    @Override
    protected SearchPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @OnClick({R.id.title_left_text})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.title_left_text:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatViewService.startCommand(mContext, Actions.SERVICE_VISABLE_WINDOW);
    }

    @Override
    public void onItemClick(TCBean3 tcBean3) {
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        intent.putExtra("bookid",tcBean3.getBookID());
        intent.putExtra("booktitle",tcBean3.getBookName());
        intent.putExtra("booktype",tcBean3.getBookType());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void refreshData(int pageNo, int pageSize, boolean isLoadMore) {

    }

    @Override
    public void loadMoreData(int pageNo, int pageSize) {

    }

    @Override
    protected void refreshUISuccess(RootListBean<TCBean3> data, boolean isLoadMore) {
        if (data!=null && data.getData()!=null && data.getData().size()!=0){
            if(!isLoadMore){
                items.clear();
            }
            items.addAll(data.getData());
        }else {
            ToastUtil.showToast("没有此书");
        }

        itemAdapter.notifyDataSetChanged();
        if (items.size()!=0){
            result_rv.setVisibility(View.VISIBLE);
        }else {
            result_rv.setVisibility(View.GONE);
        }
        hideProgress();
    }

        @Override
    protected void refreshUIFail(int code, String msg) {
        hideProgress();
        ToastUtil.showToast("没有此书");
    }

    @Override
    public void emptyView(boolean isEmpty) {

    }

}
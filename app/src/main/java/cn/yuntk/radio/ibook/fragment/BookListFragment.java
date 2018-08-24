package cn.yuntk.radio.ibook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.activity.BookDetailActivity;
import cn.yuntk.radio.ibook.adapter.BooksAdapter;
import cn.yuntk.radio.ibook.adapter.RvItemClickInterface;
import cn.yuntk.radio.ibook.ads.ADConstants;
import cn.yuntk.radio.ibook.ads.AdController;
import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.bean.BookListBean;
import cn.yuntk.radio.ibook.bean.ItemBookBean;
import cn.yuntk.radio.ibook.cache.ACache;
import cn.yuntk.radio.ibook.common.Api;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.component.DaggerBookComponent;
import cn.yuntk.radio.ibook.fragment.presenter.BookListPresenter;
import cn.yuntk.radio.ibook.fragment.view.IBookListView;
import cn.yuntk.radio.ibook.util.GsonUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.NetworkUtils;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;
import cn.yuntk.radio.ibook.widget.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BookListFragment extends BaseFragment<BookListPresenter>
        implements RvItemClickInterface,IBookListView {

    @BindView(R.id.ad_container_fl)
    FrameLayout ad_container_ll;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rv_subject)
    EmptyRecyclerView rv_subject;
    @BindView(R.id.no_data_ll)
    LinearLayout no_data_ll;

    BooksAdapter adapter;
    List<ItemBookBean> timeListBeans = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private ACache aCache;

    // TODO: Rename and change types and number of parameters
    public static BookListFragment newInstance(String param1, String param2) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        return R.layout.fragment_listener_booklist;
    }

    @Override
    protected void initViews() {
        LogUtils.showLog("BookListFragment:"+mParam1);
    }

    @Override
    protected void bindEvent() {
        aCache = ACache.get(mContext);
        adapter = new BooksAdapter(getActivity(), timeListBeans,R.layout.listener_item_booklist);
        adapter.setRvItemClickInterface(this);
        rv_subject.setEmptyView(no_data_ll);
        rv_subject.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_subject.setAdapter(adapter);

        //设置 Header 为 Material风格
        //smartLayout.setRefreshHeader(new MaterialHeader(this).setShowBezierWave(true));
        //smartLayout.setRefreshHeader(new BezierCircleHeader(this));

        //设置 Footer 为 球脉冲
        //smartLayout.setRefreshFooter(
        //        new BallPulseFooter(this)
        //                .setSpinnerStyle(SpinnerStyle.Scale)
        //                .setAnimatingColor(Color.parseColor("#fff4511e"))
        //                .setNormalColor(Color.parseColor("#ff0000")));

        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableAutoLoadmore(false);

        refreshLayout.setOnRefreshListener(refreshlayout -> refreshData(1,100,false));

        //        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
//            @Override
//            public void onLoadmore(RefreshLayout refreshlayout) {
//                refreshData(1,100,false);
//            }
//        });

    }

    @Override
    protected void loadData() {
//        requestUrl();
        refreshLayout.autoRefresh();
    }

    @Override
    protected BookListPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    //recycleriew的item点击
    @Override
    public void onItemClick(Object o) {
        ItemBookBean book = (ItemBookBean) o;
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        intent.putExtra("bookid",book.getId());
        intent.putExtra("booktitle",book.getTitle());
        intent.putExtra("booktype",mParam2);
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
    public void refreshSuccess(BookListBean data) {
        LogUtils.showLog("refreshSuccess:");
        if (data==null){
            emptyView(true);
        }else {
//            缓存首页列表
            aCache.put(mParam1, GsonUtils.parseToJsonString(data));
            switch (mParam1){
                case Api.BOOK_LIST1:
                    if (data.getTime_list()==null||data.getTime_list().size()==0){
                        emptyView(true);
                    }else {
                        timeListBeans.clear();
                        timeListBeans.addAll(data.getTime_list());
                    }
                    break;
                case Api.BOOK_LIST2:
                    if (data.getPs_list()==null||data.getPs_list().size()==0){
                        emptyView(true);
                    }else {
                        timeListBeans.clear();
                        timeListBeans.addAll(data.getPs_list());
                    }
                    break;
                case Api.BOOK_LIST3:
                    if (data.getYs_list()==null||data.getYs_list().size()==0){
                        emptyView(true);
                    }else {
                        timeListBeans.clear();
                        timeListBeans.addAll(data.getYs_list());
                    }
                    break;
                case Api.BOOK_LIST4:
                    if (data.getLz_list()==null||data.getLz_list().size()==0){
                        emptyView(true);
                    }else {
                        timeListBeans.clear();
                        timeListBeans.addAll(data.getLz_list());
                    }
                    break;
            }
            adapter.notifyDataSetChanged();
        }
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadmore();
    }

    @Override
    public void refreshFail(int code, String msg) {
        LogUtils.showLog("refreshFail:");
        refreshLayout.finishRefresh();
        refreshLayout.finishLoadmore();
        String json = aCache.getAsString(mParam1);
        if (!StringUtils.isEmpty(json)){
            BookListBean data = GsonUtils.parseObject(json,BookListBean.class);
            refreshSuccess(data);
        }else {
            ToastUtil.showToast(getString(R.string.load_again));
        }
    }

    @Override
    public void emptyView(boolean isEmpty) {
        LogUtils.showLog("emptyView:");
    }

    //    loadurl
    private void requestUrl(){
        mPresenter.getBookList(mParam1);
    }

    AdController builder;
    String fragment_tag;
    @Override
    public void onResume() {
        super.onResume();
        fragment_tag =ADConstants.HOME_PAGE_LIST1;
        switch (mParam1){
            case Api.BOOK_LIST1:
                fragment_tag =ADConstants.HOME_PAGE_LIST1;
                break;
            case Api.BOOK_LIST2:
                fragment_tag =ADConstants.HOME_PAGE_LIST2;
                break;
            case Api.BOOK_LIST3:
                fragment_tag =ADConstants.HOME_PAGE_LIST3;
                break;
            case Api.BOOK_LIST4:
                fragment_tag =ADConstants.HOME_PAGE_LIST4;
                break;
        }

        LogUtils.showLog("BookListFragment:onResume:"+fragment_tag);
        if (isVisible){
            builder = new AdController
                    .Builder(getActivity())
                    .setContainer(ad_container_ll)
                    .setPage(fragment_tag)
                    .setTag_ad(mParam2)
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.showLog("BookListFragment:setUserVisibleHint:"+isVisibleToUser);
        LogUtils.showLog("BookListFragment:setUserVisibleHint:"+fragment_tag);
        if (isVisibleToUser&&!StringUtils.isEmpty(fragment_tag)){
            builder = new AdController
                    .Builder(getActivity())
                    .setContainer(ad_container_ll)
                    .setPage(fragment_tag)
                    .setTag_ad(mParam2)
                    .create();
            builder.show();
        }
    }
}
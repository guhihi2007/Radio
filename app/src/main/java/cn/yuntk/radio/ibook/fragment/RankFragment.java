package cn.yuntk.radio.ibook.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.LinearLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.activity.BookDetailActivity;
import cn.yuntk.radio.ibook.adapter.RvItemClickInterface;
import cn.yuntk.radio.ibook.adapter.TCBooksAdapter;
import cn.yuntk.radio.ibook.base.RootBase;
import cn.yuntk.radio.ibook.base.RootListBean;
import cn.yuntk.radio.ibook.base.refresh.BaseRefreshFragment;
import cn.yuntk.radio.ibook.bean.TCBean3;
import cn.yuntk.radio.ibook.common.refresh.RefreshConfig;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.component.DaggerBookComponent;
import cn.yuntk.radio.ibook.fragment.presenter.BookListPresenter;
import cn.yuntk.radio.ibook.fragment.view.IBookListView;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.NetworkUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;

public class RankFragment extends BaseRefreshFragment<BookListPresenter,RootListBean<TCBean3>>
        implements RvItemClickInterface,IBookListView {

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.rv_subject)
    RecyclerView rv_subject;
    @BindView(R.id.no_data_ll)
    LinearLayout no_data_ll;

    TCBooksAdapter adapter;
    List<TCBean3> timeListBeans = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private String mParam1;//bookTypeId  分类id
    private String mParam2;//type 小说1 评书2
    private String mParam3;//广告标志

    // TODO: Rename and change types and number of parameters
    public static RankFragment newInstance(String param1, String param2, String param3) {
        RankFragment fragment = new RankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
        }
        return R.layout.ting_fragment_classify_rank;
    }

    @Override
    protected void initViews() {
        LogUtils.showLog("BookListFragment:"+mParam1);
    }

    @Override
    protected void bindEvent() {
        adapter = new TCBooksAdapter(mContext, timeListBeans,R.layout.ting_item_booklist_new,"1");
        adapter.setRvItemClickInterface(this);
//        rv_subject.setEmptyView(no_data_ll);
        rv_subject.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
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

        initRefreshView(refreshLayout, RefreshConfig.create().pageSize(40));
    }

    @Override
    protected void loadData() {
        executeRefresh();
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


    @Override
    public void onItemClick(RootBase o) {
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        TCBean3 book = (TCBean3) o;
        intent.putExtra("bookid",book.getBookID());
        intent.putExtra("booktitle",book.getBookName());
        intent.putExtra("booktype",mParam2);
        startActivity(intent);
    }

    @Override
    public void refreshData(int pageNo, int pageSize, boolean isLoadMore) {
        LogUtils.showLog("refreshData:");
        if (!NetworkUtils.isConnected(mContext)){
            refreshFail(-1,"");
        }else {
            requestUrl(pageNo,pageSize);
        }
    }

    @Override
    public void loadMoreData(int pageNo, int pageSize) {
        requestUrl(pageNo,pageSize);
    }


    @Override
    protected void refreshUISuccess(RootListBean<TCBean3> data, boolean isLoadMore) {
        LogUtils.showLog("refreshSuccess:");
        if (data==null||data.getStatus()!=1||data.getData() == null||data.getData().size()==0){
            emptyView(true);
        }else {
            if (!isLoadMore){
                timeListBeans.clear();
            }
            timeListBeans.addAll(data.getData());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void refreshUIFail(int code, String msg) {
        LogUtils.showLog("refreshFail:");
        if (code == -2){
            ToastUtil.showToast(getString(R.string.load_over));
        }else {
            ToastUtil.showToast(getString(R.string.load_again));
        }
    }

    @Override
    public void emptyView(boolean isEmpty) {
        LogUtils.showLog("emptyView:");
    }

    public void requestUrl(int pageNo,int pageSize){
        //bookTypeId=129&hotAndNew=2&oauth_token=&pagenum=1&pagesize=20&type=1
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("bookTypeId",mParam1);//1热门榜2热搜榜
        map.put("type",mParam2);//小说传1评书传2
        map.put("hotAndNew","1");//1最新 2最热
        map.put("pagenum",pageNo+"");//页码
        map.put("pagesize",pageSize+"");//每页数量
        mPresenter.getAlbumByBookTypeid(map);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.showLog("BookListFragment:setUserVisibleHint:"+isVisibleToUser);
    }

    @Override
    public Context getLoadingContext() {
        return null;
    }
}
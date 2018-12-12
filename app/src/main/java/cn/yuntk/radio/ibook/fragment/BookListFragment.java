package cn.yuntk.radio.ibook.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ad.AdController;
import cn.yuntk.radio.ibook.activity.BookDetailActivity;
import cn.yuntk.radio.ibook.adapter.RvItemClickInterface;
import cn.yuntk.radio.ibook.adapter.TCBooksAdapter;
import cn.yuntk.radio.ibook.base.RootBase;
import cn.yuntk.radio.ibook.base.RootListBean;
import cn.yuntk.radio.ibook.base.refresh.BaseRefreshFragment;
import cn.yuntk.radio.ibook.bean.TCBean3;
import cn.yuntk.radio.ibook.cache.ACache;
import cn.yuntk.radio.ibook.common.refresh.RefreshConfig;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.component.DaggerBookComponent;
import cn.yuntk.radio.ibook.fragment.presenter.BookListPresenter;
import cn.yuntk.radio.ibook.fragment.view.IBookListView;
import cn.yuntk.radio.ibook.util.GsonUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.NetworkUtils;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;

public class BookListFragment extends BaseRefreshFragment<BookListPresenter,RootListBean<TCBean3>>
        implements RvItemClickInterface,IBookListView {

    @BindView(R.id.ad_container_fl)
    FrameLayout ad_container_ll;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rv_subject)
    RecyclerView rv_subject;
    @BindView(R.id.no_data_ll)
    LinearLayout no_data_ll;
    @BindView(R.id.load_again_tv)
    TextView load_again_tv;

    TCBooksAdapter adapter;
    List<TCBean3> timeListBeans = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private String mParam1;//bookTypeId  1热门榜2热搜榜
    private String mParam2;//type 小说1 评书2
    private String mParam3;//广告标志

    private ACache aCache;

    // TODO: Rename and change types and number of parameters
    public static BookListFragment newInstance(String param1, String param2, String param3) {
        BookListFragment fragment = new BookListFragment();
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
        return R.layout.ting_fragment_booklist;
    }

    @Override
    protected void initViews() {
        LogUtils.showLog("BookListFragment:"+mParam1);
    }

    @Override
    protected void bindEvent() {
        aCache = ACache.get(mContext);
        adapter = new TCBooksAdapter(mContext, timeListBeans,R.layout.ting_item_booklist_new,"0");
        adapter.setRvItemClickInterface(this);
        rv_subject.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        rv_subject.setAdapter(adapter);

        RefreshConfig config = new RefreshConfig();
        config.enableRefresh(true);
        config.enableLoadMore(false);
        initRefreshView(refreshLayout,config);

    }

    @Override
    protected void loadData() {
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


    @OnClick({R.id.load_again_tv})
    public void onViewClick(View view){
        switch (view.getId()){
            case R.id.load_again_tv:
                refreshLayout.autoRefresh();
                break;
        }
    }
    @Override
    public void onItemClick(RootBase o) {
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        if (o instanceof  TCBean3){
            TCBean3 bookBean = (TCBean3) o;
            intent.putExtra("bookid",bookBean.getBookID());
            intent.putExtra("booktitle",bookBean.getBookName());
            intent.putExtra("booktype",mParam2);

        }
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
    public void loadMoreData(int pageNo, int pageSize) {
        requestUrl();
    }

    @Override
    protected void refreshUISuccess(RootListBean<TCBean3> data, boolean isLoadMore) {
        LogUtils.showLog("refreshSuccess:");
        if (data==null||data.getStatus()!=1||data.getData() == null||data.getData().size()==0){
            emptyView(true);
        }else {
            //            缓存首页列表
            aCache.put(mParam1, GsonUtils.parseToJsonString(data));
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
        String json = aCache.getAsString(mParam1);
        if (!StringUtils.isEmpty(json)){
//            BookListBean data = GsonUtils.parseObject(json,BookListBean.class);
//            refreshSuccess(data);
        }else {
            ToastUtil.showToast(getString(R.string.load_again));
        }
    }

    @Override
    public void emptyView(boolean isEmpty) {
        LogUtils.showLog("emptyView:");
    }

    public void requestUrl(){
        //榜单只有一页 可以有100个
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("bookTypeId",mParam1);//1热门榜2热搜榜
        map.put("type",mParam2);//小说传1评书传2
        map.put("banglei","1");//1周榜 2月榜3总榜
//        map.put("pagenum",pageNo+"");//页码
        map.put("pagesize","100");//每页数量
        mPresenter.getAlbumInRank(map);
    }

    AdController builder;
    String fragment_tag;
    @Override
    public void onResume() {
        super.onResume();
        LogUtils.showLog("BookListFragment:onResume:"+mParam3);
        if (isVisible){
            builder = new AdController
                    .Builder(getActivity())
                    .setContainer(ad_container_ll)
                    .setPage(mParam3)
//                    .setTag_ad(mParam2)
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
//            builder = new AdController
//                    .Builder(getActivity())
//                    .setContainer(ad_container_ll)
//                    .setPage(fragment_tag)
//                    .setTag_ad(mParam2)
//                    .create();
//            builder.show();
        }
    }

    @Override
    public Context getLoadingContext() {
        return null;
    }

}
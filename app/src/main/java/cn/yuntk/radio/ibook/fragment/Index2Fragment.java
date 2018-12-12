package cn.yuntk.radio.ibook.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.yuntk.radio.Constants;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ad.AdController;
import cn.yuntk.radio.ibook.activity.StoryTellingListActivity;
import cn.yuntk.radio.ibook.adapter.ClassifiesAdapter;
import cn.yuntk.radio.ibook.adapter.ClassifyItemClickInterface;
import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.base.RootBean;
import cn.yuntk.radio.ibook.bean.TCBean1;
import cn.yuntk.radio.ibook.bean.TCBean1_1;
import cn.yuntk.radio.ibook.bean.TCBean2;
import cn.yuntk.radio.ibook.cache.ACache;
import cn.yuntk.radio.ibook.common.TingConstants;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.component.DaggerBookComponent;
import cn.yuntk.radio.ibook.fragment.presenter.ClassifyListPresenter;
import cn.yuntk.radio.ibook.fragment.view.IClassifyListView;
import cn.yuntk.radio.ibook.util.GsonUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.NetworkUtils;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;

public class Index2Fragment extends BaseFragment<ClassifyListPresenter> implements
        ClassifyItemClickInterface,IClassifyListView {

    @BindView(R.id.search_tv)
    TextView searchTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.game_tv)
    TextView gameTv;
    @BindView(R.id.rv_subject)
    RecyclerView rvSubject;
    @BindView(R.id.no_data_ll)
    LinearLayout noDataLl;
    @BindView(R.id.load_again_tv)
    TextView load_again_tv;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.ad_container_fl)
    FrameLayout ad_container_fl;

    ClassifiesAdapter adapter;
    RootBean<TCBean1> data;
    List<TCBean1_1> types = new ArrayList<TCBean1_1>();

    ACache aCache;

    @Override
    protected int getLayoutId() {
        return R.layout.ting_fragment_index2;
    }

    @Override
    protected void initViews() {
        titleTv.setText(getString(R.string.classif));
        gameTv.setVisibility(View.GONE);
        searchTv.setVisibility(View.GONE);
        aCache = ACache.get(mContext);
    }

    @Override
    protected void bindEvent() {
        adapter = new ClassifiesAdapter(mContext,types,R.layout.ting_item_classify);
        adapter.setClickInterface(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

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

    @OnClick({R.id.search_tv, R.id.game_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.game_tv:
                break;
        }
    }

    @Override
    public void onTCBean2ItemClick(TCBean2 t, String content) {
        Intent intent = new Intent(mContext, StoryTellingListActivity.class);
        intent.putExtra("book_type_id",t.getBookTypeId()+"");
        intent.putExtra("type_name",t.getName()+"");
        if (content.equals("ys")){//有声小说1评书相声2
            intent.putExtra("type","1");
        }else {
            intent.putExtra("type","2");
        }
        startActivity(intent);
    }

    @Override
    public void refreshData(int pageNo, int pageSize, boolean isLoadMore) {
        LogUtils.showLog("refreshData:");
        if (!NetworkUtils.isConnected(mContext)){
            refreshFail(-1,"");
        }else {
            HashMap<String,String> map = new HashMap<String,String>();
            mPresenter.getBookTypeList(map);
        }
    }

    @Override
    public void loadMoreData(int pageNo, int pageSize) {

    }

    @Override
    public void refreshSuccess(RootBean<TCBean1> data) {
        LogUtils.showLog("refreshSuccess:");
        if (data!=null&&data.getData()!=null){
            aCache.put(TingConstants.ACACHE_CLASSIFY, GsonUtils.parseToJsonString(data));

            TCBean1_1 tcBean1_1 = new TCBean1_1();
            tcBean1_1.setContent("评书大全");
            tcBean1_1.setTypes(data.getData().getPs());
            tcBean1_1.setNum(data.getData().getPs().size());
            tcBean1_1.setType("ps");

            TCBean1_1 tcBean1_2 = new TCBean1_1();
            tcBean1_2.setContent("有声小说");
            tcBean1_2.setTypes(data.getData().getYs());
            tcBean1_2.setNum(data.getData().getYs().size());
            tcBean1_2.setType("ys");

            types.clear();
            types.add(tcBean1_2);
            types.add(tcBean1_1);
            adapter.notifyDataSetChanged();
        }else {
            emptyView(true);
        }
        refreshLayout.finishRefresh();
    }

    @Override
    public void refreshFail(int code, String msg) {
        LogUtils.showLog("refreshFail:");
        String json = aCache.getAsString(TingConstants.ACACHE_CLASSIFY);
        if (!StringUtils.isEmpty(json)){
            Type jsonType = new TypeToken<RootBean<TCBean1>>() {}.getType();
            this.data = new Gson().fromJson(json, jsonType);
            refreshSuccess(data);
        }else {
            ToastUtil.showToast(getString(R.string.load_again));
            // 这两个方法是在加载失败时调用的
            refreshLayout.finishRefresh(false);//结束刷新（刷新失败）
        }
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
    public void emptyView(boolean isEmpty) {
        LogUtils.showLog("emptyView:");
        if (isEmpty){
            ToastUtil.showToast(getString(R.string.load_again));
            rvSubject.setVisibility(View.GONE);
            noDataLl.setVisibility(View.VISIBLE);
        }else {
            rvSubject.setVisibility(View.VISIBLE);
            noDataLl.setVisibility(View.GONE);
        }
    }

    AdController builder;

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible){
            builder = new AdController
                    .Builder(getActivity())
                    .setContainer(ad_container_fl)
                    .setPage(Constants.CATEGORY_PAGE)
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
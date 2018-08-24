package cn.yuntk.radio.ibook.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.activity.presenter.SearchPresenter;
import cn.yuntk.radio.ibook.activity.view.ISearchView;
import cn.yuntk.radio.ibook.adapter.RvItemClickInterface;
import cn.yuntk.radio.ibook.adapter.SearchHistoryAdapter;
import cn.yuntk.radio.ibook.adapter.SearchItemAdapter;
import cn.yuntk.radio.ibook.adapter.TagAdapter;
import cn.yuntk.radio.ibook.base.BaseTitleActivity;
import cn.yuntk.radio.ibook.bean.SearchBean;
import cn.yuntk.radio.ibook.bean.SearchItemBean;
import cn.yuntk.radio.ibook.common.Constants;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.component.DaggerBookComponent;
import cn.yuntk.radio.ibook.util.DialogUtils;
import cn.yuntk.radio.ibook.util.KeyboardUtil;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;
import cn.yuntk.radio.ibook.widget.FlowTagLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/*搜索页面*/
public class BookSearchActivtity extends BaseTitleActivity<SearchPresenter> implements ISearchView,RvItemClickInterface
        ,DialogUtils.HistoryClickInterface{

    @BindView(R.id.history_rv)
    RecyclerView history_rv;
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
    @BindView(R.id.flow_history)
    FlowTagLayout flowHistory;
    @BindView(R.id.history_linearlayout)
    LinearLayout historyLinearlayout;
    @BindView(R.id.search_view)
    SearchView search_view;
    @BindView(R.id.local_history_ll)
    LinearLayout local_history_ll;
    @BindView(R.id.clear_record_ll)
    LinearLayout clear_record_ll;
    @BindView(R.id.er_v)
    RecyclerView er_v;

    TagAdapter tagAdapter;
    SearchItemAdapter itemAdapter;

    public List<String> tags = new ArrayList<String>();
    public List<SearchItemBean> items = new ArrayList<SearchItemBean>();
    public List<String> historys = new ArrayList<String>();

    public static void jumpToSearch(Context context){
        context.startActivity(new Intent(context,BookSearchActivtity.class));
    }

    @Override
    protected void initViews() {
        tags.add("黄金瞳");
        tags.add("凡人修仙传");
        tags.add("盘龙");
        tags.add("仙逆");
        tags.add("我欲封天");
        tags.add("斗破苍穹");
        tags.add("星辰变");
        tags.add("绝世唐门");
//        tags.add("鬼吹灯");
//        tags.add("魔兽领主");
//        tags.add("都市特种兵");
//        tags.add("傲世九重天");
//        tags.add("舞动乾坤");

        titleTv.setText("搜索");
        nextLl.setVisibility(View.GONE);

        tagAdapter = new TagAdapter(this,tags);
        //设置这个模式意思是处理流标签点击事件
        flowHistory.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_NONE);
        flowHistory.setAdapter(tagAdapter);
        tagAdapter.notifyDataSetChanged();
        //设置搜索框直接展开显示。左侧有放大镜(在搜索框中) 右侧有叉叉 可以关闭搜索框
        //search_view.setIconified(false);
        //设置搜索框直接展开显示。左侧有放大镜(在搜索框外) 右侧无叉叉 有输入内容后有叉叉 不能关闭搜索框
        search_view.setIconifiedByDefault(false);
        //设置搜索框直接展开显示。左侧有无放大镜(在搜索框中) 右侧无叉叉 有输入内容后有叉叉 不能关闭搜索框
        //search_view.onActionViewExpanded();
        //设置最大宽度
        //search_view.setMaxWidth(500);
        //设置是否显示搜索框展开时的提交按钮
        search_view.setSubmitButtonEnabled(true);
        //设置输入框提示语
        search_view.setQueryHint("请输入要搜索的书名");
        search_view.onActionViewExpanded();//表示在内容为空时不显示取消的x按钮，内容不为空时显示.
        search_view.clearFocus();  //可以收起键盘
        KeyboardUtil.hideKeyboard(BookSearchActivtity.this);

        itemAdapter = new SearchItemAdapter(this,items,R.layout.listener_item_search_book);
        itemAdapter.setRvItemClickInterface(this);
        history_rv.setLayoutManager(new CustomLinearManager(this));
        history_rv.setAdapter(itemAdapter);
    }

    @Override
    protected void bindEvent() {
        //点击流标签让历史文字出现在EditText上,并执行搜索
        flowHistory.setOnTagClickListener((parent, view, position) -> {
            View view1 = parent.getAdapter().getView(position, null, null);
            String tag = (String) view1.getTag();
            search_view.requestFocus();
            search_view.setQuery(tag,true);
        });
        //搜索框展开时后面叉叉按钮的点击事件
        search_view.setOnCloseListener(() -> {
            search_view.setQuery("",false);
            return false;
        });
        //搜索图标按钮(打开搜索框的按钮)的点击事件
        search_view.setOnSearchClickListener(v ->
                ToastUtil.showToast("open")
        );
        //搜索框文字变化监听
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                LogUtils.showLog("CSDN_LQR TextSubmit : " + s);
                if (!StringUtils.isEmpty(s)){
                    if (!historys.contains(s.trim())){
                        historys.add(s.trim());
                    }
                    requestUrl(s);
//                search_view.setQuery("", false);
                    search_view.clearFocus();  //可以收起键盘
                    KeyboardUtil.hideKeyboard(BookSearchActivtity.this);
//                search_view.onActionViewCollapsed();    //可以收起SearchView视图
                }else {
                    ToastUtil.showToast("请输入搜索内容");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                LogUtils.showLog("CSDN_LQR TextChange --> " + s);
                return false;
            }
        });
    }

    @Override
    protected void loadData() {
        getHistorys();
        initHistory();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_listener_search_;
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

    @OnClick({R.id.back_ll,R.id.clear_record_ll})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.back_ll:
                finish();
                break;
            case R.id.clear_record_ll:
                clearData();
                break;
                default:
        }
    }

    //    loadurl
    private void requestUrl(String key) {
        showProgressBarDialog("搜索中...");
        mPresenter.serchBookForKeyword(key);
    }

    @Override
    public void searchSuccess(SearchBean searchBean) {
        hideProgress();
        items.clear();
        if (searchBean!=null&&searchBean.getUrl_list()!=null&&searchBean.getUrl_list().size()!=0){
            if (searchBean.getUrl_list().size()>40){
                items.addAll(searchBean.getUrl_list().subList(0,40));
            }else {
                items.addAll(searchBean.getUrl_list());
            }
        }else {
            ToastUtil.showToast("没有此书");
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                itemAdapter.notifyDataSetChanged();
                if (items.size()!=0){
                    local_history_ll.setVisibility(View.GONE);
                    history_rv.setVisibility(View.VISIBLE);
                }else if (historys.size()!=0){
                    local_history_ll.setVisibility(View.VISIBLE);
                    history_rv.setVisibility(View.GONE);
                }else {
                    local_history_ll.setVisibility(View.GONE);
                    history_rv.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void searchFailear(String fail) {
        hideProgress();
        ToastUtil.showToast("搜索失败");
    }

    @Override
    public void onItemClick(Object o) {
        SearchItemBean book = (SearchItemBean) o;
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        intent.putExtra("bookid",book.getU());
        intent.putExtra("booktitle",book.getTitle());
        intent.putExtra("booktype","搜索");
        startActivity(intent);
    }

    SearchHistoryAdapter adapter;
    //   初始化
    private void initHistory(){
        adapter = new SearchHistoryAdapter(this,historys,R.layout.listener_item_search_history);
        adapter.setItemClickInterface(this);
        er_v.setLayoutManager(new LinearLayoutManager(this));
        er_v.setAdapter(adapter);
        if (historys.size()==0){
            local_history_ll.setVisibility(View.GONE);
            history_rv.setVisibility(View.VISIBLE);
        }else {
            local_history_ll.setVisibility(View.VISIBLE);
            history_rv.setVisibility(View.GONE);
        }
    }

    //    存储历史记录
    private void saveHistory(){
        StringBuffer history = new StringBuffer();
        if (historys.size()>1){
            for (int i=0;i<historys.size();i++){
                if (i == 0){
                    history.append("[\""+historys.get(0)+"\",");
                }else if (i==(historys.size()-1)){
                    history.append("\""+historys.get(i)+"\"]");
                }else {
                    history.append("\""+historys.get(i)+"\",");
                }
            }
        }else if (historys.size()==1){
            history.append("[\""+historys.get(0)+"\"]");
        }else {
            history.append("[]");
        }
        LogUtils.showLog("historys:"+history.toString());
        SharedPreferencesUtil.getInstance().putString(Constants.BOOK_SEARCH_HISTORY,history.toString());

    }
    //读取历史记录
    private List<String> getHistorys(){
       String history =  SharedPreferencesUtil.getInstance().getString(Constants.BOOK_SEARCH_HISTORY);
       if (!StringUtils.isEmpty(history)){
           Gson gson = new Gson();
           historys.addAll(gson.fromJson(history,List.class));
       }
        return historys;
    }
    //搜索历史点击
    @Override
    public void itemClick(String o) {
        String tag = o;
        search_view.requestFocus();
        search_view.setQuery(tag,true);
    }

    @Override
    public void clearData() {
        historys.clear();
        initHistory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveHistory();
    }
}
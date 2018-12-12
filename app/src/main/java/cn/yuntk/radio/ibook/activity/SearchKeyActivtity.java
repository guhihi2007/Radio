package cn.yuntk.radio.ibook.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.adapter.SearchHistoryAdapter;
import cn.yuntk.radio.ibook.adapter.TagAdapter;
import cn.yuntk.radio.ibook.base.BaseTitleActivity;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.common.TingConstants;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.util.DialogUtils;
import cn.yuntk.radio.ibook.util.KeyboardUtil;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;
import cn.yuntk.radio.ibook.widget.FlowTagLayout;

/** 搜索页面*/
public class SearchKeyActivtity extends BaseTitleActivity implements DialogUtils.HistoryClickInterface{

    @BindView(R.id.title_left_text)
    TextView title_left_text;
    @BindView(R.id.title_right_text)
    TextView title_right_text;
    @BindView(R.id.title_content_text)
    TextView title_content_text;

    @BindView(R.id.flow_history)
    FlowTagLayout flowHistory;

    @BindView(R.id.search_et)
    EditText search_et;

    @BindView(R.id.local_history_ll)
    LinearLayout local_history_ll;

    @BindView(R.id.history_rv)
    RecyclerView history_rv;

    public TagAdapter tagAdapter;
    public SearchHistoryAdapter adapter;//搜索历史记录
    public List<String> tags = new ArrayList<String>();
    public List<String> historys = new ArrayList<String>();//s存放搜索过的历史纪录

    public static void jumpToSearch(Context context){
        context.startActivity(new Intent(context,SearchKeyActivtity.class));
    }

    @Override
    protected void initViews() {
        title_content_text.setText("搜索");
        title_right_text.setVisibility(View.GONE);
        search_et.setHint("请输入要搜索的书名");
        KeyboardUtil.hideKeyboard(this);

        tags.add("黄金瞳");
        tags.add("凡人修仙传");
        tags.add("盘龙");
        tags.add("仙逆");
        tags.add("我欲封天");
        tags.add("斗破苍穹");
        tags.add("星辰变");
        tags.add("绝世唐门");

        tagAdapter = new TagAdapter(this,tags);
        //标签选中模式：三种模式：FLOW_TAG_CHECKED_NONE、FLOW_TAG_CHECKED_SINGLE、FLOW_TAG_CHECKED_MULTI
        flowHistory.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_NONE);
        flowHistory.setAdapter(tagAdapter);
        tagAdapter.notifyDataSetChanged();

    }

    @Override
    protected void bindEvent() {
        //点击流标签让历史文字出现在EditText上,并执行搜索
        flowHistory.setOnTagClickListener((parent, view, position) -> {
            View view1 = parent.getAdapter().getView(position, null, null);
            String tag = (String) view1.getTag();
            search_et.setText(tag);
            TextSubmit(tag);
        });

        search_et.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH){//搜索按键action
                String content = search_et.getText().toString();
                TextSubmit(content);
                return true;
            }
            return false;
        });
    }

    //    搜索
    private void TextSubmit(String s){
        LogUtils.showLog("searchString TextSubmit : " + s);
        if (!StringUtils.isEmpty(s.trim())){
            if (historys.contains(s.trim())){
                historys.remove(s.trim());
            }
            historys.add(0,s.trim());
            initHistory();

            search_et.setSelection(s.length());
            gotoResultActivity(s);
            KeyboardUtil.hideKeyboard(this);
        }else {
            ToastUtil.showToast("请输入搜索内容");
        }
    }

    @Override
    protected void loadData() {
        getHistorys();
        initHistory();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ting_activity_search_key;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @OnClick({R.id.title_left_text,R.id.clear_record_ll})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.title_left_text:
                finish();
                break;
            case R.id.clear_record_ll:
                clearData();
                break;
        }
    }


    //   初始化
    private void initHistory(){
        if (adapter == null){
            adapter = new SearchHistoryAdapter(this,historys,R.layout.ting_item_search_history);
            adapter.setItemClickInterface(this);
            history_rv.setLayoutManager(new LinearLayoutManager(this));
            history_rv.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
        if (historys.size()==0){
            local_history_ll.setVisibility(View.GONE);
        }else {
            local_history_ll.setVisibility(View.VISIBLE);
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
        SharedPreferencesUtil.getInstance().putString(TingConstants.BOOK_SEARCH_HISTORY,history.toString());
    }


    //读取历史记录
    private List<String> getHistorys(){
        String history =  SharedPreferencesUtil.getInstance().getString(TingConstants.BOOK_SEARCH_HISTORY);
        if (!StringUtils.isEmpty(history)){
            Gson gson = new Gson();
            historys.addAll(gson.fromJson(history,List.class));
        }
        return historys;
    }

    //    loadurl
    private void gotoResultActivity(String key) {
        LogUtils.showLog("请求网络...");
        Intent intent = new Intent(this,SearchResultActivtity.class);
        intent.putExtra("Key",key);
        startActivity(intent);
    }

    //搜索历史点击
    @Override
    public void itemClick(String o) {
        String tag = o;
        search_et.setText(tag);
        TextSubmit(tag);
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
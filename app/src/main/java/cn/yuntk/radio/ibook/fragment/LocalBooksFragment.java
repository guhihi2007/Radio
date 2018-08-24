package cn.yuntk.radio.ibook.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.activity.BookDetailActivity;
import cn.yuntk.radio.ibook.adapter.BooksAdapter;
import cn.yuntk.radio.ibook.adapter.RvItemClickInterface;
import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.DownloadMusicInfo;
import cn.yuntk.radio.ibook.bean.ItemBookBean;
import cn.yuntk.radio.ibook.common.Constants;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.dbdao.Mp3DaoUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.widget.EmptyRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;

public class LocalBooksFragment extends BaseFragment implements RvItemClickInterface{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    EmptyRecyclerView rv_subject;
    LinearLayout no_data_ll;
    SmartRefreshLayout refreshLayout;

    private String mParam1;
    private String mParam2;

    BooksAdapter adapter;
    List<ItemBookBean> timeListBeans = new ArrayList<>();

    Mp3DaoUtils mp3DaoUtils;
    CustomHandler handler;

    // TODO: Rename and change types and number of parameters
    public static LocalBooksFragment newInstance(String param1, String param2) {
        LocalBooksFragment fragment = new LocalBooksFragment();
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
        return R.layout.fragment_listener_local;
    }

    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);
        rv_subject = mContentView.findViewById(R.id.rv_subject);
        no_data_ll = mContentView.findViewById(R.id.no_data_ll);
        refreshLayout = mContentView.findViewById(R.id.refreshLayout);

        handler = new CustomHandler(getActivity());
        mp3DaoUtils = new Mp3DaoUtils();
    }

    @Override
    protected void bindEvent() {
        adapter = new BooksAdapter(getActivity(), timeListBeans,R.layout.listener_item_booklist);
        adapter.setRvItemClickInterface(this);
        rv_subject.setEmptyView(no_data_ll);
//        rv_subject.addItemDecoration(new SpaceItemDecoration(getActivity(), LinearLayoutManager.VERTICAL,2, Color.parseColor("#e5e5e5")));
        rv_subject.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_subject.setAdapter(adapter);

        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableAutoLoadmore(false);
    }

    @Override
    protected void loadData() {
        //0历史 1下载
        if (mParam2.equals("0")){
            Executors.newSingleThreadExecutor().submit(() -> {
                timeListBeans.clear();
                timeListBeans.addAll(Mp3DaoUtils.music2book(mp3DaoUtils.queryListDB_History()));
                Collections.reverse(timeListBeans);
                handler.sendEmptyMessage(1);
            });
        }else {
            timeListBeans.clear();
            timeListBeans.addAll(saveBookInfo());
            Collections.reverse(timeListBeans);
            handler.sendEmptyMessage(1);
        }
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void onItemClick(Object o) {
        ItemBookBean book = (ItemBookBean) o;
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        intent.putExtra("bookid",book.getId());
        intent.putExtra("booktitle",book.getTitle());
        intent.putExtra("booktype",mParam1);
        startActivity(intent);
    }

    //    通知刷历史和下载
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String string) {
        loadData();
        LogUtils.showLog("LocalBookFragment:"+string);
    }

    private List<ItemBookBean> saveBookInfo(){
        List<DownloadMusicInfo> infos = new ArrayList<DownloadMusicInfo>();
        String json =  SharedPreferencesUtil.getInstance().getString(Constants.BOOK_DOWNLOAD_RECORD1);
        if (!StringUtils.isEmpty(json)){
            Gson gson = new Gson();
            infos.addAll(gson.fromJson(json,new TypeToken<List<DownloadMusicInfo>>(){}.getType()));
        }
        List<ItemBookBean> bookBeans = new ArrayList<ItemBookBean>();
        TreeMap<String,ItemBookBean> treeMap = new TreeMap<String,ItemBookBean>();
        if (infos!=null&&infos.size()!=0){
            for (DownloadMusicInfo music:infos){
                ItemBookBean bookBean = new ItemBookBean();
                bookBean.setId(music.getBook_id()+"");
                bookBean.setTitle(music.getBook_title());
                bookBean.setType(music.getBook_type());
                treeMap.put(music.getBook_id()+"",bookBean);
            }
            for (Map.Entry<String, ItemBookBean> entry : treeMap.entrySet()) {
                System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                bookBeans.add(entry.getValue());
            }

        }
        return bookBeans;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /*自定义Handler*/
    public class CustomHandler extends Handler {
        private WeakReference<Activity> activityWeakReference;

        public CustomHandler(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            Activity activity = activityWeakReference.get();
            if (activity != null) {
                switch (msg.what){
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }

}

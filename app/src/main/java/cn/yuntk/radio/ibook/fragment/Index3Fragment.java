package cn.yuntk.radio.ibook.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.activity.BookDetailActivity;
import cn.yuntk.radio.ibook.activity.BookSearchActivtity;
import cn.yuntk.radio.ibook.adapter.BooksAdapter;
import cn.yuntk.radio.ibook.adapter.RvItemClickInterface;
import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.ItemBookBean;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.dbdao.Mp3DaoUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.widget.EmptyRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;

public class Index3Fragment extends BaseFragment implements RvItemClickInterface{
    @BindView(R.id.search_tv)
    TextView searchTv;
    @BindView(R.id.listener_title_tv)
    TextView titleTv;
    @BindView(R.id.game_tv)
    TextView gameTv;
    @BindView(R.id.rv_subject)
    EmptyRecyclerView rv_subject;
    @BindView(R.id.no_data_ll)
    LinearLayout no_data_ll;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.comm_back_ll)
    LinearLayout comm_back_ll;

    BooksAdapter adapter;
    List<ItemBookBean> timeListBeans = new ArrayList<>();
    Mp3DaoUtils mp3DaoUtils;
    CustomHandler handler;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_listener_index3;
    }

    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);
        titleTv.setText(getString(R.string.collect));
        searchTv.setVisibility(View.GONE);
        comm_back_ll.setVisibility(View.VISIBLE);

        gameTv.setText("搜索");
        mp3DaoUtils = new Mp3DaoUtils();
        handler = new CustomHandler(getActivity());
    }

    @Override
    protected void bindEvent() {

        adapter = new BooksAdapter(getActivity(), timeListBeans,R.layout.listener_item_booklist);
        adapter.setRvItemClickInterface(this);
        rv_subject.setEmptyView(no_data_ll);
//        rv_subject.addItemDecoration(new SpaceItemDecoration(getActivity(),LinearLayoutManager.VERTICAL,2, Color.parseColor("#e5e5e5")));
        rv_subject.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_subject.setAdapter(adapter);

        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableAutoLoadmore(false);

    }

    @Override
    protected void loadData() {

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                timeListBeans.clear();
                timeListBeans.addAll(Mp3DaoUtils.music2book(mp3DaoUtils.queryListDB_Collect()));
                Collections.reverse(timeListBeans);
                handler.sendEmptyMessage(1);
            }
        });

    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

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
        ItemBookBean book = (ItemBookBean) o;
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        intent.putExtra("bookid",book.getId());
        intent.putExtra("booktitle",book.getTitle());
        intent.putExtra("booktype",getString(R.string.collect));
        startActivity(intent);
    }

    //    通知刷xin 收藏
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String string) {
        loadData();
        LogUtils.showLog("Index3Fragment:"+string);

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

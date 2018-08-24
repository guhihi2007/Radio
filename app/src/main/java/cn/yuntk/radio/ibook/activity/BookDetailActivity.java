package cn.yuntk.radio.ibook.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.XApplication;
import cn.yuntk.radio.ibook.activity.presenter.BookDetailPresenter;
import cn.yuntk.radio.ibook.activity.view.IBookDetailView;
import cn.yuntk.radio.ibook.adapter.CatalogueListAdapter;
import cn.yuntk.radio.ibook.adapter.DownloadMp3Interface;
import cn.yuntk.radio.ibook.adapter.RvItemClickInterface;
import cn.yuntk.radio.ibook.ads.ADConstants;
import cn.yuntk.radio.ibook.ads.AdController;
import cn.yuntk.radio.ibook.api.BaseOkhttp;
import cn.yuntk.radio.ibook.base.refresh.BaseRefreshActivity;
import cn.yuntk.radio.ibook.bean.BookDetailBean;
import cn.yuntk.radio.ibook.bean.DownloadMusicInfo;
import cn.yuntk.radio.ibook.bean.Music;
import cn.yuntk.radio.ibook.bean.OnlineMusic;
import cn.yuntk.radio.ibook.bean.PopItemBean;
import cn.yuntk.radio.ibook.cache.ACache;
import cn.yuntk.radio.ibook.common.Constants;
import cn.yuntk.radio.ibook.common.refresh.RefreshConfig;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.component.DaggerBookComponent;
import cn.yuntk.radio.ibook.dbdao.Mp3DaoUtils;
import cn.yuntk.radio.ibook.executor.DownloadOnlineMusic;
import cn.yuntk.radio.ibook.service.AudioPlayer;
import cn.yuntk.radio.ibook.util.DialogUtils;
import cn.yuntk.radio.ibook.util.GsonUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.NetworkUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;
import cn.yuntk.radio.ibook.widget.EmptyRecyclerView;

/**
 *小说详情页
 */
public class BookDetailActivity extends BaseRefreshActivity<BookDetailPresenter, BookDetailBean> implements
        IBookDetailView,RvItemClickInterface,DownloadMp3Interface,DialogUtils.PopClickInterface,DialogUtils.DownloadDialogClick {

    @BindView(R.id.back_tv)
    TextView backTv;
    @BindView(R.id.back_ll)
    LinearLayout backLl;
    @BindView(R.id.next_tv)
    TextView nextTv;
    @BindView(R.id.next_ll)
    LinearLayout nextLl;
    @BindView(R.id.listener_title_tv)
    TextView titleTv;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBar;
    @BindView(R.id.rv_subject)
    EmptyRecyclerView rvSubject;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.page_skip_tv)
    TextView page_skip_tv;
    @BindView(R.id.type_tv)
    TextView type_tv;
    @BindView(R.id.status_tv)
    TextView status_tv;
    @BindView(R.id.date_tv)
    TextView date_tv;
    @BindView(R.id.page_tv)
    TextView page_tv;
    @BindView(R.id.book_introduce_tv)
    TextView book_introduce_tv;
    @BindView(R.id.picture_iv)
    ImageView picture_iv;
    @BindView(R.id.history_text_rl)
    RelativeLayout history_text_rl;
    @BindView(R.id.last_position_tv)
    TextView last_position_tv;

    private String bookid;
    private String booktitle;
    private String booktype;
    private boolean isCollect =false;//是否收藏了该小说
    private boolean isReverse = false;//是否倒置

    CatalogueListAdapter adapter;
    List<BookDetailBean.UrlListBean> urls = new ArrayList<>();
    BookDetailBean data;
    Mp3DaoUtils mp3Dao;//数据库操作

    //    获取下载记录的信息
    private List<DownloadMusicInfo> getDownloadInfo(){

        List<DownloadMusicInfo> infos = new ArrayList<DownloadMusicInfo>();
        String json =  SharedPreferencesUtil.getInstance().getString(Constants.BOOK_DOWNLOAD_RECORD1);
        if (!StringUtils.isEmpty(json)){
            Gson gson = new Gson();
            infos.addAll(gson.fromJson(json,new TypeToken<List<DownloadMusicInfo>>(){}.getType()));
        }
        List<DownloadMusicInfo> dlBooks = new ArrayList<DownloadMusicInfo>();
        for (DownloadMusicInfo info:infos){
            if (info.getBook_id().equals(bookid)){
                dlBooks.add(info);
            }
        }
        return dlBooks;
    }

    @Override
    protected void refreshUISuccess(BookDetailBean data, boolean isLoadMore) {
        urls.clear();
        urls.addAll(data.getUrl_list());
        type_tv.setText(data.getType());
        status_tv.setText(data.getZztt());
        date_tv.setText(data.getTime());
        page_tv.setText(data.getSvid()+"");
        book_introduce_tv.setText(data.getCon());
        updataUI(data);

    }

    //用来缓存书籍详情以及目录
    ACache aCache;
    private void updataUI(BookDetailBean data){
        if (aCache!=null){
            //存储书籍详情
            aCache.put("ytk"+bookid, GsonUtils.parseToJsonString(data));
        }
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                List<DownloadMusicInfo> downloadMusicInfos = getDownloadInfo();
                if (downloadMusicInfos!=null&&downloadMusicInfos.size()!=0){
//                    说明这本书 有下载的章节
                    for (BookDetailBean.UrlListBean bean:urls){
                        for (DownloadMusicInfo info:downloadMusicInfos){
                            if (info.getData_id().equals(bean.getUrl())){
                                bean.setIs_download("1");
                                bean.setPath(info.getMusicPath());
                            }
                        }
                    }
                }
                BookDetailActivity.this.data = data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isReverse){
                            Collections.reverse(urls);
                            adapter.notifyDataSetChanged();
                        }else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
                getPrefix_str();//获取音频前缀
            }
        });

    }

    @Override
    protected void refreshUIFail(int code, String msg) {
        //说明这本书 有下载的章节
        if (aCache!=null){
            //存储书籍详情
            String json = aCache.getAsString("ytk"+bookid);
            if (!StringUtils.isEmpty(json)){
                refreshUISuccess(GsonUtils.parseObject(json,BookDetailBean.class),false);
            }else {
                ToastUtil.showToast(getString(R.string.load_again));
            }
        }else {
            ToastUtil.showToast(getString(R.string.load_again));
        }
    }

    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);
        bookid = getIntent().getStringExtra("bookid");
        booktitle = getIntent().getStringExtra("booktitle");
        booktype = getIntent().getStringExtra("booktype");
        backTv.setText(booktype);
        titleTv.setText(booktitle);
        mp3Dao = new Mp3DaoUtils();
        //读取数据库状态 查看是否收藏
        if (!StringUtils.isEmpty(bookid)){
            XApplication.getDaoSession(mContext).startAsyncSession().runInTx(() -> {
                //DELETE 异步操作 删除数据库
                //DELETE
                //UPDATE
                Music music = mp3Dao.queryListDB(bookid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (music!=null&&!StringUtils.isEmpty(music.getIs_collect())&&music.getIs_collect().equals("1")){
                            nextTv.setText(getString(R.string.collect_cancle));
                        }else {
                            nextTv.setText(getString(R.string.collect));
                        }
                        if (music!=null&&!StringUtils.isEmpty(music.getIs_history())&&music.getIs_history().equals("1")){
                            history_text_rl.setVisibility(View.VISIBLE);
                            last_position_tv.setText(getString(R.string.book_detail_last_page)+":"+music.getZj_title());
                            SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_DATA_ID_IN_HISTORY,music.getMark_2());
                            SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_TITLE_NAME_IN_HISTORY,music.getZj_title());
                        }else {
                            history_text_rl.setVisibility(View.GONE);
                        }
                    }
                });

            });
        }
        getBookStatus();
        handler = new CustomHandler(this);
        aCache = ACache.get(this);//初始化缓存
    }

    @Override
    protected void bindEvent() {

//        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
//            //appBar 滑动监听
//        });

        adapter = new CatalogueListAdapter(this,urls,R.layout.listener_item_catalogue);
        adapter.setRvItemClickInterface(this);
        adapter.setDownloadMp3Interface(this);
        rvSubject.setLayoutManager(new LinearLayoutManager(this));
        rvSubject.setAdapter(adapter);

        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableAutoLoadmore(false);
        initRefreshView(refreshLayout, RefreshConfig.create().pageSize(20).autoRefresh(true));
        showLoading();
        /**
         * 自定义RecyclerView实现对AppBarLayout的滚动效果
         */
//        rvSubject.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    int visiblePosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
//                    if (visiblePosition == 0) {
//                        appBar.setExpanded(false, true);
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//            }
//        });
    }

    @Override
    protected void loadData() {
//        requestUrl();

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_listener_book_detail;
    }

    @Override
    protected BookDetailPresenter getPresenter() {
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
        if (!NetworkUtils.isConnected(this)){
            List<DownloadMusicInfo> downloadMusicInfos = getDownloadInfo();
            refreshUIFail(-1,"");
            refreshLayout.finishLoadmore();
            refreshLayout.finishRefresh();
        }else {
            requestUrl();
        }
    }

    //    loadurl
    private void requestUrl() {
        mPresenter.getBookInfo(bookid);
    }
    //    获取音频地址前缀
    private void getPrefix_str(){
        String json = aCache.getAsString(Constants.YTK_PREFIX_STR+bookid);
        if (!StringUtils.isEmpty(json)){
            LogUtils.showLog("本书某章节已存储："+json);
            return;
        }
        if (data!=null&&data.getUrl_list()!=null&&data.getUrl_list().size()>0){
            Map<String,Object> map = new HashMap<>();
            map.put("data_id",data.getUrl_list().get(0).getUrl());
            map.put("svv_id",data.getSvid());
            BaseOkhttp.getInstance().getPrefix_str(map, new BaseOkhttp.RequestCallback() {
                @Override
                public void onSuccess(String response) {
                    //存储本书籍音频前缀
                    aCache.put(Constants.YTK_PREFIX_STR+bookid, GsonUtils.parseToJsonString(response));
                    LogUtils.showLog("获取本书前缀成功");
                }

                @Override
                public void onFailure(String msg, Exception e) {
                    LogUtils.showLog("获取本书前缀失败");
                }
            });
        }
    }


    @OnClick({R.id.back_ll, R.id.next_ll,R.id.fab,R.id.page_skip_tv,R.id.last_position_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_ll:
                finish();
                break;
            case R.id.next_ll:
                shiftCollect();
                break;
            case R.id.fab:
                if (urls!=null&&urls.size()!=0){
                    Collections.reverse(urls);
                    adapter.notifyDataSetChanged();
                    AudioPlayer.get().reverseList(bookid);
                    if (isReverse){
                        //变成正序 删除bookid
                        isReverse = false;
                        if (bookOrderStatuses.size()!=0&&bookOrderStatuses.contains(bookid)){
                            bookOrderStatuses.remove(bookid);
                        }
                    }else {
                        //变成倒序 添加bookid
                        isReverse = true;
                        bookOrderStatuses.add(bookid);
                    }
                    saveBookStatus();//保存倒序顺序状态
                }
                break;
            case R.id.page_skip_tv:
                showPop();
                break;
            case R.id.last_position_tv:

                if (data!=null){
                    String data_id_history = SharedPreferencesUtil.getInstance().getString(Constants.PLAY_PAGE_DATA_ID_IN_HISTORY);
                    String title_name_history = SharedPreferencesUtil.getInstance().getString(Constants.PLAY_PAGE_TITLE_NAME_IN_HISTORY);
                    int pos = 0;
                    for (int i=0;i<urls.size();i++){
                        if (urls.get(i).getUrl().equals(data_id_history)){
                            pos = i;
                            break;
                        }
                    }

                    if (!NetworkUtils.isConnected(this)&&!urls.get(pos).getIs_download().equals("1")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("温馨提示");
                        builder.setMessage("网络未连接，无法播放未下载章节！");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                        return;
                    }

                    //存储书籍详情
                    SharedPreferencesUtil.getInstance().putString(Constants.BOOK_DETAIL, GsonUtils.parseToJsonString(this.data));
                    SharedPreferencesUtil.getInstance().putString(Constants.BOOK_ID,bookid);//保存最新的小说id

                    SharedPreferencesUtil.getInstance().putInt(Constants.CLICK_POSITION,pos);//上次位置
                    SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_TYPE,data.getType());
                    SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_DATA_ID,data_id_history);
                    SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_SVV_ID,data.getSvid()+"");
                    SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_TITLE,data.getTitle());
                    SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_TITLE_NAME,title_name_history);
                    SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_BOOK_ID,bookid);
                    SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_CON,data.getCon());
                    Intent intent = new Intent(this,BookPlayActivity.class);
                    startActivity(intent);
                }else {
                    ToastUtil.showToast("小说目录加载失败，请刷新");
                }
                break;
        }
    }

    //收藏与取消收藏
    private void shiftCollect(){
        //添加数据库与删除数据库操作
        if (isCollect){
            XApplication.getDaoSession(mContext).startAsyncSession().runInTx(() -> {
                //DELETE 异步操作 删除数据库
                //DELETE
                //UPDATE
                deleteBtn();
            });
            nextTv.setText(getString(R.string.collect));

            Message message = Message.obtain();
            message.obj = getString(R.string.collect_cancle_tip);
            message.what = 1;
            handler.sendMessage(message);

        }else {
            XApplication.getDaoSession(mContext).startAsyncSession().runInTx(() -> {
                //DELETE 异步操作 插入数据库
                //DELETE
                //UPDATE
                insertDB();
            });
            nextTv.setText(getString(R.string.collect_cancle));
            Message message = Message.obtain();
            message.obj = getString(R.string.collect_succ_tip);
            message.what = 1;
            handler.sendMessage(message);
        }
        isCollect = !isCollect;
        EventBus.getDefault().post(Constants.UPDATA_COLLECT);
    }

    //    章节跳转
    private void showPop(){
        if (urls!=null&&urls.size()!=0){
            List<PopItemBean> items = new ArrayList<>();
            int size = urls.size();
            int item_count = urls.size()%20== 0?size/20:(size/20)+1;
            for (int i=0;i<item_count;i++){
                PopItemBean itemBean = new PopItemBean();
                itemBean.setType(PopItemBean.PopTpye.PAGEITEM);
                if (i == (item_count-1)){
                    itemBean.setMinValue((i*20)+1);
                    itemBean.setMaxValue(size);
                    itemBean.setShowString("第"+itemBean.getMinValue()+"章-"+itemBean.getMaxValue()+"章");
                }else {
                    itemBean.setMinValue((i*20)+1);
                    itemBean.setMaxValue((i+1)*20);
                    itemBean.setShowString("第"+itemBean.getMinValue()+"章-"+itemBean.getMaxValue()+"章");
                }
                items.add(itemBean);
            }
            DialogUtils.get().showPop(this,getString(R.string.book_detail_page),items,this);
        }
    }

   CustomHandler handler;

    @Override
    public void onItemClick(Object o) {
        if (this.data!=null){
            //存储书籍详情
            SharedPreferencesUtil.getInstance().putString(Constants.BOOK_DETAIL, GsonUtils.parseToJsonString(this.data));
        }
        SharedPreferencesUtil.getInstance().putString(Constants.BOOK_ID,bookid);//保存最新的小说id
        BookDetailBean.UrlListBean bean = (BookDetailBean.UrlListBean) o;
        if (!NetworkUtils.isConnected(this)&&!bean.getIs_download().equals("1")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("温馨提示");
            builder.setMessage("网络未连接，无法播放未下载章节！");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }

        int pos = urls.indexOf(bean);
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_TYPE,data.getType());
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_DATA_ID,bean.getUrl());
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_SVV_ID,data.getSvid()+"");
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_TITLE,data.getTitle());
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_TITLE_NAME,bean.getName());
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_BOOK_ID,bookid);
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_CON,data.getCon());
        SharedPreferencesUtil.getInstance().putInt(Constants.CLICK_POSITION,pos);
        LogUtils.showLog("data_id1："+bean.getUrl());

        Intent intent = new Intent(this,BookPlayActivity.class);
//        intent.putExtra("type",this.data.getType());
//        intent.putExtra("data_id",bean.getUrl());
//        intent.putExtra("svv_id",this.data.getSvid()+"");
//        intent.putExtra("title",this.data.getTitle());
//        intent.putExtra("title_name",bean.getName());
//        intent.putExtra("bookid",bookid);
//        intent.putExtra("con",this.data.getCon());
        startActivity(intent);
    }

    //下载按钮点击
    @Override
    public void downloadItem(int position, Object object) {
        BookDetailBean.UrlListBean bean = (BookDetailBean.UrlListBean) object;
        if (bean.getIs_download().equals("1")){
            ToastUtil.showToast(bean.getName()+getString(R.string.download_done));
            return;
        }
        if (bean.getIs_download().equals("2")){
            ToastUtil.showToast(bean.getName()+getString(R.string.download_ing));
            return;
        }
        if (!NetworkUtils.isConnected(this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("温馨提示");
            builder.setMessage("网络未连接，无法下载！");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }

        OnlineMusic onlineMusic = new OnlineMusic();
        onlineMusic.setTitle(bean.getName());
        onlineMusic.setSong_id(bookid);
        onlineMusic.setAlbum_title(booktitle);
        onlineMusic.setTitle(bean.getName());
        onlineMusic.setData_id(bean.getUrl());
        onlineMusic.setSvv_id(data.getSvid()+"");
        onlineMusic.setBook_type(data.getType());
//        DialogUtils.get().showDownloadDialog(this,bean.getName(),onlineMusic,this);
        sureDownloadBut(onlineMusic);
    }

    /**
     *加入收藏
     * */
    public void insertDB() {
        if (this.data!=null){
            Music music = new Music();
            music.setSongId(Long.parseLong(bookid));//小说id
            music.setTitle(this.data.getTitle());//小说标题
            music.setIs_collect(Music.Collect_Type.YES);//是否收藏
            music.setAlbum(this.data.getType());//小说类型
            music.setType(Music.Type.ONLINE);
            music.setPath("");//小说路径
            music.setZj_title("");//章节标题
            music.setBook_con(this.data.getCon());//小说简介
            mp3Dao.insertDB(music);
        }
    }

    /**
     *取消收藏
     * */
    public void deleteBtn() {
        if (this.data!=null){
            mp3Dao.deleteBtn(bookid);
        }
    }

    AdController builder;
    @Override
    protected void onResume() {
        super.onResume();
//        if (AudioPlayer.get().isPlaying()){//如果正在播放 则显示全局dialog
//            FloatViewService.startCommand(this, Actions.SERVICE_VISABLE_WINDOW);
//        }
        builder = new AdController
                .Builder(this)
                .setPage(ADConstants.MUSIC_DETAIL)
                .create();
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (builder != null) {
            builder.destroy();
        }
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DownloadMusicInfo downloadMusicInfo) {
        LogUtils.showLog("BookDetailActivity:"+downloadMusicInfo);
        if (!this.isFinishing()){
            updataUI(this.data);
        }
        if (downloadMusicInfo!=null){
            AudioPlayer.get().downloadRefreshMusics(downloadMusicInfo);
        }

    }

    /** 根据百分比改变颜色透明度 */
    public int changeAlpha(int color, float fraction) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int alpha = (int) (Color.alpha(color) * fraction);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    public void itemClick(PopItemBean item) {
        int minZj = item.getMinValue();
        if (minZj>1){
            rvSubject.scrollToPosition(minZj-1);
            appBar.setExpanded(false,true);
        }else {
            rvSubject.scrollToPosition(0);
             appBar.setExpanded(true,true);
        }
    }

    @Override
    public void backClick() {

    }

    //存储本地 书籍顺序状态
    private List<String> bookOrderStatuses = new ArrayList<String>();
    private void saveBookStatus(){
        StringBuffer bookStatus = new StringBuffer();
        if (bookOrderStatuses.size()>1){
            for (int i=0;i<bookOrderStatuses.size();i++){
                if (i == 0){
                    bookStatus.append("[\""+bookOrderStatuses.get(0)+"\",");
                }else if (i==(bookOrderStatuses.size()-1)){
                    bookStatus.append("\""+bookOrderStatuses.get(i)+"\"]");
                }else {
                    bookStatus.append("\""+bookOrderStatuses.get(i)+"\",");
                }
            }
        }else if (bookOrderStatuses.size()==1){
            bookStatus.append("[\""+bookOrderStatuses.get(0)+"\"]");
        }else {
            bookStatus.append("[]");
        }
        LogUtils.showLog("bookStatus:"+bookStatus.toString());
        SharedPreferencesUtil.getInstance().putString(Constants.BOOK_STATUS,bookStatus.toString());
    }

    private void getBookStatus(){
        String json = SharedPreferencesUtil.getInstance().getString(Constants.BOOK_STATUS);
        if (!StringUtils.isEmpty(json)){
            Gson gson = new Gson();
            bookOrderStatuses.addAll(gson.fromJson(json,List.class));
        }
        if (bookOrderStatuses.contains(bookid)){
            isReverse = true;
        }
    }

    /**
     *下载*/
    private void download(final OnlineMusic onlineMusic) {
        new DownloadOnlineMusic(this, onlineMusic) {
            @Override
            public void onPrepare() {
                showLoading();
            }

            @Override
            public void onExecuteSuccess(Void aVoid) {
                Message message = Message.obtain();
                message.what = 2;
                message.obj = onlineMusic.getTitle();
                handler.sendMessage(message);
            }

            @Override
            public void onExecuteFail(Exception e) {
                Message message = Message.obtain();
                message.what = 3;
                message.obj = onlineMusic.getTitle();
                handler.sendMessage(message);

            }
        }.execute();
    }

    /**
     * 下载框的确认取消回调*/
    @Override
    public void sureDownloadBut(OnlineMusic onlineMusic) {
        if (onlineMusic!=null){
            download(onlineMusic);
        }
    }

    @Override
    public void cancleDownloadBut() {

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
                        ToastUtil.showToast((String) msg.obj);
                        break;
                    case 2:
//                        获取成功 正在下载
                        hideProgress();
                        ToastUtil.showToast(getString(R.string.now_download, (String)msg.obj));
                        break;
                    case 3:
//                        获取失败无法下载
                        hideProgress();
                        ToastUtil.showToast(R.string.unable_to_download);
                        break;
                }
            }
        }
    }

}
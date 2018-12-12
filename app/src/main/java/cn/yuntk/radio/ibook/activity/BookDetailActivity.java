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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import cn.yuntk.radio.Constants;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ad.AdController;
import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.activity.presenter.BookDetailPresenter;
import cn.yuntk.radio.ibook.activity.view.IBookDetailView;
import cn.yuntk.radio.ibook.adapter.CatalogueListAdapter;
import cn.yuntk.radio.ibook.adapter.RvItemClickInterface;
import cn.yuntk.radio.ibook.base.RootBase;
import cn.yuntk.radio.ibook.base.RootBean;
import cn.yuntk.radio.ibook.base.RootJson;
import cn.yuntk.radio.ibook.base.RootListBean;
import cn.yuntk.radio.ibook.base.refresh.BaseRefreshActivity;
import cn.yuntk.radio.ibook.bean.DownloadMusicInfo;
import cn.yuntk.radio.ibook.bean.PopItemBean;
import cn.yuntk.radio.ibook.bean.TCAlbumTable;
import cn.yuntk.radio.ibook.bean.TCBean3;
import cn.yuntk.radio.ibook.bean.TCBean4;
import cn.yuntk.radio.ibook.bean.TCBean5;
import cn.yuntk.radio.ibook.bean.TCLastListenerTable;
import cn.yuntk.radio.ibook.bean.TCListenerTable;
import cn.yuntk.radio.ibook.cache.ACache;
import cn.yuntk.radio.ibook.common.TingConstants;
import cn.yuntk.radio.ibook.common.refresh.RefreshConfig;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.component.DaggerBookComponent;
import cn.yuntk.radio.ibook.dbdao.DownloadMusicInfoManager;
import cn.yuntk.radio.ibook.dbdao.TCAlbumTableManager;
import cn.yuntk.radio.ibook.dbdao.TCLastListenerTableManager;
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
public class BookDetailActivity extends BaseRefreshActivity<BookDetailPresenter, RootJson>
        implements
        RvItemClickInterface,
        DialogUtils.PopClickInterface,
        IBookDetailView
{

    @BindView(R.id.title_left_text)
    TextView title_left_text;
    @BindView(R.id.title_right_text)
    TextView title_right_text;
    @BindView(R.id.title_content_text)
    TextView title_content_text;

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

    @BindView(R.id.book_zj_count_tv)
    TextView book_zj_count_tv;//章节总数
    @BindView(R.id.like_tv)
    TextView like_tv;//收藏
    @BindView(R.id.page_skip_tv)
    TextView page_skip_tv;//章节跳转
    @BindView(R.id.fab_order_iv)
    ImageView fab_order_iv;//到序 顺序
    @BindView(R.id.detail_download_iv)
    ImageView detail_download_iv;//批量下载

    @BindView(R.id.type_tv)
    TextView type_tv;//类型
    @BindView(R.id.detail_zz_tv)
    TextView detail_zz_tv;//作者
    @BindView(R.id.detail_bf_tv)
    TextView detail_bf_tv;//播放
    @BindView(R.id.detail_bj_tv)
    TextView detail_bj_tv;//播讲
    @BindView(R.id.book_introduce_tv)
    TextView book_introduce_tv;//简介
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


    RootBean<TCBean4> albumDetail;//专辑详情
    RootListBean<TCBean5> tracksBean;//专辑目录列表
    CatalogueListAdapter adapter;//声音适配器
    List<TCBean5> urls = new ArrayList<TCBean5>();//声音列表
    //用来缓存书籍详情以及目录
    ACache aCache;

    //已收听章节进度
    List<TCListenerTable> listenerMusicInfos = new ArrayList<TCListenerTable>();

    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);
        bookid = getIntent().getIntExtra("bookid",-1)+"";
        booktitle = getIntent().getStringExtra("booktitle");
        booktype = getIntent().getStringExtra("booktype");
        if (booktype.equals("1")){
            title_left_text.setText(getString(R.string.novel_title));
        }else if (booktype.equals("2")){
            title_left_text.setText(getString(R.string.storytelling_title));
        }else {
            title_left_text.setText(booktype);
        }
        title_content_text.setText(booktitle);
        title_right_text.setVisibility(View.GONE);
        detail_download_iv.setVisibility(View.GONE);
        fab_order_iv.setSelected(true);

        //读取数据库状态 查看是否收藏
        if (!StringUtils.isEmpty(bookid)){
            queryBookStatus();//查询收藏 上次收听
            readListByDb();//查询章节下载以及收听进度
        }

        handler = new CustomHandler(this);
        aCache = ACache.get(this);//初始化缓存
    }

    //查询收藏以及最后收听章节
    private void queryBookStatus(){
        XApplication.getDaoSession(mContext).startAsyncSession().runInTx(() -> {
            //异步操作 删除数据库
            if (BookDetailActivity.this.isFinishing()){
                LogUtils.showLog("页面已关闭");
                return;
            }
            TCAlbumTable albumTable = TCAlbumTableManager.getInstance(this).queryBeanBykey(Long.parseLong(bookid));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //查询收藏
                    if (albumTable!=null&&!StringUtils.isEmpty(albumTable.getIs_collect())&&albumTable.getIs_collect().equals("1")){
                        like_tv.setSelected(true);
                        isCollect = true;
                    }else {
                        like_tv.setSelected(false);
                        isCollect = false;
                    }

                    //查询上次听到
                    if (albumTable!=null&&
                            !StringUtils.isEmpty(albumTable.getIs_history()) &&
                            albumTable.getIs_history().equals("1")){

                        TCLastListenerTable table = TCLastListenerTableManager.getInstance(BookDetailActivity.this).queryBeanBykey(Long.parseLong(bookid));
                        if (table!=null){
                            history_text_rl.setVisibility(View.VISIBLE);
                            last_position_tv.setText(getString(R.string.book_detail_last_page)+":"+table.getZname());
                        }else {
                            history_text_rl.setVisibility(View.GONE);
                        }
                    }else {
                        history_text_rl.setVisibility(View.GONE);
                    }
                }
            });
        });

        //查询正序倒序
        String json = SharedPreferencesUtil.getInstance().getString(TingConstants.BOOK_STATUS);
        if (!StringUtils.isEmpty(json)){
            Gson gson = new Gson();
            bookOrderStatuses.addAll(gson.fromJson(json,List.class));
        }
        if (bookOrderStatuses.contains(bookid)){
            isReverse = true;
        }

    }

    //查询章节收听进度以及下载状态
    private void readListByDb(){
        //查询本书的章节下载状态
//        DownloadBookInfo info =  DownloadBookInfoManager.getInstance(mContext).queryListDB(bookid);
//        if (info != null){
//            musicInfos.clear();
//            musicInfos.addAll(info.getMusicInfoList());
//        }
//        查询本书章节收听进度
        TCAlbumTable albumTable = TCAlbumTableManager.getInstance(mContext).queryBeanBykey(Long.parseLong(bookid));
        if (albumTable !=null){
            listenerMusicInfos.clear();
            listenerMusicInfos.addAll(albumTable.getMusics());
        }
    }

    @Override
    protected void bindEvent() {

//        appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
//            //appBar 滑动监听
//        });

        adapter = new CatalogueListAdapter(this,urls,R.layout.ting_item_catalogue);
        adapter.setRvItemClickInterface(this);

        rvSubject.setLayoutManager(new LinearLayoutManager(this));
        rvSubject.setAdapter(adapter);

        refreshLayout.setEnableRefresh(true);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableAutoLoadmore(false);
        initRefreshView(refreshLayout, RefreshConfig.create().pageSize(20).autoRefresh(false));

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
        requesturlDetail();
        showProgressBarDialog("加载中...");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ting_activity_book_detail;
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


    //获取专辑详情
    private void requesturlDetail(){
        //http://app.tingchina.com/book_disp.asp?bookID=21504&oauth_token=&type=1
        HashMap<String,String> detailMap = new HashMap<String,String>();
        detailMap.put("bookID",bookid);
        detailMap.put("type",booktype);
        detailMap.put("oauth_token","");
        mPresenter.getAlbumDetail(detailMap);
    }

    //获取声音列表
    private void requestTracks() {
        if (albumDetail == null)return;
        //http://app.tingchina.com/book_downlist.asp?bookId=21504&oauth_token=&pend=50&pstr=1&type=1
        HashMap<String,String> detailMap = new HashMap<String,String>();
        detailMap.put("bookID",bookid);//专辑id
        detailMap.put("type",booktype);//专辑类型
        detailMap.put("pstr","1");//开始章节
        detailMap.put("pend",albumDetail.getData().getBookCount()+"");//章节数
        mPresenter.getAlbumTracks(detailMap);
    }

    @Override
    public void refreshData(int pageNo, int pageSize, boolean isLoadMore) {
        if (!NetworkUtils.isConnected(this)){
            refreshUIFail(-1,"");
            refreshLayout.finishLoadmore();
            refreshLayout.finishRefresh();
        }else {
            requestTracks();//加载声音列表
        }
    }

    @Override
    public void loadMoreData(int pageNo, int pageSize) {

    }

    @Override
    protected void refreshUISuccess(RootJson data, boolean isLoadMore) {
        if (data instanceof RootBean){
            albumDetail = (RootBean<TCBean4>) data;
            type_tv.setText(albumDetail.getData().getTypeId());
            detail_zz_tv.setText(albumDetail.getData().getMakeName());
            detail_bf_tv.setText(albumDetail.getData().getPlayNum()+"");
            detail_bj_tv.setText(albumDetail.getData().getHostName());
            book_zj_count_tv.setText("共"+albumDetail.getData().getBookCount()+"集");

            if (!StringUtils.isEmpty(albumDetail.getData().getBookPhoto())){
                Glide.with(this)
                        .load(albumDetail.getData().getBookPhoto())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(picture_iv);
            }
            book_introduce_tv.setText(albumDetail.getData().getIntro());
            requestTracks();//加载详情之后立马加载 声音列表

        }else if (data instanceof RootListBean){
            tracksBean = (RootListBean<TCBean5>) data;
            if (!isLoadMore){
                urls.clear();
            }
            urls.addAll(tracksBean.getData());
            updataUI();//
            hideProgress();
        }
    }

    //把收听进度状态从数据库放入到目录显示
    private void updataUI(){
        adapter.notifyDataSetChanged();
        if (aCache!=null){
            //存储书籍详情 以及声音列表
            aCache.put("ytk"+bookid+"album_detail", GsonUtils.parseToJsonString(this.albumDetail));
            aCache.put("ytk"+bookid+"album_tracks",GsonUtils.parseToJsonString(this.tracksBean));
        }

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                if (listenerMusicInfos!=null&&listenerMusicInfos.size()!=0){
                    //说明这本书 有收听的章节
                    for (TCBean5 bean:urls){
                        for (TCListenerTable musicInfo:listenerMusicInfos){
                            if (musicInfo.getEpis() == bean.getEpis()){
                                bean.setDuration(musicInfo.getDuration());
                                bean.setListenerStatus(musicInfo.getListenerStatus());
                                bean.setProgress(musicInfo.getProgress());
                                int pr = (musicInfo.getProgress()*100)/musicInfo.getDuration();
                                if (pr == 0){
                                    bean.setDisplayProgress("1%");
                                }else {
                                    bean.setDisplayProgress(pr+"%");
                                }
                            }
                        }
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (BookDetailActivity.this.isFinishing()){
                            LogUtils.showLog("当前页面也关闭");
                            return;
                        }
                        if (isReverse){
                            Collections.reverse(urls);
                            adapter.notifyDataSetChanged();
                        }else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        });
    }


    @Override
    protected void refreshUIFail(int code, String msg) {

        //存储书籍详情
        String json_album = aCache.getAsString("ytk"+bookid+"album_detail");
        String json_track = aCache.getAsString("ytk"+bookid+"album_tracks");

        if (!StringUtils.isEmpty(json_album)&&!StringUtils.isEmpty(json_track)){

            Type album_type = new TypeToken<RootBean<TCBean4>>() {}.getType();
            Type track_type = new TypeToken<RootListBean<TCBean5>>() {}.getType();

            RootBean<TCBean4> albumDetail = new Gson().fromJson(json_album, album_type);
            RootListBean<TCBean5> tracks = new Gson().fromJson(json_track, track_type);
            refreshUISuccess(albumDetail,false);
            refreshUISuccess(tracks,false);
        }else {
            hideProgress();
            ToastUtil.showToast(getString(R.string.load_again));
        }

    }

    @OnClick({R.id.title_left_text,R.id.like_tv,R.id.fab_order_iv,R.id.page_skip_tv,R.id.last_position_tv,R.id.detail_download_iv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_left_text:
                finish();
                break;
            case R.id.like_tv:
                shiftCollect();
                break;
            case R.id.fab_order_iv:
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
                        showToast_("以正序");
                    }else {
                        //变成倒序 添加bookid
                        isReverse = true;
                        bookOrderStatuses.add(bookid);
                        showToast_("以倒序");
                    }
                    saveBookStatus();//保存倒序顺序状态
                }
                break;
            case R.id.page_skip_tv:
                showPop();
                break;
            case R.id.last_position_tv:
                LogUtils.showLog("上次听到");
                if (this.tracksBean!=null&&this.tracksBean.getData()!=null&&this.tracksBean.getData().size()!=0){
                    XApplication.getDaoSession(mContext).startAsyncSession().runInTx(() -> {
                        int pos = 0;//查找上次听到的位置
                        TCAlbumTable albumTable = TCAlbumTableManager.getInstance(this).queryBeanBykey(Long.parseLong(bookid));
                        //查询上次听到
                        if (albumTable!=null&&
                                !StringUtils.isEmpty(albumTable.getIs_history()) &&
                                albumTable.getIs_history().equals("1")){

                            TCLastListenerTable table = TCLastListenerTableManager.getInstance(BookDetailActivity.this).queryBeanBykey(Long.parseLong(bookid));
                            if (table!=null){
                                for (int i=0;i<urls.size();i++){
                                    if (urls.get(i).getEpis() == table.getEpis()){
                                        pos = i;
                                        LogUtils.showLog("上次收听位置："+table.getZname());
                                        break;
                                    }
                                }
                            }else {

                            }
                        }

                        int finalPos = pos;

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!NetworkUtils.isConnected(BookDetailActivity.this)){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(BookDetailActivity.this);
                                    builder.setTitle("温馨提示");
                                    builder.setMessage("网络未连接！");
                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.create().show();
                                }else {
                                    onItemClick(urls.get(finalPos));
                                }
                            }
                        });
                    });

                }else {
                    ToastUtil.showToast("小说目录加载失败，请刷新");
                }
                break;
        }
    }

    /**
     * 收藏与取消收藏
     * */
    private void shiftCollect(){
        if (albumDetail == null) return;
        //添加数据库与删除数据库操作
        if (isCollect){
            XApplication.getDaoSession(mContext).startAsyncSession().runInTx(() -> {
                //UPDATE 异步操作 删除数据库
                delCollection();
            });

            like_tv.setSelected(false);
            Message message = Message.obtain();
            message.obj = getString(R.string.collect_cancle_tip);
            message.what = 1;
            handler.sendMessage(message);
        }else {
            XApplication.getDaoSession(mContext).startAsyncSession().runInTx(() -> {
                insertCollection();
            });
            like_tv.setSelected(true);
            Message message = Message.obtain();
            message.obj = getString(R.string.collect_succ_tip);
            message.what = 1;
            handler.sendMessage(message);
        }
        isCollect = !isCollect;
        EventBus.getDefault().post(TingConstants.UPDATA_COLLECT);//刷新收藏
    }

    /**
     *加入收藏
     * */
    public void insertCollection() {
        if (this.albumDetail!=null){
            TCAlbumTable albumTable = TCAlbumTableManager.getInstance(this).queryBeanBykey((long)albumDetail.getData().getBookID());
            if (albumTable == null){
                albumTable = new TCAlbumTable();
                albumTable.setBookID((long) albumDetail.getData().getBookID());
                albumTable.setBookName(albumDetail.getData().getBookName());
                albumTable.setIntro(albumDetail.getData().getIntro());
                albumTable.setBookPhoto(albumDetail.getData().getBookPhoto());
                albumTable.setHostName(albumDetail.getData().getHostName());
                albumTable.setPlayNum(albumDetail.getData().getPlayNum());
                albumTable.setRemark1(booktype);//专辑类型 1小说2相声
                albumTable.setIs_collect("1");
                TCAlbumTableManager.getInstance(this).insertMusic(albumTable);
            }else {
                albumTable.setIs_collect("1");
                TCAlbumTableManager.getInstance(this).updateMusic(albumTable);
            }
        }
    }

    /**
     * 取消收藏
     * */
    public void delCollection(){
        if (this.albumDetail!=null){
            TCAlbumTable albumTable = TCAlbumTableManager.getInstance(this).queryBeanBykey((long)albumDetail.getData().getBookID());
            if (albumTable != null){
                albumTable.setIs_collect("0");
                TCAlbumTableManager.getInstance(this).updateMusic(albumTable);
            }
        }
    }

    //章节跳转
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
    public void onItemClick(RootBase o) {
        LogUtils.showLog("Item点击");
        if (o instanceof TCBean3){//专辑回调
            TCBean3 xgBean = (TCBean3) o;
            Intent intent = new Intent(mContext, BookDetailActivity.class);
            intent.putExtra("bookid",xgBean.getBookID());
            intent.putExtra("booktitle",xgBean.getBookName());
            intent.putExtra("booktype",booktype);
            startActivity(intent);
        }else if (o instanceof TCBean5){
            if (this.albumDetail!=null&&this.urls.size()!=0){

                TCBean5 bean = (TCBean5) o;
                if (!NetworkUtils.isConnected(this)&&!bean.getIs_download().equals("1")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("温馨提示");
                    builder.setMessage("网络未连接，无法播放未下载章节！");
                    builder.setPositiveButton("确定", (dialog, which) -> dialog.dismiss());
                    builder.create().show();
                    return;
                }

                //存储书籍详情
                SharedPreferencesUtil.getInstance().putString(TingConstants.ALBUM_DETAIL , GsonUtils.parseToJsonString(this.albumDetail));
                //存储专辑目录
                SharedPreferencesUtil.getInstance().putString(TingConstants.ALBUM_TRACK,GsonUtils.parseToJsonString(this.tracksBean));
                //保存最新的小说id
                SharedPreferencesUtil.getInstance().putString(TingConstants.BOOK_ID,bookid);

                int pos = urls.indexOf(bean);
                SharedPreferencesUtil.getInstance().putString(TingConstants.PLAY_PAGE_BOOK_ID,bookid);
                SharedPreferencesUtil.getInstance().putString(TingConstants.PLAY_PAGE_TYPE,booktype);//专辑类型
                SharedPreferencesUtil.getInstance().putString(TingConstants.PLAY_PAGE_TITLE,albumDetail.getData().getBookName());
                SharedPreferencesUtil.getInstance().putString(TingConstants.PLAY_PAGE_TITLE_NAME,bean.getZname());
                SharedPreferencesUtil.getInstance().putString(TingConstants.PLAY_PAGE_EPISODES,bean.getEpis()+"");
                SharedPreferencesUtil.getInstance().putString(TingConstants.PLAY_PAGE_CON,albumDetail.getData().getIntro());
                SharedPreferencesUtil.getInstance().putString(TingConstants.PLAY_PAGE_PIC,albumDetail.getData().getBookPhoto());
                SharedPreferencesUtil.getInstance().putInt(TingConstants.PLAY_CLICK_POSITION,pos);

                Intent intent = new Intent(this,BookPlayActivity.class);
                startActivity(intent);
            }else {
                LogUtils.showLog("albumDetail == null ");
                showToast_("没有数据");
            }
        }
    }


    AdController builder;
    @Override
    protected void onResume() {
        super.onResume();
        builder = new AdController
                .Builder(this)
                .setPage(Constants.MUSIC_DETAIL)
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

    //    收听进度更新
    @Subscribe(threadMode =  ThreadMode.MAIN)
    public void onEvent(String string){
        LogUtils.showLog("BookDetailActivity:"+string);
        if (string.equals(TingConstants.UPDATA_LISTENER_PROGRESS)){
            XApplication.getMainThreadHandler().postDelayed(new Runnable() {@Override
            public void run() {
                updateListenerProgress();
            }},50);
        }
    }

    //及时更新听书进度
    public void updateListenerProgress(){
        if (BookDetailActivity.this.isFinishing()){
            LogUtils.showLog("页面已关闭");
            return;
        }
        if (!StringUtils.isEmpty(bookid)){
            readListByDb();//查看章节收听进度
            //查询最后收听章节
            XApplication.getDaoSession(mContext).startAsyncSession().runInTx(() -> {
                //异步操作 删除数据库
                TCAlbumTable albumTable = TCAlbumTableManager.getInstance(this).queryBeanBykey(Long.parseLong(bookid));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //查询上次听到
                        if (albumTable!=null&&
                                !StringUtils.isEmpty(albumTable.getIs_history()) &&
                                albumTable.getIs_history().equals("1")){

                            TCLastListenerTable table = TCLastListenerTableManager.getInstance(BookDetailActivity.this).queryBeanBykey(Long.parseLong(bookid));
                            if (table!=null){
                                history_text_rl.setVisibility(View.VISIBLE);
                                last_position_tv.setText(getString(R.string.book_detail_last_page)+":"+table.getZname());
                            }else {
                                history_text_rl.setVisibility(View.GONE);
                            }
                        }else {
                            history_text_rl.setVisibility(View.GONE);
                        }
                    }
                });
            });
            XApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updataUI();
                }
            },200);
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
//            smoothMoveToPosition(rvSubject,minZj);
            appBar.setExpanded(false,true);
        }else {
            rvSubject.scrollToPosition(0);
             appBar.setExpanded(true,true);
        }
    }

    /**
     * 使指定的项平滑到顶部
     *
     * @param mRecyclerView
     * @param position      待指定的项
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        int firstItemPosition = -1;
        int lastItemPosition = -1;

        //todo 获取第一个和最后一个可见位置方式1
        // 第一个可见位置
        firstItemPosition = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        lastItemPosition = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));


        LogUtils.showLog("smoothMoveToPosition: firstItemPosition::" + firstItemPosition + " lastItemPosition::" + lastItemPosition + "\n");

        if (position < firstItemPosition) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItemPosition) {
            // 第二种可能:跳转位置在第一个可见位置之后,在最后一个可见项之前
            int movePosition = position - firstItemPosition;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int top = mRecyclerView.getChildAt(movePosition).getTop();
                mRecyclerView.smoothScrollBy(0, top);//dx>0===>向左  dy>0====>向上
            }
        } else {
            // 第三种可能:跳转位置在最后可见项之后
            mRecyclerView.smoothScrollToPosition(position);
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
        SharedPreferencesUtil.getInstance().putString(TingConstants.BOOK_STATUS,bookStatus.toString());
    }


    /**
     * 自定义Handler
     * */
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

    //  更新数据库
    public void updataBook_Chapter(DownloadMusicInfo info){
        LogUtils.showLog("FileDownloadListener 成功 更新："+info.getTitle());
        info.setMark1("1");
        DownloadMusicInfoManager.getInstance(this).updateBtn(info);
        EventBus.getDefault().post(info);
        showToast_(info.getTitle()+"下载成功");
        EventBus.getDefault().post(info.getTitle());//通知下载页面刷新
    }

    //删除数据库
    public void deleteBook_Chapter(DownloadMusicInfo info,String status){
        LogUtils.showLog("FileDownloadListener:失败 删除："+info.getTitle());
        info.setMark1("2");
        DownloadMusicInfoManager.getInstance(this).deleteBtn(info);
        if (status.equals("error")){
            showToast_(info.getTitle()+"下载失败");
        }
    }

    public void showToast_(String string){
        XApplication.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShortToast(string);
            }
        });
    }

}
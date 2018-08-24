package cn.yuntk.radio.ibook.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.XApplication;
import cn.yuntk.radio.ibook.activity.presenter.BookPalyPresenter;
import cn.yuntk.radio.ibook.activity.view.IBookPlayView;
import cn.yuntk.radio.ibook.base.BaseTitleActivity;
import cn.yuntk.radio.ibook.bean.Mp3urlBean;
import cn.yuntk.radio.ibook.bean.Music;
import cn.yuntk.radio.ibook.bean.PopItemBean;
import cn.yuntk.radio.ibook.cache.ACache;
import cn.yuntk.radio.ibook.common.Constants;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.component.DaggerBookComponent;
import cn.yuntk.radio.ibook.dbdao.Mp3DaoUtils;
import cn.yuntk.radio.ibook.fragment.BookPlayFragment;
import cn.yuntk.radio.ibook.service.Actions;
import cn.yuntk.radio.ibook.service.AudioPlayer;
import cn.yuntk.radio.ibook.service.FloatViewService;
import cn.yuntk.radio.ibook.service.MusicType;
import cn.yuntk.radio.ibook.service.OnPlayerEventListener;
import cn.yuntk.radio.ibook.service.QuitTimer;
import cn.yuntk.radio.ibook.util.DialogUtils;
import cn.yuntk.radio.ibook.util.GsonUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.NetworkUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.SystemUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class BookPlayActivity extends BaseTitleActivity<BookPalyPresenter> implements IBookPlayView,
        OnPlayerEventListener,
        DialogUtils.PopClickInterface,
        QuitTimer.OnTimerListener {

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
    @BindView(R.id.container_fl)
    FrameLayout containerFl;

    String data_id="";
    String svv_id ="";
    String title="";
    String title_name="";
    String type ="";
    String bookid = "";
    String con ="";

    String mp3Url = "";
    BookPlayFragment fragment;
    Mp3DaoUtils mp3Dao;//数据库操作

    private List<PopItemBean> items = new ArrayList<>();
    public String replay = "";//"replay_0":不重新播放 "replay_1" 重新播放

    private ACache aCache;

    /*判断本地book_id 是否与播放的book_id*/
    private boolean isSameBook(){
        String local_book_id = SharedPreferencesUtil.getInstance().getString(Constants.BOOK_ID);//得到最新的小说id
        if (!StringUtils.isEmpty(local_book_id)
                &&local_book_id.equals(AudioPlayer.get().getPlayMusic().getSongId()+"")){
            return true;
        }else {
            return false;
        }
    }

    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);

        replay = getIntent().getStringExtra("replay");
        type = SharedPreferencesUtil.getInstance().getString(Constants.PLAY_PAGE_TYPE);
        data_id = SharedPreferencesUtil.getInstance().getString(Constants.PLAY_PAGE_DATA_ID);
        svv_id = SharedPreferencesUtil.getInstance().getString(Constants.PLAY_PAGE_SVV_ID);
        title = SharedPreferencesUtil.getInstance().getString(Constants.PLAY_PAGE_TITLE);
        title_name = SharedPreferencesUtil.getInstance().getString(Constants.PLAY_PAGE_TITLE_NAME);
        bookid = SharedPreferencesUtil.getInstance().getString(Constants.PLAY_PAGE_BOOK_ID);
        con = SharedPreferencesUtil.getInstance().getString(Constants.PLAY_PAGE_CON);

        LogUtils.showLog("data_id2："+data_id);

        nextTv.setText(getString(R.string.book_sleep));
        titleTv.setText(title_name);
        titleTv.setSelected(true);
        mp3Dao = new Mp3DaoUtils();

        fragment = BookPlayFragment.newInstance(mp3Url,"");
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container_fl,fragment).commit();

        /*填装睡眠数据*/
        for (int i=0;i<6;i++){
            PopItemBean itemBean = new PopItemBean();
            itemBean.setType(PopItemBean.PopTpye.SLEEP);
            items.add(itemBean);
        }
        items.get(0).setMaxValue(600).setShowString("10分钟");
        items.get(1).setMaxValue(1200).setShowString("20分钟");
        items.get(2).setMaxValue(1800).setShowString("30分钟");
        items.get(3).setMaxValue(3600).setShowString("1小时");
        items.get(4).setMaxValue(7200).setShowString("2小时");
        items.get(5).setMaxValue(0).setShowString("取消睡眠");
        aCache = ACache.get(this);
    }

    @Override
    protected void bindEvent() {
        AudioPlayer.get().addOnPlayEventListener(this);
        QuitTimer.get().setOnTimerListener(this);
    }

    @Override
    protected void loadData() {
        if (!StringUtils.isEmpty(replay)&&replay.equals("replay_0")&&AudioPlayer.get().isPausing()){
            //暂停时点击悬浮窗 进入播放 继续上次时间点播放
            getoPlayPage();
            return;
        }

        if ((AudioPlayer.get().isPlaying()||AudioPlayer.get().isPausing())&&isSameBook()){
            setPlayPage();
        }else {
            requestUrl();//
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_listener_book_play;
    }

    @Override
    protected BookPalyPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @OnClick({R.id.back_ll, R.id.next_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_ll:
                LogUtils.showLog("返回");
                finish();
                break;
            case R.id.next_ll:
                LogUtils.showLog("睡眠");
                showPop();
                break;
        }
    }

    //    睡眠弹出
    private void showPop(){
        DialogUtils.get().showPop(this,getString(R.string.book_sleep),items,this);
    }

    //    loadurl
    private void requestUrl() {
        if (!NetworkUtils.isConnected(this)){
            //getSuccess(null);
            LogUtils.showLog("网络未连接！！！");
            XApplication.getMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getFail(-1,"");
                }
            },100);
        }else {
            Map<String,String> map = new HashMap<>();
            map.put("data_id",data_id);
            map.put("svv_id",svv_id);
            mPresenter.getMp3Url(map);
        }
    }

    @Override
    public void getSuccess(Mp3urlBean data) {
        if (data!=null&&!StringUtils.isEmpty(data.getUrl_id())){
            //存储本书籍音频前缀
            aCache.put(Constants.YTK_PREFIX_STR+bookid, GsonUtils.parseToJsonString(data));
            String prefix_str = data.getUrl_id().substring(0,data.getUrl_id().indexOf("com/"))+"com/";
            mp3Url = prefix_str+data_id;//音频地址
            SharedPreferencesUtil.getInstance().putString(Constants.BOOK_URL,prefix_str);
            AudioPlayer.get().setMusicList(MusicType.ALBUM_SOURCE);//整本小说 添加到播放列表

            if (AudioPlayer.get().isPlaying()){
                AudioPlayer.get().stopPlayer();//暂停播放
            }

            int pos = SharedPreferencesUtil.getInstance().getInt(Constants.CLICK_POSITION);
            AudioPlayer.get().setPlayPosition(pos);//更新播放曲目位置
//            AudioPlayer.get().play(AudioPlayer.get().getPlayPosition());
            XApplication.getDaoSession(mContext).startAsyncSession().runInTx(() -> {
                //DELETE 异步操作 插入数据库
                updataDB(mp3Url,"0");
            });

            Bundle bundle = new Bundle();
            bundle.putString("param1",mp3Url);
            bundle.putString("param1","false");
            fragment.setArguments(bundle);
            fragment.setmParam1();

            EventBus.getDefault().post(Constants.UPDATA_FRAGMENT4);//加入历史 并且刷新历史
        }
    }

    @Override
    public void getFail(int code, String msg) {
        LogUtils.showLog("BookPlayActivity:getFail:"+msg);
        String json = aCache.getAsString(Constants.YTK_PREFIX_STR+bookid);
        if (!StringUtils.isEmpty(json)){
            Mp3urlBean data = GsonUtils.parseObject(json,Mp3urlBean.class);
            getSuccess(data);
        }else {
            SharedPreferencesUtil.getInstance().putString(Constants.BOOK_URL,"");
            ToastUtil.showToast(getString(R.string.load_mp3_fail));
        }
    }

    /*正在播放时候从弹出进入*/
    public void setPlayPage(){
        Music music = AudioPlayer.get().getPlayMusic();
        LogUtils.showLog("music.getPath()："+music.getMark_2());
        LogUtils.showLog("data_id3："+data_id);
        if (music.getMark_2().equals(data_id)){
            LogUtils.showLog("当前播放的章节 包含："+data_id);
            fragment = BookPlayFragment.newInstance(music.getPath(),"true");
        }else {
            LogUtils.showLog("当前播放的章节 不包含："+data_id+" 更新播放章节");
            if (AudioPlayer.get().isPlaying()){
                AudioPlayer.get().stopPlayer();//暂停播放
            }
            int pos = SharedPreferencesUtil.getInstance().getInt(Constants.CLICK_POSITION);
            AudioPlayer.get().setPlayPosition(pos);//更新播放曲目位置
            fragment = BookPlayFragment.newInstance(music.getPath(),"false");
        }
        getSupportFragmentManager().beginTransaction().add(R.id.container_fl,fragment).commit();
    }
    /*暂停时点击悬浮窗*/
    public void getoPlayPage(){
        Music music = AudioPlayer.get().getPlayMusic();
        fragment = BookPlayFragment.newInstance(music.getPath(),"true");
        getSupportFragmentManager().beginTransaction().add(R.id.container_fl,fragment).commit();
    }

    /*
     *加入历史
     * */
    public Music updataDB(String path, String duration) {
        Music music = mp3Dao.queryListDB(bookid);
        if (music!=null){
            music.setIs_history(Music.History_Type.YES);
            music.setPath(path);
            music.setZj_title(title_name);//章节标题
            music.setDuration(Long.parseLong(duration));
            music.setMark_1(svv_id);
            music.setMark_2(data_id);
            mp3Dao.updateBtn(music);
        }else {
            music = new Music();
            music.setSongId(Long.parseLong(bookid));//小说id
            music.setTitle(title);//小说标题
            music.setIs_collect(Music.Collect_Type.NO);//是否收藏
            music.setAlbum(type);//小说类型
            music.setIs_history(Music.History_Type.YES);
            music.setPath(path);//小说路径
            music.setType(Music.Type.ONLINE);
            music.setZj_title(title_name);//章节标题
            music.setBook_con(con);//小说简介
            music.setDuration(Long.parseLong(duration));
            music.setMark_1(svv_id);
            music.setMark_2(data_id);
            mp3Dao.insertDB(music);
        }
        return music;
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.showLog("BookPlayActivity:onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.showLog("BookPlayActivity:onResume");
        FloatViewService.startCommand(this, Actions.SERVICE_GONE_WINDOW);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.showLog("BookPlayActivity:onPause");
//        FloatViewService.startCommand(this, Actions.SERVICE_VISABLE_WINDOW);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.showLog("BookPlayActivity:onStop");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String string) {
        LogUtils.showLog("BookPlayActivity:"+string);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatViewService.startCommand(this, Actions.SERVICE_VISABLE_WINDOW);
        LogUtils.showLog("BookPlayActivity:onDestroy");
        EventBus.getDefault().unregister(this);
        AudioPlayer.get().removeOnPlayEventListener(this);
        QuitTimer.get().setOnTimerListener(null);
    }

    @Override
    public void onChange(Music music) {
        LogUtils.showLog("BookPlayActivity:onChange");
        if (!this.isFinishing()){
            title_name = StringUtils.getString(music.getZj_title());
            titleTv.setText(title_name);
        }
    }

    @Override
    public void onPlayerStart() {
        LogUtils.showLog("BookPlayActivity:onPlayerStart");
    }

    @Override
    public void onPlayerPause() {
        LogUtils.showLog("BookPlayActivity:onPlayerPause");
    }

    @Override
    public void onPublish(int progress) {
//        LogUtils.showLog("BookPlayActivity:onPublish");
    }

    @Override
    public void onBufferingUpdate(int percent) {
//        LogUtils.showLog("BookPlayActivity:onBufferingUpdate");
    }
    //睡眠模式
    @Override
    public void itemClick(PopItemBean item) {
        if (item!=null){
            QuitTimer.get().start(item.getMaxValue()* 1000);
            if (item.getMaxValue() > 0) {
                ToastUtil.showToast(getString(R.string.timer_set, item.getShowString()));
            } else {
                ToastUtil.showToast(R.string.timer_cancel);
            }
        }
    }

    @Override
    public void backClick() {
        LogUtils.showLog("关闭睡眠弹窗");
    }

    @Override
    public void onTimer(long remain) {
        LogUtils.showLog("onTimer:"+remain);
        nextTv.setText(remain == 0 ? getString(R.string.book_sleep) : SystemUtils.formatTime(getString(R.string.book_sleep) + "(mm:ss)", remain));
    }

}
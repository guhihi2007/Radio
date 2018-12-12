package cn.yuntk.radio.ibook.activity;

import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.HashMap;

import butterknife.BindView;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.activity.presenter.BookPalyPresenter;
import cn.yuntk.radio.ibook.activity.view.IBookPlayView;
import cn.yuntk.radio.ibook.base.BaseTitleActivity;
import cn.yuntk.radio.ibook.base.RootBean;
import cn.yuntk.radio.ibook.base.RootJson;
import cn.yuntk.radio.ibook.bean.TCBean5;
import cn.yuntk.radio.ibook.bean.TCBean6;
import cn.yuntk.radio.ibook.cache.ACache;
import cn.yuntk.radio.ibook.common.TingConstants;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.component.DaggerBookComponent;
import cn.yuntk.radio.ibook.fragment.BookPlayFragment;
import cn.yuntk.radio.ibook.service.Actions;
import cn.yuntk.radio.ibook.service.AudioPlayer;
import cn.yuntk.radio.ibook.service.FloatViewService;
import cn.yuntk.radio.ibook.service.MusicType;
import cn.yuntk.radio.ibook.util.GsonUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;

public class BookPlayActivity extends
        BaseTitleActivity<BookPalyPresenter> implements
        IBookPlayView {

    @BindView(R.id.container_fl)
    FrameLayout containerFl;

    String type ="";//书籍类型 1小说 2相声
    String bookid = "";//书籍id
    String epis ="";//章节id
//    String title="";//书本名字
//    String title_name="";//章节标题
//    String con ="";//书籍简介
//    String book_cover ="";//书籍封面

    String mp3Url = "";
    BookPlayFragment fragment;
    RootBean<TCBean6> mp3Data;
    public String replay = "";//"replay_0":不重新播放 "replay_1" 重新播放

    private ACache aCache;

    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);

        replay = getIntent().getStringExtra("replay");
        bookid = SharedPreferencesUtil.getInstance().getString(TingConstants.PLAY_PAGE_BOOK_ID);
        type = SharedPreferencesUtil.getInstance().getString(TingConstants.PLAY_PAGE_TYPE);
        epis = SharedPreferencesUtil.getInstance().getString(TingConstants.PLAY_PAGE_EPISODES);

//        title = SharedPreferencesUtil.getInstance().getString(TingConstants.PLAY_PAGE_TITLE);
//        title_name = SharedPreferencesUtil.getInstance().getString(TingConstants.PLAY_PAGE_TITLE_NAME);
//        con = SharedPreferencesUtil.getInstance().getString(TingConstants.PLAY_PAGE_CON);
//        book_cover = SharedPreferencesUtil.getInstance().getString(TingConstants.PLAY_PAGE_PIC);

        fragment = BookPlayFragment.newInstance(mp3Url,"false");
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container_fl,fragment).commitAllowingStateLoss();

        aCache = ACache.get(this);
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void loadData() {
        if (!StringUtils.isEmpty(replay)&&replay.equals("replay_0")&& AudioPlayer.get().isPausing()){
            //暂停时点击悬浮窗 进入播放 继续上次时间点播放
            getoPlayPage();
            return;
        }

        if ((AudioPlayer.get().isPlaying()||AudioPlayer.get().isPausing())&&isSameBook()){
            setPlayPage();
        }else {
            //http://app.tingchina.com/play_cdn.asp?bookID=30178&bookType=1&episodes=3
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("bookID",bookid);
            map.put("bookType",type);
            map.put("episodes",epis);
           mPresenter.getPlayCdn(map);
        }
        //http://app.tingchina.com/play_cdn.asp?bookID=30178&bookType=1&episodes=3
//        HashMap<String,String> map = new HashMap<String,String>();
//        map.put("bookID",bookid);
//        map.put("bookType",type);
//        map.put("episodes",epis);
//        mPresenter.getPlayCdn(map);

    }

    /** 正在播放时候进入 （已经判断是同1本书*/
    public void setPlayPage(){
        TCBean5 music = AudioPlayer.get().getPlayMusic();
        LogUtils.showLog("setPlayPage()");
        if (!StringUtils.isEmpty(epis)&&epis.equals(music.getEpis()+"")){
            //继续播放播放曲目
            fragment = BookPlayFragment.newInstance(music.getPath(),"true");
        }else {
            //不是正在播放的曲目
            if (AudioPlayer.get().isPlaying()){
                AudioPlayer.get().stopPlayer();//暂停播放
            }
            int pos = SharedPreferencesUtil.getInstance().getInt(TingConstants.PLAY_CLICK_POSITION);
            AudioPlayer.get().setPlayPosition(pos);//更新播放曲目位置
            fragment = BookPlayFragment.newInstance(music.getPath(),"false");
        }
        getSupportFragmentManager().beginTransaction().add(R.id.container_fl,fragment).commitAllowingStateLoss();
    }

    /** 暂停时点击悬浮窗 */
    public void getoPlayPage(){
        TCBean5 music = AudioPlayer.get().getPlayMusic();
        fragment = BookPlayFragment.newInstance(music.getPath(),"true");
        getSupportFragmentManager().beginTransaction().add(R.id.container_fl,fragment).commitAllowingStateLoss();
    }


    @Override
    protected int getLayoutId() {
//        Eyes.translucentStatusBar(this, false);//背景延伸到状态栏
        return R.layout.ting_activity_book_play;
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

    @Override
    public void getSuccess(RootJson rootJson) {
        LogUtils.showLog("BookPlayActivity:onSuccess:");
       if (rootJson instanceof RootBean){
           mp3Data = (RootBean<TCBean6>) rootJson;
           if (mp3Data.getStatus() == 1){
               //存储本书籍音频前缀
               aCache.put(TingConstants.YTK_PREFIX_STR+bookid, GsonUtils.parseToJsonString(mp3Data));
               mp3Url = mp3Data.getData().getUrl();
               AudioPlayer.get().setMusicList(MusicType.ALBUM_SOURCE);//整本小说 添加到播放列表
               if (AudioPlayer.get().isPlaying()){
                   AudioPlayer.get().stopPlayer();//暂停播放
               }

               int pos = SharedPreferencesUtil.getInstance().getInt(TingConstants.PLAY_CLICK_POSITION);
               AudioPlayer.get().setPlayPosition(pos);//更新播放曲目位置
               LogUtils.showLog("BookPlayActivity:mp3Url:"+mp3Url);
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       fragment.updateParam(mp3Url,"false");
                   }
               });

           }else {
               ToastUtil.showToast(getString(R.string.load_mp3_fail));
           }
       }
    }

    @Override
    public void getFail(int code, String msg) {
        LogUtils.showLog("BookPlayActivity:onFailure:"+msg);
        String json = aCache.getAsString(TingConstants.YTK_PREFIX_STR+bookid);
        if (!StringUtils.isEmpty(json)){
            Type jsonType = new TypeToken<RootBean<TCBean6>>(){}.getType();
            mp3Data = new Gson().fromJson(json, jsonType);
            getSuccess(mp3Data);
        }else {
            ToastUtil.showToast(getString(R.string.load_mp3_fail));
        }
    }

    /** 判断本地book_id 是否与播放的book_id */
    private boolean isSameBook(){
        if (!StringUtils.isEmpty(bookid)
                &&bookid.equals(AudioPlayer.get().getPlayMusic().getBookID()+"")){
            return true;
        }else {
            return false;
        }
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
        FloatViewService.startCommand(this, Actions.SERVICE_VISABLE_WINDOW);
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
        AudioPlayer.get().updateListenerProgress();//存储听书进度
        EventBus.getDefault().unregister(this);
    }


}
package cn.yuntk.radio.ibook.service;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.api.BaseOkhttp;
import cn.yuntk.radio.ibook.base.RootBean;
import cn.yuntk.radio.ibook.base.RootListBean;
import cn.yuntk.radio.ibook.bean.Music;
import cn.yuntk.radio.ibook.bean.TCAlbumTable;
import cn.yuntk.radio.ibook.bean.TCBean4;
import cn.yuntk.radio.ibook.bean.TCBean5;
import cn.yuntk.radio.ibook.bean.TCBean6;
import cn.yuntk.radio.ibook.bean.TCLastListenerTable;
import cn.yuntk.radio.ibook.bean.TCListenerTable;
import cn.yuntk.radio.ibook.common.TingConstants;
import cn.yuntk.radio.ibook.dbdao.Mp3DaoUtils;
import cn.yuntk.radio.ibook.dbdao.TCAlbumTableManager;
import cn.yuntk.radio.ibook.dbdao.TCLastListenerTableManager;
import cn.yuntk.radio.ibook.dbdao.TCListenerTableManager;
import cn.yuntk.radio.ibook.receiver.NoisyAudioStreamReceiver;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.NetworkUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;
import cn.yuntk.radio.play.PlayManager;

/**
 * Created by xue on 2018/1/26.
 *
 * 播放器控制
 */
public class AudioPlayer {
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PLAYING = 2;
    private static final int STATE_PAUSE = 3;

    private static final long TIME_UPDATE = 300L;

    private Context context;
    private AudioFocusManager audioFocusManager;
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private NoisyAudioStreamReceiver noisyReceiver;
    private IntentFilter noisyFilter;
    private List<TCBean5> musicList;
    private final List<OnPlayerEventListener> listeners = new ArrayList<>();
    private int state = STATE_IDLE;
    private int speedLevel = 0;//倍速播放等级
    private Mp3DaoUtils mp3DaoUtils;

    public static AudioPlayer get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static AudioPlayer instance = new AudioPlayer();
    }

    private AudioPlayer() {
        mp3DaoUtils = new Mp3DaoUtils();
    }

    public void init(Context context) {

        this.context = context.getApplicationContext();

        musicList = new ArrayList<>();
        setMusicList(MusicType.ALBUM_SOURCE);//初始化 播放列表
        audioFocusManager = new AudioFocusManager(context);
        mediaPlayer = new MediaPlayer();
        handler = new Handler(Looper.getMainLooper());

        noisyReceiver = new NoisyAudioStreamReceiver();
        noisyFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
//        当前播放结束监听
        mediaPlayer.setOnCompletionListener(mp -> {
            next();
            LogUtils.showLog("AudioPlayer1:setOnCompletionListener:"+getPlayMusic().getPath());
        });
        //        当前播放错误监听
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            stopPlayer();
            ToastUtil.showToast("音频播放错误");
            LogUtils.showLog("AudioPlayer1:setOnErrorListener:"+getPlayMusic().getPath());
            return true;
        });
        mediaPlayer.setOnPreparedListener(mp -> {
            if (isPreparing()) {
                int duration = mediaPlayer.getDuration();
                TCBean5 music = getPlayMusic();
                LogUtils.showLog("AudioPlayer1:setOnPreparedListener:duration"+duration+":Path:"+music.getPath());
                music.setDuration(duration);
                updataBook_Zj(music);//存储上次收听到的章节 并将章节加入到收听进度数据库

            }
        });
        mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            for (OnPlayerEventListener listener : listeners) {
                listener.onBufferingUpdate(percent);
            }
        });
    }

    public void setMusicList(String musicType) {
        switch (musicType){
            case MusicType.ALBUM_SOURCE:
                setTheAlbum();
                break;
            case MusicType.HISTORY_SOURCE:
                break;
            case MusicType.COLLECT_SOURCE:
                break;
            case MusicType.DOWNLOAD_SOURCE:
                break;
            case MusicType.DEFAULT_SOURCE:
                default:
        }
    }

    //    设置专辑
    public void setTheAlbum(){
        String album_json = SharedPreferencesUtil.getInstance().getString(TingConstants.ALBUM_DETAIL);//获取正在播放的专辑详情
        String track_json = SharedPreferencesUtil.getInstance().getString(TingConstants.ALBUM_TRACK);//获取正在播放的专辑目录
        if (!StringUtils.isEmpty(album_json)){
            if (musicList == null){
                ToastUtil.showToast("遇到未知问题，请重启App!");
                return;
            }
            this.musicList.clear();
            Type album_type = new TypeToken<RootBean<TCBean4>>() {}.getType();
            Type track_type = new TypeToken<RootListBean<TCBean5>>() {}.getType();

            RootBean<TCBean4> albumDetail = new Gson().fromJson(album_json, album_type);
            RootListBean<TCBean5> tracks = new Gson().fromJson(track_json, track_type);

            if (albumDetail!=null&&tracks!=null&&tracks.getData().size()!=0){
                for (int i=0;i<tracks.getData().size();i++){
                    TCBean5 music = new TCBean5();
                    music.setBookID(albumDetail.getData().getBookID());//小说id
                    music.setBookname(albumDetail.getData().getBookName());//小说标题
                    music.setBookPhoto(albumDetail.getData().getBookPhoto());//专辑封面
                    music.setIntro(albumDetail.getData().getIntro());//专辑简介
                    music.setHostName(albumDetail.getData().getHostName());//播讲
                    music.setPlayNum(albumDetail.getData().getPlayNum());//播放量

                    music.setEpis(tracks.getData().get(i).getEpis());//章节id
                    music.setZname(tracks.getData().get(i).getZname());//章节标题
                    music.setFilesize(tracks.getData().get(i).getFilesize());//章节大小
                    music.setTimesize(tracks.getData().get(i).getTimesize());//章节时长

                    music.setListenerStatus(0);//播放状态
                    music.setTypeid(SharedPreferencesUtil.getInstance().getString(TingConstants.PLAY_PAGE_TYPE));//专辑类型

                    if (tracks.getData().get(i).getIs_download().equals("1")
                            &&!StringUtils.isEmpty(tracks.getData().get(i).getPath())){
                        //本地已下载过了 直接读取
                        music.setPath(tracks.getData().get(i).getPath());//小说路径
                        music.setIs_local("1");
                    }else {
                        music.setPath("onilen");//小说路径
                        music.setIs_local("0");
                    }
                    this.musicList.add(music);
                }
                //查看是否为倒序
                if (getBookStatus(albumDetail.getData().getBookID()+"")){
                    Collections.reverse(musicList);
                }
            }
        }

    }


    public void addOnPlayEventListener(OnPlayerEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeOnPlayEventListener(OnPlayerEventListener listener) {
        listeners.remove(listener);
    }


    public void play(int position) {

        if (musicList == null||musicList.isEmpty()) {
            return;
        }

        TCBean5 album_track =  musicList.get(position);

        LogUtils.showLog("play(int position):" +
                        "BookName:"+ album_track.getBookname()+
                        ":ZName:"+album_track.getZname()+
                        ":Path:"+ album_track.getPath());

        if (position < 0) {
            showToast_("已经是第一章了");
            return;
        } else if (position >= musicList.size()) {
            showToast_("已经是最后一章了");
            return;
        }


        if (album_track.getIs_download().equals("1")){
            delayPlay(position);//播放已下载音频
        }else {
            if (!NetworkUtils.isConnected(context)){
                showToast_("网络未连接 不能播放线上音频");
            }else {
                if (!isGetPlayCdn){
                    isGetPlayCdn = true;
                    getPlayCdn(
                            album_track.getBookID()+"",
                            album_track.getTypeid(),
                            album_track.getEpis()+"",
                            position);
                }
            }
        }
    }

    private boolean isGetPlayCdn = false;//判断是否在请求中 false没有请求 true请求中
    //获取播放地址
    private void getPlayCdn(String bookid, String bookType, String epis, int position){

        HashMap<String,String> map = new HashMap<String,String>();
        map.put("bookID",bookid);
        map.put("bookType",bookType);
        map.put("episodes",epis);

        BaseOkhttp.getInstance().getPlayCdn(map,new BaseOkhttp.RequestCallback(){
            @Override
            public void onSuccess(String response) {
                try{
                    Type jsonType = new TypeToken<RootBean<TCBean6>>(){}.getType();
                    RootBean<TCBean6> mp3Data = new Gson().fromJson(response, jsonType);
                    if (mp3Data!=null&&mp3Data.getStatus() == 1){
                        TCBean5 album_track =  musicList.get(position);
                        album_track.setPath(mp3Data.getData().getUrl());
                        XApplication.getMainThreadHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                delayPlay(position);
                            }
                        });
                    }else {
                        showToast_("获取播放地址错误");
                    }
                }catch (Exception e){
                    showToast_("获取播放地址错误");
                }
                isGetPlayCdn = false;
            }

            @Override
            public void onFailure(String msg, Exception e) {
                showToast_("获取播放地址失败");
                isGetPlayCdn = false;
            }
        });
    }

    private void delayPlay(int position){

        if (isPlaying()||isPausing()){
            updateListenerProgress();//切换歌曲前先保存当前播放进度到数据库
        }

        setPlayPosition(position);//存储当前位置
        TCBean5 music = getPlayMusic();

        try {

//            URL url = new URL(music.getPath());
//            String url1 = url.getPath();
//            String url2 = url.toExternalForm();
//
//            String strUTF8 = URLEncoder.encode(music.getPath(), "UTF-8");
//            System.out.println(strUTF8);
//            String str = new String(music.getPath().getBytes("UTF-8"), "UTF-8");

            //只把中文字符转成utf-8
            String allowedChars=":._-$,;~()/ ";
            String urlEncoded = Uri.encode(music.getPath(), allowedChars);


            //http://audio.xmcdn.com/group49/M02/FA/61/wKgKl1u664TSIIthAA6H1FLDF10326.m4a
            //http://tcdn44.tingchina.com/5C061AB7/4593bf6247422bae275e7f6664029837/yousheng/%E7%8E%84%E5%B9%BB%E5%A5%87%E5%B9%BB/%E6%96%97%E7%A0%B4%E8%8B%8D%E7%A9%B9_%E8%9C%A1%E7%AC%94%E5%B0%8F%E5%8B%87/0003%E7%AC%AC0002%E7%AB%A0_%E6%96%97%E6%B0%94%E5%A4%A7%E9%99%86.mp3
            //http://tcdn44.tingchina.com/5C061EAE/dafd3c88312587232bba2d62b573b3ec/yousheng/%E7%8E%84%E5%B9%BB%E5%A5%87%E5%B9%BB/%E6%96%97%E7%A0%B4%E8%8B%8D%E7%A9%B9_%E8%9C%A1%E7%AC%94%E5%B0%8F%E5%8B%87/0004%E7%AC%AC0003%E7%AB%A0_%E5%AE%A2%E4%BA%BA.mp3

            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(urlEncoded);
            mediaPlayer.prepareAsync();

            //更新播放页面参数设置 音频转换实时更新
            SharedPreferencesUtil.getInstance().putString(TingConstants.PLAY_PAGE_BOOK_ID,music.getBookID()+"");
            SharedPreferencesUtil.getInstance().putString(TingConstants.PLAY_PAGE_TITLE,music.getBookname());
            SharedPreferencesUtil.getInstance().putString(TingConstants.PLAY_PAGE_EPISODES,music.getEpis()+"");
            SharedPreferencesUtil.getInstance().putString(TingConstants.PLAY_PAGE_TITLE_NAME,music.getZname());

            // this checks on API 23 and up
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //设置上次的播放速度
                String speedLevel = SharedPreferencesUtil.getInstance().getString(TingConstants.LISTENER_SPEED);
                if (StringUtils.isEmpty(speedLevel)){speedLevel = "0";}
                int level = Integer.parseInt(speedLevel);
                this.speedLevel = level;
                float speed = 1.0f;
                if (level == 0){
                    speed = 1.0f;
                }if (level == 1){
                    speed = 1.25f;
                }else if (level == 2){
                    speed = 1.5f;
                }else if (level == 3){
                    speed = 1.75f;
                }else if (level == 4){
                    speed = 1.99f;
                }else if (level == 5){
                    speed = 0.5f;
                }
                if (isPlaying()) {
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                } else if (isPausing()){
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                }
            }

            state = STATE_PREPARING;
            for (OnPlayerEventListener listener : listeners) {
                listener.onChange(music);
            }

            Music m = new Music();
            m.setTitle(music.getBookname());
            m.setZj_title(music.getZname());
            m.setAlbum("");
            m.setDuration(Long.parseLong(music.getTimesize())*1000);
            m.setAlbumId(music.getBookID());
            m.setCoverPath(music.getBookPhoto());
            if (music.getIs_local().equals("1")){
                m.setType(0);//本地
            }else {
                m.setType(1);//在线
            }

//        Notifier.get().showPlay(m);//没有和收音机兼容，先不用
        MediaSessionManager.get().updateMetaData(m);
        MediaSessionManager.get().updatePlaybackState();
        } catch (IOException e) {
            e.printStackTrace();
            showToast_("当前音频无法播放");
        }catch (Exception e){
            e.printStackTrace();
        }catch (Throwable throwable){
            throwable.printStackTrace();
            LogUtils.showLog("1111Throwable:"+throwable.toString());
        }

    }

    public void delete(int position) {
        int playPosition = getPlayPosition();
        TCBean5 music = musicList.remove(position);
        mp3DaoUtils.deleteBtn(music.getBookID()+"");
        if (playPosition > position) {
            setPlayPosition(playPosition - 1);
        } else if (playPosition == position) {
            if (isPlaying() || isPreparing()) {
                setPlayPosition(playPosition - 1);
                next();
            } else {
                stopPlayer();
                for (OnPlayerEventListener listener : listeners) {
                    listener.onChange(getPlayMusic());
                }
            }
        }
    }

    public void playPause() {
        if (isPreparing()) {
            stopPlayer();
        } else if (isPlaying()) {
            pausePlayer();
        } else if (isPausing()) {
            startPlayer();
        } else {
            play(getPlayPosition());
        }
    }

    public void startPlayer() {

        if (!isPreparing() && !isPausing()) {
            return;
        }
        PlayManager.Companion.getInstance().stop();//停止收音机播放

        if (audioFocusManager.requestAudioFocus()) {
            mediaPlayer.start();
            state = STATE_PLAYING;
            handler.post(mPublishRunnable);
            TCBean5 bean5 = getPlayMusic();
            Music m = new Music();
            m.setTitle(bean5.getBookname());
            m.setZj_title(bean5.getZname());
            m.setAlbum("");
            m.setDuration(Long.parseLong(bean5.getTimesize())*1000);
            m.setAlbumId(bean5.getBookID());
            m.setCoverPath(bean5.getBookPhoto());
            if (bean5.getIs_local().equals("1")){
                m.setType(0);//本地
            }else {
                m.setType(1);//在线
            }
//            Notifier.get().showPlay(m);//没有和收音机兼容，先不用
            MediaSessionManager.get().updatePlaybackState();
            context.registerReceiver(noisyReceiver, noisyFilter);

            for (OnPlayerEventListener listener : listeners) {
                listener.onPlayerStart();
            }
        }

    }

    public void pausePlayer() {
        pausePlayer(true);
    }

    public void pausePlayer(boolean abandonAudioFocus) {
        if (!isPlaying()) {
            return;
        }

        mediaPlayer.pause();
        state = STATE_PAUSE;
        handler.removeCallbacks(mPublishRunnable);

        TCBean5 bean5 = getPlayMusic();
        Music m = new Music();
        m.setTitle(bean5.getBookname());
        m.setZj_title(bean5.getZname());
        m.setAlbum("");
        m.setDuration(Long.parseLong(bean5.getTimesize())*1000);
        m.setAlbumId(bean5.getBookID());
        m.setCoverPath(bean5.getBookPhoto());
        if (bean5.getIs_local().equals("1")){
            m.setType(0);//本地
        }else {
            m.setType(1);//在线
        }

//        Notifier.get().showPause(m);//没有和收音机兼容，先不用
        MediaSessionManager.get().updatePlaybackState();
        try{
            context.unregisterReceiver(noisyReceiver);
        }catch (Exception e){
        }

        if (abandonAudioFocus) {
            audioFocusManager.abandonAudioFocus();
        }
        //暂停时候记录进度
        updateListenerProgress();
        for (OnPlayerEventListener listener : listeners) {
            listener.onPlayerPause();
        }
    }

    public void stopPlayer() {
        if (isIdle()) {
            return;
        }

        pausePlayer();
        mediaPlayer.reset();
        state = STATE_IDLE;
    }

    public void next() {
        if (musicList == null||musicList.isEmpty()) {
            return;
        }
        int po = getPlayPosition()+1;
        if (po<musicList.size()){
            play(po);
        }else {
            ToastUtil.showToast("已经是最后一章了");
        }
        LogUtils.showLog("next--next"+po);

//        PlayModeEnum mode = PlayModeEnum.valueOf(SharedPreferencesUtil.getInstance().getInt(TingConstants.PLAY_MODE));
//        switch (mode) {
//            case SHUFFLE:
//                play(new Random().nextInt(musicList.size()));
//                break;
//            case SINGLE:
//                play(getPlayPosition());
//                break;
//            case LOOP:
//            default:
//
//                break;
//        }

    }

    public void prev() {
        if (musicList == null||musicList.isEmpty()) {
            return;
        }
        int po = getPlayPosition()-1;
        if (po<0){
            ToastUtil.showToast("已经是第一章了");
        }else {
            play(po);
        }
        LogUtils.showLog("prev--prev"+po);

//        PlayModeEnum mode = PlayModeEnum.valueOf(SharedPreferencesUtil.getInstance().getInt(TingConstants.PLAY_MODE));
//        switch (mode) {
//            case SHUFFLE:
//                play(new Random().nextInt(musicList.size()));
//                break;
//            case SINGLE:
//                play(getPlayPosition());
//                break;
//            case LOOP:
//            default:
//
//                break;
//        }
    }

    /**
     * 跳转到指定的时间位置
     * @param msec 时间
     */
    public void seekTo(int msec) {
        if (isPlaying() || isPausing()) {
            mediaPlayer.seekTo(msec);
            MediaSessionManager.get().updatePlaybackState();
            for (OnPlayerEventListener listener : listeners) {
                listener.onPublish(msec);
            }
        }
    }

    private Runnable mPublishRunnable = new Runnable() {
        @Override
        public void run() {
            if (isPlaying()) {
                for (OnPlayerEventListener listener : listeners) {
                    listener.onPublish(mediaPlayer.getCurrentPosition());
                }
            }
            handler.postDelayed(this, TIME_UPDATE);
        }
    };

    public long getAudioPosition() {
        if (isPlaying() || isPausing()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public TCBean5 getPlayMusic() {
        if (musicList == null||musicList.isEmpty()) {
            return null;
        }
        return musicList.get(getPlayPosition());
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public List<TCBean5> getMusicList() {
        return musicList;
    }

    public boolean isPlaying() {
        return state == STATE_PLAYING;
    }

    public boolean isPausing() {
        return state == STATE_PAUSE;
    }

    public boolean isPreparing() {
        return state == STATE_PREPARING;
    }

    public boolean isIdle() {
        return state == STATE_IDLE;
    }

    public int getPlayPosition() {
        if (musicList == null||musicList.isEmpty()) {
            return 0;
        }
        int position = SharedPreferencesUtil.getInstance().getInt(TingConstants.PLAY_POSITION);
        if (position < 0 || position >= musicList.size()) {
            position = 0;
            SharedPreferencesUtil.getInstance().putInt(TingConstants.PLAY_POSITION,position);
        }
        return position;
    }

    public void setPlayPosition(int position) {
        SharedPreferencesUtil.getInstance().putInt(TingConstants.PLAY_POSITION,position);
        SharedPreferencesUtil.getInstance().putInt(TingConstants.PLAY_CLICK_POSITION,position);
    }

    //倒序当前播放
    public void reverseList(String book_id){//集合倒序
        if (musicList!=null&&musicList.size()!=0){
            if (musicList.get(0).getBookID() == Long.parseLong(book_id)){
                Collections.reverse(musicList);
            }
        }
    }

    /** 异步更新 */
    public void updataBook_Zj(TCBean5 music_zj){
        XApplication.getDaoSession(context).startAsyncSession().runInTx(() -> {
            //DELETE 异步操作 插入数据库
            savaListenerProgress(music_zj);//新增章节收听进度
        });
    }

    /** 查看该书籍是否倒序 */
    private boolean getBookStatus(String bookid){
        String json = SharedPreferencesUtil.getInstance().getString(TingConstants.BOOK_STATUS);
        List<String> bookOrderStatuses = new ArrayList<String>();
        if (!StringUtils.isEmpty(json)){
            Gson gson = new Gson();
            bookOrderStatuses.addAll(gson.fromJson(json,List.class));
        }
        if (bookOrderStatuses.contains(bookid)){
            return true;
        }else {
            return false;
        }
    }

    //    倍速
    public void changeplayerSpeed(int level) {
        if (level == this.speedLevel) return;
        this.speedLevel = level;
        float speed = 1.0f;
        if (level == 0){
            speed = 1.0f;
        }if (level == 1){
            speed = 1.25f;
        }else if (level == 2){
            speed = 1.5f;
        }else if (level == 3){
            speed = 1.75f;
        }else if (level == 4){
            speed = 1.99f;
        }else if (level == 5){
            speed = 0.5f;
        }
        try {
            // this checks on API 23 and up
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                LogUtils.showLog("Speed:"+mediaPlayer.getPlaybackParams().getSpeed());
                if (isPlaying()) {
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                } else {
                    mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speed));
                    pausePlayer();
                }
                SharedPreferencesUtil.getInstance().putString(TingConstants.LISTENER_SPEED,speedLevel+"");
            }
        }catch (Exception e){
            LogUtils.showLog("changeplayerSpeed:"+e.toString());
            SharedPreferencesUtil.getInstance().putString(TingConstants.LISTENER_SPEED,speedLevel+"");
        }
    }

    //存储书籍 章节进度
    public void savaListenerProgress(TCBean5 music){
        //查询本书章节收听进度
        TCAlbumTable albumInfo = TCAlbumTableManager.getInstance(context).queryBeanBykey((long)music.getBookID());
        if (albumInfo==null){
            //插入收听记录
            TCAlbumTable bookInfo = new TCAlbumTable();
            bookInfo.setBookID((long)music.getBookID());
            bookInfo.setBookName(music.getBookname());
            bookInfo.setIntro(music.getIntro());
            bookInfo.setBookPhoto(music.getBookPhoto());
            bookInfo.setHostName(music.getHostName());
            bookInfo.setPlayNum(music.getPlayNum());
            bookInfo.setIs_history("1");
            bookInfo.setRemark1(SharedPreferencesUtil.getInstance().getString(TingConstants.PLAY_PAGE_TYPE));//专辑类型 1小说2评书
            TCAlbumTableManager.getInstance(context).insertMusic(bookInfo);
            albumInfo = bookInfo;//
            EventBus.getDefault().post(TingConstants.UPDATA_HISTORY);//通知刷新历史记录页面
        }
//        else {
//            albumInfo.setIs_history("1");
//            TCAlbumTableManager.getInstance(context).insertMusic(albumInfo);
//        }


        //查询最后收听章节
        TCLastListenerTable lastListenerTable = TCLastListenerTableManager.getInstance(context).queryBeanBykey((long)music.getBookID());
        if (lastListenerTable==null){
            //插入收听记录
            lastListenerTable = new TCLastListenerTable();
            lastListenerTable.setBookID((long)music.getBookID());
            lastListenerTable.setBookName(music.getBookname());
            lastListenerTable.setEpis(music.getEpis());
            lastListenerTable.setZname(music.getZname());
            TCLastListenerTableManager.getInstance(context).insertMusic(lastListenerTable);
        }else {
            lastListenerTable.setEpis(music.getEpis());
            lastListenerTable.setZname(music.getZname());
            TCLastListenerTableManager.getInstance(context).updateMusic(lastListenerTable);
        }

        albumInfo.getMusics();
        List<TCListenerTable> musicInfos = albumInfo.getMusics();
        if (musicInfos!=null||musicInfos.size() == 0){
            for (TCListenerTable musicInfo:musicInfos){
                if (musicInfo.getEpis() == music.getEpis()){
//                    如果数据库中存在相同数据 就不新增了 并且将进度条调到数据库记录进度
                    if (musicInfo.getListenerStatus() == 2){//已经播放完成 开始从头播放
                        onPreparedPlay(music,"0",0);
                    }else {//播放了一段 从记录进度处开始继续播放
                        onPreparedPlay(music,"1",musicInfo.getProgress());
                    }
                    LogUtils.showLog("进度表数据库存在数据：Bookname:"+musicInfo.getBookname());
                    LogUtils.showLog("进度表数据库存在数据：Zname:"+musicInfo.getZname());
                    LogUtils.showLog("进度表数据库存在数据：Seek To Progress:"+musicInfo.getProgress());
                    return;
                }
            }
        }

        TCListenerTable info = new TCListenerTable();
        info.setBookID((long)music.getBookID());
        info.setBookname(music.getBookname());
        info.setEpis(music.getEpis());
        info.setZname(music.getZname());
        info.setIs_local(music.getIs_local());//歌曲类型:本地1/网络0
        info.setListenerStatus(1);//已经开始播放 播放状态
        info.setIs_download(music.getIs_download());
        info.setDuration(music.getDuration());//总时长
        info.setPath(music.getPath());//音频路径
        info.setProgress(1);//播放进度
        TCListenerTableManager.getInstance(context).insertMusic(info);
        //数据流准备完成 数据查完之后 开始通知播放
        onPreparedPlay(music,"0",0);
    }

    //准备完成后 查询数据库后 播放 isSeekTo 0不滑动1滑动
    private void onPreparedPlay(TCBean5 music, String isSeekTo, int progress){
        XApplication.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                startPlayer();
                if (music.getIs_local() .equals("1")){
                    for (OnPlayerEventListener listener : listeners) {
                        listener.onBufferingUpdate(100);
                    }
                }
                if (isSeekTo.equals("1")){
                    seekTo(progress);
                }
            }
        });
    }

    //更新章节进度
    public void updateListenerProgress(){
        TCBean5 music = getPlayMusic();
        if (music == null)
            return;
        long progress = mediaPlayer.getCurrentPosition();
        //查询本书章节收听进度
        TCAlbumTable albumInfo = TCAlbumTableManager.getInstance(context).queryBeanBykey((long)music.getBookID());
        if (albumInfo!=null){

            //查询最后收听章节
            TCLastListenerTable lastListenerTable = TCLastListenerTableManager.getInstance(context).queryBeanBykey((long)music.getBookID());
            if (lastListenerTable==null){
                //插入收听记录
                lastListenerTable = new TCLastListenerTable();
                lastListenerTable.setBookID((long)music.getBookID());
                lastListenerTable.setBookName(music.getBookname());
                lastListenerTable.setEpis(music.getEpis());
                lastListenerTable.setZname(music.getZname());
                TCLastListenerTableManager.getInstance(context).insertMusic(lastListenerTable);
            }else {
                lastListenerTable.setEpis(music.getEpis());
                lastListenerTable.setZname(music.getZname());
                TCLastListenerTableManager.getInstance(context).updateMusic(lastListenerTable);
            }

            albumInfo.getMusics();
            List<TCListenerTable> musicInfos = albumInfo.getMusics();
            if (musicInfos!=null&&musicInfos.size() != 0){
                for (TCListenerTable musicInfo:musicInfos){
                    if (musicInfo.getEpis() == music.getEpis()){
                        if (musicInfo.getDuration()-progress<=1000){
                            musicInfo.setListenerStatus(2);
                            musicInfo.setProgress(musicInfo.getDuration());

                        }else {
                            musicInfo.setListenerStatus(1);
                            musicInfo.setProgress(Integer.parseInt(progress+""));
                        }
                        LogUtils.showLog("进度表数据库更新数据：Bookname:"+musicInfo.getBookname());
                        LogUtils.showLog("进度表数据库更新数据：Zname:"+musicInfo.getZname());
                        LogUtils.showLog("进度表数据库更新数据：Seek To Progress:"+musicInfo.getProgress());
                        TCListenerTableManager.getInstance(context).updateMusic(musicInfo);
                        return;
                    }
                }

                EventBus.getDefault().post(TingConstants.UPDATA_LISTENER_PROGRESS);//通知刷新收听进度

            }
        }
    }

    //吐司
    public void showToast_(String string){
        XApplication.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShortToast(string);
            }
        });
    }

}
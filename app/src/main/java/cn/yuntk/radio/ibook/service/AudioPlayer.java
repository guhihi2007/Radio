package cn.yuntk.radio.ibook.service;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;

import cn.yuntk.radio.ibook.XApplication;
import cn.yuntk.radio.ibook.bean.BookDetailBean;
import cn.yuntk.radio.ibook.bean.DownloadMusicInfo;
import cn.yuntk.radio.ibook.bean.Music;
import cn.yuntk.radio.ibook.common.Constants;
import cn.yuntk.radio.ibook.dbdao.Mp3DaoUtils;
import cn.yuntk.radio.ibook.receiver.NoisyAudioStreamReceiver;
import cn.yuntk.radio.ibook.util.GsonUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.NetworkUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.yuntk.radio.ibook.bean.Music;
import cn.yuntk.radio.manager.PlayServiceManager;

/**
 * Created by hzwangchenyan on 2018/1/26.
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
    private List<Music> musicList;
    private final List<OnPlayerEventListener> listeners = new ArrayList<>();
    private int state = STATE_IDLE;
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
            LogUtils.showLog("AudioPlayer1:setOnCompletionListener:" + getPlayMusic().getPath());
        });
        //        当前播放错误监听
        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            stopPlayer();
//            ToastUtil.showToast("音频播放错误");
            LogUtils.showLog("AudioPlayer1:setOnErrorListener:" + getPlayMusic().getPath());
            return true;
        });
        mediaPlayer.setOnPreparedListener(mp -> {
            if (isPreparing()) {
                int duration = mediaPlayer.getDuration();
                Music music = getPlayMusic();
                LogUtils.showLog("AudioPlayer1:setOnPreparedListener:duration" + duration + ":Path:" + music.getPath());
                music.setDuration(duration);
                updataBook_Zj(music);
                startPlayer();
                if (music.getType() == Music.Type.LOCAL) {
                    for (OnPlayerEventListener listener : listeners) {
                        listener.onBufferingUpdate(100);
                    }
                }
            }
        });
        mediaPlayer.setOnBufferingUpdateListener((mp, percent) -> {
            for (OnPlayerEventListener listener : listeners) {
                listener.onBufferingUpdate(percent);
            }
        });
    }

    public void setMusicList(String musicType) {
        switch (musicType) {
            case MusicType.ALBUM_SOURCE:
                setTheAlbum();
                break;
            case MusicType.HISTORY_SOURCE:
                this.musicList = mp3DaoUtils.queryListDB_History();
                break;
            case MusicType.COLLECT_SOURCE:
                this.musicList = mp3DaoUtils.queryListDB_Collect();
                break;
            case MusicType.DOWNLOAD_SOURCE:
                this.musicList = mp3DaoUtils.queryListDB_DownLoad();
                break;
            case MusicType.DEFAULT_SOURCE:
                this.musicList = mp3DaoUtils.queryListDB();
            default:
        }
    }

    //    设置专辑
    public void setTheAlbum() {
        String json = SharedPreferencesUtil.getInstance().getString(Constants.BOOK_DETAIL);
        String book_url = SharedPreferencesUtil.getInstance().getString(Constants.BOOK_URL);//有可能是空 断网播放本地音频
        if (!StringUtils.isEmpty(json)) {
            this.musicList.clear();
            BookDetailBean bookDetailBean = GsonUtils.parseObject(json, BookDetailBean.class);
            if (bookDetailBean != null && bookDetailBean.getUrl_list() != null && bookDetailBean.getUrl_list().size() != 0) {
                for (int i = 0; i < bookDetailBean.getUrl_list().size(); i++) {
                    Music music = new Music();
                    music.setSongId(bookDetailBean.getHtml_id());//小说id
                    music.setTitle(bookDetailBean.getTitle());//小说标题
                    music.setIs_collect(Music.Collect_Type.NO);//是否收藏
                    music.setAlbum(bookDetailBean.getType());//小说类型
                    music.setIs_history(Music.History_Type.YES);

                    if (bookDetailBean.getUrl_list().get(i).getIs_download().equals("1") && !StringUtils.isEmpty(bookDetailBean.getUrl_list().get(i).getPath())) {
                        //本地已下载过了 直接读取
                        music.setPath(bookDetailBean.getUrl_list().get(i).getPath());//小说路径
                        music.setType(Music.Type.LOCAL);
                    } else {
                        music.setPath(book_url + bookDetailBean.getUrl_list().get(i).getUrl());//小说路径
                        music.setType(Music.Type.ONLINE);
                    }

                    music.setZj_id(Integer.parseInt(bookDetailBean.getUrl_list().get(i).getU()));//章节id
                    music.setZj_title(bookDetailBean.getUrl_list().get(i).getName());//章节标题
                    music.setBook_con(bookDetailBean.getCon());//小说简介
                    music.setMark_1(bookDetailBean.getSvid() + "");
                    music.setMark_2(bookDetailBean.getUrl_list().get(i).getUrl());
                    this.musicList.add(music);
                }
                //查看是否为倒序
                if (getBookStatus(bookDetailBean.getHtml_id() + "")) {
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

    public void addAndPlay(Music music) {
        int position = musicList.indexOf(music);
        if (position < 0) {
            musicList.add(music);
            mp3DaoUtils.insertDB(music);
            position = musicList.size() - 1;
        }
        play(position);
    }

    public void play(int position) {

        if (musicList == null || musicList.isEmpty()) {
            return;
        }
        if (!NetworkUtils.isConnected(context) && musicList.get(position).getType() == Music.Type.ONLINE) {
            LogUtils.showLog("网络未连接 不能播放线上歌曲：Path:" + musicList.get(position).getPath() + ":Type:" + musicList.get(position).getType());
            ToastUtil.showToast("网络未连接 不能播放线上音频");
            return;
        }
        if (position < 0) {
            ToastUtil.showToast("已经是第一章了");
            return;
        } else if (position >= musicList.size()) {
            ToastUtil.showToast("已经是最后一章了");
            return;
        }

        setPlayPosition(position);
        Music music = getPlayMusic();

        try {
            delayPlay(music);
            state = STATE_PREPARING;
            for (OnPlayerEventListener listener : listeners) {
                listener.onChange(music);
            }
        } catch (IOException e) {
            e.printStackTrace();
            ToastUtil.showToast("当前音频无法播放");
        }

    }

    private void delayPlay(Music music) throws IOException {

        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.setDataSource(music.getPath());
        mediaPlayer.prepareAsync();
        //更新播放页面参数设置 音频转换实时更新
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_TYPE, music.getAlbum());
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_DATA_ID, music.getMark_2());
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_SVV_ID, music.getMark_1());
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_TITLE, music.getTitle());
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_TITLE_NAME, music.getZj_title());
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_BOOK_ID, music.getSongId() + "");
        SharedPreferencesUtil.getInstance().putString(Constants.PLAY_PAGE_CON, music.getBook_con());

        Notifier.get().showPlay(music);
        MediaSessionManager.get().updateMetaData(music);
        MediaSessionManager.get().updatePlaybackState();
    }

    public void delete(int position) {
        int playPosition = getPlayPosition();
        Music music = musicList.remove(position);
        mp3DaoUtils.deleteBtn(music.getSongId() + "");
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
        //听小说道前，如果广播频道不是空闲状态就停止
        if (!PlayServiceManager.INSTANCE.isListenerIdle()) {
            PlayServiceManager.INSTANCE.stop();
        }
        if (!isPreparing() && !isPausing()) {
            return;
        }
        if (audioFocusManager.requestAudioFocus()) {
            mediaPlayer.start();
            state = STATE_PLAYING;
            handler.post(mPublishRunnable);
            Notifier.get().showPlay(getPlayMusic());
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
        //听小说道前，如果广播频道不是空闲状态就停止
        if (!PlayServiceManager.INSTANCE.isListenerIdle()) {
            PlayServiceManager.INSTANCE.stop();
        }

        if (!isPlaying()) {
            return;
        }

        mediaPlayer.pause();
        state = STATE_PAUSE;
        handler.removeCallbacks(mPublishRunnable);
        Notifier.get().showPause(getPlayMusic());
        MediaSessionManager.get().updatePlaybackState();
        context.unregisterReceiver(noisyReceiver);

        if (abandonAudioFocus) {
            audioFocusManager.abandonAudioFocus();
        }

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
        if (musicList == null || musicList.isEmpty()) {
            return;
        }
        int po = getPlayPosition() + 1;
        if (po < musicList.size()) {
            play(po);
        } else {
            ToastUtil.showToast("已经是最后一章了");
        }
        LogUtils.showLog("next--next" + po);
//        PlayModeEnum mode = PlayModeEnum.valueOf(SharedPreferencesUtil.getInstance().getInt(Constants.PLAY_MODE));
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
        if (musicList == null || musicList.isEmpty()) {
            return;
        }
        int po = getPlayPosition() - 1;
        if (po < 0) {
            ToastUtil.showToast("已经是第一章了");
        } else {
            play(po);
        }
        LogUtils.showLog("prev--prev" + po);
//        PlayModeEnum mode = PlayModeEnum.valueOf(SharedPreferencesUtil.getInstance().getInt(Constants.PLAY_MODE));
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
     *
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

    public int getAudioSessionId() {
        return mediaPlayer.getAudioSessionId();
    }

    public long getAudioPosition() {
        if (isPlaying() || isPausing()) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public Music getPlayMusic() {
        if (musicList == null || musicList.isEmpty()) {
            return null;
        }
        return musicList.get(getPlayPosition());
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public List<Music> getMusicList() {
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
        if (musicList == null || musicList.isEmpty()) {
            return 0;
        }
        int position = SharedPreferencesUtil.getInstance().getInt(Constants.PLAY_POSITION);
        if (position < 0 || position >= musicList.size()) {
            position = 0;
            SharedPreferencesUtil.getInstance().putInt(Constants.PLAY_POSITION, position);
        }
        return position;
    }

    public void setPlayPosition(int position) {
        SharedPreferencesUtil.getInstance().putInt(Constants.PLAY_POSITION, position);
    }

    /**
     * 下载完成后 如果是当前播放的列表 就及时更新
     */
    public void downloadRefreshMusics(DownloadMusicInfo downloadMusicInfo) {//集合倒序
        String book_id = downloadMusicInfo.getBook_id();
        if (musicList != null && musicList.size() != 0 && !StringUtils.isEmpty(book_id)) {
            if (musicList.get(0).getSongId() == Long.parseLong(book_id)) {
                for (Music music : musicList) {
                    if (music.getMark_2().equals(downloadMusicInfo.getData_id())) {
                        music.setPath(downloadMusicInfo.getMusicPath());//小说路径
                        music.setType(Music.Type.LOCAL);
                        break;
                    }
                }
            }
        }
    }


    //倒序当前播放
    public void reverseList(String book_id) {//集合倒序
        if (musicList != null && musicList.size() != 0) {
            if (musicList.get(0).getSongId() == Long.parseLong(book_id)) {
                Collections.reverse(musicList);
            }
        }
    }


    /*异步更新*/
    public void updataBook_Zj(Music music_zj) {
        XApplication.getDaoSession(context).startAsyncSession().runInTx(() -> {
            //DELETE 异步操作 插入数据库
            //DELETE
            //UPDATE
            updataDB(music_zj);
        });
    }

    /*
     *更新到最新章节信息
     * */
    public Music updataDB(Music music_zj) {
        Music music = mp3DaoUtils.queryListDB(music_zj.getSongId() + "");
        if (music != null) {
            music.setIs_history(Music.History_Type.YES);
            music.setPath(music_zj.getPath());
            music.setZj_title(music_zj.getZj_title());//章节标题
            music.setDuration(music_zj.getDuration());
            music.setZj_id(music_zj.getZj_id());
            music.setMark_1(music_zj.getMark_1());
            music.setMark_2(music_zj.getMark_2());
            mp3DaoUtils.updateBtn(music);
        } else {
            music = new Music();
            music.setSongId(music_zj.getSongId());//小说id
            music.setTitle(music_zj.getTitle());//小说标题
            music.setIs_collect(Music.Collect_Type.NO);//是否收藏
            music.setAlbum(music_zj.getAlbum());//小说类型
            music.setIs_history(Music.History_Type.YES);
            music.setPath(music_zj.getPath());//小说路径
            music.setType(Music.Type.ONLINE);
            music.setZj_title(music_zj.getZj_title());//章节标题
            music.setBook_con(music_zj.getBook_con());//小说简介
            music.setDuration(music_zj.getDuration());
            music.setZj_id(music_zj.getZj_id());
            music.setMark_1(music_zj.getMark_1());
            music.setMark_2(music_zj.getMark_2());
            mp3DaoUtils.insertDB(music);
        }
        return music;
    }


    /*查看该书籍是否倒序*/
    private boolean getBookStatus(String bookid) {
        String json = SharedPreferencesUtil.getInstance().getString(Constants.BOOK_STATUS);
        List<String> bookOrderStatuses = new ArrayList<String>();
        if (!StringUtils.isEmpty(json)) {
            Gson gson = new Gson();
            bookOrderStatuses.addAll(gson.fromJson(json, List.class));
        }
        if (bookOrderStatuses.contains(bookid)) {
            return true;
        } else {
            return false;
        }
    }
}
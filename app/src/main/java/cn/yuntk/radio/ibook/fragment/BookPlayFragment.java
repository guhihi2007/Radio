package cn.yuntk.radio.ibook.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.ads.ADConstants;
import cn.yuntk.radio.ibook.ads.AdController;
import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.Music;
import cn.yuntk.radio.ibook.common.Constants;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.service.AudioPlayer;
import cn.yuntk.radio.ibook.service.OnPlayerEventListener;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.SystemUtils;
import cn.yuntk.radio.ibook.widget.AlbumCoverView;
import cn.yuntk.radio.ibook.widget.CoverLoader;

import butterknife.BindView;
import butterknife.OnClick;

/*播放页面*/
public class BookPlayFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener,OnPlayerEventListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.iv_play_page_bg)
    ImageView ivPlayPageBg;
    @BindView(R.id.tv_current_time)
    TextView tvCurrentTime;
    @BindView(R.id.sb_progress)
    SeekBar sbProgress;
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;
    @BindView(R.id.iv_prev)
    ImageView ivPrev;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_next)
    ImageView ivNext;
    @BindView(R.id.album_cover_view)
    AlbumCoverView mAlbumCoverView;
    @BindView(R.id.sb_volume)
    SeekBar sbVolume;
    @BindView(R.id.con_tv)
    TextView con_tv;
    @BindView(R.id.nativeADContainer)
    RelativeLayout nativeADContainer;
    @BindView(R.id.ad_container_fl)
    FrameLayout ad_container_fl;

//    private List<View> mViewPagerContent;

    private String mParam1;//音频地址
    private String mParam2 = "";//是否可以继续播放 false 不继续 true继续
    private AudioManager mAudioManager;

    private int mLastProgress;
    private boolean isDraggingProgress;

    // TODO: Rename and change types and number of parameters
    public static BookPlayFragment newInstance(String param1, String param2) {
        BookPlayFragment fragment = new BookPlayFragment();
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
        return R.layout.fragment_listener_play;
    }

    @Override
    protected void initViews() {
        LogUtils.showLog("MP3:URL" + mParam1);
        if (!StringUtils.isEmpty(mParam1)){
            initViewPager(); //先进入 不展示页面 等获取到音频地址成功后 展示页面内容
        }
    }

    private void initViewPager() {
        mAlbumCoverView.initNeedle(AudioPlayer.get().isPlaying());
        initVolume();
        music = AudioPlayer.get().getPlayMusic();
        if (music!=null){
            LogUtils.showLog("BookPlayFragment:Music:"+music.toString());
            LogUtils.showLog("BookPlayFragment:"+AudioPlayer.get().getAudioSessionId());
            LogUtils.showLog("BookPlayFragment:isIdle:"+AudioPlayer.get().isIdle());
            LogUtils.showLog("BookPlayFragment:isPreparing:"+AudioPlayer.get().isPreparing());
            LogUtils.showLog("BookPlayFragment:isPlaying:"+AudioPlayer.get().isPlaying());
            LogUtils.showLog("BookPlayFragment:isPausing:"+AudioPlayer.get().isPausing());
            if (!AudioPlayer.get().isPlaying()){
                if (!StringUtils.isEmpty(mParam2)&&mParam2.equals("true")&&AudioPlayer.get().isPausing()){
                    play();
                }else {
                    AudioPlayer.get().play(AudioPlayer.get().getPlayPosition());
                }
            }
            onChangeImpl(music);
        }else {
            LogUtils.showLog("initViewPager:Music == null");
        }
        AudioPlayer.get().addOnPlayEventListener(this);
        sbProgress.setOnSeekBarChangeListener(this);
        sbVolume.setOnSeekBarChangeListener(this);
    }

//    PlayOnlineMusic onlineMusic;
//    获取音频时长
//    private void playOnline(String string) {
//            if (!StringUtils.isEmpty(string)){
//                LogUtils.showLog("playOnline:"+new Date(System.currentTimeMillis()));
//                if (onlineMusic!=null){
//                    onlineMusic.onDestory();
//                }
//                onlineMusic = new PlayOnlineMusic((Activity) mContext) {
//
//                    @Override
//                    public void onPrepare() {
//                        LogUtils.showLog("onPrepare");
//                        ((BaseTitleActivity)mContext).showProgressBarDialog(getString(R.string.loading));
//                    }
//
//                    @Override
//                    public void onExecuteSuccess(String duration) {
//                        ((BaseTitleActivity)mContext).hideProgress();
//                        LogUtils.showLog("onExecuteSuccess");
//                        LogUtils.showLog("playOnline:"+new Date(System.currentTimeMillis()));
//                        AudioPlayer.get().getPlayMusic().setDuration(Long.parseLong(duration));
//                        BookPlayFragment.this.music.setDuration(Long.parseLong(duration));
//                        onChangeImpl(BookPlayFragment.this.music);
//                    }
//
//                    @Override
//                    public void onExecuteFail(Exception e) {
//                        LogUtils.showLog("onExecuteFail");
//                        ((BaseTitleActivity)mContext).hideProgress();
//                        ToastUtil.showToast(R.string.unable_to_play);
//                    }
//                };
//                onlineMusic.setUrl(string).execute();//开始获取时长
//            }else {
//                ToastUtil.showToast(R.string.unable_to_error_play_url);
//            }
//    }

    private Music music;

    private void onChangeImpl(Music music) {
        try{
            if (music == null) {
                return;
            }
            con_tv.setText(music.getBook_con());
            sbProgress.setProgress((int) AudioPlayer.get().getAudioPosition());

            if (AudioPlayer.get().getAudioPosition()>1){
                onBufferingUpdate(100);
            }else {
                sbProgress.setSecondaryProgress(0);
            }

            sbProgress.setMax((int) music.getDuration());
            mLastProgress = 0;
            tvCurrentTime.setText(R.string.play_time_start);
            tvTotalTime.setText(formatTime(music.getDuration()));
            setCoverAndBg(music);
//        setLrc(music);
            if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing()) {
                ivPlay.setSelected(true);
                mAlbumCoverView.start();
            } else {
                ivPlay.setSelected(false);
                mAlbumCoverView.pause();
            }
        }catch (Exception e){
            LogUtils.showLog("onChangeImpl:"+e.toString());
        }
    }

    private void initVolume() {
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        sbVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    private void initPlayMode() {
//        获取播放顺序
        int mode = SharedPreferencesUtil.getInstance().getInt(Constants.PLAY_MODE);
//        ivMode.setImageLevel(mode);
    }

    private void setCoverAndBg(Music music) {
        mAlbumCoverView.setCoverBitmap(CoverLoader.get().loadRound(music));
        ivPlayPageBg.setImageBitmap(CoverLoader.get().loadBlur(music));
    }

    private String formatTime(long time) {
        return SystemUtils.formatTime("mm:ss", time);
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void loadData() {

    }

    //获取到地址之后给fragment更新
    public void setmParam1(){
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        LogUtils.showLog("MP3:URL" + mParam1);
        initViewPager();
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    //音频控制
    @Override
    public void onChange(Music music) {
        onChangeImpl(music);
//        if (!NetworkUtils.isConnected(mContext)){
//            LogUtils.showLog("网络状态不好。。。。");
//            return;
//        }
//        LogUtils.showLog("BookPlayFragment:onChange");
    }


    @Override
    public void onPlayerStart() {
        ivPlay.setSelected(true);
        mAlbumCoverView.start();
        try{
            if (music!=null){
                tvTotalTime.setText(formatTime(music.getDuration()));
                sbProgress.setMax((int) music.getDuration());//得到时间后 设置进度条时间
            }
        }catch (Exception e){
            LogUtils.showLog("BookPlayFragment:onPlayerStart:Exception:"+e.getMessage());
        }

        LogUtils.showLog("BookPlayFragment:onPlayerStart");
    }

    @Override
    public void onPlayerPause() {
        ivPlay.setSelected(false);
        mAlbumCoverView.pause();
        LogUtils.showLog("BookPlayFragment:onPlayerPause");
    }

    //进度条进度显示
    @Override
    public void onPublish(int progress) {
        if (!isDraggingProgress) {
            sbProgress.setProgress(progress);
        }

    }
    //缓冲进度显示
    @Override
    public void onBufferingUpdate(int percent) {
        if (percent>0){
            sbProgress.setSecondaryProgress(sbProgress.getMax() * 100 / percent);
        }
        //LogUtils.showLog("BookPlayFragment:onBufferingUpdate:percent:"+percent);
//        LogUtils.showLog("BookPlayFragment:onBufferingUpdate");
    }


    @OnClick({R.id.iv_prev, R.id.iv_play, R.id.iv_next})
    public void onViewClicked(View view) {
        if (!StringUtils.isEmpty(mParam1)){
            switch (view.getId()) {
                case R.id.iv_prev:
                    prev();
                    break;
                case R.id.iv_play:
                    play();
                    break;
                case R.id.iv_next:
                    next();
                    break;
            }
        }else {
            LogUtils.showLog("音频地址没有获取到 请不要点击按钮");
        }
    }

    private void play() {
        AudioPlayer.get().playPause();
    }

    private void next() {
        AudioPlayer.get().next();
    }

    private void prev() {
        AudioPlayer.get().prev();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    AdController builder;
    @Override
    public void onResume() {
        super.onResume();
//        IntentFilter filter = new IntentFilter(Actions.VOLUME_CHANGED_ACTION);
//        getContext().registerReceiver(mVolumeReceiver, filter);

        builder = new AdController
                .Builder(getActivity())
                .setContainer(ad_container_fl)
                .setPage(ADConstants.LISTENING_PAGE)
                .create();
        builder.show();
    }

    private BroadcastReceiver mVolumeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            音量按钮
//            sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
//        getContext().unregisterReceiver(mVolumeReceiver);
        if (builder != null) {
            builder.destroy();
        }
        AudioPlayer.get().removeOnPlayEventListener(this);
    }

    //SeekBar监听
    //该方法拖动进度条进度改变的时候调用
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sbProgress) {
            if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                tvCurrentTime.setText(formatTime(progress));
                mLastProgress = progress;
            }
        }
    }

    //    开始拖动进度条时候条用
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = true;
        }
    }

    //    停止拖动进度条时候条用
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = false;
            if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPausing()) {
                int progress = seekBar.getProgress();
                AudioPlayer.get().seekTo(progress);
            } else {
                seekBar.setProgress(0);
            }
        } else if (seekBar == sbVolume) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(),
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }

}
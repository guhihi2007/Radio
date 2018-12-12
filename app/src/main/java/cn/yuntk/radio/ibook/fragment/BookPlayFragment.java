package cn.yuntk.radio.ibook.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.yuntk.radio.Constants;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ad.AdController;
import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.PopItemBean;
import cn.yuntk.radio.ibook.bean.TCBean5;
import cn.yuntk.radio.ibook.common.TingConstants;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.service.AudioPlayer;
import cn.yuntk.radio.ibook.service.OnPlayerEventListener;
import cn.yuntk.radio.ibook.service.QuitTimer;
import cn.yuntk.radio.ibook.util.DialogUtils;
import cn.yuntk.radio.ibook.util.ImageUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.SystemUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;
import cn.yuntk.radio.ibook.widget.AlbumCoverView;

/*播放页面*/
public class BookPlayFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener,
        OnPlayerEventListener,
        DialogUtils.PopClickInterface,
        QuitTimer.OnTimerListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.title_left_text)
    TextView title_left_text;
    @BindView(R.id.title_content_text)
    TextView title_content_text;
    @BindView(R.id.title_right_text)
    TextView title_right_text;

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

    @BindView(R.id.sleep_view)
    LinearLayout sleep_view;//睡眠点击区域
    @BindView(R.id.sleep_text_tv)
    TextView sleep_text_tv;//睡眠显示
    @BindView(R.id.play_speed_ll)
    LinearLayout play_speed_ll;//播放速度点击区域
    @BindView(R.id.play_speed_value_tv)
    TextView play_speed_value_tv;//播放速度显示

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
        return R.layout.ting_fragment_play;
    }

    //获取到地址之后给fragment更新
    public void updateParam(String par1, String par2) {
        LogUtils.showLog("MP3:URL" + mParam1);
        LogUtils.showLog("BookPlayFragment-:setmParam1");
        if (!StringUtils.isEmpty(par1)) {
            mParam1 = par1;
            mParam2 = par2;
            initViewPager();
        }
    }

    @Override
    protected void initViews() {
        LogUtils.showLog("MP3:URL" + mParam1);
        title_right_text.setVisibility(View.GONE);
        handler = new CustomHandler(getActivity());
        if (!StringUtils.isEmpty(mParam1)) {
            initViewPager(); //先进入 不展示页面 等获取到音频地址成功后 展示页面内容
        }
    }

    private void initViewPager() {
        initVolume();
        getMediaPlayer();
        initSleepTimer();//初始化睡眠数据
        mAlbumCoverView.initNeedle(AudioPlayer.get().isPlaying());

        String con = SharedPreferencesUtil.getInstance().getString(TingConstants.PLAY_PAGE_CON);//书籍简介
        String book_cover = SharedPreferencesUtil.getInstance().getString(TingConstants.PLAY_PAGE_PIC);//书籍图片
        con_tv.setText(con);//设置简介
        setCoverAndBg(book_cover);//设置封面

        music = AudioPlayer.get().getPlayMusic();
        if (music != null) {
            if (!AudioPlayer.get().isPlaying()) {//暂停播放状态
                if (!StringUtils.isEmpty(mParam2) && mParam2.equals("true") && AudioPlayer.get().isPausing()) {
                    //继续播放
                    play();
                } else {
                    //播放新的音频
                    AudioPlayer.get().play(AudioPlayer.get().getPlayPosition());
                }
            }
            handler.sendEmptyMessage(1);
        } else {
            LogUtils.showLog("initViewPager:Music == null");
        }
        AudioPlayer.get().addOnPlayEventListener(this);
        QuitTimer.get().addOnTimerListener(this);
        sbProgress.setOnSeekBarChangeListener(this);
        sbVolume.setOnSeekBarChangeListener(this);
    }

    private CustomHandler handler;
    private TCBean5 music;

    private void onChangeImpl() {
        try {
            if (music == null) {
                return;
            }

            title_content_text.setText(music.getZname());
            sbProgress.setProgress((int) AudioPlayer.get().getAudioPosition());

            if (AudioPlayer.get().getAudioPosition() > 1) {
                onBufferingUpdate(100);
            } else {
                sbProgress.setSecondaryProgress(0);
            }

            sbProgress.setMax(music.getDuration());
            mLastProgress = 0;
            tvCurrentTime.setText(R.string.play_time_start);
            tvTotalTime.setText(formatTime(music.getDuration()));

            if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing()) {
                ivPlay.setSelected(true);
                mAlbumCoverView.start();
            } else {
                ivPlay.setSelected(false);
                mAlbumCoverView.pause();
            }
        } catch (Exception e) {
            LogUtils.showLog("onChangeImpl:" + e.toString());
        }
    }

    private void initVolume() {
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        sbVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    //获取上次播放的进度
    private void getMediaPlayer() {
        String level = SharedPreferencesUtil.getInstance().getString(TingConstants.LISTENER_SPEED);
        if (StringUtils.isEmpty(level)) {
            level = "0";
        }
        int levelInt = Integer.parseInt(level);
        if (levelInt == 1) {
            play_speed_value_tv.setText("x1.25");
        } else if (levelInt == 2) {
            play_speed_value_tv.setText("x1.5");
        } else if (levelInt == 3) {
            play_speed_value_tv.setText("x1.75");
        } else if (levelInt == 4) {
            play_speed_value_tv.setText("x2.0");
        } else if (levelInt == 5) {
            play_speed_value_tv.setText("x0.5");
        } else {
            play_speed_value_tv.setText("x1.0");
        }
    }

    private List<PopItemBean> items = new ArrayList<>();

    /**
     * 初始化睡眠信息
     */
    private void initSleepTimer() {
        sleep_text_tv.setText(getString(R.string.book_sleep));
        items.clear();
        /*填装睡眠数据*/
        for (int i = 0; i < 6; i++) {
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
    }

    //    睡眠弹出
    private void showPop() {
        DialogUtils.get().showPop(mContext, getString(R.string.book_sleep), items, this);
    }

    private void initPlayMode() {
        //   获取播放顺序
        int mode = SharedPreferencesUtil.getInstance().getInt(TingConstants.PLAY_MODE);
        //   ivMode.setImageLevel(mode);
    }

    private void setCoverAndBg(String book_cover) {

        if (!StringUtils.isEmpty(book_cover)) {
            Glide.with(mContext).load(book_cover).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                    ivPlayPageBg.setImageBitmap(resource);
                    resource = ImageUtils.createCircleImage2(resource);
//                    resource = ImageUtils.setImgSize(resource, DensityUtil.dp2px(148),DensityUtil.dp2px(148));
                    Bitmap finalResource = resource;
                    XApplication.getMainThreadHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAlbumCoverView.setCoverBitmap(finalResource);
                        }
                    }, 100);
                }
            });
        }

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


    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    //音频控制
    @Override
    public void onChange(TCBean5 music) {
        this.music = music;
        mParam1 = music.getPath();
        handler.sendEmptyMessage(1);
    }


    @Override
    public void onPlayerStart() {
        ivPlay.setSelected(true);
        mAlbumCoverView.start();
        try {
            music = AudioPlayer.get().getPlayMusic();
            mParam1 = music.getPath();
            if (music != null) {
                tvTotalTime.setText(formatTime(music.getDuration()));
                sbProgress.setMax(music.getDuration());//得到时间后 设置进度条时间
            }
        } catch (Exception e) {
            LogUtils.showLog("BookPlayFragment:onPlayerStart:Exception:" + e.getMessage());
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
        if (percent > 0) {
            sbProgress.setSecondaryProgress(sbProgress.getMax() * 100 / percent);
        }
        //LogUtils.showLog("BookPlayFragment:onBufferingUpdate:percent:"+percent);
//        LogUtils.showLog("BookPlayFragment:onBufferingUpdate");
    }


    @OnClick({R.id.title_left_text, R.id.iv_prev, R.id.iv_play, R.id.iv_next, R.id.sleep_view, R.id.play_speed_ll})
    public void onViewClicked(View view) {
        if (!StringUtils.isEmpty(mParam1)) {
            switch (view.getId()) {
                case R.id.title_left_text:
                    getActivity().finish();
                    break;
                case R.id.iv_prev:
                    prev();
                    break;
                case R.id.iv_play:
                    play();
                    break;
                case R.id.iv_next:
                    next();
                    break;
                case R.id.sleep_view:
                    LogUtils.showLog("睡眠");
                    showPop();
                    break;
                case R.id.play_speed_ll:
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        ToastUtil.showShortToast(getString(R.string.system_low));//23（android6.0）以下系统 官方不支持倍速播放
                        return;
                    }
                    String level = SharedPreferencesUtil.getInstance().getString(TingConstants.LISTENER_SPEED);
                    if (StringUtils.isEmpty(level)) {
                        level = "0";
                    }
                    int levelInt = Integer.parseInt(level);
                    if (levelInt >= 5) {
                        levelInt = 0;
                        changePalyerSpeed(0);
                    } else {
                        levelInt = levelInt + 1;
                        changePalyerSpeed(levelInt);
                    }
                    if (levelInt == 1) {
                        play_speed_value_tv.setText("x1.25");
                    } else if (levelInt == 2) {
                        play_speed_value_tv.setText("x1.5");
                    } else if (levelInt == 3) {
                        play_speed_value_tv.setText("x1.75");
                    } else if (levelInt == 4) {
                        play_speed_value_tv.setText("x2.0");
                    } else if (levelInt == 5) {
                        play_speed_value_tv.setText("x0.5");
                    } else {
                        play_speed_value_tv.setText("x1.0");
                    }
                    break;
            }
        } else {
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

    //    改变速度
    private void changePalyerSpeed(int level) {
        AudioPlayer.get().changeplayerSpeed(level);
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
                .setNativeAdLayout(ad_container_fl)
                .setPage(Constants.LISTENING_PAGE)
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
        QuitTimer.get().removeOnTimerListener(this);
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

    @Override
    public void onTimer(long remain) {
        LogUtils.showLog("onTimer:" + remain);
        sleep_text_tv.setText(remain == 0 ? getString(R.string.book_sleep) : SystemUtils.formatTime(getString(R.string.book_sleep) + "(mm:ss)", remain));
    }

    //睡眠模式
    @Override
    public void itemClick(PopItemBean item) {
        if (item != null) {
            QuitTimer.get().start(item.getMaxValue() * 1000);
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

    /**
     * 自定义Handler
     */
    public class CustomHandler extends Handler {
        private WeakReference<Activity> activityWeakReference;

        public CustomHandler(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            Activity activity = activityWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        onChangeImpl();
                        break;
                }
            }
        }
    }

}
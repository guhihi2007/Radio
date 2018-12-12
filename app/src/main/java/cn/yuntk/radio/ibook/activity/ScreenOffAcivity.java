//package cn.yuntk.radio.ibook.activity;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.graphics.drawable.AnimationDrawable;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.text.format.DateUtils;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//
//import java.util.Calendar;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import cn.yuntk.radio.R;
//import cn.yuntk.radio.ibook.XApplication;
//import cn.yuntk.radio.ibook.bean.TCBean5;
//import cn.yuntk.radio.ibook.service.AudioPlayer;
//import cn.yuntk.radio.ibook.service.OnPlayerEventListener;
//import cn.yuntk.radio.ibook.util.LogUtils;
//import cn.yuntk.radio.ibook.util.SystemUtils;
//import cn.yuntk.radio.ibook.util.statusbar.Eyes;
//import cn.yuntk.radio.ibook.widget.SlideRightViewDragHelper;
//
//public class ScreenOffAcivity extends AppCompatActivity implements OnPlayerEventListener,SeekBar.OnSeekBarChangeListener {
//
//    @BindView(R.id.screen_parent_slideview)
//    SlideRightViewDragHelper screen_parent_slideview;
//
//    @BindView(R.id.title_tv)
//    TextView titleTv;
//    @BindView(R.id.iv_prev)
//    ImageView ivPrev;
//    @BindView(R.id.iv_play)
//    ImageView ivPlay;
//    @BindView(R.id.iv_next)
//    ImageView ivNext;
//    @BindView(R.id.close_iv)
//    ImageView close_iv;
//    //当前播放的MUSIC
//    TCBean5 music;
//    @BindView(R.id.tv_current_time)
//    TextView tvCurrentTime;
//    @BindView(R.id.sb_progress)
//    SeekBar sbProgress;
//    @BindView(R.id.tv_total_time)
//    TextView tvTotalTime;
//
//    @BindView(R.id.time_show_tv)
//    TextView time_show_tv;
//    @BindView(R.id.date_show_tv)
//    TextView date_show_tv;
//    @BindView(R.id.week_show_tv)
//    TextView week_show_tv;
//    @BindView(R.id.arrow_iv)
//    ImageView arrow_iv;
//
//    private int mLastProgress;
//    private boolean isDraggingProgress;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
//        this.getWindow().addFlags(
//                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
//        setContentView(R.layout.ting_activity_screen_off_acivity);
//        Eyes.translucentStatusBar(this, false);//背景延伸到状态栏
//        ButterKnife.bind(this);
//        init();
//        LogUtils.showLog("ScreenOffAcivity:onCreate");
//        XApplication.isScreenOff = true;//已经进入锁屏
//    }
//
//    private void init() {
//        music = AudioPlayer.get().getPlayMusic();
//        AudioPlayer.get().addOnPlayEventListener(this);
//        sbProgress.setOnSeekBarChangeListener(this);
//        onChange(music);
//        updateTimeView();
//        screen_parent_slideview.setOnReleasedListener(new SlideRightViewDragHelper.OnReleasedListener() {
//            @Override
//            public void onReleased() {
//                LogUtils.showLog("onReleased");
//                finish();
//            }
//        });
//        setAnim();
//    }
//
//    @Override
//    public void onBackPressed() {
//        //锁屏界面当然不响应Back按键, 只需要重写Activity的onBackPressed方法即可
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//    }
//
//    @OnClick({R.id.iv_prev, R.id.iv_play, R.id.iv_next, R.id.close_iv})
//    public void onViewClicked(View view) {
//        switch (view.getId()) {
//            case R.id.iv_prev:
//                prev();
//                break;
//            case R.id.iv_play:
//                play();
//                break;
//            case R.id.iv_next:
//                next();
//                break;
//            case R.id.close_iv:
//                finish();
//                break;
//            default:
//        }
//    }
//
//    private void play() {
//        AudioPlayer.get().playPause();
//    }
//
//    private void next() {
//        AudioPlayer.get().next();
//    }
//
//    private void prev() {
//        AudioPlayer.get().prev();
//    }
//
//    @Override
//    public void onChange(TCBean5 music) {
//        this.music = music;
//        initMusic();
//    }
//
//    @Override
//    public void onPlayerStart() {
//        ivPlay.setSelected(true);
//        initMusic();
//    }
//
//    @Override
//    public void onPlayerPause() {
//        ivPlay.setSelected(false);
//    }
//
//    @Override
//    public void onPublish(int progress) {
//        if (!isDraggingProgress) {
//            sbProgress.setProgress(progress);
//        }
//    }
//
//    @Override
//    public void onBufferingUpdate(int percent) {
//        if (percent>0){
//            sbProgress.setSecondaryProgress(sbProgress.getMax() * 100 / percent);
//        }
//    }
//
//    //    初始化歌曲信息
//    private void initMusic() {
//        //LogUtils.showLog("music:time:" + music.getDuration());
//        try{
//            if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing()) {
//                ivPlay.setSelected(true);
//            } else {
//                ivPlay.setSelected(false);
//            }
//            titleTv.setText(music.getZname());
//            sbProgress.setProgress((int) AudioPlayer.get().getAudioPosition());
//            sbProgress.setSecondaryProgress(0);
//            sbProgress.setMax((int) music.getDuration());
//            mLastProgress = 0;
//            tvCurrentTime.setText(R.string.play_time_start);
//            tvTotalTime.setText(SystemUtils.formatTime("mm:ss", music.getDuration()));
//        }catch (Exception e){
//            LogUtils.showLog("initMusic:" + e.getMessage());
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        LogUtils.showLog("ScreenOffActivity:onDestroy");
//        XApplication.isScreenOff = false;//已经进入锁屏
//        AudioPlayer.get().removeOnPlayEventListener(this);
//        unregisterReceiver(receiver);
//    }
//
//    //SeekBar监听
//    //该方法拖动进度条进度改变的时候调用
//    @Override
//    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//        if (seekBar == sbProgress) {
//            if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
//                tvCurrentTime.setText(SystemUtils.formatTime("mm:ss",progress));
//                mLastProgress = progress;
//            }
//        }
//    }
//
//    //    开始拖动进度条时候条用
//    @Override
//    public void onStartTrackingTouch(SeekBar seekBar) {
//        if (seekBar == sbProgress) {
//            isDraggingProgress = true;
//        }
//    }
//
//    //    停止拖动进度条时候条用
//    @Override
//    public void onStopTrackingTouch(SeekBar seekBar) {
//        if (seekBar == sbProgress) {
//            isDraggingProgress = false;
//            if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPausing()) {
//                int progress = seekBar.getProgress();
//                AudioPlayer.get().seekTo(progress);
//            } else {
//                seekBar.setProgress(0);
//            }
//        }
//    }
//
//    private void updateTimeView(){
//        getDate();//更新时间
//        IntentFilter filter=new IntentFilter();
//        filter.addAction(Intent.ACTION_TIME_TICK);
//        registerReceiver(receiver,filter);
//    }
//
//
//    private final BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(Intent.ACTION_TIME_TICK)) {
//                //刷新UI
//                LogUtils.showLog("BroadcastReceiver:Intent.ACTION_TIME_TICK");
//                getDate();
//            }
//        }
//    };
//
//    //    获取日期
//    public void getDate(){
//        Calendar calendar = Calendar.getInstance();
//        //获取系统的日期
//        //年
//        int year = calendar.get(Calendar.YEAR);
//        //月
//        int month = calendar.get(Calendar.MONTH)+1;
//        //日
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        //小时
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        //分钟
//        int minute = calendar.get(Calendar.MINUTE);
//        //秒
//        int second = calendar.get(Calendar.SECOND);
//        LogUtils.showLog("Calendar获取当前日期"+year+"年"+month+"月"+day+"日"+hour+":"+minute+":"+second);
//
//        date_show_tv.setText(month+"月0"+day+"日");
//        if (minute<10){
//            time_show_tv.setText(hour+":0"+minute); //更新时间
//        }else {
//            time_show_tv.setText(hour+":"+minute); //更新时间
//        }
//        week_show_tv.setText(DateUtils.getWeek());
//    }
//
//    private AnimationDrawable mAnimation;
//
//    private void setAnim(){
//        mAnimation = new AnimationDrawable();
//        mAnimation.addFrame(getResources().getDrawable(R.mipmap.arrow_1), 200);
//        mAnimation.addFrame(getResources().getDrawable(R.mipmap.arrow_2), 200);
//        mAnimation.addFrame(getResources().getDrawable(R.mipmap.arrow_3), 400);
//        mAnimation.setOneShot(false);
//        arrow_iv.setImageDrawable(mAnimation);
//        mAnimation.start();
//    }
//
//}
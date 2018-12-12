package cn.yuntk.radio.ibook.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.activity.BookPlayActivity;
import cn.yuntk.radio.ibook.base.ActivityManager;
import cn.yuntk.radio.ibook.bean.TCBean5;
import cn.yuntk.radio.ibook.common.TingConstants;
import cn.yuntk.radio.ibook.floatwindow.FloatWindowPermissionChecker;
import cn.yuntk.radio.ibook.util.DisplayUtil;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.PackageUtils;
import cn.yuntk.radio.ibook.util.SystemUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;
import cn.yuntk.radio.manager.PlayServiceManager;
import cn.yuntk.radio.play.PlayManager;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
import static android.view.WindowManager.LayoutParams.TYPE_TOAST;

/**
 *悬浮窗口
 */
public class FloatViewService extends Service implements View.OnTouchListener,View.OnClickListener,OnPlayerEventListener{

    private String TAG = "FloatViewService";

    public static String isShow = "0";//0展示1未展示

    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    //定义浮动窗口布局
    FrameLayout mFloatLayout;
    LinearLayout parent_ll;
    TextView float_title_tv;
    TextView float_progress_tv;//当前进度
    TextView float_progress_total_tv;//总进度
    LinearLayout click_aren_ll;
    ImageView close_iv;
    ImageView iv_play_pause;//暂停播放
    ImageView iv_next;//下一曲
    WindowManager.LayoutParams wmParams;
    //创建浮动窗口设置布局参数的对象
    WindowManager mWindowManager;
    private boolean isAddToWindow = false;

    public FloatViewService() {
    }

    public class FloatViewBinder extends Binder {
        public FloatViewService getService() {
            return FloatViewService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
    //  throw new UnsupportedOperationException("Not yet implemented");
        return new FloatViewBinder();
    }

    public static void startCommand(Context context, String action) {
//        由于coloros的OPPO手机自动熄屏一段时间后，会启用系统自带的电量优化管理，禁止一切自启动的APP（用户设置的自启动白名单除外），需要try catch(腾讯Bugly)
        try{
            Intent intent = new Intent(context, FloatViewService.class);
            intent.setAction(action);
            context.startService(intent);
        }catch (Exception e){
//            LogUtils.showLog("Exception:"+e.toString());
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isAddToWindow){
            createFloatView();
        }
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Actions.SERVICE_GONE_WINDOW:
                    setWindowVisable(false);
                    break;
                case Actions.SERVICE_VISABLE_WINDOW:
                    setWindowVisable(true);
                    break;
                case Actions.SERVICE_STOP:
                    stopSelf();
                    default:
                        break;
            }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createFloatView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!isAddToWindow) return;
        if (mFloatLayout != null) {
            mWindowManager.removeView(mFloatLayout);
        }
        isAddToWindow = false;
    }

    private void createFloatView() {

        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams = new WindowManager.LayoutParams();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            wmParams.type = TYPE_APPLICATION_OVERLAY;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            wmParams.type = TYPE_TOAST;
        } else {
            wmParams.type = TYPE_SYSTEM_ALERT;
        }
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //wmParams.flags = FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL | FLAG_HARDWARE_ACCELERATED | FLAG_LAYOUT_NO_LIMITS;
        //设置图片格式，效果为背景透明
        //wmParams.format = PixelFormat.TRANSLUCENT;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.windowAnimations = android.R.style.Animation_Translucent;

        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.CENTER;

        // 以屏幕底部为原点，设置x、y初始值
        wmParams.x = 0;
        wmParams.y = TingConstants.height-(DisplayUtil.dip2px(this,330f));

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局
        mFloatLayout = (FrameLayout) inflater.inflate(R.layout.ting_float_view, null);
        parent_ll = mFloatLayout.findViewById(R.id.parent_ll);
        float_title_tv  = mFloatLayout.findViewById(R.id.float_title_tv);
        click_aren_ll = mFloatLayout.findViewById(R.id.click_aren_ll);
        float_progress_tv = mFloatLayout.findViewById(R.id.float_progress_tv);
        float_progress_total_tv = mFloatLayout.findViewById(R.id.float_progress_total_tv);
        close_iv = mFloatLayout.findViewById(R.id.close_iv);
        iv_play_pause = mFloatLayout.findViewById(R.id.iv_play_pause);
        iv_next = mFloatLayout.findViewById(R.id.iv_next);

        close_iv.setOnClickListener(this);
        iv_play_pause.setOnClickListener(this);
        iv_next.setOnClickListener(this);

        //添加mFloatLayout
        //mWindowManager.addView(mFloatLayout, wmParams);

        //浮动窗口按钮
        mFloatLayout.setOnTouchListener(this);
        click_aren_ll.setOnTouchListener(this);
        mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
                .makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        isShow = "0";
        this.music = AudioPlayer.get().getPlayMusic();
        if (this.music!=null){
            onPlayerStart();
        }

        if(AudioPlayer.get().isPlaying()){
            iv_play_pause.setImageResource(R.drawable.ting_ic_status_bar_pause_dark_selector);
        }else {
            iv_play_pause.setImageResource(R.drawable.ting_ic_status_bar_play_dark_selector);
        }
        iv_play_pause.setColorFilter(Color.parseColor("#ff000c"));

        AudioPlayer.get().addOnPlayEventListener(this);

        showFloatWindow();
    }

    public void showFloatWindow(){
        try{
            if (!FloatWindowPermissionChecker.checkFloatWindowPermission()) {
                if (ActivityManager.getInstance().getCurrentActivity()==null
                        ||ActivityManager.getInstance().getCurrentActivity().isFinishing())return;
                FloatWindowPermissionChecker.askForFloatWindowPermission(ActivityManager.getInstance().getCurrentActivity());
                return;
            }
        }catch (Exception e){
            ToastUtil.showToast("请开启悬浮窗权限");
            return;
        }
        if (isAddToWindow) return;
        try {
            mWindowManager.addView(mFloatLayout, wmParams);
        } catch (Exception e) {
            mWindowManager.updateViewLayout(mFloatLayout, wmParams);
        }
        isAddToWindow = true;
    }

    private float StartX;
    private float StartY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //获取相对屏幕的坐标，即以屏幕左上角为原点
        x = event.getRawX();
        y = event.getRawY() - 25;   //25是系统状态栏的高度
        Log.i("currP", "currX" + x + "====currY" + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取相对View的坐标，即以此View左上角为原点
                mTouchStartX = event.getX();
                mTouchStartY = event.getY();
                StartX = x;
                StartY = y;
                Log.i("startP", "startX" + mTouchStartX + "====startY" + mTouchStartY);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(x - StartX) > 5 && Math.abs(y - StartY) > 5) {
                    updateViewPosition();
                }
                break;
            case MotionEvent.ACTION_UP:
                 if (Math.abs(x - StartX) > 5 && Math.abs(y - StartY) > 5) {
                    updateViewPosition();
                    mTouchStartX = mTouchStartY = 0;
                }else {
                     onClick(v);
                 }
                break;
        }
        return true;
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void updateViewPosition() {
        try{
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
                //更新浮动窗口位置参数
                wmParams.x = (int) (x - mTouchStartX);
                wmParams.y = (int) (y - mTouchStartY);
                mWindowManager.updateViewLayout(mFloatLayout, wmParams);
            }else {
                if (mFloatLayout.isAttachedToWindow()){
                    //更新浮动窗口位置参数
                    wmParams.x = (int) (x - mTouchStartX);
                    wmParams.y = (int) (y - mTouchStartY);
                    mWindowManager.updateViewLayout(mFloatLayout, wmParams);
                }
            }
        }catch (Exception e){

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.close_iv:
                boolean isRunning = PackageUtils.isServiceRunning(this,Actions.SERVICE_NAME);
                if (isRunning){
                    stopSelf();
                    if (AudioPlayer.get().isPlaying()|| AudioPlayer.get().isPreparing()){
                        AudioPlayer.get().pausePlayer();
                    }
                }
                isShow = "1";
                break;
            case R.id.float_view:
            case R.id.click_aren_ll:
                LogUtils.showLog("全局点击 进入播放页面");
                if (AudioPlayer.get().getMusicList()!=null&&AudioPlayer.get().getMusicList().size()!=0){
                    Intent intent = new Intent(this, BookPlayActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("replay","replay_0");//"replay_0":不重新播放 "replay_1" 重新播放
                    startActivity(intent);
                }else {
                    Toast toast = Toast.makeText(this,"暂无播放内容", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP , 0, TingConstants.height-(DisplayUtil.dip2px(this,190f)));
                    toast.show();
                }
                isShow = "1";
                break;
            case R.id.iv_play_pause:
                play();
                break;
            case R.id.iv_next:
                next();
                break;
        }
    }

    //设置窗口是否显示
    public void setWindowVisable(boolean isShow){
        LogUtils.showLog("FloatViewService:isShow:"+isShow);
        if (isShow){
            mFloatLayout.setVisibility(View.VISIBLE);
            this.isShow = "0";
//            if (!isAddToWindow){
//                showFloatWindow();
//            }
        }else {
            mFloatLayout.setVisibility(View.GONE);
            this.isShow = "1";
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

    TCBean5 music;

    @Override
    public void onChange(TCBean5 music) {
        LogUtils.showLog("FloatViewService:onChange");
        this.music = music;
        float_title_tv.setText(getString(R.string.hadr_loading));
    }

    @Override
    public void onPlayerStart() {
        try{
            LogUtils.showLog("FloatViewService:onPlayerStart");
            float_title_tv.setText(music.getZname());
            float_progress_total_tv.setText("/"+ SystemUtils.formatTime("mm:ss",music.getDuration()));
            iv_play_pause.setImageResource(R.drawable.ting_ic_status_bar_pause_dark_selector);
            iv_play_pause.setColorFilter(Color.parseColor("#d81e06"));
        }catch (Exception e){

        }
    }

    @Override
    public void onPlayerPause() {
        LogUtils.showLog("FloatViewService:onPlayerPause");
        iv_play_pause.setImageResource(R.drawable.ting_ic_status_bar_play_dark_selector);
        iv_play_pause.setColorFilter(Color.parseColor("#d81e06"));
    }

    @Override
    public void onPublish(int progress) {
        float_progress_tv.setText(SystemUtils.formatTime("mm:ss", progress));
//        LogUtils.showLog("FloatViewService:onPublish:progress:"+progress);
//        LogUtils.showLog("FloatViewService:onPublish");
    }

    @Override
    public void onBufferingUpdate(int percent) {
//        LogUtils.showLog("FloatViewService:onBufferingUpdate:percent:"+percent);
//        LogUtils.showLog("FloatViewService:onBufferingUpdate");
    }

}
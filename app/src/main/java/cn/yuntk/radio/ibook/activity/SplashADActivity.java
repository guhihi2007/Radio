//package cn.yuntk.radio.ibook.activity;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.yuntk.ibook.R;
//import com.yuntk.ibook.ads.ADConstants;
//import com.yuntk.ibook.ads.AdController;
//import com.yuntk.ibook.util.LogUtils;
//
//
///**
// * Created by yuan on 2017/3/14.
// * * 用于跳转广告的临时页面
// */
//
//public class SplashADActivity extends Activity {
//
//    private FrameLayout container;
//    private TextView skipView;
//    private boolean isFromGameCenterActivity;
//    private ImageView rootIv;
//    private AdController builder;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.ting_activity_splash_ad);
//        container = (FrameLayout) this.findViewById(R.id.splash_container);
//        skipView = (TextView) findViewById(R.id.skip_view);
//        rootIv = (ImageView) findViewById(R.id.app_logo);
//        builder = new AdController
//                .Builder(this)
//                .setContainer(container)
//                .setSkipView(skipView)
//                .setLogo(rootIv)
//                .setPage(ADConstants.START_PAGE)
//                .setLoading(true)
//                .create();
//        builder.show();
//        isFromGameCenterActivity = getIntent().getBooleanExtra("fromGameCenterActivity", false);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //继承了Activity的onTouchEvent方法，直接监听点击事件
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            //当手指按下的时候
//            LogUtils.showLog("SplashADActivity:onTouchEvent");
//            finish();
//        }
//        return super.onTouchEvent(event);
//    }
//
//    /**
//     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
//     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (builder!=null) {
//            builder.destroy();
//        }
//    }
//}

//package cn.yuntk.radio.ibook.activity;
//
//import android.annotation.TargetApi;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Rect;
//import android.net.Uri;
//import android.os.Build;
//import android.provider.Settings;
//import android.support.v4.app.FragmentTransaction;
//
//import cn.yuntk.radio.ibook.MainActivity;
//import cn.yuntk.radio.R;
//import cn.yuntk.radio.ibook.ads.AdsManager;
//import cn.yuntk.radio.ibook.base.BaseTitleActivity;
//import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
//import cn.yuntk.radio.ibook.component.AppComponent;
//import cn.yuntk.radio.ibook.fragment.LoadingFragment;
//import cn.yuntk.radio.ibook.util.LogUtils;
//import cn.yuntk.radio.ibook.util.statusbar.Eyes;
//
//
//public class SplashActivity extends BaseTitleActivity {
//
//
//    private static final int REQUEST_CODE_WRITE_SETTINGS = 500;
//
//    LoadingFragment loadingFragment;
//
//    @Override
//    protected void initViews() {
//        showContent();
//    }
//
//    @Override
//    protected void bindEvent() {
//
//    }
//
//    @Override
//    protected void loadData() {
//
//    }
//
//    @Override
//    protected int getLayoutId() {
//        Eyes.translucentStatusBar(this, false);//背景延伸到状态栏
//        return R.layout.activity_listener_splash;
//    }
//
//    @Override
//    protected BasePresenter getPresenter() {
//        return null;
//    }
//
//    @Override
//    protected void setupActivityComponent(AppComponent appComponent) {
//
//    }
//
//    private void showContent() {
//        showLoadingFragment();
//    }
//
//    private void showLoadingFragment() {
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        if (loadingFragment == null) {
//            loadingFragment = new LoadingFragment();
//            transaction.add(R.id.fragment_content, loadingFragment);
//        }
//        hideFragment(transaction);
//        transaction.show(loadingFragment);
//        transaction.commitAllowingStateLoss();
//    }
//
//    private void hideFragment(FragmentTransaction transaction) {
//        if (loadingFragment != null) {
//            transaction.hide(loadingFragment);
//        }
//    }
//
//    public void goHome(){
//        LogUtils.showLog(SplashActivity.class.getSimpleName()+":goHome");
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//            if (hasSettingPermission()) {
//                checkin();
//            } else {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
//                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
//            }
//        } else {
//            checkin();
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.M)
//    private boolean hasSettingPermission() {
//        return Settings.System.canWrite(SplashActivity.this);
//    }
//
//    private void checkin() {
//        startActivity(new Intent(SplashActivity.this, cn.yuntk.radio.ui.activity.MainActivity.class));
//        finish();
//    }
//
//}
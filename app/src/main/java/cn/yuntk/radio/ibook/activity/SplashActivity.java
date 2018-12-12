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
//import com.yuntk.ibook.R;
//import com.yuntk.ibook.TingMainActivity;
//import com.yuntk.ibook.ads.AdsManager;
//import com.yuntk.ibook.base.BaseTitleActivity;
//import com.yuntk.ibook.base.presenter.BasePresenter;
//import com.yuntk.ibook.component.AppComponent;
//import com.yuntk.ibook.fragment.LoadingFragment;
//import com.yuntk.ibook.util.LogUtils;
//import com.yuntk.ibook.util.PackageUtils;
//import com.yuntk.ibook.util.statusbar.Eyes;
//
//import java.io.ByteArrayOutputStream;
//import java.util.HashMap;
//import java.util.Map;
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
//
//        String channel =  PackageUtils.getAppMetaData(this,"UMENG_CHANNEL");
//        String version = PackageUtils.getVersionName(this);
//        Map<String,String> prams = new HashMap<String,String>();
//        prams.put("name","lanrentingxiaoshuo");
//        prams.put("channel",channel);
//        prams.put("version",version);
//
//        AdsManager adsManager = new AdsManager();
////        adsManager.requestListenerUrl();
//        adsManager.getState(prams);
////        adsManager.getUAConfig();
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
//        return R.layout.ting_activity_splash;
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
////    读取图片
//    private Bitmap getBitmap(){
//        Bitmap bmp = BitmapFactory.decodeResource(getResources(),R.mipmap.splash_pic);
//        LogUtils.showLog("getBitmap:"+bmp.getByteCount());
//        // 尺寸压缩倍数,值越大，图片尺寸越小
//        int ratio = 2;
//        // 压缩Bitmap到对应尺寸
//        Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(result);
//        Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
//        canvas.drawBitmap(bmp, null, rect, null);
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        // 把压缩后的数据存放到baos中
//        result.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        LogUtils.showLog("getBitmap:"+result.getByteCount());
//        return result;
//    }
//
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
//        startActivity(new Intent(SplashActivity.this, TingMainActivity.class));
//        finish();
//    }
//
////    //读写权限
////    private static String[] PERMISSIONS_STORAGE = {
////            Manifest.permission.READ_EXTERNAL_STORAGE,
////            Manifest.permission.WRITE_EXTERNAL_STORAGE};
////    //请求状态码
////    private static int REQUEST_PERMISSION_CODE = 1;
////
////    private void requestPermission(){
////        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
////            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
////                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
////            }
////        }
////    }
////    @Override
////    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////        if (requestCode == REQUEST_PERMISSION_CODE) {
////            for (int i = 0; i < permissions.length; i++) {
////                LogUtils.i("TingMainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
////            }
////        }
////    }
//
//
//}
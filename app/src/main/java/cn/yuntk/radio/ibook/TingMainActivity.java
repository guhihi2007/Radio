package cn.yuntk.radio.ibook;

import android.app.Dialog;
import android.content.ComponentName;
import android.media.AudioManager;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.liulishuo.filedownloader.FileDownloadMonitor;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import cn.yuntk.radio.R;
import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.base.BaseTitleActivity;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.dbdao.DownloadBookInfoManager;
import cn.yuntk.radio.ibook.dbdao.DownloadMusicInfoManager;
import cn.yuntk.radio.ibook.dbdao.ListenerBookInfoManager;
import cn.yuntk.radio.ibook.dbdao.ListenerMusicInfoManager;
import cn.yuntk.radio.ibook.dbdao.MP3DaoManager;
import cn.yuntk.radio.ibook.download_.GlobalMonitor;
import cn.yuntk.radio.ibook.fragment.Index1Fragment;
import cn.yuntk.radio.ibook.fragment.Index2Fragment;
import cn.yuntk.radio.ibook.fragment.Index4Fragment;
import cn.yuntk.radio.ibook.receiver.MediaButtonReceiver;
import cn.yuntk.radio.ibook.service.Actions;
import cn.yuntk.radio.ibook.service.AppCache;
import cn.yuntk.radio.ibook.service.FloatViewService;
import cn.yuntk.radio.ibook.service.LockService;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.PackageUtils;

public class TingMainActivity extends BaseTitleActivity {

    @BindView(R.id.container_fl)
    FrameLayout containerFl;

    FragmentTabHost mTabHost;

    private String texts[] = new String[3];//4];

    private int imageButton[] = {
         R.drawable.ting_select_index_icon1,
         R.drawable.ting_select_index_icon2,
         R.drawable.ting_select_index_icon3};//, R.drawable.ting_select_index_icon4};

     private Class fragmentArray[] = {
             Index1Fragment.class,
             Index2Fragment.class,
             Index4Fragment.class};//,Index5Fragment.class};

    @Override
    protected void initViews() {

        texts[0] = getString(R.string.index1);
        texts[1] = getString(R.string.classif);
        texts[2] = getString(R.string.collect);
        //texts[3] = getString(R.string.setting);

        mTabHost = findViewById(android.R.id.tabhost);
        /**
         * 在setup()里边，其才去获取到TabWidget，所以在此之前，不能直接调用getTabWidget()方法；
         */
        mTabHost.setup(this, getSupportFragmentManager(), R.id.container_fl);
        for (int i = 0; i < texts.length; i++) {
            TabHost.TabSpec spec = mTabHost.newTabSpec(texts[i]).setIndicator(getView(i));
            mTabHost.addTab(spec, fragmentArray[i], null);
            //设置背景(必须在addTab之后，由于需要子节点（底部菜单按钮）否则会出现空指针异常)
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.ting_bt_selector);
            }
    }

    @Override
    protected void bindEvent() {
        if (android.os.Build.VERSION.SDK_INT < 21){
            LogUtils.showLog("android.os.Build.VERSION.SDK_INT < 21");
            ((AudioManager)getSystemService(AUDIO_SERVICE)).registerMediaButtonEventReceiver(new ComponentName(this,MediaButtonReceiver.class));
        }

        LogUtils.showLog("DaoMaster.SchemaVersion:"+ XApplication.getDaoMaster(this).getSchemaVersion());
        FileDownloadMonitor.setGlobalMonitor(GlobalMonitor.getImpl());
    }

    @Override
    protected void loadData() {
        String channel =  PackageUtils.getAppMetaData(this,"UMENG_CHANNEL");
        LogUtils.showLog("CHANNEL:"+channel);
//        RateDialog.initConfig(this);
//        UpdateUtils.getInstance(mContext).checkUpdate(false,false);

        //删除以前数据库的东西
        DownloadBookInfoManager.getInstance(this).deleteAll();
        DownloadMusicInfoManager.getInstance(this).deleteAll();
        ListenerBookInfoManager.getInstance(this).delAll();
        ListenerMusicInfoManager.getInstance(this).delAll();
        MP3DaoManager.getInstance(this).deleteAll();
    }

    @Override
    protected int getLayoutId() { return R.layout.ting_activity_main;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    private View getView(int i) {
           //取得布局实例
           View view= View.inflate(TingMainActivity.this, R.layout.ting_tabcontent, null);

           //取得布局对象
           ImageView imageView=(ImageView) view.findViewById(R.id.image);
           TextView textView=(TextView) view.findViewById(R.id.text);

           //设置图标
           imageView.setImageResource(imageButton[i]);
           //设置标题
           textView.setText(texts[i]);
           return view;
       }

    //    用户退出弹窗
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//

            return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LockService.status = "default";
        FloatViewService.startCommand(this, Actions.SERVICE_VISABLE_WINDOW);

    }

    private Dialog dialog;

    private void Exit() {
        dialog = new Dialog(this, R.style.my_dialog);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.ting_item_tip_dialog, null);
        //初始化控件
        TextView tv_cancel = inflate.findViewById(R.id.tv_cancel);
        TextView tv_sure = inflate.findViewById(R.id.tv_sure);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.sure_exit_app) + "？");
        tv_cancel.setOnClickListener(v -> dialog.dismiss());
        tv_sure.setOnClickListener(v -> {
            LogUtils.showLog("TingMainActivity:Exit()");
            dialog.dismiss();
            AppCache.get().clearStack();
//            MobclickAgent.onKillProcess(TingMainActivity.this);//杀死友盟统计
//            LockService.status = "stop";//改写soup服务状态
//            TingPlayService.startCommand(TingMainActivity.this, Actions.ACTION_STOP);
//            if (android.os.Build.VERSION.SDK_INT < 21){
//                ((AudioManager)getSystemService(AUDIO_SERVICE)).unregisterMediaButtonEventReceiver(new ComponentName(this,MediaButtonReceiver.class));
//            }
        });

        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        //获得窗体的属性
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.y = 20;//设置Dialog距离底部的距离
//       将属性设置给窗体
//        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabHost = null;

        LogUtils.showLog("TingMainActivity:onDestroy()");
        MobclickAgent.onKillProcess(TingMainActivity.this);//杀死友盟统计
        //下面的放到Radio的MainActivity
//        LockService.status = "stop";//改写soup服务状态
//        TingPlayService.startCommand(TingMainActivity.this, Actions.ACTION_STOP);
//        if (android.os.Build.VERSION.SDK_INT < 21){
//            ((AudioManager)getSystemService(AUDIO_SERVICE)).unregisterMediaButtonEventReceiver(new ComponentName(this,MediaButtonReceiver.class));
//        }
//
//        FileDownloader.getImpl().unBindServiceIfIdle();
//        FileDownloadMonitor.releaseGlobalMonitor();
    }


}
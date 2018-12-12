package cn.yuntk.radio.ibook.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.PopItemBean;
import cn.yuntk.radio.ibook.common.TingConstants;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.service.Actions;
import cn.yuntk.radio.ibook.service.FloatViewService;
import cn.yuntk.radio.ibook.service.QuitTimer;
import cn.yuntk.radio.ibook.util.DialogUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.PackageUtils;
import cn.yuntk.radio.ibook.util.SystemUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;
import cn.yuntk.radio.ibook.util.UpdateUtils;

public class Index5Fragment extends BaseFragment implements View.OnClickListener,DialogUtils.PopClickInterface,
        QuitTimer.OnTimerListener
{

    TextView search_tv;//左边
    TextView title_tv;//标题
    TextView game_tv;//右边
    TextView app_version_name_tv;//版本名字
    LinearLayout setting_ll1;//检测更新
    LinearLayout setting_ll2;//看书专区
    LinearLayout setting_ll3;//热门听书
    LinearLayout setting_ll4;//意见反馈
    LinearLayout setting_ll5;//睡眠模式
    TextView sleep_text_tv;//睡眠显示

    @Override
    protected int getLayoutId() {
        return R.layout.ting_fragment_index5;
    }

    @Override
    protected void initViews() {
        search_tv = mContentView.findViewById(R.id.search_tv);
        title_tv = mContentView.findViewById(R.id.title_tv);
        game_tv = mContentView.findViewById(R.id.game_tv);
        app_version_name_tv = mContentView.findViewById(R.id.app_version_name_tv);
        setting_ll1 = mContentView.findViewById(R.id.setting_ll1);
        setting_ll2 = mContentView.findViewById(R.id.setting_ll2);
        setting_ll3 = mContentView.findViewById(R.id.setting_ll3);
        setting_ll4 = mContentView.findViewById(R.id.setting_ll4);
        setting_ll5 = mContentView.findViewById(R.id.setting_ll5);
        sleep_text_tv = mContentView.findViewById(R.id.sleep_text_tv);

        search_tv.setVisibility(View.GONE);
        game_tv.setVisibility(View.GONE);
        setting_ll3.setVisibility(View.GONE);
        title_tv.setText(R.string.setting);

        app_version_name_tv.setText("V"+ PackageUtils.getVersionName(mContext));

        setting_ll1.setOnClickListener(this);
        setting_ll2.setOnClickListener(this);
        setting_ll3.setOnClickListener(this);
        setting_ll4.setOnClickListener(this);
        setting_ll5.setOnClickListener(this);
    }

    @Override
    protected void bindEvent() {
        QuitTimer.get().addOnTimerListener(this);
        initSleepTimer();
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

    Intent intent;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_ll1:
                UpdateUtils.getInstance(mContext).checkUpdate(true,false);
                break;
            case R.id.setting_ll2:
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("AppName", PackageUtils.getAppName(mContext));
                map.put("AppChannel",  PackageUtils.getAppMetaData(mContext,"UMENG_CHANNEL"));
                map.put("AppVersion", PackageUtils.getVersionName(mContext)+"");
                map.put("ToApp","quanbenxiaoshuoyuedu");
                map.put("ToAppUrl", TingConstants.QBXS_Simple_download);
                MobclickAgent.onEvent(mContext, "ToAppClick", map);
                intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(TingConstants.QBXS_Simple_download);
                intent.setData(content_url);
                startActivity(intent);
                break;
            case R.id.setting_ll3:
//                HashMap<String,String> map2 = new HashMap<String,String>();
//                map2.put("AppName", PackageUtils.getAppName(mContext));
//                map2.put("AppChannel",  PackageUtils.getAppMetaData(mContext,"UMENG_CHANNEL"));
//                map2.put("AppVersion", PackageUtils.getVersionName(mContext)+"");
//                map2.put("ToApp","rementingshu");
//                map2.put("ToAppUrl",TingConstants.RMTS_Simple_download);
//                MobclickAgent.onEvent(mContext, "ToAppClick", map2);
//                intent = new Intent();
//                intent.setAction("android.intent.action.VIEW");
//                Uri content_url1 = Uri.parse(TingConstants.RMTS_Simple_download);
//                intent.setData(content_url1);
//                startActivity(intent);
                break;
            case R.id.setting_ll4:
                JSONObject extInfo = new JSONObject();
                try {
                    extInfo.put("FeedbackTime", System.currentTimeMillis()+"");
                    extInfo.put("AppName", PackageUtils.getAppName(mContext));
                    extInfo.put("AppChannel",  PackageUtils.getAppMetaData(mContext,"UMENG_CHANNEL"));
                    extInfo.put("AppVersion", PackageUtils.getVersionName(mContext)+"");
                    extInfo.put("DataType", "ting_china_ibook");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FeedbackAPI.setAppExtInfo(extInfo);
                FeedbackAPI.openFeedbackActivity();
                FloatViewService.startCommand(mContext, Actions.SERVICE_GONE_WINDOW);
                break;
            case R.id.setting_ll5:
                showPop();
//                if (AudioPlayer.get().isPlaying()){
//                }else {
//                    ToastUtil.showToast("当前未在播放，无需使用睡眠模式");
//                }
                break;
        }
    }

    private List<PopItemBean> items = new ArrayList<>();
    /**
     * 初始化睡眠信息
     * */
    private void initSleepTimer(){
        sleep_text_tv.setText(getString(R.string.book_sleep));
        items.clear();
        /*填装睡眠数据*/
        for (int i=0;i<6;i++){
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
    private void showPop(){
        DialogUtils.get().showPop(mContext,getString(R.string.book_sleep),items,this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Index5Fragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Index5Fragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        QuitTimer.get().removeOnTimerListener(this);
    }

    //睡眠模式回调
    @Override
    public void itemClick(PopItemBean item) {
        if (item!=null){
            QuitTimer.get().start(item.getMaxValue()* 1000);
            if (item.getMaxValue() > 0) {
                ToastUtil.showToast(getString(R.string.timer_set, item.getShowString()));
            } else {
                ToastUtil.showToast(R.string.timer_cancel);
            }
        }
    }

    @Override
    public void backClick() {

    }

    @Override
    public void onTimer(long remain) {
        LogUtils.showLog("onTimer:"+remain);
        sleep_text_tv.setText(remain == 0 ? getString(R.string.book_sleep) : SystemUtils.formatTime(getString(R.string.book_sleep) + "(mm:ss)", remain));
    }
}
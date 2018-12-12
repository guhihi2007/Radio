package cn.yuntk.radio.ibook.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.BaseTitleActivity;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.service.Actions;
import cn.yuntk.radio.ibook.service.FloatViewService;
import cn.yuntk.radio.ibook.util.PackageUtils;

public class SettingActivity extends BaseTitleActivity {

    @BindView(R.id.setting_ll1)
    LinearLayout settingLl1;
    @BindView(R.id.setting_ll2)
    LinearLayout settingLl2;
    @BindView(R.id.setting_ll3)
    LinearLayout settingLl3;
    @BindView(R.id.setting_ll4)
    LinearLayout setting_ll4;

    @BindView(R.id.title_left_text)
    TextView title_left_text;
    @BindView(R.id.title_content_text)
    TextView title_content_text;
    @BindView(R.id.title_right_text)
    TextView title_right_text;

    @Override
    protected void initViews() {
        settingLl1.setVisibility(View.VISIBLE);
        settingLl2.setVisibility(View.VISIBLE);
        settingLl3.setVisibility(View.VISIBLE);
        setting_ll4.setVisibility(View.VISIBLE);

        title_content_text.setText("设置");
        title_right_text.setVisibility(View.GONE);
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.ting_activity_setting;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @OnClick({R.id.title_left_text,R.id.setting_ll1, R.id.setting_ll2, R.id.setting_ll3,R.id.setting_ll4})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting_ll1:

                break;
            case R.id.setting_ll2:

                break;
            case R.id.setting_ll3:
                JSONObject extInfo = new JSONObject();
                try {
                    extInfo.put("FeedbackTime", System.currentTimeMillis()+"");
                    extInfo.put("AppName", PackageUtils.getAppName(this));
                    extInfo.put("AppChannel",  PackageUtils.getAppMetaData(this,"UMENG_CHANNEL"));
                    extInfo.put("AppVersion", PackageUtils.getVersionName(this)+"");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FeedbackAPI.setAppExtInfo(extInfo);
                FeedbackAPI.openFeedbackActivity();
                FloatViewService.startCommand(this, Actions.SERVICE_GONE_WINDOW);
                break;
            case R.id.title_left_text:
                finish();
                break;
            case R.id.setting_ll4:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatViewService.startCommand(this, Actions.SERVICE_VISABLE_WINDOW);
    }

}
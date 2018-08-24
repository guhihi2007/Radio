package cn.yuntk.radio.ibook.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.BaseTitleActivity;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.service.Actions;
import cn.yuntk.radio.ibook.service.FloatViewService;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingActivity extends BaseTitleActivity {

    @BindView(R.id.setting_ll1)
    LinearLayout settingLl1;
    @BindView(R.id.setting_ll2)
    LinearLayout settingLl2;
    @BindView(R.id.setting_ll3)
    LinearLayout settingLl3;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.back_tv)
    TextView backTv;
    @BindView(R.id.back_ll)
    LinearLayout backLl;
    @BindView(R.id.next_tv)
    TextView nextTv;
    @BindView(R.id.next_iv)
    ImageView nextIv;
    @BindView(R.id.next_ll)
    LinearLayout nextLl;
    @BindView(R.id.listener_title_tv)
    TextView titleTv;

    @Override
    protected void initViews() {
        settingLl1.setVisibility(View.GONE);
        settingLl2.setVisibility(View.GONE);
        settingLl3.setVisibility(View.VISIBLE);

        titleTv.setText("设置");
        nextLl.setVisibility(View.GONE);
    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_listener_setting;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @OnClick({R.id.back_ll,R.id.setting_ll1, R.id.setting_ll2, R.id.setting_ll3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.setting_ll1:

                break;
            case R.id.setting_ll2:

                break;
            case R.id.setting_ll3:
                FeedbackAPI.openFeedbackActivity();
                FloatViewService.startCommand(this, Actions.SERVICE_GONE_WINDOW);
                break;
            case R.id.back_ll:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatViewService.startCommand(this, Actions.SERVICE_VISABLE_WINDOW);
    }
}

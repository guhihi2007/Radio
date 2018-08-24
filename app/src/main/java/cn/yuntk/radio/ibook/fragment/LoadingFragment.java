package cn.yuntk.radio.ibook.fragment;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.XApplication;
import cn.yuntk.radio.ibook.activity.SplashActivity;
import cn.yuntk.radio.ibook.ads.ADConstants;
import cn.yuntk.radio.ibook.ads.AdController;
import cn.yuntk.radio.ibook.ads.LoadEvent;
import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.util.LogUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.yuntk.radio.ibook.util.NetworkUtils;

/**
 * @author
 * @time
 * @des ${TODO}
 */

public class LoadingFragment extends BaseFragment {

    @BindView(R.id.skip_view)
    TextView skip_view;
    @BindView(R.id.splash_container)
    FrameLayout container;
    @BindView(R.id.app_logo)
    ImageView rootIv;
    private AdController builder;

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (builder != null) {
            builder.destroy();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.listener_loading_splash;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void bindEvent() {

    }

    @Override
    protected void loadData() {
        builder = new AdController
                .Builder(getActivity())
                .setPage(ADConstants.START_PAGE)
                .setContainer(container)
                .setLogo(rootIv)
                .setSkipView(skip_view)
                .create();
        builder.show();
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @OnClick(R.id.skip_view)
    public void jumpToHome() {
        LogUtils.d("leading", "点击跳过");
        if (null != getActivity()) {
            ((SplashActivity) getActivity()).goHome();
        } else {
            EventBus.getDefault().post(new LoadEvent(true));
        }
    }
}
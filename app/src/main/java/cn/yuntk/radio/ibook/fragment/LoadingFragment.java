//package cn.yuntk.radio.ibook.fragment;
//
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//
//import butterknife.BindView;
//import butterknife.OnClick;
//
///**
// * @author
// * @time
// * @des ${TODO}
// */
//
//public class LoadingFragment extends BaseFragment {
//
//    @BindView(R.id.skip_view)
//    TextView skip_view;
//    @BindView(R.id.splash_container)
//    FrameLayout container;
//    @BindView(R.id.app_logo)
//    ImageView rootIv;
//    private AdController builder;
//
//    @Override
//    protected void setupActivityComponent(AppComponent appComponent) {
//    }
//
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if (builder != null) {
//            builder.destroy();
//        }
//    }
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.ting_loading_splash;
//    }
//
//    @Override
//    protected void initViews() {
//
//    }
//
//    @Override
//    protected void bindEvent() {
//
//    }
//
//    @Override
//    protected void loadData() {
//        builder = new AdController
//                .Builder(getActivity())
//                .setPage(ADConstants.START_PAGE)
//                .setContainer(container)
//                .setSkipView(skip_view)
//                .create();
//        builder.show();
//    }
//
//    @Override
//    protected BasePresenter getPresenter() {
//        return null;
//    }
//
//    @OnClick(R.id.skip_view)
//    public void jumpToHome() {
//        LogUtils.d("leading", "点击跳过");
//        if (null != getActivity()) {
//            ((SplashActivity) getActivity()).goHome();
//        } else {
//
//        }
//    }
//}
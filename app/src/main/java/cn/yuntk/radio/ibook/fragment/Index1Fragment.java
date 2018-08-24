package cn.yuntk.radio.ibook.fragment;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.activity.BookSearchActivtity;
import cn.yuntk.radio.ibook.adapter.MileageAdapter;
import cn.yuntk.radio.ibook.ads.ADConstants;
import cn.yuntk.radio.ibook.ads.AdController;
import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.common.Api;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class Index1Fragment extends BaseFragment {

    @BindView(R.id.search_tv)
    TextView search_tv;//搜索
    @BindView(R.id.listener_title_tv)
    TextView title_tv;//
    @BindView(R.id.game_tv)
    TextView game_tv;
    @BindView(R.id.comm_back_ll)
    LinearLayout comm_back_ll;
    @BindView(R.id.integral_tabs)
    Indicator integral_tabs;
    @BindView(R.id.dispatch_vp)
    ViewPager dispatch_vp;

    private IndicatorViewPager indicatorViewPager;

    List<BookListFragment> fragments = new ArrayList<BookListFragment>();
    String[] TITLES = new String[4];
    MileageAdapter<BookListFragment> adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_listener_index1;
    }

    @Override
    protected void initViews() {

        title_tv.setText(getString(R.string.index1));//默认标题
        search_tv.setVisibility(View.GONE);
        comm_back_ll.setVisibility(View.VISIBLE);
        game_tv.setText("搜索");

        TITLES[0] = getString(R.string.recent_updates);
        TITLES[1] = getString(R.string.hot_storytelling);
        TITLES[2] = getString(R.string.hot_book);
        TITLES[3] = getString(R.string.the_latest_serial);

        fragments.add(BookListFragment.newInstance(Api.BOOK_LIST1, TITLES[0]));
        fragments.add(BookListFragment.newInstance(Api.BOOK_LIST2, TITLES[1]));
        fragments.add(BookListFragment.newInstance(Api.BOOK_LIST3, TITLES[2]));
        fragments.add(BookListFragment.newInstance(Api.BOOK_LIST4, TITLES[3]));

        dispatch_vp.setOffscreenPageLimit(TITLES.length);
//      // 设置它可以自定义实现在滑动过程中，tab项的字体变化，颜色变化等等过渡效果
//      indicatorViewPager.setIndicatorOnTransitionListener(onTransitionListener);
//      // 设置它可以自定义滑动块的样式
//      indicatorViewPager.setIndicatorScrollBar(scrollBar);
//      TextWidthColorBar 大小同tab里的text一样宽的颜色的滑动块
//      ColorBar 颜色的滑动块
//      DrawableBar 图片滑动块
//      LayoutBar 布局滑动块
//      SpringBar 实现拖拽效果的圆形滑动块 该类修改于https://github.com/chenupt/SpringIndicator
        integral_tabs.setScrollBar(new ColorBar(getActivity(), ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark), 5));// 下划线的颜色和高度
//      integral_tabs.setScrollBar(new TextWidthColorBar(this,indicator,R.color.color_red_bg,30));// 下划线的颜色和高度
        float unSelectSize = 14;// 未选中的字体大小
        float selectSize = 14;// 选中的字体大小
        int selectColor = ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark);// 选中的字体颜色
        int unSelectColor = ContextCompat.getColor(getActivity(), R.color.color_20);// 没有选中的字体颜色
        integral_tabs.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));
        indicatorViewPager = new IndicatorViewPager(integral_tabs, dispatch_vp);
        indicatorViewPager.setPageOffscreenLimit(TITLES.length);
        adapter = new MileageAdapter<BookListFragment>(getChildFragmentManager(), fragments, TITLES);
        indicatorViewPager.setAdapter(adapter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.showLog("setUserVisibleHint" + isVisibleToUser + ":     TITLES.length:" + TITLES.length);
    }

    @Override
    protected void bindEvent() {
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                LogUtils.showLog("preItem:" + preItem + ":currentItem:" + currentItem);
                title_tv.setText(TITLES[currentItem]);
            }
        });
    }

    @Override
    protected void loadData() {
        //初始化change Banner定时器 状态
        SharedPreferencesUtil.getInstance().putBoolean(ADConstants.HOME_PAGE_LIST1 + ADConstants.AD_BANNER_IS_TIMER, false);
        SharedPreferencesUtil.getInstance().putBoolean(ADConstants.HOME_PAGE_LIST2 + ADConstants.AD_BANNER_IS_TIMER, false);
        SharedPreferencesUtil.getInstance().putBoolean(ADConstants.HOME_PAGE_LIST3 + ADConstants.AD_BANNER_IS_TIMER, false);
        SharedPreferencesUtil.getInstance().putBoolean(ADConstants.HOME_PAGE_LIST4 + ADConstants.AD_BANNER_IS_TIMER, false);
        SharedPreferencesUtil.getInstance().putBoolean(ADConstants.CATEGORY_PAGE + ADConstants.AD_BANNER_IS_TIMER, false);
        LogUtils.showLog("Index1Fragment loadData()");
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
//        DaggerBookComponent.builder()
//                .appComponent(appComponent)
//                .build()
//                .inject(this);
    }

    AdController builder;

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.showLog("Index1Fragment onResume()");
        builder = new AdController
                .Builder(getActivity())
                .setPage(ADConstants.HOME_PAGE_NEW)
                .setTag_ad(ADConstants.HOME_PAGE_NEW)
                .create();
        builder.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.showLog("Index1Fragment:onDestroy()");
        if (builder != null) {
            builder.destroy();
        }
    }


    @OnClick({R.id.search_tv, R.id.game_tv, R.id.comm_back_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.search_tv:
                break;
            case R.id.game_tv:
                //搜索
                LogUtils.showLog("搜索");
                BookSearchActivtity.jumpToSearch(mContext);
                break;
            case R.id.comm_back_ll:
                if (getActivity() != null)
                    getActivity().finish();
                break;
        }
    }
}

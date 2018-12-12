package cn.yuntk.radio.ibook.fragment;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.adapter.MileageAdapter;
import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.util.LogUtils;

public class Index4Fragment extends BaseFragment {

    Indicator integral_tabs;
    ViewPager container_fl;

   IndicatorViewPager indicatorViewPager;

    // 标题
    private String mFragmentTags[] = new String[2];
    private List<LocalBooksFragment> fragments = new ArrayList<>();
    private MileageAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.ting_fragment_index4;
    }

    @Override
    protected void initViews() {
        container_fl = mContentView.findViewById(R.id.container_fl);
        integral_tabs = mContentView.findViewById(R.id.integral_tabs);


        mFragmentTags[0] = getString(R.string.collect);
        mFragmentTags[1] = getString(R.string.history);
//        mFragmentTags[2] = getString(R.string.download);

        //1收藏列表2历史列表3下载列表
        fragments.add(LocalBooksFragment.newInstance(mFragmentTags[0],"1"));
        fragments.add(LocalBooksFragment.newInstance(mFragmentTags[1],"2"));
//        fragments.add(LocalBooksFragment.newInstance(mFragmentTags[2],"3"));

        container_fl.setOffscreenPageLimit(mFragmentTags.length);
        integral_tabs.setScrollBar(new ColorBar(getActivity(), ContextCompat.getColor(getActivity(), android.R.color.white), 5));// 下划线的颜色和高度
//      integral_tabs.setScrollBar(new TextWidthColorBar(this,indicator,R.color.color_red_bg,30));// 下划线的颜色和高度
        float unSelectSize = 16;// 未选中的字体大小
        float selectSize = 16;// 选中的字体大小
        int selectColor = ContextCompat.getColor(getActivity(), android.R.color.white);// 选中的字体颜色
        int unSelectColor = ContextCompat.getColor(getActivity(), R.color.text_unselect);// 没有选中的字体颜色
        integral_tabs.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));
        indicatorViewPager = new IndicatorViewPager(integral_tabs, container_fl);
        indicatorViewPager.setPageOffscreenLimit(mFragmentTags.length);
        adapter = new MileageAdapter<LocalBooksFragment>(getChildFragmentManager(), fragments, mFragmentTags);
        indicatorViewPager.setAdapter(adapter);
    }

    @Override
    protected void bindEvent() {
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {
                LogUtils.showLog("preItem:"+preItem+":currentItem:"+currentItem);
            }
        });
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

}
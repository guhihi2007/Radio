package cn.yuntk.radio.ibook.fragment;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.activity.BookSearchActivtity;
import cn.yuntk.radio.ibook.activity.SettingActivity;
import cn.yuntk.radio.ibook.adapter.ViewPagerAdapter;
import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.component.AppComponent;

import java.util.ArrayList;
import java.util.List;

public class Index4Fragment extends BaseFragment implements View.OnClickListener{

    TextView search_tv;
    TextView setting_tv;
    LinearLayout title_ll;
    TextView title_tv1;
    TextView title_tv2;
    ViewPager container_fl;
    @BindView(R.id.comm_back_ll)
    LinearLayout comm_back_ll;

    // 标题
    private String mFragmentTags[] = new String[2];
    private List<LocalBooksFragment> fragments = new ArrayList<>();
    private ViewPagerAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_listener_index4;
    }

    @Override
    protected void initViews() {
        search_tv = mContentView.findViewById(R.id.search_tv);
        setting_tv = mContentView.findViewById(R.id.setting_tv);
        title_ll = mContentView.findViewById(R.id.title_ll);
        title_tv1 = mContentView.findViewById(R.id.title_tv1);
        title_tv2 = mContentView.findViewById(R.id.title_tv2);
        container_fl = mContentView.findViewById(R.id.container_fl);
        comm_back_ll.setVisibility(View.VISIBLE);
        comm_back_ll.setOnClickListener(this);
        search_tv.setVisibility(View.GONE);
        setting_tv.setText("搜索");
        setting_tv.setOnClickListener(this);
        title_tv1.setOnClickListener(this);
        title_tv2.setOnClickListener(this);

        mFragmentTags[0] = getString(R.string.history);
        mFragmentTags[1] = getString(R.string.download);

        fragments.add(LocalBooksFragment.newInstance(mFragmentTags[0],"0"));
        fragments.add(LocalBooksFragment.newInstance(mFragmentTags[1],"1"));

        title_tv1.setText(mFragmentTags[0]);
        title_tv2.setText(mFragmentTags[1]);
    }

    @Override
    protected void bindEvent() {
        adapter = new ViewPagerAdapter(getChildFragmentManager(),fragments);
        container_fl.setOffscreenPageLimit(mFragmentTags.length);
        container_fl.setAdapter(adapter);
        container_fl.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                shiftTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        shiftTitle(0);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_tv:
                BookSearchActivtity.jumpToSearch(mContext);
                break;
            case R.id.setting_tv:
//                Intent intent = new Intent(mContext,SettingActivity.class);
//                startActivity(intent);
                BookSearchActivtity.jumpToSearch(mContext);

                break;
            case R.id.title_tv1:
                shiftTitle(0);
                break;
            case R.id.title_tv2:
                shiftTitle(1);
                break;
            case R.id.comm_back_ll:
                if (getActivity()!=null){
                    getActivity().finish();
                }
        }
    }


    //    切换选中项
    private void shiftTitle(int index){
        for (int i = 0;i<title_ll.getChildCount();i++){
            if (i!=index){
                title_ll.getChildAt(i).setSelected(false);
            }else {
                title_ll.getChildAt(i).setSelected(true);
                container_fl.setCurrentItem(index);
            }
        }
    }

}
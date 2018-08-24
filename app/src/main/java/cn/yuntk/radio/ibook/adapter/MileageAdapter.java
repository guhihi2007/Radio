package cn.yuntk.radio.ibook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shizhefei.view.indicator.IndicatorViewPager;
import cn.yuntk.radio.R;

import java.util.List;

/*Tab 适配器*/
public class MileageAdapter<T extends Fragment> extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

    private List<T> fragments;
    private String[] titles;
    private LayoutInflater inflater;

    public MileageAdapter(FragmentManager fragmentManager, List<T> fragments, String[] titles) {
        super(fragmentManager);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public int getCount() {
        return fragments == null?0:fragments.size();
    }

    @Override
    public View getViewForTab(int position, View convertView, ViewGroup container) {
        if (convertView==null){
            inflater = LayoutInflater.from(container.getContext());
            convertView = inflater.inflate(R.layout.listener_tab_top, container, false);
        }
        TextView tabName = (TextView) convertView;
        tabName.setText(titles[position]);
        return convertView;
    }

    @Override
    public Fragment getFragmentForPage(int position) {
        if (fragments != null && fragments.get(position) != null) {
            return fragments.get(position);
        } else {
            return new Fragment();
        }
    }
}

package cn.yuntk.radio.ibook.base.adapter;

import android.content.Context;


import java.util.List;

import cn.yuntk.radio.ibook.widget.recycle.CommRecyclerViewAdapter;

/**
 * <p>类描述：</p>
 * <p>创建人：yb</p>
 * <p>创建时间：2018/4/3</p>
 */
public abstract class BaseRefreshAdapter<T> extends CommRecyclerViewAdapter<T> {
    public BaseRefreshAdapter(Context context, List<T> data, int itemLayoutId) {
        super(context, data, itemLayoutId);
    }

    public final void refresh(List<T> data, boolean isLoadMore) {
        if (!isLoadMore)
            mDatas.clear();
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    public final void replaceAll(List<T> data){
        mDatas.clear();
        mDatas.addAll(data);
        notifyDataSetChanged();
    }
}

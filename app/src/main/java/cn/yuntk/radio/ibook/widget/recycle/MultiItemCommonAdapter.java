package cn.yuntk.radio.ibook.widget.recycle;

import android.content.Context;
import android.view.ViewGroup;

import java.util.List;

/**
 * 创建:yb 2016/10/27.
 * 描述:recycler多布局Adapter
 */

public abstract class MultiItemCommonAdapter<T> extends CommRecyclerViewAdapter<T> {

    protected MultiItemTypeSupport<T> mMultiItemTypeSupport;

    public MultiItemCommonAdapter(Context context, List<T> data, MultiItemTypeSupport<T> support) {
        super(context,data,-1);
        mMultiItemTypeSupport = support;
    }

    public MultiItemCommonAdapter(Context context, List<T> data, int itemLayoutId) {
        super(context, data, itemLayoutId);
    }

    @Override
    public int getItemViewType(int position) {
        if (mMultiItemTypeSupport != null)
            return mMultiItemTypeSupport.getItemViewType(position,mDatas.get(position));
        return super.getItemViewType(position);
    }

    @Override
    public CommRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = mMultiItemTypeSupport.getLayoutId(viewType);
        return CommRecyclerViewHolder.get(mContext,parent,layoutId);
    }

    public interface MultiItemTypeSupport<T>
    {
        int getLayoutId(int itemType);

        int getItemViewType(int position, T t);
    }

}


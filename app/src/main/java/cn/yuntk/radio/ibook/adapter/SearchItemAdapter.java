package cn.yuntk.radio.ibook.adapter;

import android.content.Context;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.adapter.BaseRefreshAdapter;
import cn.yuntk.radio.ibook.bean.SearchItemBean;
import cn.yuntk.radio.ibook.widget.recycle.CommRecyclerViewHolder;

import java.util.List;

//搜索历史 搜索结果的适配器
public class SearchItemAdapter extends BaseRefreshAdapter<SearchItemBean> {
    private RvItemClickInterface rvItemClickInterface;

    public SearchItemAdapter(Context context, List<SearchItemBean> data, int itemLayoutId) {
        super(context, data, itemLayoutId);
    }

    public void setRvItemClickInterface(RvItemClickInterface rvItemClickInterface) {
        this.rvItemClickInterface = rvItemClickInterface;
    }

    @Override
    public void convert(CommRecyclerViewHolder holder, SearchItemBean urlListBean) {
        holder.setText(R.id.book_name_tv,urlListBean.getTitle());
        holder.itemView.setOnClickListener(v -> {
            if (rvItemClickInterface!=null){
                rvItemClickInterface.onItemClick(urlListBean);
            }
        });
    }
}

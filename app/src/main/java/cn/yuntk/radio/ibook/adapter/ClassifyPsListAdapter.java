package cn.yuntk.radio.ibook.adapter;

import android.content.Context;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.adapter.BaseRefreshAdapter;
import cn.yuntk.radio.ibook.bean.PsUrlItemBean;
import cn.yuntk.radio.ibook.widget.recycle.CommRecyclerViewHolder;

import java.util.List;

/*分类评书目录适配器*/
public class ClassifyPsListAdapter extends BaseRefreshAdapter<PsUrlItemBean> {

    public ClassifyPsListAdapter(Context context, List<PsUrlItemBean> data, int itemLayoutId) {
        super(context, data, itemLayoutId);
    }

    private RvItemClickInterface rvItemClickInterface;

    public void setRvItemClickInterface(RvItemClickInterface rvItemClickInterface) {
        this.rvItemClickInterface = rvItemClickInterface;
    }

    @Override
    public void convert(CommRecyclerViewHolder holder, PsUrlItemBean bean) {
        holder.setText(R.id.book_info_tv,bean.getTitle());
        holder.setText(R.id.book_author_tv,bean.getZt());

        holder.itemView.setOnClickListener(v -> {
            if (rvItemClickInterface!=null){
                rvItemClickInterface.onItemClick(bean);
            }
        });
    }
}
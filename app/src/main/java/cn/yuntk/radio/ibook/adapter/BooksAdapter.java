package cn.yuntk.radio.ibook.adapter;

import android.content.Context;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.adapter.BaseRefreshAdapter;
import cn.yuntk.radio.ibook.bean.ItemBookBean;
import cn.yuntk.radio.ibook.widget.recycle.CommRecyclerViewHolder;

import java.util.List;

/*小说列表 适配器*/
public class BooksAdapter extends BaseRefreshAdapter<ItemBookBean> {

    private RvItemClickInterface rvItemClickInterface;

    public BooksAdapter(Context context, List<ItemBookBean> data, int itemLayoutId) {
        super(context, data, itemLayoutId);
    }

    public void setRvItemClickInterface(RvItemClickInterface rvItemClickInterface) {
        this.rvItemClickInterface = rvItemClickInterface;
    }

    @Override
    public void convert(CommRecyclerViewHolder holder, ItemBookBean timeListBean) {
        holder.setText(R.id.book_info_tv,timeListBean.getTitle());
        holder.setText(R.id.book_author_tv,timeListBean.getType());
        holder.itemView.setOnClickListener(v -> {
            if (rvItemClickInterface!=null){
                rvItemClickInterface.onItemClick(timeListBean);
            }
        });
    }

}
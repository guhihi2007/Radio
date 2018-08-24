package cn.yuntk.radio.ibook.adapter;

import android.content.Context;
import android.view.View;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.adapter.BaseRefreshAdapter;
import cn.yuntk.radio.ibook.bean.PopItemBean;
import cn.yuntk.radio.ibook.widget.recycle.CommRecyclerViewHolder;

import java.util.List;

public class PopListAdapter extends BaseRefreshAdapter<PopItemBean> {

    private RvItemClickInterface rvItemClickInterface;

    public void setRvItemClickInterface(RvItemClickInterface rvItemClickInterface) {
        this.rvItemClickInterface = rvItemClickInterface;
    }

    public PopListAdapter(Context context, List<PopItemBean> data, int itemLayoutId) {
        super(context, data, itemLayoutId);
    }

    @Override
    public void convert(CommRecyclerViewHolder holder, PopItemBean popItemBean) {
        holder.setText(R.id.item_content_tv,popItemBean.getShowString());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rvItemClickInterface!=null){
                    rvItemClickInterface.onItemClick(popItemBean);
                }
            }
        });
    }

}
package cn.yuntk.radio.ibook.adapter;

import android.content.Context;
import android.view.View;


import java.util.List;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.adapter.BaseRefreshAdapter;
import cn.yuntk.radio.ibook.bean.PopItemBean;
import cn.yuntk.radio.ibook.widget.recycle.CommRecyclerViewHolder;

public class PopListAdapter extends BaseRefreshAdapter<PopItemBean> {

    private PopItemClickInterface anInterface;

    public void setAnInterface(PopItemClickInterface anInterface) {
        this.anInterface = anInterface;
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
                if (anInterface!=null){
                    anInterface.popItemClick(popItemBean);
                }
            }
        });
    }

}
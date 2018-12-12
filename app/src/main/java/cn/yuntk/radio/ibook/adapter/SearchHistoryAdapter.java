package cn.yuntk.radio.ibook.adapter;

import android.content.Context;
import android.view.View;


import java.util.List;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.adapter.BaseRefreshAdapter;
import cn.yuntk.radio.ibook.util.DialogUtils;
import cn.yuntk.radio.ibook.widget.recycle.CommRecyclerViewHolder;

public class SearchHistoryAdapter extends BaseRefreshAdapter {

    DialogUtils.HistoryClickInterface itemClickInterface;

    public SearchHistoryAdapter(Context context, List data, int itemLayoutId) {
        super(context, data, itemLayoutId);
    }

    public void setItemClickInterface(DialogUtils.HistoryClickInterface itemClickInterface) {
        this.itemClickInterface = itemClickInterface;
    }

    @Override
    public void convert(CommRecyclerViewHolder holder, Object o) {
        holder.setText(R.id.book_name_tv,(String) o);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (itemClickInterface!=null){
                   itemClickInterface.itemClick((String) o);
               }
            }
        });
    }

}

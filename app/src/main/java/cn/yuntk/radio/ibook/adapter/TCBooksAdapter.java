package cn.yuntk.radio.ibook.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;


import java.util.List;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.adapter.BaseRefreshAdapter;
import cn.yuntk.radio.ibook.bean.TCBean3;
import cn.yuntk.radio.ibook.widget.recycle.CommRecyclerViewHolder;

/**
 * 小说列表 适配器
 * */
public class TCBooksAdapter extends BaseRefreshAdapter<TCBean3> {

    private RvItemClickInterface rvItemClickInterface;
    private String pageName = "0";//0首页书籍列表1分类点击

    public TCBooksAdapter(Context context, List<TCBean3> data, int itemLayoutId, String pageName) {
        super(context, data, itemLayoutId);
        this.pageName = pageName;
    }

    public void setRvItemClickInterface(RvItemClickInterface rvItemClickInterface) {
        this.rvItemClickInterface = rvItemClickInterface;
    }

    @Override
    public void convert(CommRecyclerViewHolder holder, TCBean3 timeListBean) {

        holder.setText(R.id.book_info_tv,timeListBean.getBookName());
        holder.setText(R.id.book_type_tv,"播讲："+timeListBean.getHostName());
//        holder.setText(R.id.book_type_tv,"播放："+timeListBean.getPlayNum()+
        holder.getView(R.id.book_author_tv).setVisibility(View.GONE);
        ConstraintLayout item_content_cl = holder.getView(R.id.content_cl);//item 内容

        item_content_cl.setOnClickListener(v -> {
            if (rvItemClickInterface!=null){
                rvItemClickInterface.onItemClick(timeListBean);
            }
        });
    }

}
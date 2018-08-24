package cn.yuntk.radio.ibook.adapter;

import android.content.Context;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.adapter.BaseRefreshAdapter;
import cn.yuntk.radio.ibook.bean.BookDetailBean;
import cn.yuntk.radio.ibook.widget.recycle.CommRecyclerViewHolder;

import java.util.List;

/*小说目录适配器*/
public class CatalogueListAdapter extends BaseRefreshAdapter<BookDetailBean.UrlListBean> {


    public CatalogueListAdapter(Context context, List<BookDetailBean.UrlListBean> data, int itemLayoutId) {
        super(context, data, itemLayoutId);
    }

    private RvItemClickInterface rvItemClickInterface;
    private DownloadMp3Interface downloadMp3Interface;

    public void setRvItemClickInterface(RvItemClickInterface rvItemClickInterface) {
        this.rvItemClickInterface = rvItemClickInterface;
    }

    public void setDownloadMp3Interface(DownloadMp3Interface downloadMp3Interface) {
        this.downloadMp3Interface = downloadMp3Interface;
    }

    @Override
    public void convert(CommRecyclerViewHolder holder, BookDetailBean.UrlListBean urlListBean) {
        holder.setText(R.id.book_name_tv,urlListBean.getName());
        if (urlListBean.getIs_download().equals("0")){
            holder.setText(R.id.book_status_tv,mContext.getString(R.string.download));
        }else {
            holder.setText(R.id.book_status_tv,mContext.getString(R.string.download_done));
        }
//        holder.getView(R.id.book_status_tv).setVisibility(View.GONE);
        holder.setOnClickListener(R.id.book_status_tv, v -> {
            if (downloadMp3Interface!=null){
                downloadMp3Interface.downloadItem(holder.getLayoutPosition(),urlListBean);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (rvItemClickInterface!=null){
                rvItemClickInterface.onItemClick(urlListBean);
            }
        });
    }
}

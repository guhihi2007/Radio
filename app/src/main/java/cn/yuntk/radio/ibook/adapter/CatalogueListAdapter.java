package cn.yuntk.radio.ibook.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.adapter.BaseRefreshAdapter;
import cn.yuntk.radio.ibook.bean.BookDetailBean;
import cn.yuntk.radio.ibook.bean.TCBean5;
import cn.yuntk.radio.ibook.util.FileInfoUtils;
import cn.yuntk.radio.ibook.util.SystemUtils;
import cn.yuntk.radio.ibook.widget.SwipeMenuLayout;
import cn.yuntk.radio.ibook.widget.recycle.CommRecyclerViewHolder;

import static cn.yuntk.radio.ibook.util.FileUtils.SIZETYPE_MB;


/*小说目录适配器*/
public class CatalogueListAdapter extends BaseRefreshAdapter<TCBean5> {


    public CatalogueListAdapter(Context context, List<TCBean5> data, int itemLayoutId) {
        super(context, data, itemLayoutId);
    }

    private RvItemClickInterface rvItemClickInterface;
    private DownloadMp3Interface downloadMp3Interface;
    private ListItemDialogMeun2 dialogMeun;//删除回调

    public void setRvItemClickInterface(RvItemClickInterface rvItemClickInterface) {
        this.rvItemClickInterface = rvItemClickInterface;
    }

    public void setDownloadMp3Interface(DownloadMp3Interface downloadMp3Interface) {
        this.downloadMp3Interface = downloadMp3Interface;
    }

    public void setDialogMeun(ListItemDialogMeun2 dialogMeun) {
        this.dialogMeun = dialogMeun;
    }

    @Override
    public void convert(CommRecyclerViewHolder holder, TCBean5 urlListBean) {
        SwipeMenuLayout item_swipe_view = holder.getView(R.id.item_swipe_view);//侧滑
        ConstraintLayout catalogue_content_cl = holder.getView(R.id.catalogue_content_cl);//内容
        TextView track_title_tv = holder.getView(R.id.track_title_tv);//声音标题
        TextView filesize_tv = holder.getView(R.id.filesize_tv);//文件大小
        TextView duration_tv = holder.getView(R.id.duration_tv);//时长
        TextView listener_progress_tv = holder.getView(R.id.listener_progress_tv);//收听进度
        TextView download_status_tv = holder.getView(R.id.download_status_tv);//下载按钮
        ProgressBar download_pb = holder.getView(R.id.download_pb);//下载进度条
        Button btn_zd = holder.getView(R.id.btn_zd);//置顶
        Button btn_delete = holder.getView(R.id.btn_delete);//删除按钮

        track_title_tv.setText(urlListBean.getZname());
        filesize_tv.setText("音频："+ FileInfoUtils.FormetFileSize(Long.parseLong(urlListBean.getFilesize()),SIZETYPE_MB)+"M");//播放文件大小
        holder.setText(R.id.duration_tv,"时长："+
                SystemUtils.formatTime("mm:ss", Long.parseLong(urlListBean.getTimesize())*1000)+"");//播放时长
        filesize_tv.setVisibility(View.GONE);
        download_status_tv.setVisibility(View.INVISIBLE);//隐藏下载
        download_status_tv.setOnClickListener( v -> {
            if (downloadMp3Interface!=null){
                downloadMp3Interface.downloadItem(holder.getLayoutPosition(),urlListBean);
            }
        });
        catalogue_content_cl.setOnClickListener(v -> {
            if (rvItemClickInterface!=null){
                rvItemClickInterface.onItemClick(urlListBean);
            }
        });

        item_swipe_view.setSwipeEnable(false);
        item_swipe_view.setLeftSwipe(false);
        item_swipe_view.setIos(false);

        if (urlListBean.getListenerStatus() == 0){
            listener_progress_tv.setVisibility(View.GONE);
        }else {
            listener_progress_tv.setVisibility(View.VISIBLE);
            listener_progress_tv.setText("已播放 "+urlListBean.getDisplayProgress());
        }

    }


    public void showItemMeun(Context context, String[] items, BookDetailBean.UrlListBean urlListBean){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogMeun !=null){
                    dialogMeun.selectItem(items[which],which,urlListBean);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

}

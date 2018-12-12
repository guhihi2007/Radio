package cn.yuntk.radio.ibook.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;


import java.util.List;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.base.adapter.BaseRefreshAdapter;
import cn.yuntk.radio.ibook.bean.TCBean3;
import cn.yuntk.radio.ibook.widget.SwipeMenuLayout;
import cn.yuntk.radio.ibook.widget.recycle.CommRecyclerViewHolder;


/**
 * 小说列表 适配器
 * */
public class TCBooksLocalAdapter extends BaseRefreshAdapter<TCBean3> {

    private RvItemClickInterface rvItemClickInterface;
    private boolean enableDelete = false;
    private ListItemDialogMeun dialogMeun;
    private String pageName = "1";//1收藏列表2历史列表3下载列表
    private String menuTip = "删除全部";//操作目录

    public TCBooksLocalAdapter(Context context, List<TCBean3> data, int itemLayoutId, String pageName) {
        super(context, data, itemLayoutId);
        this.pageName = pageName;
        if (pageName.equals("1")){
            menuTip = "删除所有收藏";
        }else if (pageName.equals("2")){
            menuTip = "删除所有历史";
        }else if (pageName.equals("3")){
            menuTip = "删除所有下载";
        }
    }

    public void setRvItemClickInterface(RvItemClickInterface rvItemClickInterface) {
        this.rvItemClickInterface = rvItemClickInterface;
    }
    //    设置删除监听
    public void setDialogMeun(ListItemDialogMeun dialogMeun) {
        this.dialogMeun = dialogMeun;
    }

    //设置是否拥有删除功能
    public void setEnableDelete(boolean enable){
        this.enableDelete = enable;
    }

    @Override
    public void convert(CommRecyclerViewHolder holder, TCBean3 timeListBean) {

        holder.setText(R.id.book_info_tv,timeListBean.getBookName());
        holder.setText(R.id.book_author_tv,"播讲："+timeListBean.getHostName());
        SwipeMenuLayout menuLayout = holder.getView(R.id.item_swipe_view);//侧滑
        ConstraintLayout item_content_cl = holder.getView(R.id.content_cl);//item 内容
        Button btn_delete = holder.getView(R.id.btn_delete);

        menuLayout.setSwipeEnable(false);
        menuLayout.setIos(false);
        menuLayout.setLeftSwipe(false);

        item_content_cl.setOnClickListener(v -> {
            if (rvItemClickInterface!=null){
                rvItemClickInterface.onItemClick(timeListBean);
            }
        });
        if (enableDelete){
            item_content_cl.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showItemMeun(mContext,new String[]{"删除\""+ timeListBean.getBookName()+"\"",menuTip},(long)timeListBean.getBookID());
                    return false;
                }
            });
//            btn_delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showItemMeun(mContext,new String[]{"删除\""+ timeListBean.getBookName()+"\"",menuTip}, (long) timeListBean.getBookID());
//                    menuLayout.quickClose();
//                }
//            });
        }
    }

    public void showItemMeun(Context context, String[] items, Long bookid){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (pageName.equals("1")){
            builder.setTitle("收藏");
        }else if (pageName.equals("2")){
            builder.setTitle("历史");
        }else if (pageName.equals("3")){
            builder.setTitle("下载");
        }
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialogMeun !=null){
                    dialogMeun.selectItem(items[which],which,bookid);
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

}
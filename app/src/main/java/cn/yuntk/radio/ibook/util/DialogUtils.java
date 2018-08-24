package cn.yuntk.radio.ibook.util;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.adapter.PopListAdapter;
import cn.yuntk.radio.ibook.bean.OnlineMusic;
import cn.yuntk.radio.ibook.bean.PopItemBean;
import cn.yuntk.radio.ibook.common.Constants;

import java.util.List;

public class DialogUtils {


    public static DialogUtils get() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static DialogUtils instance = new DialogUtils();
    }

    private DialogUtils(){

    }

    Dialog dialog;
    //    睡眠弹窗 章节选择弹窗
    public void showPop(Context context, String title, List<PopItemBean> items, PopClickInterface popClickInterface) {
        dialog = new Dialog(context, R.style.my_dialog);
        //填充对话框的布局
        View inflate = LayoutInflater.from(context).inflate(R.layout.listener_pop_window_, null);
        //初始化控件
        RecyclerView content_rv = inflate.findViewById(R.id.content_rv);
        TextView pop_title_tv = inflate.findViewById(R.id.pop_title_tv);
        TextView pop_back_tv = inflate.findViewById(R.id.pop_back_tv);
        pop_title_tv.setText(title);
        pop_back_tv.setText("返回");
        PopListAdapter adapter = new PopListAdapter(context,items,R.layout.listener_item_pop_);
        content_rv.setLayoutManager(new LinearLayoutManager(context));
        content_rv.setAdapter(adapter);
        adapter.setRvItemClickInterface(o -> {
            if (popClickInterface!=null){
                popClickInterface.itemClick((PopItemBean) o);
                dialog.dismiss();
            }
        });
        pop_back_tv.setOnClickListener(v -> {
            if (popClickInterface!=null){
                if (dialog!=null&&dialog.isShowing()){
                    dialog.dismiss();
                }
                popClickInterface.backClick();
            }
        });

        //将布局设置给Dialog
        dialog.setContentView(inflate);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dip2px(context,250),LinearLayout.LayoutParams.WRAP_CONTENT);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();


        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        //获得窗体的属性
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        //lp.y = 20;//设置Dialog距离底部的距离
        //将属性设置给窗体
        if (items.size()>6){
            lp.height = (int) (Constants.height*0.6);
        }
        lp.width = (int) (Constants.width*0.65);
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    //    确认下载弹窗
    public void showDownloadDialog(Context context, String title, OnlineMusic onlineMusic, DownloadDialogClick listener){
        dialog = new Dialog(context, R.style.my_dialog);
        //填充对话框的布局
        View inflate = LayoutInflater.from(context).inflate(R.layout.listener_item_tip_dialog, null);
        //初始化控件
        TextView tv_cancel = inflate.findViewById(R.id.tv_cancel);
        TextView tv_sure = inflate.findViewById(R.id.tv_sure);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        tv_title.setText("是否下载 "+title+"？");
        tv_sure.setOnClickListener(v -> {
            dialog.dismiss();
            listener.sureDownloadBut(onlineMusic);
        });
        tv_cancel.setOnClickListener(v -> {
            dialog.dismiss();
            listener.cancleDownloadBut();
        });

        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();

        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();//显示对话框
    }

    public interface PopClickInterface{
        void itemClick(PopItemBean item);
        void backClick();
    }

    public interface HistoryClickInterface{
        void itemClick(String o);
        void clearData();
    }

    public interface DownloadDialogClick{
        void sureDownloadBut(OnlineMusic onlineMusic);
        void cancleDownloadBut();
    }

}
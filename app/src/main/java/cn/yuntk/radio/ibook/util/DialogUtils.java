package cn.yuntk.radio.ibook.util;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.yuntk.radio.R;
import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.adapter.PopListAdapter;
import cn.yuntk.radio.ibook.bean.BookDetailBean;
import cn.yuntk.radio.ibook.bean.OnlineMusic;
import cn.yuntk.radio.ibook.bean.PopItemBean;
import cn.yuntk.radio.ibook.common.TingConstants;

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
        View inflate = LayoutInflater.from(context).inflate(R.layout.ting_pop_window_, null);
        //初始化控件
        RecyclerView content_rv = inflate.findViewById(R.id.content_rv);
        TextView pop_title_tv = inflate.findViewById(R.id.pop_title_tv);
        TextView pop_back_tv = inflate.findViewById(R.id.pop_back_tv);
        pop_title_tv.setText(title);
        pop_back_tv.setText("返回");
        PopListAdapter adapter = new PopListAdapter(context,items,R.layout.ting_item_pop_);
        content_rv.setLayoutManager(new LinearLayoutManager(context));
        content_rv.setAdapter(adapter);
        adapter.setAnInterface(o -> {
            if (popClickInterface!=null){
                popClickInterface.itemClick(o);
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
            lp.height = (int) (TingConstants.height*0.6);
        }
        lp.width = (int) (TingConstants.width*0.65);
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    //   批量下载选择弹窗
    public void showDownloadDialog(Context context, List<BookDetailBean.UrlListBean> urls, ChapterCallback listener){
        dialog = new Dialog(context, R.style.my_dialog);
        //填充对话框的布局
        View inflate = LayoutInflater.from(context).inflate(R.layout.ting_dialog_batch_download, null);
        //初始化控件
        TextView total_chapter_tv = inflate.findViewById(R.id.total_chapter_tv);
        TextView batch_download_cancle_tv = inflate.findViewById(R.id.batch_download_cancle_tv);
        TextView batch_download_sure_tv = inflate.findViewById(R.id.batch_download_sure_tv);
        TextView start_reduce_tv = inflate.findViewById(R.id.start_reduce_tv);
        EditText start_et = inflate.findViewById(R.id.start_et);
        TextView start_add_tv = inflate.findViewById(R.id.start_add_tv);
        TextView end_reduce_tv = inflate.findViewById(R.id.end_reduce_tv);
        EditText end_et = inflate.findViewById(R.id.end_et);
        TextView end_add_tv = inflate.findViewById(R.id.end_add_tv);

        total_chapter_tv.setText("共 "+urls.size()+"章");

        start_et.setSelection(1);
        if (urls.size()<100){
            end_et.setText(urls.size()+"");
            end_et.setSelection(end_et.getText().toString().length());
        }else {
            end_et.setText("100");
            end_et.setSelection(3);
        }

        //取消
        batch_download_cancle_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog!=null&&dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });

        //下载
        batch_download_sure_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startChapter = start_et.getText().toString();
                String endChapter = end_et.getText().toString();
                if (startChapter.isEmpty()||endChapter.isEmpty()){
                    showToast_("请选择章节区间");
                    return;
                }
                int startChapterInt = Integer.parseInt(startChapter);
                int endChapterInt = Integer.parseInt(endChapter);
                if (startChapterInt<=0 ||endChapterInt>urls.size()){
                    showToast_("请选择有效的章节区间");
                }else if (startChapterInt>=endChapterInt){
                    showToast_("结束章节必须大于开始章节");
                }else {
//                    List<BookDetailBean.UrlListBean> tempList = urls.subList(startChapterInt-1,endChapterInt);
//                    for (BookDetailBean.UrlListBean bean:tempList){
//                        //0未下载1下载成功2正在下载3等待中
//                        if (bean.getIs_download().equals("1")||bean.getIs_download().equals("2")||bean.getIs_download().equals("3")){
//                            showToast_("章节已下载过了，请选择未下载章节区间");
//                            return;
//                        }
//                    }
                    if (dialog!=null&&dialog.isShowing()){
                        dialog.dismiss();
                    }
                    listener.cancleChapterDownload(startChapterInt,endChapterInt);
                }
            }
        });

        start_reduce_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startChapter = start_et.getText().toString();
                if (startChapter.isEmpty()){
                    showToast_("开始章节不能为空");
                    return;
                }
                int startChapterInt = Integer.parseInt(startChapter);
                if (startChapterInt>1){
                    startChapterInt--;
                    start_et.setText(startChapterInt+"");
                }else {
                    showToast_("已经是第一章了");
                }
            }
        });

        start_add_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startChapter = start_et.getText().toString();
                if (startChapter.isEmpty()){
                    showToast_("开始章节不能为空");
                    return;
                }
                int startChapterInt = Integer.parseInt(startChapter);
                if (startChapterInt<urls.size()){
                    startChapterInt++;
                    start_et.setText(startChapterInt+"");
                }else {
                    showToast_("已经是最后一章了");
                }
            }
        });

        end_reduce_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startChapter = end_et.getText().toString();
                if (startChapter.isEmpty()){
                    showToast_("结束章节不能为空");
                    return;
                }
                int startChapterInt = Integer.parseInt(startChapter);
                if (startChapterInt<urls.size()){
                    startChapterInt--;
                    end_et.setText(startChapterInt+"");
                }else {
                    showToast_("已经是第一章了");
                }
            }
        });

        end_add_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startChapter = end_et.getText().toString();
                if (startChapter.isEmpty()){
                    showToast_("结束章节不能为空");
                    return;
                }
                int startChapterInt = Integer.parseInt(startChapter);
                if (startChapterInt<urls.size()){
                    startChapterInt++;
                    end_et.setText(startChapterInt+"");
                }else {
                    showToast_("已经是最后一章了");
                }
            }
        });

        //设置字符 不能0开头
        start_et.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                if (source.equals("0") && dest.toString().length() == 0) {
                    return "";
                }
                return null;
            }
        }});

        end_et.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals("0") && dest.toString().length() == 0) {
                    return "";
                }
                return null;
            }
        }});

        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();

        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();//显示对话框
    }

    //    倍速选择
    public void playerSpeedChange(Context context, PlaySpeedCallback callback){
        dialog = new Dialog(context,R.style.my_dialog);
        //填充对话框的布局
        View inflate = LayoutInflater.from(context).inflate(R.layout.ting_pop_window_, null);
        //初始化控件
        RecyclerView content_rv = inflate.findViewById(R.id.content_rv);
        TextView pop_title_tv = inflate.findViewById(R.id.pop_title_tv);
        TextView pop_back_tv = inflate.findViewById(R.id.pop_back_tv);
        pop_title_tv.setText("倍速");
        pop_back_tv.setVisibility(View.GONE);

        List<PopItemBean> items = new ArrayList<PopItemBean>();
        PopItemBean item1 = new PopItemBean();
        item1.setShowString("x1.0");
        item1.setType(PopItemBean.PopTpye.SPEED);
        item1.setMinValue(0);
        PopItemBean item2 = new PopItemBean();
        item2.setShowString("x1.5");
        item2.setType(PopItemBean.PopTpye.SPEED);
        item2.setMinValue(1);
        PopItemBean item3 = new PopItemBean();
        item3.setShowString("x2.0");
        item3.setType(PopItemBean.PopTpye.SPEED);
        item3.setMinValue(2);
        items.add(item1);
        items.add(item2);
        items.add(item3);

        PopListAdapter adapter = new PopListAdapter(context,items,R.layout.ting_item_pop_);
        content_rv.setLayoutManager(new LinearLayoutManager(context));
        content_rv.setAdapter(adapter);
        adapter.setAnInterface(o -> {
            if (callback!=null){
                callback.speedChoose("SPEED",o);
                dialog.dismiss();
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
            lp.height = (int) (TingConstants.height*0.6);
        }
        lp.width = (int) (TingConstants.width*0.65);
        dialogWindow.setAttributes(lp);
        dialog.show();//显示对话框
    }

    public void showToast_(String string){
        XApplication.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShortToast(string);
            }
        });
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

    public interface ChapterCallback{
        void cancleChapterDownload(int start, int end);
    }

    //    倍速选择
    public interface PlaySpeedCallback{
        void speedChoose(String type, Object popItemBean);
    }

}
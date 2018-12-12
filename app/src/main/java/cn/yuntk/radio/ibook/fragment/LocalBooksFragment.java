package cn.yuntk.radio.ibook.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;

import cn.yuntk.radio.R;
import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.activity.BookDetailActivity;
import cn.yuntk.radio.ibook.adapter.ListItemDialogMeun;
import cn.yuntk.radio.ibook.adapter.RvItemClickInterface;
import cn.yuntk.radio.ibook.adapter.TCBooksLocalAdapter;
import cn.yuntk.radio.ibook.base.BaseFragment;
import cn.yuntk.radio.ibook.base.RootBase;
import cn.yuntk.radio.ibook.base.presenter.BasePresenter;
import cn.yuntk.radio.ibook.bean.DownloadBookInfo;
import cn.yuntk.radio.ibook.bean.DownloadMusicInfo;
import cn.yuntk.radio.ibook.bean.Music;
import cn.yuntk.radio.ibook.bean.TCAlbumTable;
import cn.yuntk.radio.ibook.bean.TCBean3;
import cn.yuntk.radio.ibook.common.TingConstants;
import cn.yuntk.radio.ibook.component.AppComponent;
import cn.yuntk.radio.ibook.dbdao.DownloadBookInfoManager;
import cn.yuntk.radio.ibook.dbdao.DownloadMusicInfoManager;
import cn.yuntk.radio.ibook.dbdao.TCAlbumTableManager;
import cn.yuntk.radio.ibook.util.FileInfoUtils;
import cn.yuntk.radio.ibook.util.FileUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;
import cn.yuntk.radio.ibook.widget.EmptyRecyclerView;

public class LocalBooksFragment extends BaseFragment implements RvItemClickInterface,ListItemDialogMeun {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    EmptyRecyclerView rv_subject;
    LinearLayout no_data_ll;
    SmartRefreshLayout refreshLayout;

    private String mParam1;
    private String mParam2;//1收藏列表2历史列表3下载列表

    TCBooksLocalAdapter adapter;
    List<TCBean3> timeListBeans = new ArrayList<>();
    CustomHandler handler;

    // TODO: Rename and change types and number of parameters
    public static LocalBooksFragment newInstance(String param1, String param2) {
        LocalBooksFragment fragment = new LocalBooksFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        return R.layout.ting_fragment_local;
    }

    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);
        rv_subject = mContentView.findViewById(R.id.rv_subject);
        no_data_ll = mContentView.findViewById(R.id.no_data_ll);
        refreshLayout = mContentView.findViewById(R.id.refreshLayout);

        handler = new CustomHandler(getActivity());
    }

    @Override
    protected void bindEvent() {
        adapter = new TCBooksLocalAdapter(mContext, timeListBeans,R.layout.ting_item_booklist_local,mParam2);
        adapter.setRvItemClickInterface(this);
        adapter.setEnableDelete(true);//设置删除监听
        adapter.setDialogMeun(this);
        rv_subject.setEmptyView(no_data_ll);
//        rv_subject.addItemDecoration(new SpaceItemDecoration(getActivity(), LinearLayoutManager.VERTICAL,2, Color.parseColor("#e5e5e5")));
        rv_subject.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv_subject.setAdapter(adapter);

        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableAutoLoadmore(false);
    }

    @Override
    protected void loadData() {
        //1收藏列表2历史列表3下载列表
        if (mParam2.equals("1")){
            getBookCollection();
        }else if (mParam2.equals("2")){
            getListenerHistory();
        }else if (mParam2.equals("3")){
            getDownloadChapter();
        }
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void onItemClick(RootBase o) {
        TCBean3 book = (TCBean3) o;
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        intent.putExtra("bookid",book.getBookID());
        intent.putExtra("booktitle",book.getBookName());
        intent.putExtra("booktype",book.getBookType());
        startActivity(intent);
    }

    //    通知刷历史和下载
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String string) {
        if (string.equals(TingConstants.UPDATA_COLLECT)&&mParam2.equals("1")){
            getBookCollection();
        }else if (string.equals(TingConstants.UPDATA_HISTORY)&&mParam2.equals("2")){
            getListenerHistory();
        }else{

        }
        LogUtils.showLog("LocalBookFragment:"+string);
    }

    /**
     * 删除数据库全部历史
     * */
    private void clearAllHistory(){
        List<TCAlbumTable> tables = TCAlbumTableManager.getInstance(mContext).queryHistoryList();
        for (TCAlbumTable music:tables){
            music.setIs_history(Music.History_Type.NO);
        }
        TCAlbumTableManager.getInstance(mContext).updataList(tables);
    }

    /**
     * 删除单本历史
     * */
    private void clearBookHistory(Long type){
        TCAlbumTable table = TCAlbumTableManager.getInstance(mContext).queryBeanBykey(type);
        table.setIs_history(Music.History_Type.NO);
        TCAlbumTableManager.getInstance(mContext).updateMusic(table);
    }

    /**
     * 查询收听历史记录
     * */
    private void getListenerHistory(){
        Executors.newSingleThreadExecutor().submit(() -> {
            timeListBeans.clear();
            timeListBeans.addAll(TCAlbumTableManager.tCAlbumTable2TCBean3(TCAlbumTableManager.getInstance(mContext).queryHistoryList()));
            handler.sendEmptyMessage(1);
        });
    }

    /**
     * 取消所有收藏
     * */
    private void clearAllCollection(){
        List<TCAlbumTable> collections = TCAlbumTableManager.getInstance(mContext).queryCollectionList();
        if (collections!=null&&collections.size()!=0){
            for (TCAlbumTable music:collections){
                music.setIs_collect(Music.Collect_Type.NO);
            }
            TCAlbumTableManager.getInstance(mContext).updataList(collections);
        }
    }

    /**
     * 取消一个收藏
     * */
    private void clearBookCollection(Long type){
        TCAlbumTable table = TCAlbumTableManager.getInstance(mContext).queryBeanBykey(type);
        table.setIs_collect(Music.Collect_Type.NO);
        TCAlbumTableManager.getInstance(mContext).updateMusic(table);
    }

    /**
     * 查询收藏
     * */
    private void getBookCollection(){
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                timeListBeans.clear();
                timeListBeans.addAll(TCAlbumTableManager.tCAlbumTable2TCBean3(TCAlbumTableManager.getInstance(mContext).queryCollectionList()));
                handler.sendEmptyMessage(1);
            }
        });
    }

    /**
     * 删除数据库全部下载
     * */
    private void clearAllDownload(){
        clearCache();
        DownloadBookInfoManager.getInstance(mContext).deleteAll();
        DownloadMusicInfoManager.getInstance(mContext).deleteAll();
    }

    /**
     * 删除单本书籍
     * */
    private void clearBookDownload(String type){
        DownloadBookInfo info = DownloadBookInfoManager.getInstance(mContext).queryListDB(type);
        if (info!=null){
            List<DownloadMusicInfo> infos = info.getMusicInfoList();
            if (infos!=null&&infos.size()!=0){
                for (DownloadMusicInfo musicInfo:infos){
                    deleteFile(musicInfo.getMusicPath());
                }
                DownloadMusicInfoManager.getInstance(mContext).deleteListMusic(infos);
                info.resetMusicInfoList();
            }
            DownloadBookInfoManager.getInstance(mContext).deleteBtn(info);
        }
    }

    /**
     * 查询下载
     * */
    private void getDownloadChapter(){
        XApplication.getDaoSession(mContext).startAsyncSession().runInTx(() -> {
            //DELETE 异步操作 删除数据库
            //查询本书的章节下载状态
            List<DownloadBookInfo> books =  DownloadBookInfoManager.getInstance(mContext).queryListDB();
            if (books != null&&books.size()!=0){
                List<TCBean3> bookBeans = new ArrayList<TCBean3>();
                TreeMap<String,TCBean3> treeMap = new TreeMap<String,TCBean3>();
                for (DownloadBookInfo music:books){
                    TCBean3 bookBean = new TCBean3();
                    bookBean.setBookName(music.getTitle());
                    bookBean.setIntro(music.getCon());
                    treeMap.put(music.getId()+"",bookBean);
                }

                for (Map.Entry<String, TCBean3> entry : treeMap.entrySet()) {
                    System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                    bookBeans.add(entry.getValue());
                }
                timeListBeans.clear();
                timeListBeans.addAll(bookBeans);
                Collections.reverse(timeListBeans);
            }else {
                timeListBeans.clear();
            }
            handler.sendEmptyMessage(1);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //列表菜单
    @Override
    public void selectItem(String str, int position, Long type) {
        if(position == 1){
            showClearCacheDialog();
        } else {
            showProgressDialog("删除中...");
            if (mParam2.equals("1")){
                clearBookCollection(type);
            }else if (mParam2.equals("2")){
                clearBookHistory(type);
            }else if (mParam2.equals("3")){
                clearBookDownload(type+"");
            }
            refreshUI();
        }
    }

    //    下载列表删除所有小说
    public void showClearCacheDialog() {
        Dialog dialog = new Dialog(mContext, R.style.my_dialog);
        //填充对话框的布局
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.ting_item_tip_dialog, null);
        //初始化控件
        TextView tv_cancel = inflate.findViewById(R.id.tv_cancel);
        TextView tv_sure = inflate.findViewById(R.id.tv_sure);
        TextView tv_title = inflate.findViewById(R.id.tv_title);
        if(mParam2.equals("1")){
            tv_title.setText("确定删除所有收藏文件？");
        }else if (mParam2.equals("2")){
            tv_title.setText("确定删除所有历史记录？");
        }else if (mParam2.equals("3")){
            tv_title.setText("确定删除所有下载文件？");
        }
        tv_cancel.setOnClickListener(v -> dialog.dismiss());
        tv_sure.setOnClickListener(v -> {
            dialog.dismiss();
            showProgressDialog("删除中...");
            if(mParam2.equals("1")){
                clearAllCollection();
            }else if (mParam2.equals("2")){
                clearAllHistory();
            }else if (mParam2.equals("3")){
                clearAllDownload();
            }
            refreshUI();
        });

        //将布局设置给Dialog
        dialog.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialog.getWindow();
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.CENTER);
        dialog.show();//显示对话框
    }

    //删除或者清空后的界面刷新
    private void refreshUI(){
        XApplication.getMainThreadHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissProgresDialog();
                if(mParam2.equals("1")){
                    getBookCollection();
                }else if (mParam2.equals("2")){
                    getListenerHistory();
                }else if (mParam2.equals("3")){
                    getDownloadChapter();
                }
                ToastUtil.showShortToast("删除成功");
            }
        },200);
    }

    /*自定义Handler*/
    public class CustomHandler extends Handler {

        private WeakReference<Activity> activityWeakReference;

        public CustomHandler(Activity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            Activity activity = activityWeakReference.get();
            if (activity != null) {
                switch (msg.what){
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
            }
        }
    }

    /**
     * 计算缓存的大小
     * */
    private void getCache() {
        long cache1 = 0l;
        try {
            cache1 = FileInfoUtils.getFileSizes(new File(FileUtils.getMusicDir()));
        } catch (Exception e) {
            System.out.print("获取文件失败：" + FileUtils.getMusicDir());
        }
        String dispalySize = FileInfoUtils.FormetFileSize(cache1);
        LogUtils.showLog("dispalySize:"+dispalySize);
    }

    /**
     * 清除文件夹
     * */
    private void clearCache() {
        FileInfoUtils.deleteDir(new File(FileUtils.getMusicDir()));
        getCache();
    }

    /**
     * 清除文件
     * */
    private void deleteFile(String filePath){
        FileInfoUtils.deleteFile(filePath);
    }

    private ProgressDialog progressDialog;

    public void showProgressDialog(String msg) {
//        progressDialog = new ProgressDialog(context,ProgressDialog.STYLE_HORIZONTAL);//黑色
        progressDialog = new ProgressDialog(mContext, ProgressDialog.STYLE_SPINNER);//默认
        progressDialog.setMessage(msg);
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//单独设置这个才会有进度条
//        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void dismissProgresDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
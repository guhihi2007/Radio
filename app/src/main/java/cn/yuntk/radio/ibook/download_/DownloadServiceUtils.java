package cn.yuntk.radio.ibook.download_;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.api.BaseOkhttp;
import cn.yuntk.radio.ibook.bean.BookDetailBean;
import cn.yuntk.radio.ibook.bean.DownloadBookInfo;
import cn.yuntk.radio.ibook.bean.DownloadMusicInfo;
import cn.yuntk.radio.ibook.common.Api;
import cn.yuntk.radio.ibook.dbdao.DownloadBookInfoManager;
import cn.yuntk.radio.ibook.dbdao.DownloadMusicInfoManager;
import cn.yuntk.radio.ibook.executor.IExecutor;
import cn.yuntk.radio.ibook.util.FileUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.NetworkUtils;

public abstract class DownloadServiceUtils implements IExecutor<List<DownloadMusicInfo>> {

    private List<BookDetailBean.UrlListBean> urls;
    private BookDetailBean bookDetailBean;
    private Context context;
    private int startPosition;
    private int endPosition;

    public DownloadServiceUtils(BookDetailBean bookDetailBean, Context context, int startPosition, int endPosition) {
        this.bookDetailBean = bookDetailBean;
        this.context = context;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    @Override
    public void execute() {
        checkNetwork();
    }

    private void checkNetwork() {
        if (NetworkUtils.isActiveNetworkMobile(context)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.tips);
            builder.setMessage(R.string.download_tips);
            builder.setPositiveButton(R.string.download_tips_sure, (dialog, which) -> downloadLists());
            builder.setNegativeButton(R.string.cancel, null);
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        } else {
            downloadLists();
        }
    }

    public void downloadLists(){
        try{
            if (bookDetailBean == null ||bookDetailBean.getUrl_list() == null||bookDetailBean.getUrl_list().size() == 0){
                return;
            }
            urls = bookDetailBean.getUrl_list().subList(startPosition,endPosition);
            LogUtils.s("Frist_Title:"+urls.get(0).getName());
            LogUtils.s("Last_Title:"+urls.get(urls.size()-1).getName());
            if (urls.size() == 0){
                return;
            }
            if (urls.size()>100){
                onExecuteFail(new Exception(context.getString(R.string.download_limit)));
                return;
            }

        }catch (Exception e){
            return;
        }

        BookDetailBean.UrlListBean fristChapter= urls.get(0);

        final String data_id = fristChapter.getUrl();
        final String svv_id = bookDetailBean.getSvid()+"";

        if (!TextUtils.isEmpty(data_id)&&!TextUtils.isEmpty(svv_id)){
            Map<String,Object> map = new HashMap<>();
            map.put("data_id",data_id);
            map.put("svv_id",svv_id);
            //String url = "http://177h.tt56w.com/%E9%80%9A%E4%BF%97%E5%B0%8F%E8%AF%B4%2F%E9%BB%84%E9%87%91%E7%9E%B3%2F002.mp3";
            BaseOkhttp.getInstance().post(Api.APP_BASE_URL+ Api.BOOK_MP3_URL,map, new BaseOkhttp.RequestCallback() {
                @Override
                public void onSuccess(String response) {
                    if (response.isEmpty()) {
                        onFailure("",null);
                        return;
                    }
                    try {
                        JSONObject object = new JSONObject(response);
                        String url_id = object.getString("url_id");
                        String prefix_str =url_id.substring(0,url_id.indexOf("com:8000/"))+"com:8000/";
                        gotoSerivce(prefix_str);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        onFailure("",e);
                    }
                }

                @Override
                public void onFailure(String msg, Exception e) {
                    onExecuteFail(e);
                }
            });
        }
    }

    private void gotoSerivce(String prefix_str){
        Iterator iterator = urls.iterator();

        DownloadBookInfo bookInfo = new DownloadBookInfo();
        List<DownloadMusicInfo> chapters = new ArrayList<DownloadMusicInfo>();

        bookInfo.setId(bookDetailBean.getHtml_id()+"");
        bookInfo.setCon(bookDetailBean.getCon());
        bookInfo.setTitle(bookDetailBean.getTitle());
        bookInfo.setType(bookDetailBean.getType());
        bookInfo.setZztt(bookDetailBean.getZztt());
        int posi = startPosition;
        while (iterator.hasNext()){
            posi++;
            BookDetailBean.UrlListBean urlListBean = (BookDetailBean.UrlListBean)iterator.next();
            if (urlListBean.getIs_download().equals("0")) {//如果是未下载 就加入下载列表
                String fileName = FileUtils.getMp3FileName(bookDetailBean.getTitle(), urlListBean.getName());
                String musicAbsPath = FileUtils.getMusicDir().concat(fileName);

                DownloadMusicInfo str = new DownloadMusicInfo(
                        bookDetailBean.getTitle(),
                        bookDetailBean.getType(),
                        urlListBean.getName(),
                        bookDetailBean.getHtml_id()+"",
                        urlListBean.getUrl(),
                        musicAbsPath,
                        prefix_str+urlListBean.getUrl()
                );
                str.setMark1("0");//等待下载
                str.setTaskid(posi);//对应列表的位置
//            Intent intent = new Intent(context, DownloadService.class);
//            intent.putExtra("info", str);
//            context.startService(intent);
                chapters.add(str);
                }
            }
        insertBook_Chapter(bookInfo,chapters);
    }

    public void insertBook_Chapter(DownloadBookInfo str,List<DownloadMusicInfo> chapters) {
        LogUtils.showLog("添加数据库" + str.getTitle());
        if (chapters != null && chapters.size() != 0) {
            DownloadBookInfo bookInfo = DownloadBookInfoManager.getInstance(context).queryListDB(str.getId());
            if (bookInfo == null) {
                DownloadBookInfoManager.getInstance(context).insertDB(str);
            }
            DownloadMusicInfoManager.getInstance(context).insertListToDB(chapters);
        }
        onExecuteSuccess(chapters);
    }
}
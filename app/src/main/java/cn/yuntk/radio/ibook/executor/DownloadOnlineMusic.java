package cn.yuntk.radio.ibook.executor;

import android.app.Activity;
import android.text.TextUtils;

import cn.yuntk.radio.ibook.api.BaseOkhttp;
import cn.yuntk.radio.ibook.bean.OnlineMusic;
import cn.yuntk.radio.ibook.common.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 下载音乐
 * Created by wcy on 2016/1/3.
 */
public abstract class DownloadOnlineMusic extends DownloadMusic {
    private OnlineMusic mOnlineMusic;

    public DownloadOnlineMusic(Activity activity, OnlineMusic onlineMusic) {
        super(activity);
        mOnlineMusic = onlineMusic;
    }

    @Override
    protected void download() {
        final String artist = mOnlineMusic.getArtist_name();
        final String book_title = mOnlineMusic.getAlbum_title();
        final String book_type = mOnlineMusic.getBook_type();
        final String title = mOnlineMusic.getTitle();
        final String book_id = mOnlineMusic.getSong_id();
        final String data_id = mOnlineMusic.getData_id();
        final String svv_id = mOnlineMusic.getSvv_id();
        final String file_path = mOnlineMusic.getFile_path();
        // 下载歌词
//        String lrcFileName = FileUtils.getLrcFileName(artist, title);
//        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
//        if (!TextUtils.isEmpty(mOnlineMusic.getLrclink()) && !lrcFile.exists()) {
//            HttpClient.downloadFile(mOnlineMusic.getLrclink(), FileUtils.getLrcDir(), lrcFileName, null);
//        }

        // 下载封面
//        String albumFileName = FileUtils.getAlbumFileName(artist, title);
//        final File albumFile = new File(FileUtils.getAlbumDir(), albumFileName);
//        String picUrl = mOnlineMusic.getPic_big();
//        if (TextUtils.isEmpty(picUrl)) {
//            picUrl = mOnlineMusic.getPic_small();
//        }

//        if (!albumFile.exists() && !TextUtils.isEmpty(picUrl)) {
//            HttpClient.downloadFile(picUrl, FileUtils.getAlbumDir(), albumFileName, null);
//        }

        // 获取歌曲下载链接
//        HttpClient.getMusicDownloadInfo(mOnlineMusic.getSong_id(), new HttpCallback<DownloadInfo>() {
//            @Override
//            public void onSuccess(DownloadInfo response) {
//                if (response == null || response.getBitrate() == null) {
//                    onFail(null);
//                    return;
//                }
//
//                downloadMusic(response.getBitrate().getFile_link(), artist, title, albumFile.getPath());
//                onExecuteSuccess(null);
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                onExecuteFail(e);
//            }
//        });
        if (!TextUtils.isEmpty(data_id)&&!TextUtils.isEmpty(svv_id)&& TextUtils.isEmpty(file_path)){
            Map<String,Object> map = new HashMap<>();
            map.put("data_id",data_id);
            map.put("svv_id",svv_id);
            //String url = "http://177h.tt56w.com/%E9%80%9A%E4%BF%97%E5%B0%8F%E8%AF%B4%2F%E9%BB%84%E9%87%91%E7%9E%B3%2F002.mp3";
            BaseOkhttp.getInstance().post(Api.APP_BASE_URL+Api.BOOK_MP3_URL,map, new BaseOkhttp.RequestCallback() {
                @Override
                public void onSuccess(String response) {
                    if (response.isEmpty()) {
                        onFailure("",null);
                        return;
                    }
                    try {
                        JSONObject object = new JSONObject(response);
                        String url_id = object.getString("url_id");
                        downloadMusic(url_id,book_title,book_type,title,book_id,data_id);
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

}
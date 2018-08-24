package cn.yuntk.radio.ibook.executor;

import android.accounts.NetworkErrorException;
import android.content.Context;

/**
 *
 * Created by wcy on 2016/1/3.
 */
public abstract class PlayOnlineMusic extends PlayMusic implements GetMusicInfoInterface{
    private String url;
    private GetMusicInfo info;

    public PlayOnlineMusic(Context activity) {
        super(activity, 3);
    }

    public PlayOnlineMusic setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    protected void getPlayInfo() {
        // 下载歌词
//        String lrcFileName = FileUtils.getLrcFileName(artist, title);
//        File lrcFile = new File(FileUtils.getLrcDir() + lrcFileName);
//        if (!lrcFile.exists() && !TextUtils.isEmpty(mOnlineMusic.getLrclink())) {
//            downloadLrc(mOnlineMusic.getLrclink(), lrcFileName);
//        } else {
//            mCounter++;
//        }

        // 下载封面
//        String albumFileName = FileUtils.getAlbumFileName(artist, title);
//        File albumFile = new File(FileUtils.getAlbumDir(), albumFileName);
//        String picUrl = mOnlineMusic.getPic_big();
//        if (TextUtils.isEmpty(picUrl)) {
//            picUrl = mOnlineMusic.getPic_small();
//        }
//        if (!albumFile.exists() && !TextUtils.isEmpty(picUrl)) {
//            downloadAlbum(picUrl, albumFileName);
//        } else {
//            mCounter++;
//        }
//        music.setCoverPath(albumFile.getPath());

        // 获取歌曲播放链接
//        HttpClient.getMusicDownloadInfo(mOnlineMusic.getSong_id(), new HttpCallback<DownloadInfo>() {
//            @Override
//            public void onSuccess(DownloadInfo response) {
//                if (response == null || response.getBitrate() == null) {
//                    onFail(null);
//                    return;
//                }
//
//                music.setPath(response.getBitrate().getFile_link());
//                music.setDuration(response.getBitrate().getFile_duration() * 1000);
//                checkCounter();
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                onExecuteFail(e);
//            }
//        });
        info = new GetMusicInfo(this);
        info.execute(url);
    }

    @Override
    public void getDuration(String duration) {
        onExecuteSuccess(duration);
    }
//    private void downloadLrc(String url, String fileName) {
//        HttpClient.downloadFile(url, FileUtils.getLrcDir(), fileName, new HttpCallback<File>() {
//            @Override
//            public void onSuccess(File file) {
//            }
//
//            @Override
//            public void onFail(Exception e) {
//            }
//
//            @Override
//            public void onFinish() {
//                checkCounter();
//            }
//        });
//    }
//
//    private void downloadAlbum(String picUrl, String fileName) {
//        HttpClient.downloadFile(picUrl, FileUtils.getAlbumDir(), fileName, new HttpCallback<File>() {
//            @Override
//            public void onSuccess(File file) {
//            }
//
//            @Override
//            public void onFail(Exception e) {
//            }
//
//            @Override
//            public void onFinish() {
//                checkCounter();
//            }
//        });
//    }

    @Override
    public void getFail(String string) {
    onExecuteFail(new NetworkErrorException());
    }

    public void onDestory(){
        if (info!=null){
            info.cancel(true);
        }
    }
}
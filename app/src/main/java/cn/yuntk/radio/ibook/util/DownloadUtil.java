package cn.yuntk.radio.ibook.util;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadUtil {
    private String TAG = "DownloadUtil";
    private String targetUrl = null;
    private String targetFile = null;
    //多线程下载的线程数量
    private int threadNum;
    private Context mContext;
    //下载文件的大小
    private int fileSize;
    //下载任务是否是暂停状态
    private boolean pause;
    //下载任务是否被删除了
    private boolean delete;
    //下载完成的线程数，当所有线程下载完成时该值等于threadNum
    private int downloadSuccessThread;
    //下载的线程数组
    private DownloadThread[] downloadThreads;

    public DownloadUtil (String targetUrl, String targetFile, int threadNum, Context context) {
        this.targetUrl = targetUrl;
        this.targetFile = targetFile;
        this.threadNum = threadNum;
        downloadThreads = new DownloadThread[threadNum];
        mContext = context;
    }

    public void download() {
        try {
            URL url = new URL(targetUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5000);
            //获取下载文件的大小
            fileSize = conn.getContentLength();
            conn.disconnect();
            Log.d(TAG, "download：fileSize = " + fileSize);
            File file = new File(targetFile);
            if (!file.exists()) {
                file.createNewFile();
                Log.d(TAG, "create file:" + file);
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.setLength(fileSize);
            raf.close();
            //划分每个线程的下载长度
            int currentFileSize = fileSize / threadNum == 0 ? fileSize / threadNum : fileSize / threadNum + 1;
            for (int i=0; i<threadNum; i++) {
                downloadThreads[i] = new DownloadThread(targetUrl, targetFile, currentFileSize * i, currentFileSize);
                downloadThreads[i].start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取线程进度
    public int getDownloadProgress() {
        int downloadLength = 0;
        for (DownloadThread thread : downloadThreads) {
            if (thread != null) downloadLength += thread.length;
        }
        Log.d(TAG, "fileSize = " + fileSize + " downloadLength = " +downloadLength);
        return fileSize > 0 ? downloadLength * 100 / fileSize: 0;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    //下载线程
    private class DownloadThread extends Thread {
        //当前线程下载的长度
        public int length;
        //下载的资源链接
        private String downloadUrl = null;
        //下载内容到file文件中
        private String file;
        //下载的起始位置
        private int startPos;
        //需要下载的长度
        private int currentFileSize;
        //读取网络音乐数据的缓存
        private BufferedInputStream bis;

        public DownloadThread (String downloadUrl, String file, int startPos, int currentFileSize) {
            this.downloadUrl = downloadUrl;
            this.file = file;
            this.startPos = startPos;
            this.currentFileSize = currentFileSize;
        }

        public void run() {
            try {
                HttpURLConnection conn = (HttpURLConnection)new URL(downloadUrl).openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                //获取的输入流数据跳过指定字节数
                is.skip(startPos);
                File downloadFile = new File(file);
                RandomAccessFile raf = new RandomAccessFile(downloadFile, "rwd");
                //对应文件写入位置也跳过指定字节数
                raf.seek(startPos);
                bis = new BufferedInputStream(is);
                int hasRead = 0;
                byte[] buff = new byte[1024*4];
                while (length < currentFileSize && (hasRead = bis.read(buff)) > 0 && !delete) {
                    while (pause){
                        Log.d(TAG, "DownloadUtil pause!");
                    }
                    if (!pause) {
                        //字节读入对应文件
                        raf.write(buff, 0, hasRead);
                        length += hasRead;
                        Log.d(TAG, "read " + hasRead + " bytes");
                    }
                }
                Log.d(TAG, "download success? " + (fileSize == length));
                //关闭资源和链接
                raf.close();
                bis.close();
                if (delete) {
                    boolean isDeleted = downloadFile.delete();
                    Log.d(TAG, "delete file success ? " + isDeleted);
                } else {
                    Log.d(TAG, "run:currentFileSize = " + currentFileSize + " downloadLength = " + length);
                    //当前线程下载完成时，DownloadUtil的downloadSuccessThread值+1
                    downloadSuccessThread++;
                    if (downloadSuccessThread == threadNum) {
                        Log.d(TAG, "download success");
                        Intent notifyIntent = new Intent("action_download_success");
                        notifyIntent.putExtra("url", targetUrl);
                        //给DownloadFragment发送下载完成的广播
                        mContext.sendBroadcast(notifyIntent);
                        scanFileToMedia(targetFile);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //将新下载的歌曲扫描到媒体库中，为后面MusicListFragment添加下拉刷新做铺垫
    public void scanFileToMedia(final String url) {
        new Thread(new Runnable() {
            public void run() {
                MediaScannerConnection.scanFile(mContext, new String[] {url}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.d(TAG, "scan completed : file = " + url);
                            }
                        });
            }

        }).start();
    }

}

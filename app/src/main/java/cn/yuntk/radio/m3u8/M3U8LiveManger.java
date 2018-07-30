package cn.yuntk.radio.m3u8;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.yuntk.radio.m3u8.bean.M3U8;
import cn.yuntk.radio.m3u8.bean.M3U8Ts;
import cn.yuntk.radio.m3u8.bean.OnDownloadListener;
import cn.yuntk.radio.m3u8.bean.OnUpdateM3U8UrlListener;
import cn.yuntk.radio.m3u8.utils.MUtils;

/**
 * 直播管理器
 * Created by HDL on 2018/3/7.
 *
 * @author HDL
 */

public class M3U8LiveManger {
    private static M3U8LiveManger mM3U8LiveManger;
    private ExecutorService executor = Executors.newFixedThreadPool(5);
    private ExecutorService downloadExecutor = Executors.newFixedThreadPool(5);
    private Timer getM3U8InfoTimer;
    private String basePath;
    private OnDownloadListener onDownloadListener;
    private OnUpdateM3U8UrlListener onUpdateM3U8UrlListener;
    /**
     * 当前已经在下完成的大小
     */
    private long curLenght = 0;
    /**
     * 读取超时时间
     */
    private int readTimeout = 30 * 60 * 1000;
    //当前下载完成的文件个数
    private static int curTs = 0;
    //总文件的个数
    private static int totalTs = 0;
    //单个文件的大小
    private static long itemFileSize = 0;
    /**
     * 链接超时时间
     */
    private int connTimeout = 10 * 1000;
    private static final int WHAT_ON_ERROR = 1001;
    private static final int WHAT_ON_PROGRESS = 1002;
    private static final int WHAT_ON_SUCCESS = 1003;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(Looper.getMainLooper()) {//加上Looper.getMainLooper() 是因为Playservice中开启了子线程，new会出错
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_ON_ERROR:
                    if (onUpdateM3U8UrlListener != null)
                        onUpdateM3U8UrlListener.onError((Throwable) msg.obj);
                    if (onDownloadListener != null)
                        onDownloadListener.onError((Throwable) msg.obj);
                    break;
                case WHAT_ON_PROGRESS:
                    if (onDownloadListener != null)
                        onDownloadListener.onDownloading(itemFileSize, totalTs, curTs);
                    break;
                case WHAT_ON_SUCCESS:
                    if (netSpeedTimer != null) {
                        netSpeedTimer.cancel();
                    }
                    if (onDownloadListener != null)
                        onDownloadListener.onSuccess();
                    break;
            }
        }
    };
    /**
     * 定时任务
     */
    private Timer netSpeedTimer;
    /**
     * 文件保存的目录
     */
    private String tempDir = "/sdcard/tempDir/" + System.currentTimeMillis();

    private String saveDir = "/sdcard/11/" + System.currentTimeMillis() + ".ts";

    /**
     * 已经下载的文件列表
     */
    private List<File> downloadedFileList = new ArrayList<>();

    private M3U8LiveManger() {
    }

    public static M3U8LiveManger getInstance() {
        if (mM3U8LiveManger == null) {
            synchronized (M3U8LiveManger.class) {
                if (mM3U8LiveManger == null) {
                    mM3U8LiveManger = new M3U8LiveManger();
                }
            }
        }
        return mM3U8LiveManger;
    }

    /**
     * 获取文件临时保存的目录
     *
     * @return
     */
    public String getTempDir() {
        return tempDir;
    }

    /**
     * 设置文件临时保存的目录
     *
     * @param tempDir
     */
    public M3U8LiveManger setTempDir(String tempDir) {
        this.tempDir = tempDir;
        return this;
    }

    /**
     * 文件保存的目录（包含文件名字，必须是ts结尾）
     */

    public String getSaveDir() {
        return saveDir;
    }


    /**
     * 设置需要将缓存文件保存的位置（包含文件名字，必须是ts结尾）
     *
     * @param saveDir
     */
    public M3U8LiveManger setSaveDir(String saveDir) {
        this.saveDir = saveDir;
        return this;
    }

    /**
     * 缓存视频中
     *
     * @param url
     * @param onDownloadListener1
     */
    public void caching(String url, OnDownloadListener onDownloadListener1) {
        this.onDownloadListener = onDownloadListener1;
        onDownloadListener.onStart();
        netSpeedTimer = new Timer();
        netSpeedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                onDownloadListener.onProgress(curLenght);
            }
        }, 0, 1000);
        startUpdateM3U8Info(url);
    }

    /**
     * 开始下载
     */
    private void startDownloadM3U8() {
        final File dir = new File(tempDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        for (final M3U8Ts m3U8Ts : m3U8TsList) {
            downloadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    File file = new File(dir, m3U8Ts.getFileName());
                    if (!file.exists()) {
                        FileOutputStream fos = null;
                        InputStream inputStream = null;
                        try {
                            Log.i("hdltag", "run(M3U8DownloadTask.java:278):" + m3U8Ts.getFile());
                            String urlPath;
                            if ("http".equals(m3U8Ts.getFile().substring(0, 4))) {
                                urlPath = m3U8Ts.getFile();
                            } else {
                                urlPath = basePath + m3U8Ts.getFile();
                            }
                            URL url = new URL(urlPath);

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setConnectTimeout(connTimeout);
                            conn.setReadTimeout(readTimeout);
                            if (conn.getResponseCode() == 200) {
                                inputStream = conn.getInputStream();
                                fos = new FileOutputStream(file);//会自动创建文件
                                int len = 0;
                                byte[] buf = new byte[8 * 1024 * 1024];
                                while ((len = inputStream.read(buf)) != -1) {
                                    curLenght += len;
                                    fos.write(buf, 0, len);//写入流中
                                }
                                downloadedFileList.add(file);
                                Log.i("hdltag", "run(M3U8LiveManger.java:138):下完一个了" + file.getAbsolutePath());
//                                Log.e("hdltag", "run(M3U8DownloadTask.java:188):进度\t" + totalTs + "-----" + curTs);
                            } else {
                                handlerError(new Throwable(String.valueOf(conn.getResponseCode())));
                            }
                        } catch (MalformedURLException e) {
//                            e.printStackTrace();
                            handlerError(e);
                        } catch (IOException e) {
//                            e.printStackTrace();
                            handlerError(e);
                        } finally {//关流
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
//                                    e.printStackTrace();
                                }
                            }
                            if (fos != null) {
                                try {
                                    fos.close();
                                } catch (IOException e) {
//                                    e.printStackTrace();
                                }
                            }
                        }
                        curTs++;
                        if (curTs == 3) {
                            itemFileSize = file.length();
                        }
                        mHandler.sendEmptyMessage(WHAT_ON_PROGRESS);
                    }
                }
            });

        }
    }

    /**
     * 通知异常
     *
     * @param e
     */
    private void handlerError(Throwable e) {
        if (!"Task running".equals(e.getMessage())) {
            stop();
        }
        //不提示被中断的情况
        if ("thread interrupted".equals(e.getMessage())) {
            return;
        }
        Message msg = mHandler.obtainMessage();
        msg.obj = e;
        msg.what = WHAT_ON_ERROR;
        mHandler.sendMessage(msg);
    }

    /**
     * 请求到的所有下载列表
     */
    private List<M3U8Ts> m3U8TsList = new ArrayList<>();
    /**
     * 请求到的所有下载地址  by gpp
     */
    private List<String> m3U8UrlList = new ArrayList<>();

    /**
     * 定时获取m3u8信息
     *
     * @param url
     */
    private void startUpdateM3U8Info(final String url) {
        if (getM3U8InfoTimer != null) {
            getM3U8InfoTimer.cancel();
            getM3U8InfoTimer = null;
        }
        getM3U8InfoTimer = new Timer();
        getM3U8InfoTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            M3U8 m3u8 = MUtils.parseIndex(url);
                            if (m3u8 != null && m3u8.getTsList().size() > 0) {
                                basePath = m3u8.getBasepath();
                                addTs(m3u8.getTsList());
//                                m3U8TsList.addAll(m3u8.getTsList());
                            }
//                            Log.e("hdltag", "run(M3U8LiveManger.java:59):" + m3u8);
                        } catch (IOException e) {
                            e.printStackTrace();
                            handlerError(e);
                        }
                    }
                });
            }
        }, 0, 2000);
    }

    //更新M3U8最新地址
    public void updateM3U8Url(final String url, OnUpdateM3U8UrlListener onUpdateM3U8UrlListener1) {
        this.onUpdateM3U8UrlListener = onUpdateM3U8UrlListener1;
        onUpdateM3U8UrlListener.onStart();
        if (getM3U8InfoTimer != null) {
            getM3U8InfoTimer.cancel();
            getM3U8InfoTimer = null;
        }
        getM3U8InfoTimer = new Timer();
        getM3U8InfoTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            M3U8 m3u8 = MUtils.parseIndex(url);
                            if (m3u8 != null && m3u8.getTsList().size() > 0) {
                                basePath = m3u8.getBasepath();
                                addListUrl(m3u8.getTsList());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            handlerError(e);
                        }
                    }
                });
            }
        }, 0, 2000);
    }

    //更新保存list
    private synchronized void addListUrl(List<M3U8Ts> tsList) {
        List<M3U8Ts> tempTsList = new ArrayList<>();
        for (M3U8Ts m3U8Ts : tsList) {
            boolean isExisted = false;
            for (M3U8Ts mTs : m3U8TsList) {
                if (mTs.getFile().equals(m3U8Ts.getFile())) {
                    isExisted = true;
                    break;
                }
            }
            if (!isExisted) {
                tempTsList.add(m3U8Ts);
                String urlPath;

                if ("http".equals(m3U8Ts.getFile().substring(0, 4))) {
                    urlPath = m3U8Ts.getFile();
                } else {
                    urlPath = basePath + m3U8Ts.getFile();
                }
                m3U8UrlList.add(urlPath);
                onUpdateM3U8UrlListener.onUpdateAddress(urlPath);
            }
        }
        if (tempTsList.size() > 0) {
            m3U8TsList.addAll(tempTsList);
        }

    }

    /**
     * 添加到下载列表，做去重处理
     *
     * @param tsList
     */
    private synchronized void addTs(List<M3U8Ts> tsList) {
        List<M3U8Ts> tempTsList = new ArrayList<>();
        for (M3U8Ts m3U8Ts : tsList) {
            boolean isExisted = false;
            for (M3U8Ts mTs : m3U8TsList) {
                if (mTs.getFile().equals(m3U8Ts.getFile())) {
                    isExisted = true;
                    break;
                }
            }
            if (!isExisted) {
                tempTsList.add(m3U8Ts);
            }
        }
        if (tempTsList.size() > 0) {
            m3U8TsList.addAll(tempTsList);
        }
        Log.i("hdltag", "addTs(M3U8LiveManger.java:98):有几个了 ---->" + m3U8TsList.size());
        for (M3U8Ts m3U8Ts : m3U8TsList) {
            Log.i("hdltag", "addTs(M3U8LiveManger.java:101):" + m3U8Ts.getFile());
        }
        //有更新，通知下载
        startDownloadM3U8();
    }

    /**
     * 停止任务
     */
    public void stop() {
        Log.i("hdltag", "stop(M3U8LiveManger.java:106):调用停止了");
        if (getM3U8InfoTimer != null) {
            getM3U8InfoTimer.cancel();
            getM3U8InfoTimer = null;
        }
        if (executor != null) {
            if (!executor.isShutdown()) {
                executor.shutdownNow();
            }
        }
    }


    /**
     * 获取从开始到现在的视频
     */
    public String getCurrentTs() {
        try {
            MUtils.merge(downloadedFileList, saveDir);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        Log.i("hdltag", "getCurrentTs(M3U8LiveManger.java:287):已保存至 " + saveDir);
        return saveDir;
    }
}

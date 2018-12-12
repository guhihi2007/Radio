package cn.yuntk.radio.ibook.download_;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.bean.DownloadMusicInfo;
import cn.yuntk.radio.ibook.dbdao.DownloadMusicInfoManager;
import cn.yuntk.radio.ibook.util.FileUtils;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;


/** 批量下载后台服务*/
public class DownloadService2 extends Service {

    private List<SingleDownloadListener.DownloadProgressBarInterface> progressBarInterfaces = new ArrayList<SingleDownloadListener.DownloadProgressBarInterface>();
    private FileDownloadQueueSet queueSet;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("DemoLog", "DownloadService -> onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            String type = intent.getStringExtra("type");
            String book_id = intent.getStringExtra("book_id");
            List<DownloadMusicInfo> chapters = (List<DownloadMusicInfo>) intent.getSerializableExtra("datas_book");
            gotToFileDownloader(chapters,type);
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
//        return mybinder;
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("DemoLog", "DownloadService -> onDestroy");
    }

    //下载任务执行
    private void gotToFileDownloader(List<DownloadMusicInfo> chapters, String cOrb){
        // 第一种方式 :

//for (String url : URLS) {
//    FileDownloader.getImpl().create(url)
//            .setCallbackProgressTimes(0) // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
//            .setListener(queueTarget)
//            .asInQueueTask()
//            .enqueue();
//}

//if(serial){
        // 串行执行该队列
//    FileDownloader.getImpl().start(queueTarget, true);
// }

// if(parallel){
        // 并行执行该队列
//    FileDownloader.getImpl().start(queueTarget, false);
//}

// 第二种方式:
//        if (((GlobalMonitor)FileDownloadMonitor.getMonitor()).getMarkStart()!=0 ||
//                ((GlobalMonitor)FileDownloadMonitor.getMonitor()).getMarkOver()!=0){
//            showToast_("请等待任务下载完成在开始新的下载任务");
//        }else {
//
//        }
        queueSet = new FileDownloadQueueSet(queueTarget);

        List<BaseDownloadTask> tasks = new ArrayList<>();
        for (int i = 0; i < chapters.size(); i++) {
            DownloadMusicInfo musicInfo = chapters.get(i);
            String fileName = FileUtils.getMp3FileName(musicInfo.getBook_title(), musicInfo.getTitle());
            String musicAbsPath = FileUtils.getMusicDir().concat(fileName);
            tasks.add(FileDownloader
                    .getImpl()
                    .create(chapters.get(i).getPathOnline())
                    .setTag(musicInfo)
                    .setPath(musicAbsPath)
            );
        }

        //        queueSet.disableCallbackProgressTimes(); // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.

                // 所有任务在下载失败的时候都自动重试一次

        if (cOrb.equals("parallel")) {
            // 并行执行该任务队列
            queueSet.downloadTogether(tasks);
            // 如果你的任务不是一个List，可以考虑使用下面的方式，可读性更强
//            queueSet.downloadTogether(
//            FileDownloader.getImpl().create(url).setPath(...),
//            FileDownloader.getImpl().create(url).setPath(...),
//            FileDownloader.getImpl().create(url).setSyncCallback(true)
//    );
        }else if (cOrb.equals("serial")) {
            // 串行执行该任务队列
            queueSet.downloadSequentially(tasks);
            // 如果你的任务不是一个List，可以考虑使用下面的方式，可读性更强
//              queueSet.downloadSequentially(
//              FileDownloader.getImpl().create(url).setPath(...),
//              FileDownloader.getImpl().create(url).addHeader(...,...),
//              FileDownloader.getImpl().create(url).setPath(...)
//      );
        }else {
            queueSet.downloadTogether(tasks);
        }
        // 由于是队列任务, 这里是我们假设了现在不需要每个任务都回调`FileDownloadListener#progress`, 我们只关系每个任务是否完成, 所以这里这样设置可以很有效的减少ipc.
        // queueSet.disableCallbackProgressTimes();
        // 所有任务在下载失败的时候都自动重试一次
        queueSet.setAutoRetryTimes(1);
        // 最后你需要主动调用start方法来启动该Queue
        queueSet.start();
    }

    //    下载监听
   private FileDownloadListener queueTarget = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            if (task.getListener()==null)
                return;
            DownloadMusicInfo musicInfo = (DownloadMusicInfo) task.getTag();
            LogUtils.showLog("FileDownloadListener:pending:BaseDownloadTask:"+musicInfo.getTitle());
            LogUtils.showLog("FileDownloadListener:pending:BaseDownloadTask:"+musicInfo.getPathOnline());

            progressBarInterfaces.clear();
            progressBarInterfaces.addAll(SingleDownloadListener.getInstance().getProgressBarInterfaces());

            if (progressBarInterfaces!=null&&progressBarInterfaces.size()!=0){
                for (SingleDownloadListener.DownloadProgressBarInterface barInterface:progressBarInterfaces){
                    barInterface.pending(task,soFarBytes,totalBytes);
                }
            }

        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            DownloadMusicInfo musicInfo = (DownloadMusicInfo) task.getTag();
            LogUtils.showLog("FileDownloadListener:connected");
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            DownloadMusicInfo musicInfo = (DownloadMusicInfo) task.getTag();
            LogUtils.showLog("FileDownloadListener:progress:soFarBytes:"+soFarBytes+":totalBytes:"+totalBytes);

            progressBarInterfaces.clear();
            progressBarInterfaces.addAll(SingleDownloadListener.getInstance().getProgressBarInterfaces());
            try{
            if (progressBarInterfaces!=null&&progressBarInterfaces.size()!=0){
                for (SingleDownloadListener.DownloadProgressBarInterface barInterface:progressBarInterfaces){
                    barInterface.progress(task,soFarBytes,totalBytes);
                }
            }
            }catch (Exception e){

            }
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
            DownloadMusicInfo musicInfo = (DownloadMusicInfo) task.getTag();
            LogUtils.showLog("FileDownloadListener:blockComplete"+musicInfo.getTitle());

            progressBarInterfaces.clear();
            progressBarInterfaces.addAll(SingleDownloadListener.getInstance().getProgressBarInterfaces());

            if (progressBarInterfaces!=null&&progressBarInterfaces.size()!=0){
                for (SingleDownloadListener.DownloadProgressBarInterface barInterface:progressBarInterfaces){
                    barInterface.blockComplete(task);
                }
            }
        }

        @Override
        protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
            DownloadMusicInfo musicInfo = (DownloadMusicInfo) task.getTag();
            LogUtils.showLog("FileDownloadListener:retry:ex:"+ex.getMessage()+":retryingTimes:"+retryingTimes);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            DownloadMusicInfo musicInfo = (DownloadMusicInfo) task.getTag();
            LogUtils.showLog("FileDownloadListener:completed:"+musicInfo.getTitle()+"::"+task.isSyncCallback());
            try {
            if (progressBarInterfaces!=null&&progressBarInterfaces.size()!=0){

                    for (SingleDownloadListener.DownloadProgressBarInterface barInterface:progressBarInterfaces){
                        barInterface.completed(task);
                    }
            }
            }catch (Exception e){

            }
            updataBook_Chapter(musicInfo);
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            DownloadMusicInfo musicInfo = (DownloadMusicInfo) task.getTag();
            LogUtils.showLog("FileDownloadListener:paused"+musicInfo.getTitle()+"::"+task.isSyncCallback());
            progressBarInterfaces.clear();
            progressBarInterfaces.addAll(SingleDownloadListener.getInstance().getProgressBarInterfaces());
            if (progressBarInterfaces!=null&&progressBarInterfaces.size()!=0){
                for (SingleDownloadListener.DownloadProgressBarInterface barInterface:progressBarInterfaces){
                    barInterface.paused(task,soFarBytes,totalBytes);
                }
            }
            deleteBook_Chapter(musicInfo,"paused");
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            DownloadMusicInfo musicInfo = (DownloadMusicInfo) task.getTag();
            LogUtils.showLog("FileDownloadListener:error:"+musicInfo.getTitle());
            LogUtils.showLog("FileDownloadListener:error:"+e.getMessage());

            progressBarInterfaces.clear();
            progressBarInterfaces.addAll(SingleDownloadListener.getInstance().getProgressBarInterfaces());

            if (progressBarInterfaces!=null&&progressBarInterfaces.size()!=0){
                for (SingleDownloadListener.DownloadProgressBarInterface barInterface:progressBarInterfaces){
                    barInterface.error(task,e);
                }
            }
            deleteBook_Chapter(musicInfo,"error");
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            DownloadMusicInfo musicInfo = (DownloadMusicInfo) task.getTag();
            LogUtils.showLog("FileDownloadListener:warn:"+musicInfo.getTitle());

            progressBarInterfaces.clear();
            progressBarInterfaces.addAll(SingleDownloadListener.getInstance().getProgressBarInterfaces());

            if (progressBarInterfaces!=null&&progressBarInterfaces.size()!=0){
                for (SingleDownloadListener.DownloadProgressBarInterface barInterface:progressBarInterfaces){
                    barInterface.warn(task);
                }
            }
            deleteBook_Chapter(musicInfo,"warn");
        }
    };

    //  更新数据库
    public void updataBook_Chapter(DownloadMusicInfo info){
        LogUtils.showLog("FileDownloadListener 成功 更新："+info.getTitle());
        info.setMark1("1");
        DownloadMusicInfoManager.getInstance(this).updateBtn(info);
        EventBus.getDefault().post(info);
//        showToast_(info.getTitle()+"下载成功");
        EventBus.getDefault().post(info.getTitle());//通知下载页面刷新
    }

    //  删除数据库
    public void deleteBook_Chapter(DownloadMusicInfo info,String status){
        LogUtils.showLog("FileDownloadListener:失败 删除："+info.getTitle());
        info.setMark1("2");
        DownloadMusicInfoManager.getInstance(this).deleteBtn(info);
        if (status.equals("error")){
            showToast_(info.getTitle()+"下载失败");
        }
    }

    public void showToast_(String string){
        XApplication.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                ToastUtil.showShortToast(string);
            }
        });
    }

}
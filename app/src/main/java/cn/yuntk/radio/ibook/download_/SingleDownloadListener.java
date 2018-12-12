package cn.yuntk.radio.ibook.download_;

import com.liulishuo.filedownloader.BaseDownloadTask;

import java.util.ArrayList;
import java.util.List;

/**下载任务 监听器*/
public class SingleDownloadListener {

    private static SingleDownloadListener listener;

    public static SingleDownloadListener getInstance(){
        if (listener == null){
            listener = new SingleDownloadListener();
        }
        return listener;
    }

    private SingleDownloadListener() {
    }

    private List<DownloadProgressBarInterface> progressBarInterfaces = new ArrayList<DownloadProgressBarInterface>();

    public void addDownloadListener(DownloadProgressBarInterface barInterface){
        if (barInterface!=null){
            progressBarInterfaces.add(barInterface);
        }
    }

    public void removeDownloadListener(DownloadProgressBarInterface barInterface){
        if (barInterface!=null&&progressBarInterfaces.contains(barInterface)){
            progressBarInterfaces.remove(barInterface);
        }
    }

    public List<DownloadProgressBarInterface> getProgressBarInterfaces() {
        return progressBarInterfaces;
    }

    public void setProgressBarInterfaces(List<DownloadProgressBarInterface> progressBarInterfaces) {
        this.progressBarInterfaces = progressBarInterfaces;
    }

    //  进度条回调接口
    public interface DownloadProgressBarInterface{
        void pending(BaseDownloadTask task, int soFarBytes, int totalBytes);
        void progress(BaseDownloadTask task, int soFarBytes, int totalBytes);
        void blockComplete(BaseDownloadTask task);
        void completed(BaseDownloadTask task);
        void paused(BaseDownloadTask task, int soFarBytes, int totalBytes);
        void error(BaseDownloadTask task, Throwable e);
        void warn(BaseDownloadTask task);
    }
}

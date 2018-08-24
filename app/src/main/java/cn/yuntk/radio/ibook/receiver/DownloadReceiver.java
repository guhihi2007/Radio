package cn.yuntk.radio.ibook.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import cn.yuntk.radio.R;
import cn.yuntk.radio.ibook.XApplication;
import cn.yuntk.radio.ibook.bean.DownloadMusicInfo;
import cn.yuntk.radio.ibook.common.Constants;
import cn.yuntk.radio.ibook.service.AppCache;
import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.SharedPreferencesUtil;
import cn.yuntk.radio.ibook.util.StringUtils;
import cn.yuntk.radio.ibook.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 下载完成广播接收器
 * Created by hzwangchenyan on 2015/12/30.
 */
public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        DownloadMusicInfo downloadMusicInfo = AppCache.get().getDownloadList().get(id);

        if (downloadMusicInfo != null) {
            ToastUtil.showToast(context.getString(R.string.download_success, downloadMusicInfo.getTitle()));
            saveBookInfo(downloadMusicInfo);
            // 由于系统扫描音乐是异步执行，因此延迟刷新音乐列表
            XApplication.getMainThreadHandler().postDelayed(() -> {
                if (!StringUtils.isEmpty(downloadMusicInfo.getBook_id())){
                    EventBus.getDefault().post(downloadMusicInfo.getBook_id());//刷新下载列表
                    EventBus.getDefault().post(downloadMusicInfo);//详情页面以及播放列表
                }
            }, 500);
        }
    }

    private void saveBookInfo(DownloadMusicInfo info){

        List<DownloadMusicInfo> infos = new ArrayList<DownloadMusicInfo>();

        String json =  SharedPreferencesUtil.getInstance().getString(Constants.BOOK_DOWNLOAD_RECORD1);
        if (!StringUtils.isEmpty(json)){
            Gson gson = new Gson();
            infos.addAll(gson.fromJson(json,new TypeToken<List<DownloadMusicInfo>>(){}.getType()));
        }
        infos.add(info);
        StringBuffer downloadRecord = new StringBuffer();
        if (infos.size()>1){
            for (int i=0;i<infos.size();i++){
                if (i == 0){
                    downloadRecord.append("["+infos.get(0)+",");
                }else if (i==(infos.size()-1)){
                    downloadRecord.append(infos.get(i)+"]");
                }else {
                    downloadRecord.append(infos.get(i)+",");
                }
            }
        }else if (infos.size()==1){
            downloadRecord.append("["+infos.get(0)+"]");
        }else {
            downloadRecord.append("[]");
        }
        LogUtils.showLog("downloadRecord:"+downloadRecord.toString());
        SharedPreferencesUtil.getInstance().putString(Constants.BOOK_DOWNLOAD_RECORD1,downloadRecord.toString());
    }
}
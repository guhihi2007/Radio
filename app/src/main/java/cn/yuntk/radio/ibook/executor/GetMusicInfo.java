package cn.yuntk.radio.ibook.executor;

import android.os.AsyncTask;

import cn.yuntk.radio.ibook.util.LogUtils;
import cn.yuntk.radio.ibook.util.PlayUtils;

/*得到音频信息*/
public class GetMusicInfo extends AsyncTask<String,Void,String> {

    private GetMusicInfoInterface getMusicInfoInterface;

    public GetMusicInfo(GetMusicInfoInterface getMusicInfoInterface) {
        this.getMusicInfoInterface = getMusicInfoInterface;
    }

    @Override
    protected String doInBackground(String... strings) {
        String duration = PlayUtils.getRingDuring(strings[0]);
        if (isCancelled()) {
            return "cancel";
        }
        return duration;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        getMusicInfoInterface.getDuration(s);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        getMusicInfoInterface.getDuration("60000");
        LogUtils.showLog("异步任务被取消:");
    }

    @Override
    protected void onCancelled(String s) {//此函数表示任务关闭 返回执行结果 有可能为null
        super.onCancelled(s);
        LogUtils.showLog("异步任务被取消S:" + s);
    }
}
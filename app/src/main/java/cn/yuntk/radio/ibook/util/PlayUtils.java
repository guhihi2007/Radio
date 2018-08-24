package cn.yuntk.radio.ibook.util;

import android.util.Log;

import java.util.HashMap;

public class PlayUtils {
    //    获取歌曲时间
    public static String getRingDuring(String mUri){
        String duration="0";
        android.media.MediaMetadataRetriever mmr = new android.media.MediaMetadataRetriever();
        LogUtils.showLog("getRingDuring:"+SystemUtils.formatTime("mm:ss", System.currentTimeMillis()));
        try {
            if (mUri != null) {
                HashMap<String, String> headers = null;
                if (headers == null) {
                    headers = new HashMap<String, String>();
                    headers.put("User-Agent", "Mozilla/5.0 (Linux; U; Android 4.4.2; zh-CN; MW-KW-001 Build/JRO03C) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 UCBrowser/1.0.0.001 U4/0.8.0 Mobile Safari/533.1");
                }
                mmr.setDataSource(mUri, headers);
            }
            duration = mmr.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_DURATION);
            LogUtils.showLog("getRingDuring:"+SystemUtils.formatTime("mm:ss", System.currentTimeMillis()));
        } catch (Exception ex) {
            LogUtils.showLog("getRingDuring:"+ex.toString());
        } finally {
            mmr.release();
        }
        Log.e("ryan","duration "+duration);
        return duration+"";
    }
}

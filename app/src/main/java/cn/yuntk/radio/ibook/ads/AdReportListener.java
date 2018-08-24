package cn.yuntk.radio.ibook.ads;

/**
 * Created by Gpp on 2018/1/20.
 */

interface AdReportListener {
    void onNoAD(String eventID, String source, String typeName);

    void onShowAD(String eventID, String source, String typeName);

    void onFailedAD(int errorCode, AD.AdOrigin origin);

    void onClickAD(String eventID, String source, String typeName);

    void onClosedAD();
}

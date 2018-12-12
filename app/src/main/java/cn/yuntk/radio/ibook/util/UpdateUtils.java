package cn.yuntk.radio.ibook.util;


import android.content.Context;

import com.tencent.bugly.beta.Beta;

/**
 * @author
 *用于自动更新功能
 */
public class UpdateUtils {

//    private IFlytekUpdate updManager;
    private Context context;
    private static UpdateUtils instance;

    public static UpdateUtils getInstance(Context mContext) {
        if (instance == null){
            instance = new UpdateUtils(mContext);
        }

        return instance;
    }

    public UpdateUtils(Context context) {
        // TODO Auto-generated constructor stub
        this.context=context;
    }

    public void checkUpdate(boolean isManual,boolean isSilence) {
          Beta.checkUpgrade(isManual,isSilence);
//        参数1：isManual 用户手动点击检查，非用户点击操作请传false
//        参数2：isSilence 是否显示弹窗等交互，[true:没有弹窗和toast] [false:有弹窗或toast]
    }

//
//    private void init(){
//        updManager = IFlytekUpdate.getInstance(context);
//        updManager.setDebugMode(true);
//        updManager.setParameter(UpdateConstants.EXTRA_WIFIONLY, "true");
//        // 设置通知栏icon，默认使用SDK默认
//        updManager.setParameter(UpdateConstants.EXTRA_NOTI_ICON, "false");
//        updManager.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_DIALOG);
//    }
//    /**
//     * 如果是主动请求(如更多界面)，则initiatively传入true
//     * @param initiatively
//     */
//    public void update(boolean initiatively){
////        if(initiatively)
////            updManager.autoUpdate(context, updateListener);
////        else
////            updManager.autoUpdate(context, null);
//
//        updManager.autoUpdate(context, (i, updateInfo) -> {
//            LogUtils.showLog("onResult:"+i);
//            if(i == UpdateErrorCode.OK && updateInfo!= null) {
//                if(updateInfo.getUpdateType() == UpdateType.NoNeed) {
//                    if (!initiatively){
//                        //首页检查更新 不需要吐司
//                        return;
//                    }
//                    //取得当前版本
//                    String mVersion=PackageUtils.getVersionName(context);
//                    XApplication.getMainThreadHandler().post(new Runnable() {
//                        @Override
//                        public void run() {
//                            LogUtils.showLog("已是最新版本"+mVersion);
//                            ToastUtil.showToast("当前版本为:"+mVersion+",已经是最新版本！");
//                        }
//                    });
//                    return;
//
//                }
//                updManager.showUpdateInfo(context, updateInfo);
//            } else {
//                XApplication.getMainThreadHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        LogUtils.showLog("请求更新失败:errorcode:"+i);
//                        ToastUtil.showToast("请求版本失败！");
//                    }
//                });
//            }
//        });
//    }
//
//    //升级版本
//    private IFlytekUpdateListener updateListener = new IFlytekUpdateListener() {
//        @Override
//        public void onResult(int errorcode, UpdateInfo result) {
//            LogUtils.showLog("onResult:"+errorcode);
//            if(errorcode == UpdateErrorCode.OK && result!= null) {
//                if(result.getUpdateType() == UpdateType.NoNeed) {
//
//                    //取得当前版本
//                    String mVersion=PackageUtils.getVersionName(context);
//                    XApplication.getMainThreadHandler().post(new Runnable() {
//                        @Override
//                        public void run() {
//                            LogUtils.showLog("已是最新版本"+mVersion);
//                            ToastUtil.showToast("当前版本为:"+mVersion+",已经是最新版本！");
//                        }
//                    });
//                    return;
//
//                }
//                updManager.showUpdateInfo(context, result);
//            } else {
//                XApplication.getMainThreadHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        LogUtils.showLog("请求更新失败:errorcode:"+errorcode);
//                        ToastUtil.showToast("请求版本失败！");
//                    }
//                });
//            }
//        }
//    };

}
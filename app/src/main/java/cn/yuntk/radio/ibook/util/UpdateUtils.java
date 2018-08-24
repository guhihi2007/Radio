//package cn.yuntk.radio.ibook.util;
//
//
//import android.content.Context;
//
//import com.iflytek.autoupdate.IFlytekUpdate;
//import com.iflytek.autoupdate.IFlytekUpdateListener;
//import com.iflytek.autoupdate.UpdateConstants;
//import com.iflytek.autoupdate.UpdateErrorCode;
//import com.iflytek.autoupdate.UpdateInfo;
//import com.iflytek.autoupdate.UpdateType;
//import cn.yuntk.radio.ibook.XApplication;
//
///**
// * @author lianwanfei
// *用于自动更新功能
// */
//public class UpdateUtils {
//
//    private IFlytekUpdate updManager;
//    private Context context;
//    private static UpdateUtils instance;
//
//    public static UpdateUtils getInstance(Context mContext) {
//        if (instance == null){
//            instance = new UpdateUtils(mContext);
//        }
//        return instance;
//    }
//
//    public UpdateUtils(Context context) {
//        // TODO Auto-generated constructor stub
//        this.context=context;
//        init();
//    }
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
//        if(initiatively)
//            updManager.autoUpdate(context, updateListener);
//        else
//            updManager.autoUpdate(context, null);
//    }
//
//    //升级版本
//    private IFlytekUpdateListener updateListener = new IFlytekUpdateListener() {
//        @Override
//        public void onResult(int errorcode, UpdateInfo result) {
//            LogUtils.showLog("onResult:"+errorcode);
//            if(errorcode == UpdateErrorCode.OK && result!= null) {
//                if(result.getUpdateType() == UpdateType.NoNeed) {
//                    //取得当前版本
//                    String mVersion=PackageUtils.getVersionName(context);
//
//                    XApplication.getMainThreadHandler().post(new Runnable() {
//                        @Override
//                        public void run() {
//                            LogUtils.showLog("已是最新版本"+mVersion);
////                            ToastUtil.showToast("当前版本为:"+mVersion+",已经是最新版本！");
//                        }
//                    });
//                    return;
//                }
//                updManager.showUpdateInfo(context, result);
//            } else {
//                LogUtils.showLog("请求更新失败:errorcode:"+errorcode);
//            }
//        }
//    };
//}
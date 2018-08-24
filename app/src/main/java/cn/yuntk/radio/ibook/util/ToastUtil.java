package cn.yuntk.radio.ibook.util;

import android.widget.Toast;

/**
 * 创建时间:2018/4/3
 * 创建人: yb
 * 描述:
 */

public class ToastUtil {
    public static void showShortToast(int rId) {
        showShortToast(GlobalApp.APP.getString(rId));
    }

    public static void showLongToast(int rId) {
        showLongToast(GlobalApp.APP.getString(rId));
    }

    public static void showShortToast(String content) {
        Toast.makeText(GlobalApp.APP,content, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String content) {
        Toast.makeText(GlobalApp.APP,content, Toast.LENGTH_LONG).show();
    }

    /*
     * 防止弹出多次吐司
     * */
    private static String oldMsg;
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static void showToast(String s) {
        if (toast == null) {
            toast = Toast.makeText(GlobalApp.APP, s, Toast.LENGTH_SHORT);
            toast.show();
            oldMsg = s;
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    public static void showToast(int resid) {
        if (toast == null) {
            toast = Toast.makeText(GlobalApp.APP, GlobalApp.APP.getString(resid), Toast.LENGTH_SHORT);
            toast.show();
            oldMsg =  GlobalApp.APP.getString(resid);
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if ( GlobalApp.APP.getString(resid).equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg =  GlobalApp.APP.getString(resid);
                toast.setText(GlobalApp.APP.getString(resid));
                toast.show();
            }
        }
        oneTime = twoTime;
    }
}

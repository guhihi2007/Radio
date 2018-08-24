package cn.yuntk.radio.ibook.util;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

/**
 * <p>类描述：</p>
 * <p>创建人：yb</p>
 * <p>创建时间：2017/12/26</p>
 */
public class DisplayUtil {
    private static final String TAG = "Screen";
    private int screenWidth;
    private int screenHeight;
    private static DisplayUtil instance;

    private DisplayUtil() {
        init();
    }

    public void init() {
        WindowManager windowManager = (WindowManager) GlobalApp.APP
                .getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        screenWidth = point.x;
        screenHeight = point.y;
//        Log.i(TAG, "screenWidth:" + screenWidth + " ,screenHeight:" + screenHeight);
    }

    public static DisplayUtil getInstance() {
        if (instance == null) {
            synchronized (DisplayUtil.class) {
                if (instance == null) {
                    instance = new DisplayUtil();
                }
            }
        }
        return instance;
    }

    public static int getScreenWidth() {
        return getInstance().screenWidth;
    }

    public static int getScreenHeight() {
        return getInstance().screenHeight;
    }

    public static int[] getScreenSize() {
        return new int[]{getScreenWidth(),getScreenHeight()};
    }

    /**
     * 获取状态栏高度
     * @return
     */
    public static int getStatusHeight() {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = GlobalApp.APP.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight =GlobalApp.APP.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue)
    {
        // 1 2 3
        final float scale = context.getResources().getDisplayMetrics().density;

        // 100px / 1 = 100dp  dpi = 160
        // 200px / 2 = 100dp  dpi = 320

        return (int) (pxValue / scale + 0.5f);
    }

}

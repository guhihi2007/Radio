package cn.yuntk.radio.ibook.common;

import android.content.Context;

/**
 * Created by yb on 2018/2/16.
 */

public interface ILoadingAction {
    boolean showLoading();
    Context getLoadingContext();
}

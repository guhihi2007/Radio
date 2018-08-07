package cn.yuntk.radio

import android.support.multidex.MultiDexApplication
import com.alibaba.sdk.android.feedback.impl.FeedbackAPI

/**
 * Author : Gupingping
 * Date : 2018/8/2
 * Mail : gu12pp@163.com
 */
class RadioApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        FeedbackAPI.init(this, Constants.FEED_BACK_KEY, Constants.FEED_BACK_SECRET)
    }
}
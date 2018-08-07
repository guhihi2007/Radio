package cn.yuntk.radio

import android.databinding.ObservableArrayList
import android.os.Environment
import cn.yuntk.radio.bean.ChannelBean
import java.io.File

/**
 * Author : Gupingping
 * Date : 2018/7/15
 * Mail : gu12pp@163.com
 */
object Constants {

    const val CHANNEL_CODE = "channel_code" //fragment 跳转时Bundle KEY
    const val CHANNEL_NAME = "channel_name" //fragment 跳转时Bundle KEY
    val TEMP_DIR = Environment.getExternalStorageDirectory().absolutePath + File.separator + "11gppTemp"
    val SAVE_DIR = Environment.getExternalStorageDirectory().absolutePath + File.separator + "11gppRadio"
    val PREPARING_FMBEAN = "preparing_fmbean"
    const val NATION_CODE = "73" //全国频道
    const val PROVINCE_CODE = "87"//省市频道
    const val FOREIGN_CODE = "624"//国外频道
    const val NET_CODE = "698"//网络频道
    const val KEY_SERIALIZABLE = "key_serializable"//activity 跳转时Intent serializable KEY

    const val TIMIMG = "timimg"
    const val COLLECTION = "collection"
    const val HISTORY = "history"
    const val FEEDBACK = "feedback"
    const val UPDATE = "update"
    const val ABOUTUS = "about_us"

    var channelList = ObservableArrayList<ChannelBean>().apply {
        add(ChannelBean("国家电台", NATION_CODE, R.drawable.icon_nation))
        add(ChannelBean("省市电台", PROVINCE_CODE, R.drawable.icon_province))
        add(ChannelBean("国外电台", FOREIGN_CODE, R.drawable.icon_foreign))
        add(ChannelBean("网络电台", NET_CODE, R.drawable.icon_net))
        add(ChannelBean("设置", "-1", -1))
        add(ChannelBean("定时关闭", TIMIMG, R.drawable.icon_timingclose))
        add(ChannelBean("收藏管理", COLLECTION, R.drawable.icon_favorite))
        add(ChannelBean("收听记录", HISTORY, R.drawable.icon_favorite))
        add(ChannelBean("联系我们", "-1", -1))
        add(ChannelBean("问题反馈", FEEDBACK, R.drawable.icon_feedback))
        add(ChannelBean("检查更新", UPDATE, R.drawable.icon_feedback))
        add(ChannelBean("关于我们", ABOUTUS, R.drawable.icon_about_us))

    }
    //播放状态
    const val STATE_IDLE = 0
    const val STATE_PREPARING = 1
    const val STATE_PLAYING = 2
    const val STATE_PAUSE = 3

    const val LAST_PLAY = "last_play"

    const val LOCK_SCREEN_ON = 1//亮屏
    const val LOCK_SCREEN_OFF = 0//锁屏
    const val CURRENT_PAGE = "current_page"//锁屏前页面

    const val FEED_BACK_KEY = "25007022"
    const val FEED_BACK_SECRET = "1c83a8f067e09ff796c23751a547b74a"
//    const val FEED_BACK_KEY = "24899986"
//    const val FEED_BACK_SECRET = "739e172512a378292486984b7d079ba8"
}

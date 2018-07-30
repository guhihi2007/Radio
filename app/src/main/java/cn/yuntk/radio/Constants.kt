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

    var channelList = ObservableArrayList<ChannelBean>().apply {
        add(ChannelBean("国家电台", NATION_CODE))
        add(ChannelBean("省市电台", PROVINCE_CODE))
        add(ChannelBean("国外电台", FOREIGN_CODE))
        add(ChannelBean("网络电台", NET_CODE))
    }
    //播放状态
    const val STATE_IDLE = 0
    const val STATE_PREPARING = 1
    const val STATE_PLAYING = 2
    const val STATE_PAUSE = 3

    const val LAST_PLAY = "last_play"

    const val LOCK_SCREEN_ON = 1
    const val LOCK_SCREEN_OFF = 0
}

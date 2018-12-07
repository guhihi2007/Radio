package cn.yuntk.radio

import android.databinding.ObservableArrayList
import android.os.Environment
import cn.yuntk.radio.bean.ChannelBean
import java.io.File

/**
 * Author : Gupingping
 * Date : 2018/7/15
 * Mail : gu12pp@163.com
 * 项目说明:一开始收音机是一个独立的应用，用的是MVVM框架，
 * 后来要集成懒人听小说，就直接把项目拷贝过来，懒人听小说
 * 代码在一个独立的文件夹 ibook 内，懒人听小说作者刘学平
 */
object Constants {
    /**
     * 收音机频道地址
     */
    const val HOST = "http://fm.gerenhao.com/api/open/voice/union/select/"
    /**
     * 云天空配置地址
     */
    const val YTK_HOST = "http://114.215.47.46:8080/"

    const val CHANNEL_CODE = "channel_code" //fragment 跳转时Bundle KEY
    const val CHANNEL_NAME = "channel_name" //fragment 跳转时Bundle KEY

    const val KEY_SERIALIZABLE = "key_serializable"//activity 跳转时Intent serializable KEY

    //首页抽屉列表对应字段
    const val NATION_CODE = "73" //全国频道
    const val PROVINCE_CODE = "87"//省市频道
    const val FOREIGN_CODE = "624"//国外频道
    const val NET_CODE = "698"//网络频道
    const val TIMIMG = "timimg"
    const val COLLECTION = "collection"
    const val HISTORY = "history"
    const val FEEDBACK = "feedback"
    const val UPDATE = "update"
    const val ABOUTUS = "about_us"
    const val NOVEL = "novel"
    const val FM_ACTIVITY = "fm"
    //地区电台集合，包括全国频道
    val placeCodeList:ArrayList<String> = arrayListOf(NATION_CODE,PROVINCE_CODE)
    //首页抽屉列表
    var channelList = ObservableArrayList<ChannelBean>().apply {
        add(ChannelBean("国家电台", NATION_CODE, R.mipmap.icon_nation))
        add(ChannelBean("省市电台", PROVINCE_CODE, R.mipmap.icon_province))
        add(ChannelBean("国外电台", FOREIGN_CODE, R.mipmap.icon_foreign))
        add(ChannelBean("网络电台", NET_CODE, R.mipmap.icon_net))
        add(ChannelBean("小说大全", NOVEL, R.mipmap.icon_reader))
        add(ChannelBean("设置", "-1", -1))
        add(ChannelBean("定时关闭", TIMIMG, R.mipmap.icon_timingclose))
        add(ChannelBean("收藏管理", COLLECTION, R.mipmap.icon_favorite))
        add(ChannelBean("收听记录", HISTORY, R.mipmap.icon_history))
        add(ChannelBean("联系我们", "-1", -1))
        add(ChannelBean("问题反馈", FEEDBACK, R.mipmap.icon_feedback))
        add(ChannelBean("检查更新", UPDATE, R.mipmap.icon_update))
        add(ChannelBean("关于我们", ABOUTUS, R.mipmap.icon_about_us))

    }
    //播放状态
    const val STATE_IDLE = 0
    const val STATE_PREPARING = 1
    const val STATE_PLAYING = 2
    const val STATE_PAUSE = 3

    const val LAST_PLAY = "last_play" //上一次播放频道
    const val CURRENT_PAGE = "current_page"//锁屏前页面

    const val FEED_BACK_KEY = "25007022"
    const val FEED_BACK_SECRET = "1c83a8f067e09ff796c23751a547b74a"
    const val BUGLY_KEY = "cfa043fff6"
    const val UMENG_KEY = "5b6a5dc0b27b0a590b000106"
    //startActivityForResult 返回码
    const val SET_NET_CODE = 1111

    //当前省份
    const val PROVINCE = "province"
}

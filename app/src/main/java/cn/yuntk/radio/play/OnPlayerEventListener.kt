package cn.yuntk.radio.play

import cn.yuntk.radio.bean.FMBean

/**
 * Author : Gupingping
 * Date : 2018/7/16
 * Mail : gu12pp@163.com
 */
interface OnPlayerEventListener {
    /**
     * 切换歌曲
     */
     fun onChange(fmBean: FMBean)

    /**
     * 继续播放
     */
     fun onPlayerStart()

    /**
     * 暂停播放
     */
     fun onPlayerPause()

    /**
     * 更新进度
     */
     fun onPublish(progress: Int)

    /**
     * 缓冲百分比
     */
     fun onBufferingUpdate(percent: Int)

    /**
     * 更新定时停止播放时间
     */
     fun onTimer(remain: Long)

     fun onMusicListUpdate()
}
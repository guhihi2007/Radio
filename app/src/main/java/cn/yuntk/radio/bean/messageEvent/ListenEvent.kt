package cn.yuntk.radio.bean.messageEvent

import cn.yuntk.radio.bean.FMBean

/**
 * Author : Gupingping
 * Date : 2018/7/20
 * Mail : gu12pp@163.com
 * 播放状态发生变化的EVENT
 */
class ListenEvent(var status: Int,var fmBean: FMBean? = null) {

    override fun toString(): String {
        return "ListenEvent(status=$status, fmBean=$fmBean)"
    }

}
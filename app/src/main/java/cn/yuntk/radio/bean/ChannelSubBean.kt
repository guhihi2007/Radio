package cn.yuntk.radio.bean

/**
 * Author : Gupingping
 * Date : 2018/7/11
 * Mail : gu12pp@163.com
 */
class ChannelSubBean {

    /**
     * message : Success
     * result : [{"radioId":74,"name":"CNR中国之声","parentId":73,"radioFm":"FM106.1","radioUrl":"http://audio1.china-plus.net:31080/10.102.62.44/radios/100001/index_100001.m3u8","backUrl":null,"state":1,"sort":1,"level":3,"isExisUrl":1,"addTime":null,"updateTime":1503572700000}]
     * systemTime : 1531301724517
     * level : 3
     * isExisUrl : 0
     * rootId : 1
     * res_code : 1
     */

    var message: String? = null
    var systemTime: Long = 0
    var level: Int = 0
    var isExisUrl: Int = 0
    var rootId: Int = 0
    var res_code: String? = null
    var result: List<FMBean>? = null

    override fun toString(): String {
        return "ChannelSubBean{" +
                "message='" + message + '\''.toString() +
                ", systemTime=" + systemTime +
                ", level=" + level +
                ", isExisUrl=" + isExisUrl +
                ", rootId=" + rootId +
                ", res_code='" + res_code + '\''.toString() +
                ", result=" + result +
                '}'.toString()
    }
}

package cn.yuntk.radio.ad

/**
 * Created by Gpp on 2018/1/23.
 */

class StatusBean {
    var status: Boolean = false
    var ad_origin: String = ""
    var times: Long = 0
    var ad_percent: String = ""
    var change_times: Long = 0
    var banner_status: Boolean = false
    override fun toString(): String {
        return "StatusBean(status=$status, ad_origin='$ad_origin', times=$times, ad_percent='$ad_percent', change_times=$change_times, banner_status=$banner_status)"
    }

}

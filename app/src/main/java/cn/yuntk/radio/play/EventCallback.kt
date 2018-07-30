package cn.yuntk.radio.play

/**
 * Author : Gupingping
 * Date : 2018/7/16
 * Mail : gu12pp@163.com
 */
interface EventCallback<T> {
    fun onEvent(t: T)
}
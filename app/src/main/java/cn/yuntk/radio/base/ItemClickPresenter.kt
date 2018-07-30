package cn.yuntk.radio.base

import android.view.View

/**
 * Author : Gupingping
 * Date : 2018/7/15
 * Mail : gu12pp@163.com
 */
interface ItemClickPresenter<in Any> {
    fun onItemClick(view: View? = null, item: Any)
}
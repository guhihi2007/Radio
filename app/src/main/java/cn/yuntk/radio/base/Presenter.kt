package cn.yuntk.radio.base

import android.view.View

/**
 * Author : Gupingping
 * Date : 2018/7/15
 * Mail : gu12pp@163.com
 */
interface Presenter : View.OnClickListener {
    override fun onClick(view: View?)
}
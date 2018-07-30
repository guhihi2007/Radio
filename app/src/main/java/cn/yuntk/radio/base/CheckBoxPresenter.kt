package cn.yuntk.radio.base

import android.widget.CheckBox

/**
 * Author : Gupingping
 * Date : 2018/7/26
 * Mail : gu12pp@163.com
 */
interface CheckBoxPresenter<in Any> {
    fun onCheckedChange(cb:CheckBox,isChecked: Boolean)
}
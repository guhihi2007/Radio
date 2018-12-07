package cn.yuntk.radio.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import cn.yuntk.radio.db.PageFMBeanDao

/**
 * Author : Gupingping
 * Date : 2018/7/24
 * Mail : gu12pp@163.com
 *
 *
 * Factory for ViewModels
 */
class PageViewModelFactory(private val dao: PageFMBeanDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PageViewModel::class.java)) {
            return PageViewModel(dao) as T
        }
        throw IllegalArgumentException("不知道是什么 ViewModel")
    }
}
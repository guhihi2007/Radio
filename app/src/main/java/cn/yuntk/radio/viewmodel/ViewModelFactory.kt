package cn.yuntk.radio.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import cn.yuntk.radio.db.FMBeanDao

/**
 * Author : Gupingping
 * Date : 2018/7/24
 * Mail : gu12pp@163.com
 *
 *
 * Factory for ViewModels
 */
class ViewModelFactory(private val dao: FMBeanDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FMBeanViewModel::class.java)) {
            return FMBeanViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
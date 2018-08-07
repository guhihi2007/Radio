package cn.yuntk.radio.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import cn.yuntk.radio.db.CollectionFMBeanDao

/**
 * Author : Gupingping
 * Date : 2018/7/24
 * Mail : gu12pp@163.com
 *
 *
 * Factory for ViewModels
 */
class CollectionViewModelFactory(private val dao: CollectionFMBeanDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionViewModel::class.java)) {
            return CollectionViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package cn.yuntk.radio.viewmodel

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import cn.yuntk.radio.db.HistoryFMBeanDao
import cn.yuntk.radio.db.PageFMBeanDao

/**
 * Author : Gupingping
 * Date : 2018/7/24
 * Mail : gu12pp@163.com
 *
 *
 * Factory for ViewModels
 */
class HistoryViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryViewMode::class.java)) {
            return HistoryViewMode() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
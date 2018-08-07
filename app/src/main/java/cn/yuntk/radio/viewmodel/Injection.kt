/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.yuntk.radio.viewmodel

import android.content.Context
import cn.yuntk.radio.db.AppDataBase
import cn.yuntk.radio.db.CollectionFMBeanDao
import cn.yuntk.radio.db.HistoryFMBeanDao
import cn.yuntk.radio.db.PageFMBeanDao

/**
 * Enables injection of data sources.
 */
object Injection {

    private lateinit var collectionFMBeanDao: CollectionFMBeanDao
    private lateinit var pageFMBeanDao: PageFMBeanDao
    private lateinit var historyDao: HistoryFMBeanDao
    fun init(context: Context) {
        collectionFMBeanDao = provideCollectionDao(context)
        pageFMBeanDao = providePageDao(context)
        historyDao = AppDataBase.getInstance(context).getHistoryDao()
    }

    fun getCollectionDao(): CollectionFMBeanDao {
        return collectionFMBeanDao
    }

    fun getPageDao(): PageFMBeanDao {
        return pageFMBeanDao
    }

    fun getHistoryDao(): HistoryFMBeanDao {
        return historyDao
    }

    //提供FMBeanDao
    fun provideCollectionDao(context: Context): CollectionFMBeanDao {
        val database = AppDataBase.getInstance(context)
        return database.getCollectionDao()
    }

    //构建ViewModelFactory
    fun provideCollectionViewModelFactory(context: Context): CollectionViewModelFactory {
//        val dataSource = provideCollectionDao(context)
        val dataSource = getCollectionDao()
        return CollectionViewModelFactory(dataSource)
    }


    fun providePageDao(context: Context): PageFMBeanDao {
        val dataBase = AppDataBase.getInstance(context)
        return dataBase.getPageDao()
    }

    fun providePageViewModelFactory(context: Context): PageViewModelFactory {
//        val dataSource = providePageDao(context)
        val dataSource = getPageDao()
        return PageViewModelFactory(dataSource)
    }

    fun provideHistoryDao(context: Context): HistoryFMBeanDao {
        val dataBase = AppDataBase.getInstance(context)
        return dataBase.getHistoryDao()
    }

    fun provideHistoryViewModelFactory(context: Context): HistoryViewModelFactory {
//        val dataSource = providePageDao(context)
//        val dataSource = getHistoryDao()
        return HistoryViewModelFactory()
    }

}

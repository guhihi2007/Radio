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
import cn.yuntk.radio.db.FMBeanDao
//import cn.yuntk.radio.db.LastFMBeanDao

/**
 * Enables injection of data sources.
 */
object Injection {

    //提供FMBeanDao
    fun provideFMBeanDao(context: Context): FMBeanDao {
        val database = AppDataBase.getInstance(context)
        return database.getFMBeanDao()
    }
    //构建ViewModelFactory
    fun provideFMBeanViewModelFactory(context: Context): ViewModelFactory {
        val dataSource = provideFMBeanDao(context)
        return ViewModelFactory(dataSource)
    }
}

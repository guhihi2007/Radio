package cn.yuntk.radio.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.arch.persistence.room.migration.Migration
import android.content.Context
import android.support.annotation.NonNull
import cn.yuntk.radio.bean.FMBean
import cn.yuntk.radio.bean.HistoryFMBean
import cn.yuntk.radio.bean.PageFMBean


/**
 * Author : Gupingping
 * Date : 2018/7/23
 * Mail : gu12pp@163.com
 * 数据库升级：
 * 1、version +1
 * 2、实现migration 并添加 addMigrations(migration)
 */
@Database(entities = [(FMBean::class), (PageFMBean::class), (HistoryFMBean::class)], version = 1, exportSchema = false)
@TypeConverters(Converters::class)//此处声明后ROOM可以使用定义的转换器
abstract class AppDataBase : RoomDatabase() {

    abstract fun getCollectionDao(): CollectionFMBeanDao
    abstract fun getPageDao(): PageFMBeanDao
    abstract fun getHistoryDao(): HistoryFMBeanDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        AppDataBase::class.java, "radio.db")
//                        .addMigrations(migration)
                        .build()

        //        如果需要用到Migration类来升级数据库,需要注意
//        1.清单文件中需要声明versionCode属性，否则Studio在run阶段会报错
//        private val migration = object : Migration(1, 2) {
//            override fun migrate(@NonNull database: SupportSQLiteDatabase) {
                //version1--->version2 增加表:HISTORY_FMBEAN
//                database.execSQL("CREATE TABLE `HISTORY_FMBEAN` (`id` INTEGER, `fmBean` FMBean, PRIMARY KEY(`id`))")
//                database.execSQL("CREATE TABLE `FRAGMENT_FMBEAN` (`code` INTEGER, `fmBean` FMBean, PRIMARY KEY(`code`))")
//            }
//        }
    }
}
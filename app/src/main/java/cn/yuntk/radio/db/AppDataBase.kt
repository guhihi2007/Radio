package cn.yuntk.radio.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import cn.yuntk.radio.bean.FMBean


/**
 * Author : Gupingping
 * Date : 2018/7/23
 * Mail : gu12pp@163.com
 */
@Database(entities = [(FMBean::class)], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getFMBeanDao(): FMBeanDao

//    abstract fun getLastFMBeanDao(): LastFMBeanDao

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
//                database.execSQL("CREATE TABLE `Book` (`id` INTEGER, " + "`name` TEXT, `owner` INTEGER, PRIMARY KEY(`id`))")
//            }
//        }
    }
}
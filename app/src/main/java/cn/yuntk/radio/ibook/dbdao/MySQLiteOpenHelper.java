package cn.yuntk.radio.ibook.dbdao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import org.greenrobot.greendao.database.Database;

import cn.yuntk.radio.ibook.dbdao.local.DaoMaster;
import cn.yuntk.radio.ibook.dbdao.local.DownloadBookInfoDao;
import cn.yuntk.radio.ibook.dbdao.local.DownloadMusicInfoDao;
import cn.yuntk.radio.ibook.dbdao.local.ListenerBookInfoDao;
import cn.yuntk.radio.ibook.dbdao.local.ListenerMusicInfoDao;
import cn.yuntk.radio.ibook.dbdao.local.MusicDao;
import cn.yuntk.radio.ibook.dbdao.local.TCAlbumTableDao;
import cn.yuntk.radio.ibook.dbdao.local.TCLastListenerTableDao;
import cn.yuntk.radio.ibook.dbdao.local.TCListenerTableDao;

/**
 * Created by xue
 * 数据库升级
 */
public class MySQLiteOpenHelper extends DaoMaster.OpenHelper {

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db,new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }
            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        },MusicDao.class,DownloadBookInfoDao.class, DownloadMusicInfoDao.class,
                ListenerBookInfoDao.class, ListenerMusicInfoDao.class,
                TCAlbumTableDao.class,TCListenerTableDao.class, TCLastListenerTableDao.class);
    }

}
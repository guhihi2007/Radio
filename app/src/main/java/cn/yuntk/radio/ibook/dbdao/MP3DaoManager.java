package cn.yuntk.radio.ibook.dbdao;

import android.content.Context;


import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.bean.Music;
import cn.yuntk.radio.ibook.dbdao.local.DaoMaster;
import cn.yuntk.radio.ibook.dbdao.local.DaoSession;
import cn.yuntk.radio.ibook.dbdao.local.MusicDao;

/*
*本地数据库操作
* */
public class MP3DaoManager {
private static MP3DaoManager mInstance;
private static DaoMaster.DevOpenHelper openHelper;
private static Context mContext;
private MusicDao musicDao;

    public MusicDao getMusicDao() {
        return musicDao;
    }

    private MP3DaoManager() {
}

public static MP3DaoManager getInstance(Context context) {
    if (mInstance == null) {
        synchronized ((MP3DaoManager.class)) {
            mContext = context;
            mInstance = new MP3DaoManager();
            DaoSession daoSession = XApplication.getDaoSession(mContext);
            mInstance.musicDao = daoSession.getMusicDao();
        }
    }
    return mInstance;
}

public void insertMusic(Music music) {
    musicDao.insert(music);
}

public void insertMusicList(List<Music> musicList) {
    if (musicList == null || musicList.isEmpty()) {
        return;
    }
    musicDao.insertInTx(musicList);
}

public void deleteMusic(Music music) {
    musicDao.delete(music);
}

public void deleteAll() {
    musicDao.deleteAll();
}

public void updateMusic(List<Music> musics){
   musicDao.updateInTx(musics);
}

public void updateMusic(Music music) {
    musicDao.update(music);
}

public List<Music> queryMusicList() {
    QueryBuilder<Music> qb = musicDao.queryBuilder();
    List<Music> list = qb.list();
    return list;
}

public List<Music> queryMusicList(int songid) {
    QueryBuilder<Music> qb = musicDao.queryBuilder();
    qb.where(MusicDao.Properties.SongId.gt(songid)).orderAsc(MusicDao.Properties.SongId);
    List<Music> list = qb.list();
    return list;
}

}
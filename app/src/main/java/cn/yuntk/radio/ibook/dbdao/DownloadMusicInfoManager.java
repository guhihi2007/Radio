package cn.yuntk.radio.ibook.dbdao;

import android.content.Context;
import android.util.Log;


import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.bean.DownloadMusicInfo;
import cn.yuntk.radio.ibook.dbdao.local.DaoSession;
import cn.yuntk.radio.ibook.dbdao.local.DownloadMusicInfoDao;

/**
 * 下载章节得信息
 * */
public class DownloadMusicInfoManager {

    private static DownloadMusicInfoManager mInstance;
    private static Context mContext;
    private DownloadMusicInfoDao musicDao;

    public DownloadMusicInfoDao getMusicDao() {
        return musicDao;
    }

    private DownloadMusicInfoManager() {
    }

    public static DownloadMusicInfoManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized ((DownloadMusicInfoManager.class)) {
                mContext = context;
                mInstance = new DownloadMusicInfoManager();
                DaoSession daoSession = XApplication.getDaoSession(mContext);
                mInstance.musicDao = daoSession.getDownloadMusicInfoDao();
            }
        }
        return mInstance;
    }

    /**
     * 供外界调用得增删改查
     * */
    /*
     *删除所有数据 记录
     * */
    public void deleteAll() {
        mInstance.deleteAllData();
    }


    public void deleteBtn(DownloadMusicInfo downloadMusic) {
        deleteData(downloadMusic);
    }

    /** 通过主键删除*/
    public void deleteByKey2Music(String key){
        deleteByKey(key);
    }

    /**
     * 删除一个集合的数据
     * */
    public void deleteListMusic(List<DownloadMusicInfo> dMusics){
        musicDao.deleteInTx(dMusics);
    }

    /**
     *插入数据(单个)
     * */
    public void insertDB(DownloadMusicInfo music) {
        mInstance.insertData(music);
    }

    /**
     * 插入一个集合的数据
     * */
    public void insertListToDB(List<DownloadMusicInfo> datas){
        musicDao.insertOrReplaceInTx(datas);//插入一个集合
    }

    /*
     *更新数据
     * */
    public void updateBtn(DownloadMusicInfo music) {
        mInstance.updateData(music);
    }

    /*打印数据库数据
     * */
    public void printDbTable(){
        List<DownloadMusicInfo> musicList = mInstance.queryDataList();
        int leng = musicList.size();
        for (int i = 0; i<leng;i++){
            DownloadMusicInfo u = musicList.get(i);
            Log.i("DownloadMusicInfo:" , u.toString());
        }
    }

    private void insertData(DownloadMusicInfo music) {
        musicDao.insert(music);
    }

    private void deleteData(DownloadMusicInfo music) {
        musicDao.delete(music);
    }

    private void deleteAllData() {
        musicDao.deleteAll();
    }

    private void updateData(DownloadMusicInfo music) {
        musicDao.update(music);
    }

    private List<DownloadMusicInfo> queryDataList() {
        QueryBuilder<DownloadMusicInfo> qb = musicDao.queryBuilder();
        List<DownloadMusicInfo> list = qb.list();
        return list;
    }

    public void deleteByKey(String key) {
        musicDao.deleteByKey(key);
    }
}

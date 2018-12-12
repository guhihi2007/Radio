package cn.yuntk.radio.ibook.dbdao;

import android.content.Context;


import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.bean.ListenerMusicInfo;
import cn.yuntk.radio.ibook.dbdao.local.DaoSession;
import cn.yuntk.radio.ibook.dbdao.local.ListenerMusicInfoDao;


/** 查询章节进度的数据库操作 */
public class ListenerMusicInfoManager {
    private static ListenerMusicInfoManager mInstance;
    private static Context mContext;
    private ListenerMusicInfoDao musicDao;

    public ListenerMusicInfoDao getMusicDao() {
        return musicDao;
    }

    private ListenerMusicInfoManager() {
    }

    public static ListenerMusicInfoManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized ((DownloadMusicInfoManager.class)) {
                mContext = context;
                mInstance = new ListenerMusicInfoManager();
                DaoSession daoSession = XApplication.getDaoSession(mContext);
                mInstance.musicDao = daoSession.getListenerMusicInfoDao();
            }
        }
        return mInstance;
    }

    /**提供外界调用的数据库操作接口*/
    public void delAll(){deleteAllData();}
    public void delMusicByKey(String key){deleteByKey(key);}
    public void delBookList(List<ListenerMusicInfo> infos){deleteList(infos);}
    public void updateMusic(ListenerMusicInfo info){updateData(info);}
    public void insertMusic(ListenerMusicInfo info){insertData(info);}
    public void insertMusicList(List<ListenerMusicInfo> infos){insertDataList(infos);}
    public List<ListenerMusicInfo> queryListDB() {
        return mInstance.queryDataList();
    }


    private void insertData(ListenerMusicInfo music) {
        musicDao.insertInTx(music);
    }
    private void deleteData(ListenerMusicInfo music) {
        musicDao.delete(music);
    }
    private void deleteAllData() {
        musicDao.deleteAll();
    }
    //删除一个数据集合
    private void deleteList(List<ListenerMusicInfo> infos){
        musicDao.deleteInTx(infos);
    }
    //插入一个数据集合
    private void insertDataList(List<ListenerMusicInfo> infos){
        musicDao.insertOrReplaceInTx(infos);
    }
    //通过主键删除数据
    private void deleteByKey(String key){
        musicDao.deleteByKey(key);
    }
    private void updateData(ListenerMusicInfo music) {
        musicDao.update(music);
    }
    private List<ListenerMusicInfo> queryDataList() {
        QueryBuilder<ListenerMusicInfo> qb = musicDao.queryBuilder();
        List<ListenerMusicInfo> list = qb.list();
        return list;
    }

}

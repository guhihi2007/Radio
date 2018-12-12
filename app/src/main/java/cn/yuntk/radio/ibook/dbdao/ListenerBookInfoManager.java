package cn.yuntk.radio.ibook.dbdao;

import android.content.Context;


import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.bean.ListenerBookInfo;
import cn.yuntk.radio.ibook.dbdao.local.DaoSession;
import cn.yuntk.radio.ibook.dbdao.local.ListenerBookInfoDao;


/**收听进度数据库管理类*/
public class ListenerBookInfoManager {
    private static ListenerBookInfoManager mInstance;
    private static Context mContext;
    private ListenerBookInfoDao musicDao;

    public ListenerBookInfoDao getMusicDao() {
        return musicDao;
    }

    private ListenerBookInfoManager() {
    }

    public static ListenerBookInfoManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized ((DownloadMusicInfoManager.class)) {
                mContext = context;
                mInstance = new ListenerBookInfoManager();
                DaoSession daoSession = XApplication.getDaoSession(mContext);
                mInstance.musicDao = daoSession.getListenerBookInfoDao();
            }
        }
        return mInstance;
    }

    /**提供外界调用的数据库操作接口*/
    public void delAll(){deleteAllData();}
    public void delBookByKey(String key){deleteByKey(key);}
    public void delBookList(List<ListenerBookInfo> infos){deleteList(infos);}
    public void updateBook(ListenerBookInfo info){updateData(info);}
    public void insertBook(ListenerBookInfo info){insertData(info);}
    public void insertBookList(List<ListenerBookInfo> infos){insertDataList(infos);}
    public ListenerBookInfo queryListDB(String bookid) {
        ListenerBookInfo music = null;
        List<ListenerBookInfo> musicList = mInstance.queryDataList();
        for (ListenerBookInfo u : musicList) {
            if (u.getId().equals(bookid)){
                music = u;
            }
        }
        return music;
    }

    public List<ListenerBookInfo> queryListDB() {
        return mInstance.queryDataList();
    }


    private void insertData(ListenerBookInfo music) {
        musicDao.insertInTx(music);
    }
    private void deleteData(ListenerBookInfo music) {
        musicDao.delete(music);
    }
    private void deleteAllData() {
        musicDao.deleteAll();
    }
    //删除一个数据集合
    private void deleteList(List<ListenerBookInfo> infos){
        musicDao.deleteInTx(infos);
    }
    //插入一个数据集合
    private void insertDataList(List<ListenerBookInfo> infos){
        musicDao.insertOrReplaceInTx(infos);
    }
    //通过主键删除数据
    private void deleteByKey(String key){
        musicDao.deleteByKey(key);
    }
    private void updateData(ListenerBookInfo music) {
        musicDao.update(music);
    }
    private List<ListenerBookInfo> queryDataList() {
        QueryBuilder<ListenerBookInfo> qb = musicDao.queryBuilder();
        List<ListenerBookInfo> list = qb.list();
        return list;
    }

}
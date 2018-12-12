package cn.yuntk.radio.ibook.dbdao;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.bean.TCListenerTable;
import cn.yuntk.radio.ibook.dbdao.local.DaoSession;
import cn.yuntk.radio.ibook.dbdao.local.TCListenerTableDao;

/** 查询章节进度的数据库操作 */
public class TCListenerTableManager {

    private static TCListenerTableManager mInstance;
    private static Context mContext;
    private TCListenerTableDao beanDao;

    public TCListenerTableDao getBeanDao() {
        return beanDao;
    }

    private TCListenerTableManager() {
    }

    public static TCListenerTableManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized ((TCListenerTableManager.class)) {
                mContext = context;
                mInstance = new TCListenerTableManager();
                DaoSession daoSession = XApplication.getDaoSession(mContext);
                mInstance.beanDao = daoSession.getTCListenerTableDao();
            }
        }
        return mInstance;
    }

    /** 提供外界调用的数据库操作接口 */
    public void delAll(){deleteAllData();}

    public void delBookList(List<TCListenerTable> infos){deleteList(infos);}
    //更新一个数据
    public void updateMusic(TCListenerTable info){updateData(info);}
    //插入一个数据
    public void insertMusic(TCListenerTable info){insertData(info);}
    //插入一个集合
    public void insertMusicList(List<TCListenerTable> infos){insertDataList(infos);}
    //更新一个集合
    public void updataList(List<TCListenerTable> infos){
        updateDataList(infos);
    }

    //查询表中所有数据
    public List<TCListenerTable> queryListDB() {
        return mInstance.queryDataList();
    }


    //    查询 历史 收藏的数据
    public List<TCListenerTable> queryHCList(String h, String c) {
        QueryBuilder<TCListenerTable> qb = beanDao.queryBuilder().where(
                TCListenerTableDao.Properties.Is_history.eq(h),
                TCListenerTableDao.Properties.Is_collect.eq(c));
        List<TCListenerTable> list = qb.list();
        return list;
    }

    //插入一个数据
    private void insertData(TCListenerTable music) {
        beanDao.insertInTx(music);
    }

    //插入一个数据集合
    private void insertDataList(List<TCListenerTable> infos){
        beanDao.insertOrReplaceInTx(infos);
    }

    //删除所有数据
    private void deleteAllData() {
        beanDao.deleteAll();
    }

    //删除一个数据集合
    private void deleteList(List<TCListenerTable> infos){
        beanDao.deleteInTx(infos);
    }

    //更新一个数据
    private void updateData(TCListenerTable music) {
        beanDao.update(music);
    }

    //更新一个数据集合
    private void updateDataList(List<TCListenerTable> infos){beanDao.updateInTx(infos);}


    //查询表中所有数据
    private List<TCListenerTable> queryDataList() {
        QueryBuilder<TCListenerTable> qb = beanDao.queryBuilder();
        List<TCListenerTable> list = qb.list();
        return list;
    }

}
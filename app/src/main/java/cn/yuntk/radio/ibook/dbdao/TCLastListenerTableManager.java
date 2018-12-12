package cn.yuntk.radio.ibook.dbdao;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import cn.yuntk.radio.XApplication;
import cn.yuntk.radio.ibook.bean.TCLastListenerTable;
import cn.yuntk.radio.ibook.dbdao.local.DaoSession;
import cn.yuntk.radio.ibook.dbdao.local.TCLastListenerTableDao;


/** 查询章节进度的数据库操作 */
public class TCLastListenerTableManager {

    private static TCLastListenerTableManager mInstance;
    private static Context mContext;
    private TCLastListenerTableDao beanDao;

    public TCLastListenerTableDao getBeanDao() {
        return beanDao;
    }

    private TCLastListenerTableManager() {
    }

    public static TCLastListenerTableManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized ((TCLastListenerTableManager.class)) {
                mContext = context;
                mInstance = new TCLastListenerTableManager();
                DaoSession daoSession = XApplication.getDaoSession(mContext);
                mInstance.beanDao = daoSession.getTCLastListenerTableDao();
            }
        }
        return mInstance;
    }

    /**提供外界调用的数据库操作接口*/
    public void delAll(){deleteAllData();}

    public void delMusicByKey(Long key){deleteByKey(key);}

    //更新一个数据
    public void updateMusic(TCLastListenerTable info){updateData(info);}

    //插入一个数据
    public void insertMusic(TCLastListenerTable info){insertData(info);}

    //根据主键查询一个数据
    public TCLastListenerTable queryBeanBykey(Long key){
        return beanDao.load(key);
    }

    //查询表中所有数据
    public List<TCLastListenerTable> queryListDB() {
        return mInstance.queryDataList();
    }

    //插入一个数据
    private void insertData(TCLastListenerTable music) {
        beanDao.insertInTx(music);
    }

    //删除所有数据
    private void deleteAllData() {
        beanDao.deleteAll();
    }

    //通过主键删除数据
    private void deleteByKey(Long key){
        beanDao.deleteByKey(key);
    }

    //更新一个数据
    private void updateData(TCLastListenerTable music) {
        beanDao.update(music);
    }

    //查询表中所有数据
    private List<TCLastListenerTable> queryDataList() {
        QueryBuilder<TCLastListenerTable> qb = beanDao.queryBuilder();
        List<TCLastListenerTable> list = qb.list();
        return list;
    }

}
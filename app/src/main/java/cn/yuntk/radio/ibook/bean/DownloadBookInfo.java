package cn.yuntk.radio.ibook.bean;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToMany;

import java.io.Serializable;
import java.util.List;

import cn.yuntk.radio.ibook.dbdao.local.DaoSession;
import cn.yuntk.radio.ibook.dbdao.local.DownloadMusicInfoDao;
import cn.yuntk.radio.ibook.dbdao.local.DownloadBookInfoDao;

/** 下载的书籍 信息 */
@Entity(nameInDb = "dao_download_book")
public class DownloadBookInfo implements Serializable {

    static final long serialVersionUID = 45L;

    @Property(nameInDb = "id_auto")
    private Long id_auto;
    @Id(autoincrement = false)
    @Property(nameInDb = "id")
    private String id;
    @Property(nameInDb = "title")
    private String title;

    @Property(nameInDb = "type")
    private String type;
    @Property(nameInDb = "zztt")
    private String zztt;
    @Property(nameInDb = "con")
    private String con;
    @ToMany(joinProperties = {
            @JoinProperty(name = "id", referencedName = "book_id")
    })
    private List<DownloadMusicInfo> musicInfoList;

    @Property(nameInDb = "mark1")
    private String mark1;
    @Property(nameInDb = "mark2")
    private String mark2;

    private String pic;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1738865587)
    private transient DownloadBookInfoDao myDao;

    public DownloadBookInfo(Long id_auto, String id, String title,
                            String type, String zztt, String con,
                            String mark1, String mark2) {
        this.id_auto = id_auto;
        this.id = id;
        this.title = title;
        this.type = type;
        this.zztt = zztt;
        this.con = con;
        this.mark1 = mark1;
        this.mark2 = mark2;
    }

    public DownloadBookInfo() {
    }

    @Generated(hash = 1176876474)
    public DownloadBookInfo(Long id_auto, String id, String title, String type,
                            String zztt, String con, String mark1, String mark2, String pic) {
        this.id_auto = id_auto;
        this.id = id;
        this.title = title;
        this.type = type;
        this.zztt = zztt;
        this.con = con;
        this.mark1 = mark1;
        this.mark2 = mark2;
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getZztt() {
        return zztt;
    }

    public void setZztt(String zztt) {
        this.zztt = zztt;
    }

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public String getMark1() {
        return mark1;
    }

    public void setMark1(String mark1) {
        this.mark1 = mark1;
    }

    public String getMark2() {
        return mark2;
    }

    public void setMark2(String mark2) {
        this.mark2 = mark2;
    }

    public Long getId_auto() {
        return this.id_auto;
    }

    public void setId_auto(Long id_auto) {
        this.id_auto = id_auto;
    }

    public void setMusicInfoList(List<DownloadMusicInfo> musicInfoList) {
        this.musicInfoList = musicInfoList;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1515832129)
    public List<DownloadMusicInfo> getMusicInfoList() {
        if (musicInfoList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DownloadMusicInfoDao targetDao = daoSession.getDownloadMusicInfoDao();
            List<DownloadMusicInfo> musicInfoListNew = targetDao
                    ._queryDownloadBookInfo_MusicInfoList(id);
            synchronized (this) {
                if (musicInfoList == null) {
                    musicInfoList = musicInfoListNew;
                }
            }
        }
        return musicInfoList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1947244740)
    public synchronized void resetMusicInfoList() {
        musicInfoList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1087837971)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDownloadBookInfoDao() : null;
    }


}

package com.zwhd.appbase.download;

import java.io.Serializable;

/**
 * 分类模型
 * Created by YI on 2015/12/9.
 */
public class CateModel implements Serializable {
    /**
     * 数据库id
     */
    long id;
    /**
     * 分组id
     */
    int cId;
    /**
     * 分组名称
     */
    String cName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }
}

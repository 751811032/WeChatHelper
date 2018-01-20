package com.rain.wechathelper.params;

/**
 * Created by USER on 2018/1/3.
 */

public class Test {
    private int Type ;
    private String version ;
    private int pageindex ;
    private int pagesize ;
    private String uid ;
    private String data ;

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getPageindex() {
        return pageindex;
    }

    public void setPageindex(int pageindex) {
        this.pageindex = pageindex;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}

package com.rain.wechathelper.params;

import com.rain.wechathelper.util.DataUtil;

/**
 * Created by USER on 2017/3/23.
 */

public class BaseRequestParams {

    private String  Uin;
    private String  Sid;
    private String  Skey;
    private String  DeviceID;

    public String getUin() {
        return Uin;
    }

    public void setUin(String uin) {
        Uin = uin;
    }

    public String getSid() {
        return Sid;
    }

    public void setSid(String sid) {
        Sid = sid;
    }

    public String getSkey() {
        return Skey;
    }

    public void setSkey(String skey) {
        Skey = skey;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }
}

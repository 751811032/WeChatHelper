package com.rain.wechathelper.service;

import com.rain.wechathelper.util.DataUtil;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by USER on 2017/3/18.
 */

public class LoginApi extends  BaseApi{
    private ApiStore mApiStore;

    public LoginApi(String mBaseUrl) {
        super(mBaseUrl);
        mApiStore = mRetrofit.create(ApiStore.class);
    }

    public void Login(String url ,String ticket , String uuid , String lang , String scan ,String fun ,ApiCallback callback) {
        Call<ResponseBody> call = ((ApiStore)mApiStore).Login(url,ticket,uuid,lang,scan,fun);
        call.enqueue(callBack(callback));
    }

    public interface ApiStore {
        //登录
        @Headers("user-Agent:Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6 Greatwqs")
        @GET
//        https://web.wechat.com/cgi-bin/mmwebwx-bin/webwxnewloginpage?ticket=AivKRuMRGM5q1Ld5qALEibE8@qrticket_0&uuid=YfQci6fKtQ==&lang=zh_CN&scan=1490005777&fun=new
        Call<ResponseBody> Login(@Url String url, @Query("ticket") String ticket, @Query("uuid") String uuid, @Query("lang") String lang, @Query("scan") String scan, @Query("fun") String fun);
//        Call<ResponseBody> Login(@Path("id") String id);
    }
}

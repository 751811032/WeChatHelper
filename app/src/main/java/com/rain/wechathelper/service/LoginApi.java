package com.rain.wechathelper.service;

import com.google.gson.JsonObject;

import com.rain.wechathelper.params.Test;
import com.rain.wechathelper.util.DataUtil;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
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

    public void TestAAA(Test fun , ApiCallback callback) {
        Call<ResponseBody> call = ((ApiStore)mApiStore).TESTAAA(fun);
        call.enqueue(callBack(callback));
    }

    public void WxInit(String url ,String jsonObject ,ApiCallback callback) {
        Call<ResponseBody> call = ((ApiStore)mApiStore).WxInit(url,jsonObject);
        call.enqueue(callBack(callback));
    }

    public interface ApiStore {
        //登录
        @GET
//        https://web.wechat.com/cgi-bin/mmwebwx-bin/webwxnewloginpage?ticket=AivKRuMRGM5q1Ld5qALEibE8@qrticket_0&uuid=YfQci6fKtQ==&lang=zh_CN&scan=1490005777&fun=new
        Call<ResponseBody> Login(@Url String url, @Query("ticket") String ticket, @Query("uuid") String uuid, @Query("lang") String lang, @Query("scan") String scan, @Query("fun") String fun);

//        @Headers("ContentType: application/json; charset=UTF-8")
        @POST("collection/GetCollectionList")
//        https://web.wechat.com/cgi-bin/mmwebwx-bin/webwxnewloginpage?ticket=AivKRuMRGM5q1Ld5qALEibE8@qrticket_0&uuid=YfQci6fKtQ==&lang=zh_CN&scan=1490005777&fun=new
        Call<ResponseBody> TESTAAA(@Body Test fun);

        //初始化
//        @Headers("ContentType: application/json; charset=UTF-8")
        //POST https://web.wechat.com/cgi-bin/mmwebwx-bin/webwxinit?r=1490452348&pass_ticket=9S60902YWGibIHzgxjsCXgeOT6iNkaVMD3U8ImChHoyBQ8rrtvG4aCN%2Bn5z9yhJc&skey=@crypt_dd4b9a3a_d0f11789e72407689c10bd00ff975da6
        @POST
        Call<ResponseBody> WxInit(@Url String url, String jsonObject);
    }
}

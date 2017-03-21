package com.rain.wechathelper.service;

import android.util.Log;

import com.rain.wechathelper.util.DataUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by USER on 2017/3/18.
 */

public class BeforeLoginApi extends  BaseApi{
    private static final String mBaseUrl = "https://login.weixin.qq.com/";

    private ApiStore mApiStore;

    public BeforeLoginApi() {
        super(mBaseUrl);
        mApiStore = mRetrofit.create(ApiStore.class);
    }

    public void getUUid(BaseApi.ApiCallback callback) {
        Call<ResponseBody> call = ((ApiStore)mApiStore).getUUid("wx782c26e4c19acffb","new","zh_CN",""+DataUtil.getCurrentUnixTime());
        call.enqueue(callBack(callback));
    }

    public void getQr(String uuid , Callback callback) {
        Call<ResponseBody> call = ((ApiStore)mApiStore).getQr(uuid);
        call.enqueue(callback);
    }

    public void waitForLogin(String tip,String uuid , BaseApi.ApiCallback callback) {
        Call<ResponseBody> call = ((ApiStore)mApiStore).waitForLogin(tip,uuid,""+DataUtil.getCurrentUnixTime());
        call.enqueue(callBack(callback));
    }



    public interface ApiStore {
        //获取uuid https://login.weixin.qq.com/jslogin   method	POST  params	appid: 应用ID fun: new 应用类型 lang: zh_CN 语言 _: 时间戳
        @Headers("user-Agent:Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.6) Gecko/20100625 Firefox/3.6.6 Greatwqs")
        @POST("jslogin")
        Call<ResponseBody> getUUid(@Query("appid") String appid, @Query("fun") String fun,@Query("lang") String lang,@Query("_") String data);

        //获取二维码
        @POST("qrcode/{uuid}")
        Call<ResponseBody> getQr(@Path("uuid") String uuid);

        //等待登录
        @GET("cgi-bin/mmwebwx-bin/login")
        Call<ResponseBody> waitForLogin(@Query("tip") String tip, @Query("uuid") String uuid,@Query("_") String data);
    }
}

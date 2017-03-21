package com.rain.wechathelper.service;

import android.util.Log;

import com.rain.wechathelper.exception.WechatException;
import com.rain.wechathelper.model.BaseData;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by USER on 2017/3/18.
 */

public abstract class BaseApi {

    private static final String mBaseUrl = "http://www.baidu.com/";

    protected Retrofit mRetrofit;


    public BaseApi() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mBaseUrl)
                .build();
    }

    public BaseApi(String baseUrl) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient())//使用自己创建的OkHttp
                .build();
    }

    private OkHttpClient getOkHttpClient() {
        //日志显示级别
        HttpLoggingInterceptor.Level level= HttpLoggingInterceptor.Level.BODY;
        //新建log拦截器
        HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("zcb","OkHttp====Message:"+message);
            }
        });
        loggingInterceptor.setLevel(level);
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient
                .Builder();
        httpClientBuilder.connectTimeout(50, TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(50, TimeUnit.SECONDS);
        //OkHttp进行添加拦截器loggingInterceptor
        httpClientBuilder.addInterceptor(loggingInterceptor);
        return httpClientBuilder.build();
    }

    //处理retrofit回调 并调用ApiCallback相应返回
    protected  Callback<ResponseBody> callBack(final ApiCallback callback) {
        return new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()) {
//                    if(((BaseData)response.body()).getCodeId() == 1) {
                    String a = "";
                    try {
                        a = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    callback.onSuccess(a);
//                    } else {
//                        callback.onError(((BaseData)response.body()).getCodeId());
//                    }
                } else {
                    callback.onFailure(new WechatException(response.code()+""));
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(t);
            }
        };
    }

    public interface ApiCallback {
        void onSuccess(String res);        //ret=1时返回
        void onError(int err_code);   //ret=0时返回
        void onFailure(Throwable t);   //网络请求失败
    }
}

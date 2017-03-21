package com.rain.wechathelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.rain.wechathelper.exception.WechatException;
import com.rain.wechathelper.model.WechatMeta;
import com.rain.wechathelper.service.BaseApi;
import com.rain.wechathelper.service.BeforeLoginApi;
import com.rain.wechathelper.service.LoginApi;
import com.rain.wechathelper.util.Matchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.ElementType;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity {

    private Handler handler = new Handler();
    private ImageView imageView;
    private BeforeLoginApi api;
    private WechatMeta wechatMeta = new WechatMeta();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        Log.i("MainActivity", "image1:");
        api = new BeforeLoginApi();
        api.getUUid(new BaseApi.ApiCallback() {
            @Override
            public void onSuccess(String res) {
                Log.i("我的信息", "onSuccess:" + res);
                //window.QRLogin.code = 200; window.QRLogin.uuid = "YZZ3WrFYmw==";
                if (!res.isEmpty()) {
                    String code = Matchers.match("window.QRLogin.code = (\\d+);", res);
                    Log.d("code", code);

                    if (null != code) {
                        if (code.equals("200")) {
                            String uuid = Matchers.match("window.QRLogin.uuid = \"(.*)\";", res);
                            wechatMeta.setUuid(uuid);
                            api.getQr(uuid, new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        try {
                                            imageView.setImageBitmap(getBitmapFromByte(response.body().bytes()));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        new Thread(new Runnable(){
                                            @Override
                                            public void run() {
                                                getLoginInfo();
                                            }
                                        }).start();
                                    } else {
                                        Log.i("扫描获取失败","aa");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                }
                            });
                        } else {
                            Log.i("我的信息","错误的状态码");
                        }
                    }
                } else {
                    Log.i("我的信息","获取UUID失败");
                }
            }

            @Override
            public void onError(int err_code) {
                Log.i("我的信息", "onError:" + err_code);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i("我的信息", "onFailure:" + t);
            }
        });
    }

    private void getLoginInfo(){
        wechatMeta.setTip(1);
        Log.i("我的信息","tip11:"+wechatMeta.getTip());
        while (wechatMeta.getTip()==1) {
            Log.i("我的信息","tip:"+wechatMeta.getTip());
            api.waitForLogin("" + wechatMeta.getTip(), wechatMeta.getUuid(), new BaseApi.ApiCallback() {
                @Override
                public void onSuccess(String res) {
                    if (null == res) {
                        Toast.makeText(MainActivity.this,"扫描二维码验证失败",Toast.LENGTH_LONG).show();
                    }
                    String code = Matchers.match("window.code=(\\d+);", res);
                    if (null == code) {
                        Toast.makeText(MainActivity.this,"扫描二维码验证失败",Toast.LENGTH_LONG).show();
                    } else {
                        if (code.equals("201")) {
                            Log.i("mainActivity","成功扫描,请在手机上点击确认以登录");
                            wechatMeta.setTip(0);
                        } else if (code.equals("200")) {
                            Log.i("MainActivity","正在登录...");
                            String pm = Matchers.match("window.redirect_uri=\"(\\S+?)\";", res);
                            Log.i("我的信息", "pm:"+pm);
                            String redirect_uri = pm + "&fun=new";
                            wechatMeta.setRedirect_uri(redirect_uri);

                            String base_uri = redirect_uri.substring(0, redirect_uri.lastIndexOf("/")+1);
                            String url = redirect_uri.substring(redirect_uri.lastIndexOf("/")+1, redirect_uri.lastIndexOf("？"));
                            Log.i("我的信息", "base_uri:"+base_uri);
                            Log.i("我的信息", "uri:"+url);

                            wechatMeta.setBase_uri(base_uri);

                            Log.d("redirect_uri={}", redirect_uri);
                            Log.d("base_uri={}", base_uri);

                            String ticket = getParams(redirect_uri,"ticket");
                            String uuid = getParams(redirect_uri,"ticket");
                            String lang = getParams(redirect_uri,"ticket");
                            String scan = getParams(redirect_uri,"ticket");
                            String fun = getParams(redirect_uri,"ticket");

                            login(base_uri, url, ticket,uuid, lang,scan,fun);
                        } else if (code.equals("408")) {
                            Toast.makeText(MainActivity.this,"登录超时",Toast.LENGTH_LONG).show();
                            wechatMeta.setTip(2);  //1 未扫描  0 扫描
                        } else {
                            Log.i("扫描code={}", code);
                        }
                    }
                }
                @Override
                public void onError(int err_code) {
                    Log.i("扫描登录失败", ""+err_code);
                }
                @Override
                public void onFailure(Throwable t) {
                    Log.i("扫描登录失败", t.toString());
                }
            });
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String getParams(String url,String key){
        String u = null;
        String s = null;
        try {
            u = URLDecoder.decode(url, "UTF-8");
                Pattern p = Pattern.compile(key + "=([^&]*)(&|$)");
                Matcher m = p.matcher(u);
                if (m.find()) {
                    s = m.group(1);
                }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return s;
    }
    /**
     * byte 转换为bitmap
     * @param temp
     * @return bitmap
     */
    public Bitmap getBitmapFromByte(byte[] temp) {
        if (temp != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        } else {
            return null;
        }
    }

    /**
     * 登录
     */
    private void login(String baseUrl,String url,String ticket,String uuid,String lang,String scan ,String fun) throws WechatException {
        LoginApi api = new LoginApi(baseUrl) ;
        api.Login(url,ticket,uuid,lang,scan,fun, new BaseApi.ApiCallback() {
            @Override
            public void onSuccess(String res) {
                Log.i("我的信息",res);
                if (res!=null&&res.length()!=0){
                    Log.i("我的信息","登录失败");
                }else{
                    wechatMeta.setSkey(Matchers.match("<skey>(\\S+)</skey>", res));
                    wechatMeta.setWxsid(Matchers.match("<wxsid>(\\S+)</wxsid>", res));
                    wechatMeta.setWxuin(Matchers.match("<wxuin>(\\S+)</wxuin>", res));
                    wechatMeta.setPass_ticket(Matchers.match("<pass_ticket>(\\S+)</pass_ticket>", res));
                }
            }

            @Override
            public void onError(int err_code) {

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
    /**
     * 加载本地图片
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(File url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
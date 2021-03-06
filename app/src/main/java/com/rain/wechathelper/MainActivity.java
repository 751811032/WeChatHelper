package com.rain.wechathelper;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rain.wechathelper.exception.WechatException;

import com.rain.wechathelper.model.WechatMeta;
import com.rain.wechathelper.params.BaseRequestParams;
import com.rain.wechathelper.params.LoginBiz;
import com.rain.wechathelper.params.Test;
import com.rain.wechathelper.service.BaseApi;
import com.rain.wechathelper.service.BeforeLoginApi;
import com.rain.wechathelper.service.LoginApi;
import com.rain.wechathelper.util.DataUtil;
import com.rain.wechathelper.util.Matchers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
//        api = new BeforeLoginApi();
//        LoginApi logApi = new LoginApi("http://39.108.183.137:8041/");
//        Gson gson = new Gson();
//        Test test = new Test();  test.setData("string");test.setPageindex(0);test.setPagesize(0);test.setType(0);test.setUid("18515466954");
//        String jsonObject = gson.toJson(test);
//        logApi.TestAAA(test, new BaseApi.ApiCallback() {
//            @Override
//            public void onSuccess(String res) {
//                Log.i("我的信息",res);
//            }
//            @Override
//            public void onError(int err_code) {
//            }
//            @Override
//            public void onFailure(Throwable t) {
//            }
//        });
        LoginBiz loginBiz = new LoginBiz();
        loginBiz.execute();

//        api.getUUid(new BaseApi.ApiCallback() {
//            @Override
//            public void onSuccess(String res) {
//                Log.i("我的信息", "onSuccess:" + res);
//                //window.QRLogin.code = 200; window.QRLogin.uuid = "YZZ3WrFYmw==";
//                if (!res.isEmpty()) {
//                    String code = Matchers.match("window.QRLogin.code = (\\d+);", res);
//                    Log.d("code", code);
//
//                    if (null != code) {
//                        if (code.equals("200")) {
//                            String uuid = Matchers.match("window.QRLogin.uuid = \"(.*)\";", res);
//                            Log.d("setUuid", uuid+";;"+URLDecoder.decode(uuid));
//                            wechatMeta.setUuid(URLDecoder.decode(uuid));
//                            api.getQr(uuid, new Callback<ResponseBody>() {
//                                @Override
//                                public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
//                                    if (response.isSuccessful()) {
//                                        try {
//                                            imageView.setImageBitmap(getBitmapFromByte(response.body().bytes()));
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                        new Thread(new Runnable(){
//                                            @Override
//                                            public void run() {
//                                                getLoginInfo();
//                                            }
//                                        }).start();
//                                    } else {
//                                        Log.i("扫描获取失败","aa");
//                                    }
//                                }
//                                @Override
//                                public void onFailure(Call<ResponseBody> call, Throwable t) {
//                                }
//                            });
//                        } else {
//                            Log.i("我的信息","错误的状态码");
//                        }
//                    }
//                } else {
//                    Log.i("我的信息","获取UUID失败");
//                }
//            }
//
//            @Override
//            public void onError(int err_code) {
//                Log.i("我的信息", "onError:" + err_code);
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                Log.i("我的信息", "onFailure:" + t);
//            }
//        });
    }

    private void getLoginInfo(){
        wechatMeta.setTip(1);
        Log.i("我的信息","tip11:"+wechatMeta.getTip());
        boolean i = true;
        while (wechatMeta.getTip()!=3) {
            Log.i("我的信息","tip:"+wechatMeta.getTip());
            api.waitForLogin("" + wechatMeta.getTip(), wechatMeta.getUuid(), new BaseApi.ApiCallback() {
                @Override
                public void onSuccess(String res) {
                    if (null == res) {
                        Toast.makeText(MainActivity.this,"扫描二维码验证失败",Toast.LENGTH_LONG).show();
                        wechatMeta.setTip(3);
                    }
                    String code = Matchers.match("window.code=(\\d+);", res);
                    if (null == code) {
                        Toast.makeText(MainActivity.this,"扫描二维码验证失败",Toast.LENGTH_LONG).show();
                        wechatMeta.setTip(3);
                    } else {
                        if (code.equals("201")) {
                            Log.i("mainActivity","成功扫描,请在手机上点击确认以登录");
                            wechatMeta.setTip(0);
                        } else if (code.equals("200")) {
//                            wechatMeta.setTip(2);
                            Log.i("MainActivity","正在登录...");
                            String pm = Matchers.match("window.redirect_uri=\"(\\S+?)\";", res);
                            String redirect_uri = pm + "&fun=new";
                            wechatMeta.setRedirect_uri(redirect_uri);

                            String base_uri = redirect_uri.substring(0, redirect_uri.lastIndexOf("/")+1);
                            String url = redirect_uri.substring(redirect_uri.lastIndexOf("/")+1, redirect_uri.lastIndexOf("?"));
                            wechatMeta.setBase_uri(base_uri);
                            String ticket = getParams(redirect_uri,"ticket");
                            String uuid = wechatMeta.getUuid();
                            String lang = getParams(redirect_uri,"lang");
                            String scan = getParams(redirect_uri,"scan");
                            String fun = getParams(redirect_uri,"fun");

                            login(base_uri, url, ticket,uuid, lang,scan,fun);
                        } else if (code.equals("408")) {
                            Toast.makeText(MainActivity.this,"登录超时",Toast.LENGTH_LONG).show();
                            wechatMeta.setTip(2);  //1 未扫描  0 扫描
                        } else {
                            Log.i("我的信息","扫描code="+ code);
                        }
                    }
                }
                @Override
                public void onError(int err_code) {
                    Log.i("扫描登录失败", ""+err_code);
                    wechatMeta.setTip(3);
                }
                @Override
                public void onFailure(Throwable t) {
                    Log.i("扫描登录失败", t.toString());
                    wechatMeta.setTip(3);
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

                    BaseRequestParams params = new BaseRequestParams();
                    params.setUin(wechatMeta.getWxuin());
                    params.setSid(wechatMeta.getWxsid());
                    params.setSkey(wechatMeta.getSkey());
                    params.setDeviceID(wechatMeta.getDeviceId());
                    wechatMeta.setBaseRequest(params);
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
     * 微信初始化
     */
    public void wxInit(String baseUrl,WechatMeta wechatMeta) {
        String url = wechatMeta.getBase_uri() + "/webwxinit?r=" + DataUtil.getCurrentUnixTime() + "&pass_ticket="
                + wechatMeta.getPass_ticket() + "&skey=" + wechatMeta.getSkey();

        LoginApi api = new LoginApi(baseUrl) ;

        Gson gson = new Gson();
        String jsonObject = gson.toJson(wechatMeta.getBaseRequest()); // {"name":"怪盗kidou","age":24}

        api.WxInit(url, jsonObject, new BaseApi.ApiCallback() {
            @Override
            public void onSuccess(String res) {
                Log.i("我的信息","微信初始化::"+res);
                if (res==null||res.length()==0){
                    Log.i("我的信息","微信初始化失败");
                }
            }
            @Override
            public void onError(int err_code) {
                Log.i("我的信息","微信初始化 onError");
            }
            @Override
            public void onFailure(Throwable t) {
                Log.i("我的信息","微信初始化 onFailure");
            }
        });
//        HttpRequest request = HttpRequest.post(url).contentType("application/json;charset=utf-8")
//                .header("Cookie", wechatMeta.getCookie()).send(body.toString());
        try {
//            JSONObject jsonObject = JSONKit.parseObject(res);
//            if (null != jsonObject) {
//                JSONObject BaseResponse = jsonObject.get("BaseResponse").asJSONObject();
//                if (null != BaseResponse) {
//                    int ret = BaseResponse.getInt("Ret", -1);
//                    if (ret == 0) {
//                        wechatMeta.setSyncKey(jsonObject.get("SyncKey").asJSONObject());
//                        wechatMeta.setUser(jsonObject.get("User").asJSONObject());
//
//                        StringBuffer synckey = new StringBuffer();
//                        JSONArray list = wechatMeta.getSyncKey().get("List").asArray();
//                        for (int i = 0, len = list.size(); i < len; i++) {
//                            JSONObject item = list.get(i).asJSONObject();
//                            synckey.append("|" + item.getInt("Key", 0) + "_" + item.getInt("Val", 0));
//                        }
//                        wechatMeta.setSynckey(synckey.substring(1));
//                    }
//                }
//            }
        } catch (Exception e) {
        }
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
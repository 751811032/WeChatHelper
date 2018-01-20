
package com.rain.wechathelper.params;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;


/**
 * Created by Rain on 2016/4/27.
 */
public class LoginBiz extends AsyncTask<Object, Integer, Test> {

    private String userAndWebName;
    private String userName;
    private String passWord;
    private String webName;

    public static boolean isCanLogin;
    private boolean checked = false;
    private int lineIndex = 0;
    private Test loginBean;
    private int lineNum = 0;

    public LoginBiz() {
        isCanLogin = canLogin();
    }

    public boolean canLogin() {
        if (userAndWebName == null || userAndWebName.equals("") || userAndWebName.length() == 0) {
            return false;
        } else {
            String[] detail = userAndWebName.split("@");
            Log.i("username","detail.length = "+detail.length +"   detail[0] = "+detail[0]);
            if (!userAndWebName.contains("@") || detail.length == 1) {
                userName = userAndWebName;
                webName = "";
            } else {
                webName = detail[detail.length - 1];
                int a = webName.length();
                userName = userAndWebName.substring(0, userAndWebName.length() - a - 1);
            }
        }
        return true;
    }

    //该方法并不运行在UI线程内，所以在方法内不能对UI当中的控件进行设置和修改
    //主要用于进行异步操作
    @Override
    protected Test doInBackground(Object... params) {
        // 打开网站
//        String userName = "RM000";
//        String passWord = "111111";
//        String webName = "tt";
        Gson gson = new Gson();
        Test test = new Test();  test.setData("string");test.setPageindex(0);test.setPagesize(0);test.setType(0);test.setUid("18515466954");
        String jsonString = HttpUtil.sendPost("http://39.108.183.137:8041/collection/GetCollectionList",gson.toJson(test));
        Log.i("aaaa", "url:" + jsonString);
        Test bean = new Test();
        return bean;
    }

    //该方法运行在Ui线程内，可以对UI线程内的控件设置和修改其属性
    @Override
    protected void onPreExecute() {
    }

    //在doInBackground方法当中，每次调用publishProgrogress()方法之后，都会触发该方法
    @Override
    protected void onProgressUpdate(Integer... values) {
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            lineIndex = msg.what;

            return false;
        }
    });

    //在doInBackground方法执行结束后再运行，并且运行在UI线程当中
    //主要用于将异步操作任务执行的结果展示给用户
    @Override
    protected void onPostExecute(final Test bean) {
        Log.i("我的信息", "LoginBean:" + bean.toString());
        loginBean = bean;
    }
}

package com.rain.wechathelper.params;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Rain on 2016/4/27.
 */
public class HttpUtil {
    public static String sendGet(String urlString, String sParams) {
        String sReturn = "";
        URL url = null;
        HttpURLConnection conn = null;
        BufferedReader in = null;
        try {
            url = new URL( urlString);

            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);

            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.connect();

            conn.getOutputStream().write(sParams.getBytes());
            // 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream）
            conn.getOutputStream().flush();
            // 关闭流对象。此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中,
            // 在调用下边的getInputStream()函数时才把准备好的http请求正式发送到服务器
            conn.getOutputStream().close();

            // 调用HttpURLConnection连接对象的getInputStream()函数,
            // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
            //  InputStream inStrm = httpConn.getInputStream(); // <===注意，实际发送请求的代码段就在这里
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            sReturn = sb.toString();
            sb = null;
        } catch (Exception e) {
            e.printStackTrace();
            sReturn = "netError";
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    sReturn = "netError";
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    sReturn = "netError";
                    e.printStackTrace();
                }
            }
            url = null;
            conn = null;
            in = null;
        }
        return sReturn;
    }

    public static String sendPost(String urlString, String sParams) {
        String sReturn = "";
        URL url = null;
        HttpURLConnection conn = null;
        BufferedReader in = null;
        try {
            url = new URL(urlString);
            Log.i("url", "url");
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(20000);

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);
            Log.i("httpSend", "connect");
            conn.setRequestProperty("Content-Type",
                    "application/json");
            conn.connect();

            conn.getOutputStream().write(sParams.getBytes());
            // 刷新对象输出流，将任何字节都写入潜在的流中（些处为ObjectOutputStream）
            conn.getOutputStream().flush();
            // 关闭流对象。此时，不能再向对象输出流写入任何数据，先前写入的数据存在于内存缓冲区中,
            // 在调用下边的getInputStream()函数时才把准备好的http请求正式发送到服务器
            conn.getOutputStream().close();

            // 调用HttpURLConnection连接对象的getInputStream()函数,
            // 将内存缓冲区中封装好的完整的HTTP请求电文发送到服务端。
            //  InputStream inStrm = httpConn.getInputStream(); // <===注意，实际发送请求的代码段就在这里
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            sReturn = sb.toString();

            sb = null;
        } catch (Exception e) {
            e.printStackTrace();
            sReturn = "netError";
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    sReturn = "netError";
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception e) {
                    sReturn = "netError";
                    e.printStackTrace();
                }
            }
            url = null;
            conn = null;
            in = null;
        }
        return sReturn;
    }
}

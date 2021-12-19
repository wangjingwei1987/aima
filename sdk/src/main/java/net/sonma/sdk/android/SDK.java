/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package net.sonma.sdk.android;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

/**
 * Description:
 * User: wanhongming
 * Date: 2017-06-03
 * Time: 下午10:19
 */

public class SDK {


    private interface InstanceHolder {
        SDK INSTANCE = new SDK();
    }

    public static SDK getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private SDK() {
        this(Env.DEMO_ONLINE);
    }

    public void setEnv(Env env) {
        this.env = env;
    }

    public Env getEnv() {
        return env;
    }

    private Env env;

    private SDK(Env env) {
        this.env = env;
    }

    public static void main(String[] args) {
        System.out.println(SDK.getInstance().print(123456789, "", 10086L, null));
    }

    public JSONObject print(long sn, String content, Long template, String token) {
        //参数构造
        SortedMap<String, String> params = new TreeMap<>();
        params.put(API.PARAMS.SN, String.valueOf(sn));
        params.put(API.PARAMS.CONTENT, content);
        //模板ID,不传模板编号默认以模板加数据的形式进行解析
        if (template != null) {
            params.put(API.PARAMS.TEMPLATE, template.toString());
        }
        if (token != null) {
            params.put(API.PARAMS.TOKEN, token);
        }
        //不使用Token鉴权时,请求必须附带签名
        return process("POST", API.PRINT, params, token == null);
    }


    public String createToken(String scope, long seconds) {
        //请求参数
        SortedMap<String, String> params = new TreeMap<>();
        params.put(API.PARAMS.SCOPE, scope);
        //过期时间
        long exp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + seconds;
        params.put(API.PARAMS.EXP, String.valueOf(exp));
        //附带签名
        JSONObject json = process("GET", API.ACCESS_TOKEN, params, true);
        if (json != null) {
            try {
                return json.getString("token");
            } catch (JSONException ignored) {
            }
        }

        return null;
    }


    private String createQueryString(SortedMap<String, String> params) {
        //排序
        StringBuilder queryString = new StringBuilder();
        //创建规范查询字符串CanonicalQueryString,对参数名和值使用URI 编码

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (queryString.length() != 0) {
                queryString.append("&");
            }
            queryString.append(SignatureUtil.encodeRFC3986(entry.getKey())).
                    append("=").
                    append(SignatureUtil.encodeRFC3986(entry.getValue()));
        }
        return queryString.toString();
    }


    private String createAuthorization(long timestamp, String canonicalQueryString) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        Log.i("sdk", "规范查询字符串(CanonicalQueryString):" + canonicalQueryString);
        String hashedQueryString = SignatureUtil.sha1AsHex(canonicalQueryString);
        Log.i("sdk", "规范查询字符串哈希(HashedCanonicalQueryString):" + hashedQueryString);
        String stringToSign = timestamp + "\n" + hashedQueryString;
        Log.i("sdk", "待签字符串(StringToSign):" + stringToSign);
        String signature = SignatureUtil.macSignature(stringToSign, env.getSecretKey());
        Log.i("sdk", "签名(Signature):" + signature);
        String authorization = Base64.encodeToString(String.format("HMAC-SHA1 %s:%s", env.getAccessKey(), signature).getBytes("UTF-8"), Base64.NO_WRAP);
        Log.i("sdk", "鉴权字符串(Authorization):" + authorization);
        return authorization;
    }


    private JSONObject process(String method, String api, SortedMap<String, String> params, boolean sign) {

        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            String queryString = createQueryString(params);


            if (method.equals("POST")) {
                conn = (HttpURLConnection) new URL(env.getHost() + api).openConnection();
                conn.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);
            } else {
                conn = (HttpURLConnection) new URL(env.getHost() + api + "?" + queryString).openConnection();
                conn.setDoOutput(false);
            }

            Log.i("sdk", "process: " + method  + " " + conn.getURL());
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setReadTimeout(5000);


            if (sign) {
                Long timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
                String authorization = createAuthorization(timestamp, queryString);
                conn.setRequestProperty(API.HEADER.AUTHORIZATION, authorization);
                conn.setRequestProperty(API.HEADER.TIMESTAMP, timestamp.toString());
            }


            conn.connect();


            if (method.equals("POST")) {
                OutputStream outputStream = conn.getOutputStream();
                outputStream.write(queryString.getBytes());
                outputStream.flush();
                outputStream.close();
                Log.i("sdk", "post: " + queryString);
            }


            int httpStatus = conn.getResponseCode();

            inputStream = httpStatus < HttpURLConnection.HTTP_BAD_REQUEST  ? conn.getInputStream() : conn.getErrorStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder response = new StringBuilder();

            String buf;
            while ((buf = reader.readLine()) != null) {
                response.append(buf);
            }

            if (httpStatus == HttpsURLConnection.HTTP_OK) {
                Log.i("sdk", "response: " +  response);
            } else {
                Log.e("sdk", "error: " + response);
            }

            return new JSONObject(response.toString());
        } catch (IOException | NoSuchAlgorithmException | JSONException | InvalidKeyException e) {
            e.printStackTrace();
            Log.e("sdk", "error: " + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }


}

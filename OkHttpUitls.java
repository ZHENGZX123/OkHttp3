package cn.kiway.exam.http;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.kiway.exam.KwApp;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static cn.kiway.exam.http.CacheInterceptor.maxAge;


/**
 * Created by Administrator on 2019/1/21.
 */

public class OkHttpUitls {

    static OkHttpUitls okHttpUtils;
    static OkHttpClient okHttpClient;
    static int maxCacheSize = 10 * 1024 * 1024;
    static Cache cache = new Cache(new File("/sdcard/exam/"),
            maxCacheSize);

    public static OkHttpUitls getInstance() {
        if (okHttpUtils == null) {
            okHttpUtils = new OkHttpUitls();
        }
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .connectionPool(new ConnectionPool())
                    .addInterceptor(new CacheInterceptor())
                    .cookieJar(new CookieJarImpl(new SPCookieStore(KwApp.getInstance())))
                    .cache(cache)
                    .build();
        }
        return okHttpUtils;
    }
    /**
     * 获取全局的cookie实例
     */
    public CookieJarImpl getCookieJar() {
        return (CookieJarImpl) okHttpClient.cookieJar();
    }
    public void get(String url, Map<String, String> params, boolean needCache, final HttpCallBack callBack) {
        StringBuilder urlBuilder = new StringBuilder(url);
        if (params != null) {
            urlBuilder.append("?");
            for (String key : params.keySet()) {
                urlBuilder.append(key).append("=").append(params.get(key)).append("&");
            }
        }

        Request request = new Request.Builder()
                .cacheControl(cacheControl(needCache)).url(urlBuilder.toString()).get().build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
              //  Logger.d("onFailure: ");
                if (callBack != null) {
                    callBack.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (callBack != null) {
                        boolean isCache = Boolean.parseBoolean(response.header("isCache"));
                        JSONObject data = new JSONObject(response.body().string());
                        //TODO 重登录
                        callBack.onResponse(call, data, isCache);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void postRequest(String url, RequestBody requestBody, boolean needCache, final HttpCallBack callBack) {
        Request request = new Request.Builder()
                .cacheControl(cacheControl(needCache)).url(url).post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               // Logger.d("onFailure: ");
                if (callBack != null) {
                    callBack.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (callBack != null) {
                        boolean isCache = Boolean.parseBoolean(response.header("isCache"));
                        JSONObject data = new JSONObject(response.body().string());
                        //TODO 重登录
                        callBack.onResponse(call, data, isCache);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void postForm(String url, Map<String, String> params, boolean needCache, final HttpCallBack callBack) {
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
        Request request = new Request.Builder()
                .cacheControl(cacheControl(needCache)).url(url).post(builder.build())
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
               // Logger.d("onFailure: ");
                if (callBack != null) {
                    callBack.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (callBack != null) {
                        boolean isCache = Boolean.parseBoolean(response.header("isCache"));
                        JSONObject data = new JSONObject(response.body().string());
                        //TODO 重登录
                        callBack.onResponse(call, data, isCache);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Post请求发送JSON数据
     * 参数一：请求Url
     * 参数二：请求的JSON
     * 参数三：请求回调
     */
    public void postJson(String url, String jsonParams, boolean needCache, final HttpCallBack callBack) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams);
        Request request = new Request.Builder().url(url).cacheControl
                (cacheControl(needCache)).post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
             //   Logger.d("onFailure: ");
                if (callBack != null) {
                    callBack.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (callBack != null) {
                        boolean isCache = Boolean.parseBoolean(response.header("isCache"));
                        JSONObject data = new JSONObject(response.body().string());
                        //TODO 重登录
                        callBack.onResponse(call, data, isCache);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }



    public CacheControl cacheControl(boolean needCache) {
        CacheControl cacheControl;
        if (needCache)
            cacheControl = new CacheControl.Builder()
                    .maxAge(maxAge, TimeUnit.MINUTES).build();
        else
            cacheControl = new CacheControl.Builder()
                    .noCache()
                    .build();
        return cacheControl;
    }

}

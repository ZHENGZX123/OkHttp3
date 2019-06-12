package com.kiway.smartclass.student.http;

import com.kiway.smartclass.student.util.CommandUtil;
import com.kiway.smartclass.student.util.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.kiway.smartclass.student.http.CacheInterceptor.maxAge;
import static com.kiway.smartclass.student.util.IContant.OKHTTPCACHE;

/**
 * Created by Administrator on 2019/1/21.
 */

public class OkHttpUitls {

    static OkHttpUitls okHttpUtils;
    static OkHttpClient okHttpClient;
    static int maxCacheSize = 10 * 1024 * 1024;
    static Cache cache = new Cache(new File(OKHTTPCACHE),
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
                    .cache(cache)
                    .build();
        }
        return okHttpUtils;
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
                Logger.d("onFailure: ");
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
                Logger.d("onFailure: ");
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
                Logger.d("onFailure: ");
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
                Logger.d("onFailure: ");
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


    public void uploadFile(String url, String path, final HttpCallBack callBack) {
        File file = new File(path);
        if (!file.exists()) {
            Logger.e("文件不能为空");
            return;
        }
        MultipartBody.Builder mBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        String fileMimeType = CommandUtil.getMimeType(file);
        MediaType mediaType = MediaType.parse(fileMimeType);
        RequestBody fileBody = RequestBody.create(mediaType, file);
        mBody.addFormDataPart("file", file.getName(), fileBody);
        RequestBody requestBody = mBody.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.d("onFailure: ");
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

    public void uploadFile(String url, List<String> filelist, final HttpCallBack callBack) {
        MultipartBody.Builder mBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        int i = 0;
        for (String filePath : filelist) {
            File file = new File(filePath);
            if (!file.exists()) {
                Logger.e("上传" + filePath + "文件不存在!!!!!!!!!");
                continue;
            }
            String fileMimeType = CommandUtil.getMimeType(file);
            //这里获取文件类型，方法自己定义
            MediaType mediaType = MediaType.parse(fileMimeType);
            RequestBody fileBody = RequestBody.create(mediaType, file);
            mBody.addFormDataPart("file" + i, file.getName(), fileBody);
            i++;
        }
        RequestBody requestBody = mBody.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Logger.d("onFailure: ");
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

    public void downFile(String fileUrl, final String destFileDir, final String fileName, final HttpCallBack callBack) {
        if (!new File(destFileDir).exists())
            new File(destFileDir).mkdirs();
        final File file = new File(destFileDir, fileName);
        Request request = new Request.Builder().url(fileUrl).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                if (callBack != null) {
                    callBack.onFailure(call, e);
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    if (callBack != null) {
                        JSONObject data = new JSONObject();
                        data.put("path", file.getAbsolutePath());
                        callBack.onResponse(call, data, false);
                    }
                } catch (IOException e) {

                    if (callBack != null) {
                        callBack.onFailure(call, e);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {

                    }
                }
            }
        });
    }

    int max = 3;

    public void downFile(String fileUrl, final String destFileDir, final String fileName, final DownloadListener
            listener) {
        if (listener == null) {
            Logger.d("回调不能为空");
            return;
        }
        if (max <= 0) {
            Logger.d("超过最大下载数");
            return;
        }
        if (!new File(destFileDir).exists())
            new File(destFileDir).mkdirs();
        final File file = new File(destFileDir, fileName);
        Request request = new Request.Builder().url(fileUrl).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onFailure(call, e);
                max++;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    listener.start(total);
                    max--;
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中更新进度条
                        listener.loading(progress);
                    }
                    fos.flush();
                    // 下载完成
                    max++;
                    listener.complete(file);
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.loadFailure(e);
                    max++;
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

    public void reLogin() {

    }

    /** 取消所有请求请求 */
    public  void cancelAll() {
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            call.cancel();
        }
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            call.cancel();
        }
    }

}

package cn.kiway.exam.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2019/1/25.
 */

public class CacheInterceptor implements Interceptor {
    int maxRetry = 3;
    long nextInterval = 1000;
    // 有网络时 设置缓存超时时间分钟
    public final static int maxAge = 5;
    // 无网络时，设置超时为1天
    int maxStale = 60 * 24;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = doRequest(chain, request);
        int maxAgeSeconds = request.cacheControl().maxAgeSeconds();
        int count = 0;
        while ((response == null || !response.isSuccessful()) && count < maxRetry) {
            try {
                Thread.sleep(nextInterval);
             //   Logger.e(":::::::::重试接口:::::::::" + count + ":::::::::" + request.url().toString());
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InterruptedIOException();
            }
            count++;
        }
        if (response == null && maxAgeSeconds > 0) {
          //  Logger.e(":::::::::需要缓存:::::::::");
            request = request.newBuilder().cacheControl(new CacheControl.Builder()
                    .maxAge(maxStale, TimeUnit.MINUTES).build()).build();
            response = chain.proceed(request);
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    .addHeader("isCache", "true")
                    .build();
        } else {
            if (response == null)
                response = chain.proceed(request);
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    .addHeader("isCache", "false")
                    .build();
        }
        return response;
    }

    private Response doRequest(Chain chain, Request request) {
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
        }
        return response;
    }
}

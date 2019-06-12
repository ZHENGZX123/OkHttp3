package com.kiway.smartclass.student.http;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;

/**
 * Created by Administrator on 2019/1/24.
 */

public interface DownloadListener {
    /**
     *  开始下载
     */
    void start(long max);
    /**
     *  正在下载
     */
    void loading(int progress);
    /**
     *  下载完成
     */
    void complete(File file);
    /**
     *  请求失败
     */
   void onFailure(Call call, IOException e);
    /**
     *  下载过程中失败
     */
    void loadFailure(Exception message);
}

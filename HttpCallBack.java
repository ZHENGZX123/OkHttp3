package com.kiway.smartclass.student.http;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;

/**
 * Created by Administrator on 2019/1/21.
 */

public interface HttpCallBack {
    public void onResponse(Call call, JSONObject data, boolean isCache);

    public void onFailure(Call call, IOException e);
}

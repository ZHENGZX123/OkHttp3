package cn.kiway.exam.http;

import android.util.Log;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by Administrator on 2019/5/5.
 */

public class CookieUtil {
    /**
     * 获取指定URL对应的cookie
     *
     * @param baseUrl
     * @param url
     * @return
     */
    public static List<Cookie> cookies(String baseUrl, String url) {
        //一般手动取出cookie的目的只是交给 webview 等等，非必要情况不要自己操作
        CookieJarImpl.CookieStore cookieStore = OkHttpUitls.getInstance().getCookieJar().getCookieStore();
        HttpUrl httpUrl = HttpUrl.parse(baseUrl + url);
        List<Cookie> cookies = cookieStore.getCookie(httpUrl);
        Log.e("cookies: ", httpUrl.host() + "对应的cookie如下：" + cookies.toString());
        return cookies;
    }

    /**
     * 获取所有的cookie
     *
     * @return
     */
    public static List<Cookie> cookieList() {
        CookieJarImpl.CookieStore cookieStore = OkHttpUitls.getInstance().getCookieJar().getCookieStore();
        List<Cookie> allCookie = cookieStore.getAllCookie();
        Log.e("所有cookie如下: ", allCookie.toString());
        return allCookie;
    }

    public static String getCookie() {
        CookieJarImpl.CookieStore cookieStore = OkHttpUitls.getInstance().getCookieJar().getCookieStore();
        List<Cookie> allCookie = cookieStore.getAllCookie();
        for (int i = 0; i < allCookie.size(); i++) {
            return allCookie.get(i).toString();
        }
        return null;
    }

    /**
     * 删除cookie（这里是全部删除，也可指定的地址删除）
     */
    public static void removeCookie() {
        CookieJarImpl.CookieStore cookieStore = OkHttpUitls.getInstance().getCookieJar().getCookieStore();
        cookieStore.removeAllCookie();

    }
}
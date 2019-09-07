package com.billing.net;

import com.billing.main.Logs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by duanjy on 2018/5/8.
 */

public class OkHttpClientRequest {
    private static OkHttpClientRequest instance = null;
    private static OkHttpClient client = null;
    private static long CONNECTTIMEOUT = 10000;
    private static long READTIMEOUT = 10000;
    private static long WRITETIMEOUT = 10000;

    public synchronized static OkHttpClientRequest get() {
        if (instance == null) {
            instance = new OkHttpClientRequest();
            client = new OkHttpClient();
            client.newBuilder().connectTimeout(CONNECTTIMEOUT, TimeUnit.MILLISECONDS).readTimeout(READTIMEOUT, TimeUnit.MILLISECONDS)
                    .writeTimeout(WRITETIMEOUT, TimeUnit.MILLISECONDS);
        }
        return instance;
    }

    public String SentRequest(String ResUrl, FormBody formBody) {
        final Request request = new Request.Builder().url(ResUrl).post(formBody).build();
        Call call = client.newCall(request);
        try {
            String result = call.execute().body().string();
            Logs.logE("result", result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

}

package com.billing.runnable;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.billing.bean.AppInfo;
import com.billing.main.Logs;
import com.billing.main.Util;
import com.billing.net.OkHttpClientRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import okhttp3.FormBody;

public class SaveVerificationRunnable implements Runnable {
    private Context context = null;
    private Handler mHandler = null;
    private String httpUrl = "";
    String phonenum;
    String phoneverify;
    String billingid;
    String productid;
    String product;
    String verifycode;
    String verification;
    String billflag;

    public SaveVerificationRunnable(String httpUrl, Context context,
                                    String phonenum, String phoneverify, String billingid,
                                    String productid, String product, String verifycode,
                                    String verification, String billflag) {
        this.httpUrl = httpUrl;
        this.context = context;
        this.mHandler = mHandler;
        this.phonenum = phonenum;
        this.phoneverify = phoneverify;
        this.billingid = billingid;
        this.productid = productid;
        this.product = product;
        this.verifycode = verifycode;
        this.verification = verification;
        this.billflag = billflag;

    }


    @Override
    public void run() {
        String req = "";
        String appid = AppInfo.sharedAppInfo().appid;
        String apphash = AppInfo.sharedAppInfo().apphash;
        String spid = AppInfo.sharedAppInfo().spid;
        String channelid = AppInfo.sharedAppInfo().channelid;
        String imsi = AppInfo.sharedAppInfo().imsi;
        String imei = AppInfo.sharedAppInfo().imei;
        String clientip = AppInfo.sharedAppInfo().clientip;
        JSONObject reqJsonData = new JSONObject();
        try {
            reqJsonData.put("appid", appid);
            reqJsonData.put("apphash", apphash);
            reqJsonData.put("spid", spid);
            reqJsonData.put("channelid", channelid);
            reqJsonData.put("imsi", imsi);
            reqJsonData.put("imei", imei);
            reqJsonData.put("clientip", clientip);
            reqJsonData.put("phonenum", phonenum);
            reqJsonData.put("phoneverify", phoneverify);
            reqJsonData.put("billingid", billingid);
            reqJsonData.put("productid", productid);
            reqJsonData.put("verifycode", verifycode);
            reqJsonData.put("verification", verification);
            reqJsonData.put("billflag", billflag);
            reqJsonData.put("product", product);
            gbEncoding(product);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        req = reqJsonData.toString();
        String result = OkHttpClientRequest.get().SentRequest(httpUrl + "gwOrder.do", getCommit(req));
        try {
            String resultString = new String(Util.b64Decode(result.toString()),
                    "GBK");
            Logs.logE("resultString", resultString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Message msg = mHandler.obtainMessage();
        msg.what = 6;
        msg.obj = result;
        mHandler.sendMessage(msg);
    }

    private FormBody getCommit(String json) {

        FormBody formBody = new FormBody
                .Builder()
                .add("requestParams", json)
                .build();
        return formBody;
    }

    private String gbEncoding(String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int byteIndex = 0; byteIndex < utfBytes.length; byteIndex++) {
            String hexB = Integer.toHexString(utfBytes[byteIndex]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }
}


package com.billing.main;

import android.util.Log;

/**
 * 日志类
 */
public class Logs {

    /** log-info 输出标记 */
    /**
     * log-error 输出标记
     */
    private static boolean isFlagError = true;// true

    /**
     * log error 信息输出
     *
     * @param msg     输出信息
     * @param
     */
    public static void logE(String tag, String msg) {
        if (isFlagError) {
            Log.e(tag, msg);
        }
    }

}

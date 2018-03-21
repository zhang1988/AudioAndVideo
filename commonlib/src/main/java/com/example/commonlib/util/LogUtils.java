package com.example.commonlib.util;

import android.util.Log;

/**
 * com.example.commonlib.util.LogUtils
 *
 * @author zhangchao
 * @date 2018/3/17
 * @desc LogUtils Log工具类
 */

public class LogUtils {
    private final static String TAG = "LogUtils";
    private static boolean isDebugMode = false;

    public static void setDebugMode(boolean isDebug) {
        isDebugMode = isDebug;
    }

    /**
     * 普通log打印
     *
     * @param tag
     * @param log
     */
    public static void print(String tag, String log) {
        if (StringUtils.isEmpty(tag)) {
            if (isDebugMode) {
                throw new RuntimeException("log tag can not be null!");
            }
            return;
        }
        if (StringUtils.isEmpty(log)) {
            return;
        }
        if (!isDebugMode) {
            return;
        }
        Log.d(tag, log);
    }

    /**
     * 调试时 临时使用
     *
     * @param log
     */
    public static void print(String log) {
        if (StringUtils.isEmpty(log)) {
            return;
        }

        Log.e(TAG, log);
    }

    /**
     * 调试 临时使用
     *
     * @param position 调试时确认打印位置
     * @param log
     */
    public static void print(int position, String log) {
        if (StringUtils.isEmpty(log)) {
            return;
        }

        Log.e(String.valueOf(position), log);
    }
}

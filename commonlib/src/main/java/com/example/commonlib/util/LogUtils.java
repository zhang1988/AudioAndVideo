package com.example.commonlib.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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

    /* use this to show Toast with Activity lifecycle. */
    public static void showToast(Activity activity, final int resId) {
        if (activity != null) {
            showToast(activity, activity.getString(resId));
        }
    }

    /* use this to show Toast with Activity lifecycle. */
    public static void showToast(Activity activity, final String s) {
        if (activity == null) {
            return ;
        }
        Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
    }

//    /* use this to show Toast with Application lifecycle. */
//    public static void showToast(final int resId) {
//        showToast(AVApp.getContext().getString(resId));
//    }
//
//    /* use this to show Toast with Application lifecycle. */
//    public static void showToast(final String s) {
//        final Context tApplicationContext = .getContext();
//        new Handler(Looper.getMainLooper()).post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(tApplicationContext, s, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}

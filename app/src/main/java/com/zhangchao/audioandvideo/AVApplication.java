package com.zhangchao.audioandvideo;

import android.app.Application;

/**
 * Created by zhangchao on 18-3-22.
 */

public class AVApplication extends Application {
    private static AVApplication mInstance;

    public AVApplication() {
    }

    public static AVApplication getAVApplication() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
}

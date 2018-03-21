package com.zhangchao.audioandvideo.main;

import android.app.Activity;

/**
 * Created by zhangchao on 18-3-16.
 */

public class ClassInfo {
    public ClassInfo(int titleRes, Class<? extends Activity> targetClass) {
        this.titleRes = titleRes;
        this.targetClass = targetClass;
    }

    int titleRes;
    Class<? extends Activity> targetClass;
}

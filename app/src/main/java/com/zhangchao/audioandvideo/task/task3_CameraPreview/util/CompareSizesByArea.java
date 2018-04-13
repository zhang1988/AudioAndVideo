package com.zhangchao.audioandvideo.task.task3_CameraPreview.util;

/**
 * Created by zhangchao on 16-7-29.
 */

import android.util.Size;

import java.util.Comparator;

public class CompareSizesByArea implements Comparator<Size> {

    @Override
    public int compare(Size lhs, Size rhs) {
        // We cast here to ensure the multiplications won't overflow
        return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                (long) rhs.getWidth() * rhs.getHeight());
    }

}

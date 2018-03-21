package com.zhangchao.audioandvideo.Util;

import com.zhangchao.audioandvideo.ClassInfo;
import com.zhangchao.audioandvideo.R;
import com.zhangchao.audioandvideo.task.task1_showbitmap.Task1ShowBitmapActivity;
import com.zhangchao.audioandvideo.task.task2_audiocaptureandplay.Task2AudioCaptureAndPlayActivity;
import com.zhangchao.audioandvideo.task.task3_CameraPreview.Task3CameraPreviewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangchao on 18-3-16.
 */

public class ModelUtil {

    public static List<ClassInfo> getMainList() {
        List<ClassInfo> result = new ArrayList<>(16);
        result.add(new ClassInfo(R.string.title1, Task1ShowBitmapActivity.class));
        result.add(new ClassInfo(R.string.title2, Task2AudioCaptureAndPlayActivity.class));
        result.add(new ClassInfo(R.string.title3, Task3CameraPreviewActivity.class));
        return result;
    }
}

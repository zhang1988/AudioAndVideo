package com.zhangchao.audioandvideo.Util;

import com.zhangchao.audioandvideo.main.ClassInfo;
import com.zhangchao.audioandvideo.R;
import com.zhangchao.audioandvideo.task.task1_showbitmap.Task1ShowBitmapActivity;
import com.zhangchao.audioandvideo.task.task2_audiocaptureandplay.Task2AudioCaptureAndPlayActivity;
import com.zhangchao.audioandvideo.task.task3_CameraPreview.camera2.Task3Camera2PreviewActivity;
import com.zhangchao.audioandvideo.task.task3_CameraPreview.Task3CameraPreviewActivity;
import com.zhangchao.audioandvideo.task.task_ffmpeg.Task_FFmpegActivity;
import com.zhangchao.audioandvideo.task.task4_extractormuxer.Task4ExtractorAndMuxerActivity;

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
        result.add(new ClassInfo(R.string.title3, Task3Camera2PreviewActivity.class));
        result.add(new ClassInfo(R.string.title10, Task_FFmpegActivity.class));
        result.add(new ClassInfo(R.string.title4, Task4ExtractorAndMuxerActivity.class));
        return result;
    }
}

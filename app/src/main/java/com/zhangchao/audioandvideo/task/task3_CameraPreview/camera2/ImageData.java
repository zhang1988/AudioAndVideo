package com.zhangchao.audioandvideo.task.task3_CameraPreview.camera2;

import android.media.Image;

import java.io.File;

/**
 * * Created by zhangchao on 18-3-27.
 */

public class ImageData {
    private final Image mImage;
    private final File mFile;

    public ImageData(Image image, File file) {
        mImage = image;
        mFile = file;
    }

    public Image getImage() {
        return mImage;
    }

    public File getFile() {
        return mFile;
    }

}

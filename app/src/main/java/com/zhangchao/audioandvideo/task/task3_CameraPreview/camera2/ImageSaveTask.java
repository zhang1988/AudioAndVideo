package com.zhangchao.audioandvideo.task.task3_CameraPreview.camera2;

import android.media.Image;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zhangchao on 18-3-27.
 */

public class ImageSaveTask extends AsyncTask<ImageData, Void, File> {
    @Override
    protected File doInBackground(ImageData... params) {
        Image image = params[0].getImage();
        File file = params[0].getFile();
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            output.write(bytes);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            image.close();
            if (null != output) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return file;
    }
}

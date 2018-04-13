package com.zhangchao.audioandvideo.task.task3_CameraPreview;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhangchao.audioandvideo.AVApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhangchao on 18-3-21.
 */

public class MediaFileHelper {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_AUDIO = 3;

    /**
     * Create a file Uri for saving an image or video
     */
    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video or pcm
     */
    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        //getExternalStoragePublicDirectory 是选择Android系统的公共文件夹，比如Pictures等
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else if (type == MEDIA_TYPE_AUDIO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "audio_" + timeStamp + ".pcm");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static File getOutputMediaPath() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        //getExternalStoragePublicDirectory 是选择Android系统的公共文件夹，比如Pictures等
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            Log.d("MyCameraApp", "no directory");
            return null;
        }
        return mediaStorageDir;
    }

    public static List<String> getOutputMediaFilesPath(int type) {
        List<String> filePathList = new ArrayList<>(16);
        File fileDir = getOutputMediaPath();
        if (fileDir != null) {
            String fileExtendedName = getFileExtendedNameByType(type);
            for (File file : fileDir.listFiles()) {
                if (file != null && file.isFile()) {
                    String path = file.getAbsolutePath();
                    if (path.contains(fileExtendedName)) {
                        filePathList.add(path);
                    }
                }
            }
        }
        return filePathList;
    }

    private static String getFileExtendedNameByType(int type) {
        if (type == MEDIA_TYPE_IMAGE) {
            return ".jpg";
        } else if (type == MEDIA_TYPE_VIDEO) {
            return ".mp4";
        } else if (type == MEDIA_TYPE_AUDIO) {
            return ".pcm";
        } else {
            return ".jpeg";
        }
    }
}

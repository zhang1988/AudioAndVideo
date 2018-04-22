package com.zhangchao.audioandvideo.task.task4_extractormuxer;

import android.os.Environment;

import com.example.commonlib.util.FileUtils;
import com.example.commonlib.util.LogUtils;
import com.example.commonlib.util.MediaSelectUtil;
import com.example.commonlib.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * ${CLASS_NAME}
 *
 * @author zhangchao
 * @date 2018/4/5-下午3:23
 * @desc ${描述类实现的功能}
 */
public class ExtractorMuxerManager {
    public ExtractorMuxerManager() {
    }

    public void extract(String filePath){

        File dcimFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        try {
            //输入文件检查，如果为空，从dcim文件夹选择




        } catch (Exception e){

        }
    }

    public void muxer(){

    }
}

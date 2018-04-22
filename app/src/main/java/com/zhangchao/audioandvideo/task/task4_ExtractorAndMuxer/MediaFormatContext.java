package com.zhangchao.audioandvideo.task.task4_ExtractorAndMuxer;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import com.example.commonlib.util.LogUtils;
import com.example.commonlib.util.StringUtils;
import com.zhangchao.audioandvideo.task.task3_CameraPreview.MediaFileHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by zhangchao on 18-4-13.
 */

public class MediaFormatContext {
    private MediaExtractor mMediaExtractor;
    private MediaMuxer mMediaMuxer;
    public MediaFormatContext(){
    }


    public void extract(String inputFile) {
        FileOutputStream videoFos = null;
        FileOutputStream audioFos = null;
        File file = null;
        mMediaExtractor = new MediaExtractor();
        if (StringUtils.isEmpty(inputFile)) {
            return;
            //List<String> input = MediaFileHelper.getOutputMediaFilesPath(MediaFileHelper.MEDIA_TYPE_VIDEO);
            //if (input.size() > 0){

            //}
        }
        try {
            file = new File(inputFile);
            if (!(file.exists() && file.isFile())) {
                return;
            }

            mMediaExtractor.setDataSource(inputFile);

            //
            //File videoFile = new File(dcimFile,"out_video.mp4");
            //File audioFile = new File(dcimFile,"out_audio");

        }catch (Exception e) {
            LogUtils.print(e.getMessage());
            return;
        }

        int numTracks = mMediaExtractor.getTrackCount();
        for (int i=0;i< numTracks;i++) {
            MediaFormat mediaFormat = mMediaExtractor.getTrackFormat(i);
        }

    }

    public void muxer(){
        //mMediaMuxer = new MediaMuxer();

    }

}

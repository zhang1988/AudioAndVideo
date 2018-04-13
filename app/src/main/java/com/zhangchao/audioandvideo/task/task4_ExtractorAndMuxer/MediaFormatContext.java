package com.zhangchao.audioandvideo.task.task4_ExtractorAndMuxer;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import com.example.commonlib.util.LogUtils;
import com.zhangchao.audioandvideo.task.task3_CameraPreview.MediaFileHelper;

/**
 * Created by zhangchao on 18-4-13.
 */

public class MediaFormatContext {
    private MediaExtractor mMediaExtractor;
    private MediaMuxer mMediaMuxer;
    public MediaFormatContext(){
    }


    public void extract(){
        mMediaExtractor = new MediaExtractor();
        try {
            mMediaExtractor.setDataSource(MediaFileHelper.getOutputMediaFilesPath(MediaFileHelper.MEDIA_TYPE_VIDEO).get(0));
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

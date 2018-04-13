package com.zhangchao.audioandvideo.task.task2_audiocaptureandplay;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;

import com.example.commonlib.util.LogUtils;
import com.zhangchao.audioandvideo.task.task3_CameraPreview.MediaFileHelper;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by zhangchao on 18-3-29.
 */

public class AudioManager {
    private static final int DEFAULT_RATE_IN_HZ = 44100;
    //default
    private int mAudioResource = MediaRecorder.AudioSource.MIC;//https://blog.csdn.net/m0_37039192/article/details/77776844
    private int mChannelConfig = AudioFormat.CHANNEL_IN_STEREO;//单声道还是双声道或者是左、右声道
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT;//编码格式
    private int mSampleRateInHz = DEFAULT_RATE_IN_HZ;
    private int mBufferSizeInBytes;

    private AudioRecord mRecord;
    private boolean mIsRecording;
    private OnRecordingListener mOnRecordingListener;

    private boolean mIsPlaying;
    private AudioTrack mTrack;

    AudioManager() {
        mBufferSizeInBytes = getProperBufferSize(0);
        mRecord = new AudioRecord(mAudioResource, mSampleRateInHz, mChannelConfig, mAudioFormat, mBufferSizeInBytes);
    }

    AudioManager(int audioSource, int sampleRateInHz, int channelConfig, int audioFormat, int bufferSizeInBytes) {
        mAudioResource = audioSource;
        mSampleRateInHz = sampleRateInHz;
        mAudioFormat = audioFormat;
        mChannelConfig = channelConfig;
        mBufferSizeInBytes = getProperBufferSize(bufferSizeInBytes);
        mRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
    }

    private int getProperBufferSize(int bufferSizeInBytes) {
        int min = AudioRecord.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat) * 2;
        return min > bufferSizeInBytes ? min : bufferSizeInBytes;
    }

    public void startRecord() {
        if (mIsRecording) {
            LogUtils.print("正在录音");
        } else {
            mIsRecording = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startRecordSync();
                }
            }).start();
        }
    }

    public void stopRecord() {
        mIsRecording = false;
    }

    private void startRecordSync() {
        LogUtils.print("录音开始");
        try {
            android.os.Process
                    .setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        } catch (Exception e) {
        }
        File pcmFile;
        try {
            mRecord.startRecording();
            byte[] buf = new byte[mBufferSizeInBytes];
            pcmFile = MediaFileHelper.getOutputMediaFile(MediaFileHelper.MEDIA_TYPE_AUDIO);
            FileOutputStream fos = new FileOutputStream(pcmFile);
            while (mIsRecording) {
                int read = mRecord.read(buf, 0, mBufferSizeInBytes);
                //大于0,读取成功
                LogUtils.print(String.valueOf(read));
                if (read > 0) {
                    if (mOnRecordingListener != null && mOnRecordingListener.onRecording(buf, 0)) {
                        return;
                    }
                    //默认处理，存储为pcm
                    fos.write(buf);
                }
            }

            try {
                fos.close();
            } catch (Exception e) {

            }
        } catch (Exception e) {
            LogUtils.print("录音失败：" + e.getMessage());
        } finally {

        }
    }

    interface OnRecordingListener {
        /**
         * 如果需要自己处理收到的数据可以实现此方法
         *
         * @param data
         * @param offSet
         * @return true: 不需要默认处理，false： 需要默认处理
         */
        boolean onRecording(byte[] data, int offSet);
    }

    //********************************AudioTrack*****************************************//
    //播放出来是噪音？？
    public void play(){
        if (mIsPlaying){
            LogUtils.print("is playing now");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                mIsPlaying = true;
                playSync();
            }
        }).start();
    }

    private void playSync() {
        int bufSize = AudioTrack.getMinBufferSize(mSampleRateInHz, mChannelConfig, mAudioFormat);
        //第二个参数必须与采样的时候一致
        mTrack = new AudioTrack(
                android.media.AudioManager.STREAM_MUSIC,
                mSampleRateInHz,
                mChannelConfig,
                mAudioFormat,
                bufSize,
                AudioTrack.MODE_STREAM);

        List<String> filesPath = MediaFileHelper.getOutputMediaFilesPath(MediaFileHelper.MEDIA_TYPE_AUDIO);
        if (filesPath.size() < 1) {
            LogUtils.print("no pcm file");
            return;
        }
        File file = new File(filesPath.get(0));
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
        } catch (Exception e) {
            LogUtils.print(e.getMessage());
        }
        if (dis == null) {
            LogUtils.print("no pcm file");
            return;
        }
        mTrack.play();
        short[] buf = new short[bufSize/4];
        try {
            while (mIsPlaying && dis.available() > 0) {
                int i = 0;
                while (dis.available() > 0 && i < buf.length)
                {
                    buf[i] = dis.readShort();
                    i++;
                }
                mTrack.write(buf, 0, buf.length);
            }

            dis.close();
            mTrack.release();
            mTrack = null;
        }catch (Exception e) {
            LogUtils.print(e.getMessage());
        }
    }

    public void stopPlay(){
        mIsPlaying = false;
        LogUtils.print("mamual stop play");
    }
}

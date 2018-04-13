package com.zhangchao.audioandvideo.task.task2_audiocaptureandplay;

import android.os.Bundle;
import android.view.View;

import com.zhangchao.audioandvideo.BaseActivity;
import com.zhangchao.audioandvideo.R;
import com.zhangchao.audioandvideo.grid.GridHelper;
import com.zhangchao.audioandvideo.grid.ItemBtnData;

public class Task2AudioCaptureAndPlayActivity extends BaseActivity {

    private AudioManager mAudioRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task1_audio_capture_and_play);
        mAudioRecorder = new AudioManager();
    }

    @Override
    protected void initView() {
        new GridHelper(this)
                .addItem(new ItemBtnData("开始录音", new ItemBtnData.onItemBtnClickListener() {
                    @Override
                    public void onItemBtnClick(View v,String key) {
                        mAudioRecorder.startRecord();
                    }
                }))
                .addItem(new ItemBtnData("停止录音", new ItemBtnData.onItemBtnClickListener() {
                    @Override
                    public void onItemBtnClick(View v,String key) {
                        mAudioRecorder.stopRecord();
                    }
                }))
                .addItem(new ItemBtnData("开始播放", new ItemBtnData.onItemBtnClickListener() {
                    @Override
                    public void onItemBtnClick(View v,String key) {
                        mAudioRecorder.play();
                    }
                }))
                .addItem(new ItemBtnData("停止播放", new ItemBtnData.onItemBtnClickListener() {
                    @Override
                    public void onItemBtnClick(View v,String key) {
                        mAudioRecorder.stopPlay();
                    }
                }))
                .notifyDataAddCompleted();
    }


}

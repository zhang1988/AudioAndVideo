package com.zhangchao.audioandvideo.task.task2_audiocaptureandplay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zhangchao.audioandvideo.R;
import com.zhangchao.audioandvideo.webview.WebViewActivity;


public class Task2AudioCaptureAndPlayActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView tvCapture;
    private TextView tvStopCapture;

    private TextView tvPlay;
    private TextView tvStopPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task1_audio_capture_and_play);
        initView();
    }

    private void initView() {
        tvCapture = (TextView) findViewById(R.id.tvStartCapture);
        tvStopCapture = (TextView) findViewById(R.id.tvStopCapture);
        tvPlay = (TextView) findViewById(R.id.tvStartPlay);
        tvStopPlay = (TextView) findViewById(R.id.tvStopPlay);

        tvCapture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tvStartCapture:
                startActivity(new Intent(this, WebViewActivity.class));
        }
    }
}

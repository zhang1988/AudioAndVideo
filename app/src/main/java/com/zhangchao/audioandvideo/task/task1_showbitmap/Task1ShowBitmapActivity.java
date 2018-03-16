package com.zhangchao.audioandvideo.task.task1_showbitmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.zhangchao.audioandvideo.R;

public class Task1ShowBitmapActivity extends AppCompatActivity {
    private ImageView ivShowBitmap;
    private ExampleSurfaceView exampleSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bitmap);
        init();
    }

    private void init() {
        //8种scaleType https://www.jianshu.com/p/ea8a48768a2e
        ivShowBitmap = (ImageView) findViewById(R.id.ivShowBitmap);
        ivShowBitmap.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //
        exampleSurfaceView = (ExampleSurfaceView) findViewById(R.id.svShowBitmap);
    }
}

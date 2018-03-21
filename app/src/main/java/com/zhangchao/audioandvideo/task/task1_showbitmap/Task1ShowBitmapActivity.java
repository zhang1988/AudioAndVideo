package com.zhangchao.audioandvideo.task.task1_showbitmap;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhangchao.audioandvideo.BaseActivity;
import com.zhangchao.audioandvideo.R;
import com.zhangchao.audioandvideo.grid.GridHelper;
import com.zhangchao.audioandvideo.grid.ItemBtnData;
import com.zhangchao.audioandvideo.grid.ItemUrlData;

public class Task1ShowBitmapActivity extends BaseActivity {
    private ImageView ivShowBitmap;
    private ExampleSurfaceView exampleSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bitmap);
        init();
    }

    private void init() {
        new GridHelper(this).addItem(new ItemUrlData("8种scaleType", "https://www.jianshu.com/p/ea8a48768a2e"))
                .addItem(new ItemBtnData("ss", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(),"sss",Toast.LENGTH_LONG).show();
                    }
                }))
                .notifyDataAddCompleted();
        //8种scaleType https://www.jianshu.com/p/ea8a48768a2e
        ivShowBitmap = (ImageView) findViewById(R.id.ivShowBitmap);
        ivShowBitmap.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //
        exampleSurfaceView = (ExampleSurfaceView) findViewById(R.id.svShowBitmap);
    }
}

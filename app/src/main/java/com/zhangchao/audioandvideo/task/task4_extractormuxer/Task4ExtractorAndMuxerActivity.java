package com.zhangchao.audioandvideo.task.task4_extractormuxer;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.commonlib.util.LogUtils;
import com.example.commonlib.util.MediaSelectUtil;
import com.zhangchao.audioandvideo.BaseActivity;
import com.zhangchao.audioandvideo.R;
import com.zhangchao.audioandvideo.grid.GridHelper;
import com.zhangchao.audioandvideo.grid.ItemBtnData;

public class Task4ExtractorAndMuxerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task4_extractor_muxer);
    }

    protected void initView() {
        new GridHelper(this)
                .addItem(new ItemBtnData("打开文件", new ItemBtnData.onItemBtnClickListener() {
                    @Override
                    public void onItemBtnClick(View v,String key) {
                        MediaSelectUtil.selectVideo(Task4ExtractorAndMuxerActivity.this);
                    }
                }))
                .addItem(new ItemBtnData("解复用", new ItemBtnData.onItemBtnClickListener() {
                    @Override
                    public void onItemBtnClick(View v,String key) {

                    }
                }))
                .notifyDataAddCompleted();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri u = data.getData();
        LogUtils.print(data.getData().toString() );
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null,
                        null, null);
                cursor.moveToFirst();
                // String imgNo = cursor.getString(0); // 图片编号
                String v_path = cursor.getString(1); // 图片文件路径
                String v_size = cursor.getString(2); // 图片大小
                String v_name = cursor.getString(3); // 图片文件名
                LogUtils.print("v_path=" + v_path);
                LogUtils.print("v_size=" + v_size);
                LogUtils.print("v_name=" + v_name);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

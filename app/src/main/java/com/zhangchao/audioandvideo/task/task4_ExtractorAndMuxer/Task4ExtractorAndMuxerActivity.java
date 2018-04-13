package com.zhangchao.audioandvideo.task.task4_ExtractorAndMuxer;

import android.os.Bundle;
import android.view.View;

import com.zhangchao.audioandvideo.BaseActivity;
import com.zhangchao.audioandvideo.R;
import com.zhangchao.audioandvideo.grid.GridHelper;
import com.zhangchao.audioandvideo.grid.ItemBtnData;

public class Task4ExtractorAndMuxerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task4_extractor_and_muxer);
    }

    @Override
    protected void initView() {
        GridHelper.newInstance(this)
                .addItem(new ItemBtnData("ss", mItemClickListener));
    }

    private ItemBtnData.onItemBtnClickListener mItemClickListener = new ItemBtnData.onItemBtnClickListener() {
        @Override
        public void onItemBtnClick(View v,String key) {
            switch (key){
                case "ss":

                    break;
                default:
                    break;
            }
        }
    };
}

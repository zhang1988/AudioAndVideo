package com.zhangchao.audioandvideo.task.task3_CameraPreview;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import com.example.xrecyclerview.XRecyclerView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhangchao.audioandvideo.R;
import com.zhangchao.audioandvideo.grid.GridHelper;
import com.zhangchao.audioandvideo.grid.ItemBtnData;
import com.zhangchao.audioandvideo.grid.ItemUrlData;

import java.util.List;

public class Task3CameraPreviewActivity extends AppCompatActivity {
    private XRecyclerView grid;
    private RecordVideoSurfaceView svVideoPreview;
    private TakePicSurfaceView svPicPreview;

    private TextureView tvPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task3_camera_preview);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        grid = (XRecyclerView) findViewById(R.id.grid);
        svVideoPreview = (RecordVideoSurfaceView) findViewById(R.id.svVideoPreview);
        //tvPreview = (TextureView) findViewById(R.id.tvPreview);
        new GridHelper(this)
                .addItem(new ItemUrlData("Camera基础", "http://blog.csdn.net/feiduclear_up/article/details/51968975"))
                .addItem(new ItemUrlData("MediaRecorder","https://www.cnblogs.com/whoislcj/p/5583833.html"))
                .addItem(new ItemUrlData("MediaRecorder的几个常见坑","https://blog.csdn.net/csdn_lqr/article/details/54347892"))
                .addItem(new ItemUrlData("动态权限1", "http://www.cnblogs.com/xmcx1995/p/5870191.html"))
                .addItem(new ItemUrlData("动态权限2", "http://blog.csdn.net/htwhtw123/article/details/76032997"))
                .addItem(new ItemBtnData("调用系统相机", new ItemBtnData.onItemBtnClickListener() {
                    @Override
                    public void onItemBtnClick(View v,String key) {
                        AndPermission.with(v.getContext())
                                .permission(Permission.CAMERA)
                                .onDenied(new Action() {
                                    @Override
                                    public void onAction(List<String> permissions) {

                                    }
                                })
                                .onGranted(new Action() {
                                    @Override
                                    public void onAction(List<String> permissions) {
                                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
                                        Task3CameraPreviewActivity.this.startActivityForResult(intent, 0);
                                    }
                                })
                                .start();

                    }
                }))
//                .addItem(new ItemBtnData("调转预览方向0", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        svPicPreview.setOrientation(0);
//                    }
//                }))
//                .addItem(new ItemBtnData("调转预览方向90", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        svPicPreview.setOrientation(90);
//                    }
//                }))
//                .addItem(new ItemBtnData("调转预览方向180", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        svPicPreview.setOrientation(180);
//                    }
//                }))
//                .addItem(new ItemBtnData("调转预览方向270", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        svPicPreview.setOrientation(270);
//                    }
//                }))
//                .addItem(new ItemBtnData("开始拍照", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        svPicPreview.startTakePicPreview();
//                    }
//                }))
//                .addItem(new ItemBtnData("拍照", new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        svPicPreview.takePicture();
//                    }
//                }))
                .addItem(new ItemBtnData("开始录/停止", new ItemBtnData.onItemBtnClickListener() {
                    @Override
                    public void onItemBtnClick(View v,String key) {
                        svVideoPreview.startRequestRecordVideoPermission();
                    }
                }))
                .notifyDataAddCompleted();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String s = data.getData().toString();
        Toast.makeText(this, "sss:" + s, Toast.LENGTH_LONG).show();
        if (data != null) {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

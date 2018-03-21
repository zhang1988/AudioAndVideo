package com.zhangchao.audioandvideo.task.task3_CameraPreview;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
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
    private SurfaceView svPreview;
    private TextureView tvPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task3_camera_preview);
        initView();
    }

    private void initView() {
        grid = (XRecyclerView) findViewById(R.id.grid);
        svPreview = (SurfaceView) findViewById(R.id.svPreview);
        tvPreview = (TextureView) findViewById(R.id.tvPreview);
        new GridHelper(this)
                .addItem(new ItemUrlData("Camera基础", "http://blog.csdn.net/feiduclear_up/article/details/51968975"))
                .addItem(new ItemUrlData("动态权限1","http://www.cnblogs.com/xmcx1995/p/5870191.html"))
                .addItem(new ItemUrlData("动态权限2","http://blog.csdn.net/htwhtw123/article/details/76032997"))
                .addItem(new ItemBtnData("调用系统相机", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                .notifyDataAddCompleted();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Toast.makeText(this, "sss", Toast.LENGTH_LONG).show();
        super.onActivityResult(requestCode, resultCode, data);
    }
}

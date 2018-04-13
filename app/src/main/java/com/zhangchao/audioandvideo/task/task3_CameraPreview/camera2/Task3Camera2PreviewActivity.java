package com.zhangchao.audioandvideo.task.task3_CameraPreview.camera2;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.commonlib.util.LogUtils;
import com.zhangchao.audioandvideo.BaseActivity;
import com.zhangchao.audioandvideo.R;
import com.zhangchao.audioandvideo.grid.GridHelper;
import com.zhangchao.audioandvideo.grid.ItemBtnData;
import com.zhangchao.audioandvideo.task.task3_CameraPreview.AutoFitTextureView;
import com.zhangchao.audioandvideo.task.task3_CameraPreview.util.FileUtil;

import java.io.File;

public class Task3Camera2PreviewActivity extends BaseActivity {
    private AutoFitTextureView tvPreview;
    private SurfaceView svPreview;
    private Camera2Manager mManager;
    private int mFingerX;
    private int mFingerY;
    private ImageView ivFocus;

    private CameraPrototype.FlashMode mCurrentFlashMode = CameraPrototype.FlashMode.AUTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task3_camera2_preview);
        initManager();
    }

    @Override
    protected void initView() {
        final int length = (int) (getResources().getDisplayMetrics().density * 64);
        tvPreview = (AutoFitTextureView) findViewById(R.id.tvPreview);
        //svPreview = (SurfaceView) findViewById(R.id.svPreview);
        ivFocus = (ImageView) findViewById(R.id.iv_focus);
        tvPreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int actionMasked = MotionEventCompat.getActionMasked(event);

                switch (actionMasked) {
                    case MotionEvent.ACTION_DOWN:
                        mFingerX = (int) event.getX();
                        mFingerY = (int) event.getY();
                        Log.d("touch", "onTouch: x->" + mFingerX + ",y->" + mFingerY);

                        ivFocus.setX(mFingerX - length / 2);
                        ivFocus.setY(mFingerY - length / 2);

                        ivFocus.setVisibility(View.VISIBLE);

                        mManager.triggerFocusArea(mFingerX, mFingerY);
                        ivFocus.setVisibility(View.VISIBLE);

                        break;
                }

                return false;
            }
        });
        new GridHelper(this)
                .addItem(new ItemBtnData("focus", new ItemBtnData.onItemBtnClickListener() {
                    @Override
                    public void onItemBtnClick(View v,String key) {
                        mManager.takePicture(new PhotoCaptureParameters());
                    }
                }))
                .notifyDataAddCompleted();
    }

    private void initManager() {
        mManager = new Camera2Manager(this, tvPreview, new Handler());
        mManager.setOnFocusStateChangeListener(new CameraPrototype.OnFocusStateChangeListener() {
            @Override
            public void onFocusStateChanged(CameraPrototype.AutoFocusState state, CameraPrototype.AutoFocusMode focusMode) {
                if (focusMode == CameraPrototype.AutoFocusMode.AUTO && state == CameraPrototype.AutoFocusState.ACTIVE_FOCUSED) {
                    //mIvFocus.setVisibility(View.INVISIBLE);
                    LogUtils.print("INVISIBLE");
                }

                //TODO need show trigger focus failed
                if (focusMode == CameraPrototype.AutoFocusMode.AUTO && state == CameraPrototype.AutoFocusState.ACTIVE_UNFOCUSED) {
                    //mIvFocus.setVisibility(View.INVISIBLE);
                }

                Log.d("camera2Activity", "onFocusStateChanged: state -> " + state.name());
            }
        });

        mManager.setOnPreviewStartListener(new CameraPrototype.OnPreviewStartListener() {
            @Override
            public void onPreviewStarted() {
                //mBtnSwitchCamera.setEnabled(true);
                Toast.makeText(Task3Camera2PreviewActivity.this, "preview start", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPreviewFailed() {
                Toast.makeText(Task3Camera2PreviewActivity.this, "occur error", Toast.LENGTH_SHORT).show();
                //mBtnSwitchCamera.setEnabled(true);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 110);
            return;
        }

        mManager.resume();
    }

    @Override
    public void onPause() {
        mManager.pause();
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            mManager.resume();
        }
    }

    //拍照
    private void takePhoto() {
        PhotoCaptureParameters parameters = new PhotoCaptureParameters();
        parameters.setOrientation(this.getWindowManager().getDefaultDisplay().getRotation());
        final File saveFile = FileUtil.getNewFile(this, "PoiCamera");
        parameters.setSavedFile(saveFile);
        parameters.setPhotoCaptureCallback(new PhotoCaptureCallback() {
            @Override
            public void onPhotoSaved() {
                Toast.makeText(Task3Camera2PreviewActivity.this, saveFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                FileUtil.notifySystemGallery(Task3Camera2PreviewActivity.this, saveFile);

                //jumpToProcess(saveFile);
            }

            @Override
            public void onPhotoCaptureFailed() {

            }
        });

        mManager.takePicture(parameters);
    }

//    private void jumpToProcess(File saveFile) {
//        if (saveFile == null) {
//            Log.e("Task3Camera2PreviewActivity", "jumpToProcess: the file is null");
//            return;
//        }
//
//        Intent intent = new Intent(getContext(), ProcessActivity.class);
//        intent.putExtra(ProcessActivity.INTENT_KEY_PATH, saveFile.getAbsolutePath());
//
//        startActivity(intent);
//    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.btn_capture:
//                takePhoto();
//                break;
//            case R.id.btn_switch_camera:
//                switchCamera();
//                break;
//            case R.id.btn_switch_grid_mode:
//                switchGridMode();
//                break;
//            case R.id.btn_switch_flash_mode:
//                switchFlashMode();
//                break;
//        }
//    }

    private void switchFlashMode() {
        switch (mCurrentFlashMode) {
            case AUTO:
                mCurrentFlashMode = CameraPrototype.FlashMode.OFF;
                //mBtnSwitchFlashMode.setImageResource(R.drawable.ic_flash_off_white_24dp);
                mManager.setFlashMode(CameraPrototype.FlashMode.OFF);
                break;
            case OFF:
                mCurrentFlashMode = CameraPrototype.FlashMode.ON;
                //mBtnSwitchFlashMode.setImageResource(R.drawable.ic_flash_on_white_24dp);
                mManager.setFlashMode(CameraPrototype.FlashMode.ON);
                break;
            case ON:
                mCurrentFlashMode = CameraPrototype.FlashMode.AUTO;
                //mBtnSwitchFlashMode.setImageResource(R.drawable.ic_flash_auto_white_24dp);
                mManager.setFlashMode(CameraPrototype.FlashMode.AUTO);
                break;

        }
    }

    private void switchGridMode() {

    }

    private void switchCamera() {
        if (mManager.getWhichCamera() == Camera2OpenHelper.REAR_CAMERA) {
            //mBtnSwitchCamera.setImageResource(R.drawable.ic_camera_rear_white_24dp);
        } else {
            //mBtnSwitchCamera.setImageResource(R.drawable.ic_camera_front_white_24dp);
        }
        mManager.switchCamera();

    }
}

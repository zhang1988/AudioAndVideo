package com.zhangchao.audioandvideo.task.task3_CameraPreview;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhangchao.audioandvideo.AVApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by zhangchao on 18-3-16.
 */

public class TakePicSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public static final int FRAME_TIME = 120;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private int mDegree = -1;
    private boolean isRecording = false;

    public TakePicSurfaceView(Context context, Camera camera) {
        super(context);
        initView();
    }

    public TakePicSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TakePicSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public TakePicSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    public void setOrientation(int degree) {
        if (mDegree == degree) {
            return;
        }
        mDegree = degree;
        mCamera.setDisplayOrientation(degree);
    }

    public void takePicture() {
        //permission
        AndPermission.with(AVApplication.getAVApplication())
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        mCamera.takePicture(null, null, mPictureCallback);
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Log.e("onPictureTaken", "Error permission denied");
                    }
                })
                .start();

    }

    private void initView() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //startTakePicPreview();
    }

    public void startTakePicPreview() {
        try {
            if (mCamera == null) {
                mCamera = getCamera();
            }
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopPreview();
    }

    public void stopPreview() {
        try {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startTakePicPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {

        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        startTakePicPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public Camera getCamera() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {

        }
        return c;
    }

    public Camera.CameraInfo getCameraInfo(int id) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(id, cameraInfo);
        return cameraInfo;
    }

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            File pic = MediaFileHelper.getOutputMediaFile(MediaFileHelper.MEDIA_TYPE_IMAGE);
            if (pic == null) {
                Log.e("onPictureTaken", "Error creating media file");
                return;
            }

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(pic);
                fos.write(data);
                fos.close();
                Log.e("onPictureTaken", "success");
            } catch (IOException e) {
                Log.e("onPictureTaken", "Error write media file");
            }

            TakePicSurfaceView.this.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCamera.startPreview();
                }
            }, 1000);
        }
    };
}

package com.zhangchao.audioandvideo.task.task3_CameraPreview;

import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.commonlib.util.LogUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhangchao.audioandvideo.AVApplication;

import java.io.IOException;
import java.util.List;

/**
 * Created by zhangchao on 18-3-16.
 * 1、预览的写法和拍照一样。因此是可以公用预览的
 * 2、录视频的时候需要调用camera的stopPreview。
 * 3、然后在设置拍摄过程中的preview。
 * 4、开始拍摄的时候还需要unlock，拍摄完之后需要lock。但是如果unlock之后没有lock（比如APP崩溃），相机无法再次打开，需要重启系统。因此一定要lock
 */

public class RecordVideoSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    public static final int FRAME_TIME = 120;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private MediaRecorder mMediaRecorder;
    private int mDegree = -1;
    private boolean isRecording = false;

    public RecordVideoSurfaceView(Context context, Camera camera) {
        super(context);
        initView();
    }

    public RecordVideoSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public RecordVideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public RecordVideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

    private void initView() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //startTakePicPreview();
    }

    public void startRequestRecordVideoPermission() {
        //permission
        AndPermission.with(AVApplication.getAVApplication())
                .permission(new String[]{Permission.WRITE_EXTERNAL_STORAGE, Permission.RECORD_AUDIO})
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Log.e("startRecordVideo", "permission granted");
                        if (isRecording) {
                            stopRecord();
                        } else {
                            startRecord();
                        }
                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Log.e("startRecordVideo", "Error permission denied");
                    }
                })
                .start();

    }

    private void startRecord() {
        try {
            //
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
            }

            //step 1 Unlock,set camera to recorder
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.unlock();
                mMediaRecorder.setCamera(mCamera);
            }

            //step 2 set sources
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            //step 3 Set a CamcorderProfile(成像分辨率) (requires API Level 8 or higher)
            mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

            //step 4 set output file
            mMediaRecorder.setOutputFile(MediaFileHelper.getOutputMediaFile(MediaFileHelper.MEDIA_TYPE_VIDEO).toString());

            //step 5 set preview output
            mMediaRecorder.setPreviewDisplay(mHolder.getSurface());

            // Step 6: Prepare configured MediaRecorder
            mMediaRecorder.prepare();//如果camera被lock，会失败
            mMediaRecorder.start();
            isRecording = true;
        } catch (IllegalStateException e) {
            Log.d("MediaRecorder.prepare", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            stopRecord();
            releaseCamera();
        } catch (IOException e) {
            Log.d("MediaRecorder.prepare", "IOException preparing MediaRecorder: " + e.getMessage());
            stopRecord();
            releaseCamera();
        }
    }

    private void stopRecord() {
        try {
            //停止录制
            mMediaRecorder.stop();
            //重置
            mMediaRecorder.reset();
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCamera != null) {
                mCamera.lock();
            }
        }
        isRecording = false;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.lock();
            try {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {

            }// queue_lock camera for later use
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRecord();
        releaseCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //必须在这儿启动预览
        try {
            mCamera = getCamera();
            //mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e("sss", e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null) {
            return;
        }

        stopRecord();


        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
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
            Log.e("sss", "open camera failed");
        }
        return c;
    }

    public Camera.CameraInfo getCameraInfo(int id) {
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(id, cameraInfo);
        return cameraInfo;
    }

    //PreviewCallback：  https://blog.csdn.net/qiguangyaolove/article/details/53130061
    //即使不设置mCamera.setPreviewDisplay(mHolder); 依然会回调。就可以在surfaceview上显示处理之后的图像
    //onPreviewFrame的data 默认是 nv21格式。看方法注释
    //NV21 保存为JPEG https://blog.csdn.net/tanmengwen/article/details/41412425
    private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            LogUtils.print("preview");
            // 处理图像

            // 显示
        }
    };
}

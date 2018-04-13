package com.zhangchao.audioandvideo.task.task3_CameraPreview.camera2;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.util.Size;
import android.view.Surface;

import com.zhangchao.audioandvideo.task.task3_CameraPreview.AutoFitTextureView;


/**
 * Created by zhangchao on 16-7-30.
 */

public class Camera2Manager implements CameraPrototype {
    private Camera2OpenHelper mCameraOpenHelper;
    private Camera2OperationHelper mCameraOperationHelper;

    private AutoFitTextureView mTextureView;
    private Size mPreviewSize;

    private OnFocusStateChangeListener mOnFocusStateChangeListener;
    private OnPreviewStartListener mOnPreviewStartListener;

    private int mWhichCamera = Camera2OpenHelper.REAR_CAMERA;


    public Camera2Manager(Activity activity, final AutoFitTextureView textureView, Handler mainHandler) {
        mTextureView = textureView;

        mCameraOpenHelper = new Camera2OpenHelper(activity, textureView, mainHandler);
        //执行完CameraManager的openCamera打开成功后，开始的回调。得到必要的对象实例
        mCameraOpenHelper.setOnCameraOpenCallback(new Camera2OpenHelper.OnCameraOpenCallback() {
            @Override
            public void onCameraOpened(Camera2OperationHelper camera, Size previewSize) {
                mCameraOperationHelper = camera;
                mPreviewSize = previewSize;

                mCameraOperationHelper.setOnPreviewStartListener(mOnPreviewStartListener);
                mCameraOperationHelper.setOnFocusStateChangeListener(mOnFocusStateChangeListener);

                SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
                surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
                Surface surface = new Surface(surfaceTexture);

                startPreview(surface);
            }

            @Override
            public void onCameraDisconnected() {
                close();
                mCameraOperationHelper = null;
            }

            @Override
            public void onCameraError(int error) {
                close();
                mCameraOperationHelper = null;
            }
        });

    }

    public void resume() {
        openCamera(mWhichCamera);
    }

    private void openCamera(int which) {
        mWhichCamera = which;
        if (mTextureView.isAvailable()) {
            mCameraOpenHelper.openCamera(mPreviewSize.getWidth(), mPreviewSize.getHeight(), which);
        } else {
            mTextureView.setSurfaceTextureListener(mCameraOpenHelper.getSurfaceTextureListener());
        }
    }

    public void pause() {
        close();
    }


    public void switchCamera() {
        if (isBackCamera()) {
            close();
            openCamera(Camera2OpenHelper.FRONT_CAMERA);
        } else {
            close();
            openCamera(Camera2OpenHelper.REAR_CAMERA);
        }
    }

    @Override
    public void startPreview(Surface surface) {
        if (mCameraOperationHelper != null)
            mCameraOperationHelper.startPreview(surface);
    }

    @Override
    public void triggerFocusArea(float x, float y) {
        if (mCameraOperationHelper != null)
            mCameraOperationHelper.triggerFocusArea(x, y);
    }

    @Override
    public void takePicture(PhotoCaptureParameters parameters) {
        if (mCameraOperationHelper != null)
            mCameraOperationHelper.takePicture(parameters);
    }

    @Override
    public boolean isFrontCamera() {
        return mCameraOperationHelper != null && mCameraOperationHelper.isFrontCamera();
    }

    @Override
    public boolean isBackCamera() {
        return mCameraOperationHelper != null && mCameraOperationHelper.isBackCamera();
    }

    @Override
    public Size[] getSupportSizes() {
        if (mCameraOperationHelper != null)
            return mCameraOperationHelper.getSupportSizes();
        return new Size[0];
    }

    @Override
    public Size[] getPreviewSizes() {
        if (mCameraOperationHelper != null)
            return mCameraOperationHelper.getPreviewSizes();
        return new Size[0];
    }

    @Override
    public Size chooseOptimalPreviewSize() {
        return null;
    }

    @Override
    public void setZoom(float zoom) {
        if (mCameraOperationHelper != null)
            mCameraOperationHelper.setZoom(zoom);
    }

    @Override
    public float getMaxZoom() {
        if (mCameraOperationHelper != null)
            return mCameraOperationHelper.getMaxZoom();
        return -1f;
    }

    @Override
    public boolean isFlashSupport() {
        return mCameraOperationHelper != null && mCameraOperationHelper.isFlashSupport();
    }

    @Override
    public boolean setFlashMode(FlashMode flashMode) {
        return mCameraOperationHelper != null && mCameraOperationHelper.setFlashMode(flashMode);
    }

    @Override
    public void setOnFocusStateChangeListener(OnFocusStateChangeListener onFocusStateChangeListener) {
        mOnFocusStateChangeListener = onFocusStateChangeListener;
    }

    @Override
    public void setOnPreviewStartListener(OnPreviewStartListener onPreviewStartListener) {
        mOnPreviewStartListener = onPreviewStartListener;
    }

    @Override
    public void close() {
        if (mCameraOperationHelper != null)
            mCameraOperationHelper.close();
    }

    public int getWhichCamera() {
        return mWhichCamera;
    }

}

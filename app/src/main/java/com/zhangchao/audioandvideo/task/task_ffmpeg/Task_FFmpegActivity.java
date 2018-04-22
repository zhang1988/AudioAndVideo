package com.zhangchao.audioandvideo.task.task_ffmpeg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.example.commonlib.util.LogUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.zhangchao.audioandvideo.R;
import com.zhangchao.audioandvideo.task.task3_CameraPreview.MediaFileHelper;

import java.util.List;

//ffmpeg的so文件添加：https://blog.csdn.net/qqchenjian318/article/details/72780874
// error adding symbols:File in wrong format 这种问题是需要制定：abiFilters 'armeabi-v7a'//, 'x86', 'armeabi-v7a', 'x86_64', 'arm64-v8a'
// java.lang.UnsatisfiedLinkError: dlopen failed: "xxx/libgnustl_shared.so" is 32-bit instead of 64-bit : 删除64位相关的abi，比如x86-64等等,https://blog.csdn.net/chichengjunma/article/details/53815299
public class Task_FFmpegActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    static {
        System.loadLibrary("avutil");
        //System.loadLibrary("fdk-aac");
        System.loadLibrary("avcodec");
        System.loadLibrary("avformat");
        System.loadLibrary("swscale");
        System.loadLibrary("swresample");
        System.loadLibrary("avfilter");
        System.loadLibrary("native-lib");
    }

    private SurfaceView svScreen;
    private SurfaceHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task__ffmpeg);
        initView();
    }

    protected void initView() {
        LogUtils.print("ss");
        svScreen = (SurfaceView) findViewById(R.id.svScreen);
        mHolder = svScreen.getHolder();
        mHolder.addCallback(this);
        //native_setMediaPath(getVideoPath());

        //native_initFFmpeg(getVideoPath(), mHolder.getSurface());
    }

    private String getVideoPath(){
         return MediaFileHelper.getOutputMediaFilesPath(MediaFileHelper.MEDIA_TYPE_VIDEO).get(2);
    }

    public void showToast(final int message){
        svScreen.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Task_FFmpegActivity.this,"kkk"+message,Toast.LENGTH_LONG).show();
            }
        });
    }

    private native int native_initFFmpeg(String path, Surface screen);

    private native void native_setMediaPath(String s);

    private native void native_setMediaScreen(Surface surface);

    private native void native_setCallback(Object thisActivity);

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        //native_setMediaScreen(svScreen.getHolder().getSurface());
        //native_initFFmpeg(getVideoPath(), holder.getSurface());
        AndPermission.with(this)
                .permission(Permission.READ_EXTERNAL_STORAGE)
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        LogUtils.print("permission granted");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int ret = native_initFFmpeg(getVideoPath(), holder.getSurface());
                                LogUtils.print("ret:"+ret);
                            }
                        }).start();

                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        LogUtils.print("permission denied");
                    }
                })
                .start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}

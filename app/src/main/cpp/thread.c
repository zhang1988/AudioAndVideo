//
// Created by zhangchao on 18-4-18.
//

#ifdef __cplusplus
extern "C" {
#endif

#include "ffmpeg_jni.h"
#include "include/libavcodec/avcodec.h"
#include "j4a/AudioTrack.h"
#include<pthread.h>
#include "log.h"
#include "include/libavformat/avformat.h"
#include "include/libavutil/imgutils.h"
#include "include/libswscale/swscale.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include "FrameQueue.h"
#include <unistd.h>
#ifdef __cplusplus
}
#endif

//线程数
#define NUMTHREADS 5

//全局变量
extern JavaVM *g_jvm;
jobject g_obj;
jobject g_screen;
int g_video_W;
int g_video_H;
enum AVPixelFormat g_pix_fmt;


void *thread_decode_video(void *arg) {
    LOGE("thread_decode_video_start\n");
    JNIEnv *env;
    jclass cls;
    jmethodID mid;
    //sleep(2);
    //Attach主线程
    if ((*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL) != JNI_OK) {
        LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
        return NULL;
    }

    //nativeWindow 初始化失败，需要在surfaceCreated里使用surface：http://www.xuebuyuan.com/2183201.html
    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, g_screen);
    if (nativeWindow == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "tag", "message: %s",
                            "Didn't allocate ANativeWindow.");
        return NULL;
    }
    // 设置native window的buffer大小,可自动拉伸
    ANativeWindow_setBuffersGeometry(nativeWindow, g_video_W, g_video_H, WINDOW_FORMAT_RGBA_8888);
    ANativeWindow_Buffer windowBuffer;

    //AVFrame *pAVFrame = av_frame_alloc();
    //用于渲染
    AVFrame *pFrameRGBA = av_frame_alloc();

//    if (pFrameRGBA == NULL || pAVFrame == NULL) {
//        __android_log_print(ANDROID_LOG_ERROR, "tag", "message: %s", "Didn't allocate frame.");
//        return -8;
//    }

    // Determine required buffer size and allocate buffer
    int numBytes = av_image_get_buffer_size(AV_PIX_FMT_RGBA, g_video_W, g_video_H, 1);
    uint8_t *buffer = (uint8_t *) av_malloc(numBytes * sizeof(uint8_t));
    av_image_fill_arrays(pFrameRGBA->data, pFrameRGBA->linesize, buffer, AV_PIX_FMT_RGBA,
                         g_video_W, g_video_H, 1);

    // 由于解码出来的帧格式不是RGBA的,在渲染之前需要进行格式转换
    struct SwsContext *pSwsCtx = sws_getContext(g_video_W,
                                                g_video_H,
                                                g_pix_fmt,
                                                g_video_W,
                                                g_video_H,
                                                AV_PIX_FMT_RGBA,
                                                SWS_BILINEAR,
                                                NULL,
                                                NULL,
                                                NULL);

    int k = 0;
    uint8_t *dst;
    int dstStride;
    uint8_t *src;
    int srcStride;
    Frame *pFrame;
    while (true) {
        pFrame = dequeue();
        while (pFrame == NULL) {
            usleep(1000);
            pFrame = dequeue();
        }

        if (pFrame->code != 0) {
            switch (pFrame->code){
                case 1:
                    break;
                default:
                    break;
            }
            break;
        }
        LOGE("show frame pos:%d",pFrame->pos);
        LOGE("show frame:%d",++k);
        LOGE("show frame:%lld,%lld,%lld,%lld",pFrame->pAVFrame->pts,pFrame->pAVFrame->pkt_pts,pFrame->pAVFrame->pkt_dts,pFrame->pAVFrame->pkt_pos);

        ANativeWindow_lock(nativeWindow, &windowBuffer, 0);
        //格式转换
        sws_scale(pSwsCtx, (uint8_t const *const *) (pFrame->pAVFrame->data),
                  pFrame->pAVFrame->linesize, 0, g_video_H,
                  pFrameRGBA->data, pFrameRGBA->linesize);

        // 获取stride
        dst = windowBuffer.bits;
        //stride表示存储一行像素需要的内存。
        dstStride = windowBuffer.stride * 4;
        src = (uint8_t *) (pFrameRGBA->data[0]);
        srcStride = pFrameRGBA->linesize[0];

        // 由于window的stride和帧的stride不同,因此需要逐行复制
        for (int h = 0; h < g_video_H; ++h) {
            memcpy(dst + h * dstStride, src + h * srcStride, srcStride);
        }

        ANativeWindow_unlockAndPost(nativeWindow);
        recycle_to_free(pFrame);
        usleep(30000);
        //LOGE("show one frame success");
    }
    av_free(buffer);
    av_free(pFrameRGBA);

    // Free the YUV frame
    //av_free(pAVFrame);
    freeAll();
    pthread_t tid = pthread_self();
    LOGE("exit:%ld",tid);

    error:
    //Detach主线程
    if ((*g_jvm)->DetachCurrentThread(g_jvm) != JNI_OK) {
        LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
    }

    pthread_exit(0);
}

void *thread_decode_audio(void *arg) {
    LOGE("thread_decode_video_start\n");
    JNIEnv *env;
    jclass cls;
    jmethodID mid;

    //Attach主线程
    if ((*g_jvm)->AttachCurrentThread(g_jvm, &env, NULL) != JNI_OK) {
        LOGE("%s: AttachCurrentThread() failed", __FUNCTION__);
        return NULL;
    }

    //init
    jobject audio_track = NULL;
    J4A_loadClass__J4AC_AudioTrack(env);
    //audio_track = J4AC_AudioTrack__AudioTrack__asGlobalRef__catchAll();

    //找到对应的类
    cls = (*env)->GetObjectClass(env, g_obj);
    if (cls == NULL) {
        LOGE("FindClass() Error.....");
        goto error;
    }
    //再获得类中的方法
    mid = (*env)->GetMethodID(env, cls, "showToast", "(I)V");
    if (mid == NULL) {
        LOGE("GetMethodID() Error.....");
        goto error;
    }
    //最后调用java中的静态方法
    (*env)->CallVoidMethod(env, g_obj, mid, (int) arg);


    error:
    //Detach主线程
    if ((*g_jvm)->DetachCurrentThread(g_jvm) != JNI_OK) {
        LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
    }


    pthread_exit(0);
}

void create_video_decode_thread() {
    int i = 10, n;
    int threadNum[4];
    int thread_number = pthread_create(&threadNum[0], NULL, &thread_decode_video, (void *) i);
    //int thread_number2 = pthread_create(&threadNum[1], NULL, &thread_decode_audio, (void *) i);
}

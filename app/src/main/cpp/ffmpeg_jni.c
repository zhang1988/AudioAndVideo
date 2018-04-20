//
// Created by zhangchao on 18-4-16.
//
#ifdef __cplusplus
extern "C" {
#endif

#include "ffmpeg_jni.h"
#include "include/libavcodec/avcodec.h"
#include "include/libavformat/avformat.h"
#include "include/libavutil/imgutils.h"
#include "include/libswscale/swscale.h"
#include <android/native_window.h>
#include <android/native_window_jni.h>
#include "log.h"
#include <android/log.h>
#include "FrameQueue.h"
#include <unistd.h>
#include <pthread.h>
#ifdef __cplusplus
}
#endif

struct PlayerContext {
    struct AVFormatContext *pAVFormatCtx;
    char *path;
    jobject callback;
    jobject screen;
};

static struct PlayerContext sPlayerCtx;
JavaVM *g_jvm;
jobject g_obj;
//activity
jobject g_screen;
int g_video_W;
int g_video_H;
enum AVPixelFormat g_pix_fmt;

//将jstring类型转换成char类型
//https://blog.csdn.net/wang_shuai_ww/article/details/52329221
//注意：在C语言中必须将要被调用的函数放在调用函数的前面来定义，否则报错
char *jstring2CStr(JNIEnv *env, jstring jstr) {
    char *rtn = NULL;
    jclass clsstring = (*env)->FindClass(env, "java/lang/String");
    jstring strencode = (*env)->NewStringUTF(env, "GB2312");
    jmethodID mid = (*env)->GetMethodID(env, clsstring, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (jbyteArray) (*env)->CallObjectMethod(env, jstr, mid, strencode);
    jsize alen = (*env)->GetArrayLength(env, barr);
    jbyte *ba = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
    if (alen > 0) {
        rtn = (char *) malloc(alen + 1); //new char[alen+1];
        memcpy(rtn, ba, alen);
        rtn[alen] = 0;
    }
    (*env)->ReleaseByteArrayElements(env, barr, ba, 0);

    return rtn;
}

//当动态库被加载时这个函数被系统调用
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = -1;

    //获取JNI版本
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        LOGE("GetEnv failed!");
        return result;
    }

    return JNI_VERSION_1_6;
}

void initEnvironment(JNIEnv *env, jobject obj, jobject screen) {
    (*env)->GetJavaVM(env, &g_jvm);
    //不能直接赋值(g_obj = obj)
    g_obj = (*env)->NewGlobalRef(env, obj);
    g_screen = (*env)->NewGlobalRef(env, screen);
}

void create_video_decode_thread();

void thread_decode_videos(JNIEnv *env, int m) {
    LOGE("thread_decode_video_start\n");
    jclass cls;
    jmethodID mid;

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
    (*env)->CallVoidMethod(env, g_obj, mid, m);


    error:
    //Detach主线程
    LOGE("%s: DetachCurrentThread() failed", __FUNCTION__);
}

JNIEXPORT int JNICALL Java_com_zhangchao_audioandvideo_task_task_1ffmpeg_Task_1FFmpegActivity_native_1initFFmpeg
        (JNIEnv *env, jobject object, jstring path, jobject screen) {
    initEnvironment(env, object, screen);

    int m;
    //thread_decode_videos(env, m);

    //log_e("S","s");
    const char *filePath = (*env)->GetStringUTFChars(env, path, NULL);//jstring2CStr(env, path);
    //char filePath[] = "/storage/emulated/0/Pictures/MyCameraApp/VID_20180323_151842.mp4";
    av_register_all();
    //avformat_network_init();
    AVFormatContext *pAvFormatContext = avformat_alloc_context();
    if (pAvFormatContext == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "tag", "message: %s",
                            "Couldn't allocate AVFormatContext.");
        return -1;
    }

    int ret = avformat_open_input(&pAvFormatContext, filePath, NULL, NULL);
    if (ret != 0) {
        __android_log_print(ANDROID_LOG_ERROR, "tag", "message: %s,%d", "Couldn't open file", ret);
        return -2; // Couldn't open file
    }

    if (avformat_find_stream_info(pAvFormatContext, NULL) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "tag", "message: %s",
                            "Couldn't find stream information.");
        return -3;
    }

    // Find the first video stream
    int videoStream = -1;
    int audioStream = -1;
    for (int i = 0; i < pAvFormatContext->nb_streams; ++i) {
        if (pAvFormatContext->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoStream = i;
        } else if (pAvFormatContext->streams[i]->codec->codec_type == AVMEDIA_TYPE_AUDIO) {
            audioStream = i;
        }
    }

    //video
    if (videoStream == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "tag", "message: %s", "Didn't find a video stream.");
        return -4; // Didn't find a video stream
    }

    // Get a pointer to the codec context for the video stream
    AVCodecContext *pCodecCtx = pAvFormatContext->streams[videoStream]->codec;
    // Find the decoder for the video stream
    AVCodec *pCodec = avcodec_find_decoder(pCodecCtx->codec_id);
    if (pCodec == NULL) {
        __android_log_print(ANDROID_LOG_ERROR, "tag", "message: %s", "Didn't find a video codec.");
        return -5; // Codec not found
    }

    //打开
    if (avcodec_open2(pCodecCtx, pCodec, NULL) < 0) {
        __android_log_print(ANDROID_LOG_ERROR, "tag", "message: %s", "Didn't open codec.");
        return -6; // Could not open codec
    }
//
//    //nativeWindow 初始化失败，需要在surfaceCreated里使用surface：http://www.xuebuyuan.com/2183201.html
//    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, screen);
//    if (nativeWindow == NULL) {
//        __android_log_print(ANDROID_LOG_ERROR, "tag", "message: %s",
//                            "Didn't allocate ANativeWindow.");
//        return -7;
//    }

    //这里width为0：set_target_properties制定ffmpeg的so文件时，使用有数字结尾的，比如libavformat-56.so
    int video_W = pCodecCtx->width;
    int video_H = pCodecCtx->height;
    g_video_W = video_W;
    g_video_H = video_H;
    g_pix_fmt =  pCodecCtx->pix_fmt;
    __android_log_print(ANDROID_LOG_ERROR, "tag", "message: %d,%d", pCodecCtx->width,
                        pCodecCtx->height);



//    // 设置native window的buffer大小,可自动拉伸
//    ANativeWindow_setBuffersGeometry(nativeWindow, video_W, video_H, WINDOW_FORMAT_RGBA_8888);
//    ANativeWindow_Buffer windowBuffer;
//
//    AVFrame *pAVFrame = av_frame_alloc();
//    //用于渲染
//    AVFrame *pFrameRGBA = av_frame_alloc();
//
//    if (pFrameRGBA == NULL || pAVFrame == NULL) {
//        __android_log_print(ANDROID_LOG_ERROR, "tag", "message: %s", "Didn't allocate frame.");
//        return -8;
//    }
//
//    // Determine required buffer size and allocate buffer
//    int numBytes = av_image_get_buffer_size(AV_PIX_FMT_RGBA, video_W, video_H, 1);
//    uint8_t *buffer = (uint8_t *) av_malloc(numBytes * sizeof(uint8_t));
//    av_image_fill_arrays(pFrameRGBA->data, pFrameRGBA->linesize, buffer, AV_PIX_FMT_RGBA,
//                         video_W, video_H, 1);
//
//    // 由于解码出来的帧格式不是RGBA的,在渲染之前需要进行格式转换
//    struct SwsContext *pSwsCtx = sws_getContext(video_W,
//                                                video_H,
//                                                pCodecCtx->pix_fmt,
//                                                video_W,
//                                                video_H,
//                                                AV_PIX_FMT_RGBA,
//                                                SWS_BILINEAR,
//                                                NULL,
//                                                NULL,
//                                                NULL);


    int frameFinished;
    AVPacket packet;
    int k = 0;
    initQueue(4);
    create_video_decode_thread();
    Frame *pFrame = obtainFrame();
    Frame *tmp_to_enqueue = NULL;
    while (av_read_frame(pAvFormatContext, &packet) >= 0) {
        if (packet.stream_index == videoStream) {
            //decode video
            avcodec_decode_video2(pCodecCtx, pFrame->pAVFrame, &frameFinished, &packet);
            if (frameFinished) {
                tmp_to_enqueue = pFrame;
                pFrame = NULL;//
                enqueue(tmp_to_enqueue);
                pFrame = obtainFrame();
                while (pFrame == NULL) {
                    sleep(1);
                    pFrame = obtainFrame();
                }
//                // queue_lock native window buffer
//                ANativeWindow_lock(nativeWindow, &windowBuffer, 0);
//
//                //格式转换
//                sws_scale(pSwsCtx, (uint8_t const *const *) pAVFrame->data,
//                          pAVFrame->linesize, 0, video_H,
//                          pFrameRGBA->data, pFrameRGBA->linesize);
//
//                // 获取stride
//                uint8_t *dst = windowBuffer.bits;
//                //stride表示存储一行像素需要的内存。
//                int dstStride = windowBuffer.stride * 4;
//                uint8_t *src = (uint8_t *) (pFrameRGBA->data[0]);
//                int srcStride = pFrameRGBA->linesize[0];
//
//                // 由于window的stride和帧的stride不同,因此需要逐行复制
//                int h;
//                for (h = 0; h < video_H; ++h) {
//                    memcpy(dst + h * dstStride, src + h * srcStride, srcStride);
//                }
//
//                ANativeWindow_unlockAndPost(nativeWindow);
                __android_log_print(ANDROID_LOG_ERROR, "tag", "message: decode %s,%d", "frame:", ++k);

            }
        } else if (packet.stream_index == audioStream) {
            //
        }
        av_packet_unref(&packet);
    }

    //退出码
    pFrame->code = 1;
    enqueue(pFrame);

    //av_free(buffer);
    //av_free(pFrameRGBA);

    // Free the YUV frame
    //av_free(pAVFrame);

    // Close the codec
    avcodec_close(pCodecCtx);

    // Close the video file
    avformat_close_input(&pAvFormatContext);

    int ver = avcodec_version();
    return 0;
}


JNIEXPORT void JNICALL Java_com_zhangchao_audioandvideo_task_task_1ffmpeg_Task_1FFmpegActivity_native_1setMediaPath
        (JNIEnv *env, jobject object, jstring string) {

}

JNIEXPORT void JNICALL Java_com_zhangchao_audioandvideo_task_task_1ffmpeg_Task_1FFmpegActivity_native_1setMediaScreen
        (JNIEnv *env, jobject object, jobject screen) {

}

JNIEXPORT void JNICALL Java_com_zhangchao_audioandvideo_task_task_1ffmpeg_Task_1FFmpegActivity_native_1setCallback
        (JNIEnv *env, jobject object, jobject callback) {

}





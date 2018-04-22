//
// Created by zhangchao on 18-4-19.
//

#ifndef AUDIOANDVIDEO_FRAMELIST_H
#define AUDIOANDVIDEO_FRAMELIST_H

#endif //AUDIOANDVIDEO_FRAMELIST_H

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
#include "thread.h"

typedef struct _Frame{
    //struct _Frame *pre;
    AVFrame *pAVFrame;
    struct _Frame *next;
    int code;//
    int pos;
} Frame;


void initQueue(int capacity);
void enqueue(Frame *frame);
Frame *dequeue();
Frame *obtainFrame();
void recycle_to_free(Frame *frame);
void freeAll();
#ifdef __cplusplus
}
#endif

//
// Created by zhangchao on 18-4-19.
//
#include "FrameQueue.h"
#include <pthread.h>

static Frame *queue_head = NULL;
static Frame *queue_tail = NULL;
//指向队尾。
static Frame *freeList = NULL;
pthread_mutex_t queue_lock;
pthread_mutex_t free_list_lock;


Frame *create_and_init_Frame() {
    Frame *frame = av_malloc(sizeof(Frame));
    frame->next = NULL;
    //frame->pre = NULL;
    frame->code = 0;
    frame->pAVFrame = av_frame_alloc();
    return frame;
}

void freeAll(){
    Frame *tmp = NULL;
    pthread_mutex_lock(&free_list_lock);
    while (freeList != NULL) {
        tmp = freeList;
        freeList = freeList->next;
        tmp->next = NULL;
        av_free(tmp->pAVFrame);
        av_free(tmp);
    }
    pthread_mutex_unlock(&free_list_lock);

    pthread_mutex_lock(&queue_lock);
    while (queue_head != NULL) {
        tmp = queue_head;
        queue_head = queue_head->next;
        tmp->next = NULL;
        av_free(tmp->pAVFrame);
        av_free(tmp);
    }
    pthread_mutex_unlock(&queue_lock);
}

void recycle_to_free(Frame *frame) {
    LOGE("recycle_to_free");
    pthread_mutex_lock(&free_list_lock);
    if (freeList == NULL) {
        freeList = frame;
    } else {
        Frame *tmp = freeList;
        freeList = frame;
        //freeList->pre = NULL;
        freeList->next = tmp;
        //tmp->pre = freeList;
    }
    pthread_mutex_unlock(&free_list_lock);
    LOGE("recycle_to_free:%s",frame != NULL?"success":"failed");
}

Frame *obtainFrame() {
    LOGE("obtainFrame");
    pthread_mutex_lock(&free_list_lock);
    Frame *frame = freeList;
    if (freeList == NULL) {
        pthread_mutex_unlock(&free_list_lock);
        LOGE("obtainFrame:%s",frame != NULL?"success":"failed");
        return NULL;
    }

    if (freeList->next == NULL) {
        freeList = NULL;
        pthread_mutex_unlock(&free_list_lock);
        LOGE("obtainFrame:%s",frame != NULL?"success":"failed");
        return frame;
    }

    freeList = frame->next;
    frame->next = NULL;
    LOGE("obtainFrame:%s",frame != NULL?"success":"failed");
    pthread_mutex_unlock(&free_list_lock);
    return frame;
}

void initQueue(int capacity) {
    if (capacity < 4) {
        capacity = 4;
    }

    pthread_mutex_init(&queue_lock, NULL);
    pthread_mutex_init(&free_list_lock, NULL);

    queue_head = av_malloc(sizeof(Frame));
    queue_head->next = NULL;
    //frame->pre = NULL;
    queue_head->pAVFrame = NULL;
    //初始化队尾。队尾==队头则队列为空
    queue_tail = queue_head;

    for (int i = 0; i < capacity; ++i) {
        recycle_to_free(create_and_init_Frame());
        LOGE("init %d",i);
    }
}

void enqueue(Frame *frame) {
    LOGE("enqueue");
    if (frame == NULL) {
        LOGE("enqueue:%s",frame != NULL?"success":"failed");
        return;
    }

    pthread_mutex_lock(&queue_lock);
    queue_tail->next = frame;
    queue_tail = frame;
    queue_tail->next = NULL;//必须要有
    pthread_mutex_unlock(&queue_lock);
    LOGE("enqueue:%s",frame != NULL?"success":"failed");
}

Frame *dequeue() {
    LOGE("dequeue");
    Frame *frame = NULL;
    int ret;
    ret = pthread_mutex_trylock(&queue_lock);
    while (ret != 0) {
        ret = pthread_mutex_trylock(&queue_lock);
    }
    //pthread_mutex_lock(&queue_lock);
    if (queue_head != queue_tail) {
        //如果队列只有一个元素（不包括head），需要将tail重置
        if (queue_head->next == queue_tail) {
            frame = queue_tail;
            queue_head->next = NULL;
            queue_tail = queue_head;
        } else {
            frame = queue_head->next;
            queue_head->next = frame->next;
            frame->next = NULL;
        }
    }
    pthread_mutex_unlock(&queue_lock);
    LOGE("dequeue:%s",frame != NULL?"success":"failed");
    return frame;
}
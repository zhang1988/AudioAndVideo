//
// Created by zhangchao on 18-4-17.
//

#ifndef AUDIOANDVIDEO_LOG_H_H
#define AUDIOANDVIDEO_LOG_H_H


#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "native-activity", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "native-activity", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "native-activity", __VA_ARGS__))

#endif //AUDIOANDVIDEO_LOG_H_H

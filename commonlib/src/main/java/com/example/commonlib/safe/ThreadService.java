package com.example.commonlib.safe;

import com.example.commonlib.util.LogUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zhangchao
 * @date 2018-03-17
 * @description 线程调度服务
 */
public class ThreadService {

    private static ThreadService _service = new ThreadService();
    private final ExecutorService executor;

    private ThreadService() {
        this.executor = Executors.newSingleThreadExecutor();
    }

    public static ThreadService sharedInstance() {
        return _service;
    }

    /**
     * 将任务提交到线程池执行。任务可能需要排队。
     * 适用于短时间、频率高、不需要立即执行的任务
     */
    public void submitTask(Runnable task) {
        try {
            executor.submit(task);
        } catch (Throwable e) {
            LogUtils.print("ThreadService", e.getMessage());
        }
    }

    /**
     * 将任务提交到一个独立的线程执行。任务提交后，不需要排队，直接开启线程执行。
     * 适用于：1. 执行时间特别长的任务；2. 需要立刻执行的任务。
     */
    public void submitTaskInSoloThread(Runnable task) {
        new Thread(task).start();
    }

}

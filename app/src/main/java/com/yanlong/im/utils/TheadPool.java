package com.yanlong.im.utils;

import net.cb.cb.library.utils.LogUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @version V1.0
 * @createAuthor （Geoff）
 * @createDate 2020-02-18
 * @updateAuthor
 * @updateDate
 * @description 线程工具类
 * @copyright copyright(c)2020 ChangSha hm Technology Co., Ltd. Inc. All rights reserved.
 */
public class TheadPool {

    private static TheadPool INSTANCE;
    private int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private int KEEP_ALIVE_TIME = 2;
    private TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private ExecutorService mExecutorService;

    public static TheadPool getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TheadPool();
        }
        return INSTANCE;
    }

    public TheadPool() {
        init();
    }

    private void init() {
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
        mExecutorService = new ThreadPoolExecutor(NUMBER_OF_CORES,
                NUMBER_OF_CORES * 2, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, taskQueue,
                new PriorityThreadFactory(), new DefaultRejectedExecutionHandler());
    }

    public void onExecute(Runnable runnable,String url) {
        if (mExecutorService == null) {
            init();
        }
        if (mExecutorService != null) {
            mExecutorService.execute(runnable);
        }
    }

    private class PriorityThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(final Runnable runnable) {
            Runnable wrapperRunnable = new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            };
            return new Thread(wrapperRunnable);
        }
    }

    private class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            LogUtil.getLog().i("DownloadUtil", "线程已结束==================================");
        }
    }
}

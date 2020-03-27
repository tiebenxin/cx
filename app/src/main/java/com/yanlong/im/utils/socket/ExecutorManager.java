package com.yanlong.im.utils.socket;

import net.cb.cb.library.utils.LogUtil;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Liszt
 * @date 2020/2/27
 * Description
 */
public class ExecutorManager {
    public static final String THREAD_NAME_PREFIX = "im-client-";
    public static final String WRITE_THREAD_NAME = THREAD_NAME_PREFIX + "write-t";
    public static final String READ_THREAD_NAME = THREAD_NAME_PREFIX + "read-t";

    public static final ExecutorManager INSTANCE = new ExecutorManager();

    private ThreadPoolExecutor writeThread;
    private ThreadPoolExecutor readThread;
    private ThreadPoolExecutor normalThread;

    public ThreadPoolExecutor getWriteThread() {
        if (writeThread == null || writeThread.isShutdown()) {
            writeThread = new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(100),
                    new NamedThreadFactory(WRITE_THREAD_NAME),
                    new RejectedHandler());
        }
        return writeThread;
    }

    public ThreadPoolExecutor getReadThread() {
        if (readThread == null || readThread.isShutdown()) {
            readThread = new ThreadPoolExecutor(1, 1,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(100),
                    new NamedThreadFactory(WRITE_THREAD_NAME),
                    new RejectedHandler());
        }
        return readThread;
    }

    public ThreadPoolExecutor getNormalThread() {
        if (normalThread == null || normalThread.isShutdown()) {
            normalThread = new ThreadPoolExecutor(1, 10,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(100),
                    new NamedThreadFactory(WRITE_THREAD_NAME),
                    new RejectedHandler());
        }
        return normalThread;
    }


    private static class RejectedHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            LogUtil.getLog().i("a task was rejected r=%s", r.toString());
        }
    }


}

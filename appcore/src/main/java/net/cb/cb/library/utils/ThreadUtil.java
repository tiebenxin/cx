package net.cb.cb.library.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Liszt
 * @date 2020/7/30
 * Description
 */
public class ThreadUtil {
    private static ThreadUtil instance = null;
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);
    private ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();

    private ThreadUtil() {
    }

    public static ThreadUtil getInstance() {
        Class var0 = ThreadUtil.class;
        synchronized(ThreadUtil.class) {
            if (instance == null) {
                instance = new ThreadUtil();
            }
        }

        return instance;
    }

    public ExecutorService getSingleThreadPool() {
        return this.singleThreadPool;
    }

    public void singleExecute(Runnable runnable) {
        if (runnable != null) {
            this.singleThreadPool.execute(runnable);
        }

    }

    public void runMainThread(Runnable runnable) {
        (new Handler(Looper.getMainLooper())).post(runnable);
    }

    public void execute(Runnable runnable) {
        this.fixedThreadPool.execute(runnable);
    }

    public void shutdown() {
        this.fixedThreadPool.shutdown();
    }

    public void shutdownNow() {
        this.fixedThreadPool.shutdownNow();
    }
}

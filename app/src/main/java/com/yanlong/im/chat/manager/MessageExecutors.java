package com.yanlong.im.chat.manager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 线程池: 避免任务不足的影响
 * 线程池：资源可用时自动运行任务，允许多个任务同事运行；
 * 一个任务多个并行,需确保线程安全，将多个线程访问的变量封装在synchroized块中，防止一个线程写入时，其他线程读取该变量
 *
 * @createAuthor Raleigh.Luo
 * @createDate 2020/6/9 0009
 * @description
 */
public class MessageExecutors {
    private Executor diskIO;
    private Executor DBIO;
    // 任务队列
    // Instantiates the queue of Runnables as a LinkedBlockingQueue
    private BlockingQueue<Runnable> decodeWorkQueue = new LinkedBlockingQueue<Runnable>();

    private MessageExecutors() {
        DBIO = Executors.newSingleThreadExecutor();//线程池:单个核线的fixed,一个任务一个任务执行的场景
        diskIO = Executors.newFixedThreadPool(3);//线程池:执行长期的任务，性能好很多
    }
    private static MessageExecutors INSTANCE;
    public static MessageExecutors getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MessageExecutors();
        }
        return INSTANCE;
    }
    public Executor getDiskIO() {
        return diskIO;
    }
    public Executor getDBIO() {
        return DBIO;
    }


    /**
     * 取消所有任务
     * Thread 对象由系统控制，系统可以在应用进程之外修改它们。因此，在中断线程之前您需要先锁定对它的访问，
     * 可将访问权限置于 synchronized 块中。
     */
    public void cancelAll() {
        /*
         * Creates and populates an array of Runnables with the Runnables in the queue
         */
        Runnable[] runnableArray = new Runnable[decodeWorkQueue.size()];
        decodeWorkQueue.toArray(runnableArray);
        /*
         * Iterates over the array of Runnables and interrupts each one's Thread.
         */
        synchronized (this) {
            // Iterates over the array of tasks
            for (Runnable thread : runnableArray) {
                //大多数情况下，Thread.interrupt() 会立即停止线程。但只会停止等待中的线程，而不会中断 CPU 或网络密集型任务。为避免减慢或锁定系统，需在尝试操作之前先测试是否存在待处理的中断请求：
                if (Thread.interrupted()) return;
                    //中断任务
                else ((Thread) thread).interrupt();
            }

        }
    }

}

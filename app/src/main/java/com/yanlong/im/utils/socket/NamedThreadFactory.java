package com.yanlong.im.utils.socket;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by LY309313 on 2017/9/23.
 */

public final class NamedThreadFactory implements ThreadFactory {
    protected final AtomicInteger threadNumber = new AtomicInteger(1);
    protected final String namePrefix;
    protected final ThreadGroup group;

    public NamedThreadFactory(final String namePrefix) {
        this.namePrefix = namePrefix;
        this.group = Thread.currentThread().getThreadGroup();
    }

    public Thread newThread(String name, Runnable r) {
        return new Thread(r, name);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = newThread(namePrefix + threadNumber.getAndIncrement(), r);
        if (t.isDaemon())
            t.setDaemon(false);
        return t;
    }

}

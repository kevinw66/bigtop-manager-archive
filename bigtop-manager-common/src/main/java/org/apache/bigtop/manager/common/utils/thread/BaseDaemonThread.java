package org.apache.bigtop.manager.common.utils.thread;

public abstract class BaseDaemonThread extends Thread {

    protected BaseDaemonThread(Runnable runnable) {
        super(runnable);
        this.setDaemon(true);
    }

    protected BaseDaemonThread(String threadName) {
        super();
        this.setName(threadName);
        this.setDaemon(true);
    }

}
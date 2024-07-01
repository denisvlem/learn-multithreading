package com.denisvlem;

public class ThreadLifeCycle {

    public static void main(String[] args) {
        var thread = new Thread(() -> {}, "ThreadName");
        System.out.println(thread.getState()); //NEW
        thread.start();
        System.out.println(thread.getState()); //RUNNABLE/TERMINATED

        var sharedResource = new SharedResource();
        var waitingThread = new Thread(sharedResource::doWait, "Waiting thread");

        var notifyingThread = new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            sharedResource.doNotify();
        }, "NotifyingThread");

        waitingThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }

        String waitingThreadName = waitingThread.getName();
        var state = waitingThread.getState(); //WAITING

        System.out.printf("[%s] is in the state: %s%n", waitingThreadName, state);
        notifyingThread.start();
    }

    public static class SharedResource {
        private static final String THREAD_STATE_LOG_TEMPLATE = "[%s] it's state%n";
        private volatile boolean condition = false;

        public synchronized void doWait() {
            while (!condition) {
                try {
                    System.out.println(Thread.currentThread().getName() + " is waiting");
                    wait(); //RUNNABLE
                    System.out.println("Wake up!!!");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Interrupted");
                    System.out.printf(THREAD_STATE_LOG_TEMPLATE, Thread.currentThread().getState());
                }
            }
            System.out.println(Thread.currentThread().getName() + " is proceeding");
            System.out.printf(THREAD_STATE_LOG_TEMPLATE, Thread.currentThread().getState());
        }

        public synchronized void doNotify() {
            condition = true;
            notifyAll();
            System.out.println(Thread.currentThread().getName() + " has notified");
            System.out.printf(THREAD_STATE_LOG_TEMPLATE, Thread.currentThread().getState());
        }
    }
}

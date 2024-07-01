package com.denisvlem.synchronization.deadlock;

import java.util.concurrent.TimeUnit;

public class DeadLockExample {

    private static final Object monitorOne = new Object();
    private static final Object monitorTwo = new Object();

    public static void main(String[] args) throws InterruptedException {

        var t1 = new Thread(new WorkerOne());
        var t2 = new Thread(new WorkerTwo());


        t1.start();
        t2.start();

        t1.join();
        t2.join();

    }


    public static class WorkerOne implements Runnable {

        @Override
        public void run() {
            while(true) {
                synchronized (monitorOne) {
                    var thread = Thread.currentThread().getName();
                    System.out.printf("[%s] acquired monitor one%n", thread);
                    sleep(100);
                    synchronized (monitorTwo) {
                        System.out.printf("[%s] acquired monitor two%n", thread);
                    }
                }
            }
        }
    }

    public static class WorkerTwo implements Runnable {

        @Override
        public void run() {
            while (true) {
                synchronized (monitorTwo) {
                    var thread = Thread.currentThread().getName();
                    System.out.printf("[%s] acquired monitor two%n", thread);
                    sleep(100);
                    synchronized (monitorOne) {
                        System.out.printf("[%s] acquired monitor one%n", thread);
                    }
                }
            }
        }
    }

    public static void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }

    }
}

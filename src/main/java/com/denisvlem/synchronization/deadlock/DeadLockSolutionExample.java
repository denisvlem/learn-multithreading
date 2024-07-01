package com.denisvlem.synchronization.deadlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadLockSolutionExample {

    private static final Lock monitorOne = new ReentrantLock();
    private static final Lock monitorTwo = new ReentrantLock();

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
            while (true) {
                try {
                    if (monitorOne.tryLock(100, TimeUnit.MILLISECONDS)) {
                        var thread = Thread.currentThread().getName();
                        System.out.printf("[%s] acquired monitor one%n", thread);
                        sleep(2000);
                        monitorOne.unlock();
                        if (monitorTwo.tryLock(1, TimeUnit.SECONDS)) {
                            System.out.printf("[%s] acquired monitor two%n", thread);
                            sleep(2000);
                            monitorTwo.unlock();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    public static class WorkerTwo implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    if (monitorTwo.tryLock(100, TimeUnit.MILLISECONDS)) {
                        var thread = Thread.currentThread().getName();
                        System.out.printf("[%s] acquired monitor two%n", thread);
                        sleep(2000);
                        monitorTwo.unlock();
                        if (monitorOne.tryLock(1, TimeUnit.SECONDS)) {
                            System.out.printf("[%s] acquired monitor one%n", thread);
                            sleep(2000);
                            monitorOne.unlock();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(e);
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

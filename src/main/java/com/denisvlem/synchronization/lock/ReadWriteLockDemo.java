package com.denisvlem.synchronization.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SuppressWarnings("CallToPrintStackTrace")
public class ReadWriteLockDemo {

    private static volatile int sharedResource = 0;
    private static final int LIMIT = 50;

    private static final Lock READ_LOCK = new ReentrantReadWriteLock().readLock();
    private static final Lock WRITE_LOCK = new ReentrantReadWriteLock().writeLock();

    public static void main(String[] args) {
        for (int i = 0; i < 10; ++i) {
            new Reader(String.valueOf(i)).start();
        }
        new Writer(String.valueOf(1)).start();
        new Writer(String.valueOf(2)).start();

    }


    public static class Reader extends Thread {

        public Reader(String name) {
            super("[Reader " + name + "]");
        }

        @Override
        public void run() {
            while (sharedResource < LIMIT) {
                READ_LOCK.lock();
                System.out.println(this.getName() + " reading: " + sharedResource);
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    this.interrupt();
                    e.printStackTrace();
                } finally {
                    READ_LOCK.unlock();
                }
            }
        }
    }

    public static class Writer extends Thread {

        public Writer(String name) {
            super("[Writer " + name + "]");
        }

        @Override
        @SuppressWarnings({"java:S2696", "java:S3078"})
        public void run() {
            while (WRITE_LOCK.tryLock() && sharedResource < LIMIT) {
                try {
                    ++sharedResource;
                    System.out.println(this.getName() + " updating:" + sharedResource);
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    this.interrupt();
                    e.printStackTrace();
                } finally {
                    WRITE_LOCK.unlock();
                }
            }
        }
    }
}

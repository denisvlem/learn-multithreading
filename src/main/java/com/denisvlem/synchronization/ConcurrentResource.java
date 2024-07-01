package com.denisvlem.synchronization;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentResource {

    private static final AtomicInteger counter = new AtomicInteger(0);

    public static void main(String[] args) {
        new Worker().start();
        new Worker().start();

        var threadName = Thread.currentThread().getName();
        System.out.printf("[%s]: counter = %d%n", threadName, counter.get());
    }

    public static class Worker extends Thread {

        @Override
        public void run() {
            while (counter.getAndIncrement() <= 10) {
                synchronized (counter) {

                    try {
                        TimeUnit.MILLISECONDS.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException(e);
                    }
                    var threadName = currentThread().getName();
                    System.out.printf("[%s]: counter = %d%n", threadName, counter.get());
                }
            }
        }
    }
}

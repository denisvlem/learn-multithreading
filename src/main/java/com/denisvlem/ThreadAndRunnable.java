package com.denisvlem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadAndRunnable {

    public static void main(String[] args) {

        ExecutorService exec = Executors.newCachedThreadPool();
        exec.submit(new MyRunnable());
        exec.submit(new MyThread());
        exec.submit(() -> System.out.println("My own implementation of runnable as a anonymous class"));
        exec.shutdown();
    }

    public static class MyRunnable implements Runnable {

        @Override
        public void run() {
            System.out.println("My own implementation of runnable as a separate class");
        }
    }

    public static class MyThread extends Thread {

        @Override
        public void run() {
            System.out.println("My own implementation of thread as a separate class");
        }
    }
}

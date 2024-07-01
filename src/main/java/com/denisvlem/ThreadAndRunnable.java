package com.denisvlem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Способы создания задачи в новом потоке.
 */
public class ThreadAndRunnable {

    public static void main(String[] args) {

        new MyRunnable(1).run(); //выполнится в главном (main) потоке
        new Thread(new MyRunnable(2)).start(); //выполнится в новом потоке
        new MyThread(3).start(); //выполнится в новом потоке
        //new MyThread(4).run(); это не start(), выполнится в главном потоке

        // Создаем отдельный пулл потоков заранее, после накидываем этому пулу задачи (Runnable).
        // Они выполняются в созданных потоках, а не в главном.
        try (ExecutorService exec = Executors.newCachedThreadPool()) {
            exec.submit(new MyRunnable(5));
            //exec.submit(new MyThread(6)); так тоже можно, но это бессмысленно
            //Анонимная имплементация Runnable
            exec.submit(() -> System.out.println(
                "[" + 100 + "]" + " " + Thread.currentThread().getName()
                    + " My own implementation of runnable as a anonymous class")
            );
        }
    }

    /**
     * Имплементация асинхронного (в другом потоке) действия через реализацию Runnable.
     * Важно понимать, что сама имплементация Runnable не создает отдельный поток,
     * лишь описывает задачу, которая будет исполняться.
     */
    public static class MyRunnable implements Runnable {

        private final int id;

        public MyRunnable(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            var threadName = Thread.currentThread().getName();
            System.out.printf("[%d] %s My own implementation of runnable as a separate class%n", id, threadName);
        }
    }

    /**
     * Имплементация асинхронного (в другом потоке) действия через наследование Thread.
     */
    public static class MyThread extends Thread {

        private final int id;

        public MyThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            var threadName = Thread.currentThread().getName();
            System.out.printf("[%d] %s My own implementation of thread as a separate class%n", id, threadName);
        }
    }
}

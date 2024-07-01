package com.denisvlem;

import java.util.concurrent.TimeUnit;

public class ThreadInterruption {

    public static void main(String[] args) {
        final var worker = new Worker("1");
        var worker2 = new Worker("2");

        worker.start();
        worker2.start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Main thread is interrupted");
        }
        worker.interrupt();


        try {
            TimeUnit.SECONDS.sleep(3);
            System.out.println("[1] state: " + worker.getState());

            //Не знаю, как убить потоки, грохнем все
            Runtime.getRuntime().exit(0);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Main thread is interrupted");
        }
    }


    public static class Worker extends Thread {

        private final String name;

        public Worker(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    System.out.printf("[%s] is interrupted: %s%n", name, this.isInterrupted());
                    TimeUnit.MILLISECONDS.sleep(600);
                    System.out.printf("[%s] Working %n", name);
                } catch (InterruptedException e) {
                    System.out.printf("[%s] Interrupted!!! %s, state is: %s%n", name, this.isInterrupted(), this.getState());
                    //Если закомментировать эту строку, то прерывание сбросится и поток продолжит работать как обычно.
                    this.interrupt();
                    System.out.printf("[%s] After this.interrupt() %s, state is: %s%n", name, this.isInterrupted(), this.getState());
                    //Если хотите убить поток, ставьте break
                }
            }
        }
    }
}
